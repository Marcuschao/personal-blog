package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.agent.KeywordHelper;
import com.blog.personalblogbackend.dto.agent.*;
import com.blog.personalblogbackend.entity.Article;
import com.blog.personalblogbackend.exception.ServiceException;
import com.blog.personalblogbackend.llm.AiService;
import com.blog.personalblogbackend.mapper.ArticleMapper;
import com.blog.personalblogbackend.service.AgentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentServiceImpl implements AgentService {

    private static final int CONTEXT_CHUNK = 1200;
    private static final int CONTEXT_CHUNK_SCOPED = 12000;
    private static final int WEEKLY_SNIPPET = 1500;
    private static final String REFUSAL = "抱歉，我只能回答博客相关的问题";

    private final AiService aiService;
    private final ArticleMapper articleMapper;
    private final ObjectMapper objectMapper;

    public AgentServiceImpl(AiService aiService, ArticleMapper articleMapper, ObjectMapper objectMapper) {
        this.aiService = aiService;
        this.articleMapper = articleMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public String outline(OutlineRequest request) {
        if (!StringUtils.hasText(request.getTitle()) && !StringUtils.hasText(request.getTopic())) {
            throw new ServiceException(400, "标题或主题至少填一项");
        }
        String sys = "你是博客写作助手。根据用户提供的主题生成清晰的文章大纲，使用 Markdown 多级列表，不要输出与大纲无关的话。";
        String user = "文章标题：" + nullToEmpty(request.getTitle())
                + "\n已有摘要：" + nullToEmpty(request.getSummary())
                + "\n补充主题说明：" + nullToEmpty(request.getTopic());
        return aiService.chat(sys, user);
    }

    @Override
    public String expand(ExpandRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ServiceException(400, "标题不能为空");
        }
        if (!StringUtils.hasText(request.getContext())) {
            throw new ServiceException(400, "上下文不能为空");
        }
        String sys = "你是博客写作助手。在保持风格一致的前提下续写正文，直接输出续写内容，不要重复已有段落。";
        String user = "标题：" + request.getTitle()
                + "\n已有内容：\n" + request.getContext()
                + "\n续写方向：" + nullToEmpty(request.getDirection());
        return aiService.chat(sys, user);
    }

    @Override
    public String polish(PolishRequest request) {
        if (!StringUtils.hasText(request.getText())) {
            throw new ServiceException(400, "文本不能为空");
        }
        String sys = "你是文字编辑。润色用户给出的文本，改正语病、优化表达，保留原意与 Markdown 结构，直接输出润色后的全文。";
        return aiService.chat(sys, request.getText());
    }

    @Override
    public String editorOutline(EditorOutlineRequest request) {
        if (!StringUtils.hasText(request.getTitle())
                && !StringUtils.hasText(request.getKeywords())
                && !StringUtils.hasText(request.getContent())) {
            throw new ServiceException(400, "标题、关键词或正文至少填一项");
        }
        String sys = "你是博客写作助手。根据标题、关键词与已有草稿节选生成清晰的文章大纲，使用 Markdown 多级列表，不要输出与大纲无关的寒暄。";
        String user = "文章标题：" + nullToEmpty(request.getTitle())
                + "\n关键词与侧重点：" + nullToEmpty(request.getKeywords())
                + "\n正文节选：\n" + truncate(nullToEmpty(request.getContent()), 8000);
        return aiService.chat(sys, user);
    }

    @Override
    public String editorContinue(EditorContinueRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ServiceException(400, "标题不能为空");
        }
        String content = nullToEmpty(request.getContent());
        int len = content.length();
        int rawStart = request.getSelectionStart() != null ? request.getSelectionStart() : len;
        int rawEnd = request.getSelectionEnd() != null ? request.getSelectionEnd() : len;
        int lo = clamp(Math.min(rawStart, rawEnd), 0, len);
        int hi = clamp(Math.max(rawStart, rawEnd), 0, len);
        int caret = lo;

        String before = truncateTail(content.substring(0, caret), 4500);
        String after = truncate(content.substring(hi), 1200);
        String ctx = before + "\n\n[[请在此处自然接续上文续写；以下为选区或光标之后的草稿节选（若有），勿重复粘贴]]\n\n" + after;

        ExpandRequest ex = new ExpandRequest();
        ex.setTitle(request.getTitle());
        ex.setContext(ctx);
        ex.setDirection(request.getKeywords());
        return expand(ex);
    }

    @Override
    public String editorPolish(EditorPolishRequest request) {
        if (!StringUtils.hasText(request.getSelectedText())) {
            throw new ServiceException(400, "待润色文本不能为空");
        }
        String sys = "你是文字编辑。润色用户给出的文本片段，改正语病、优化表达，保留原意与 Markdown 结构，直接输出润色后的全文，不要附加解释。";
        StringBuilder user = new StringBuilder();
        if (StringUtils.hasText(request.getTitle())) {
            user.append("文章标题：").append(request.getTitle()).append("\n\n");
        }
        user.append("待润色文本：\n").append(request.getSelectedText());
        return aiService.chat(sys, user.toString());
    }

    @Override
    public String summary(SummaryRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ServiceException(400, "标题不能为空");
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new ServiceException(400, "正文不能为空");
        }
        String sys = "你是博客编辑。根据标题与正文生成简短中文摘要（1～3 句），不要标题前缀，不要加引号。";
        String user = "标题：" + request.getTitle() + "\n正文：\n" + truncate(request.getContent(), 8000);
        return aiService.chat(sys, user);
    }

    @Override
    public List<String> tags(TagsRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new ServiceException(400, "标题不能为空");
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new ServiceException(400, "正文不能为空");
        }
        String sys = "你是博客编辑。根据标题与正文抽取 3～8 个简短中文标签，只输出 JSON 字符串数组，不要 markdown、不要其它说明。";
        String user = "标题：" + request.getTitle() + "\n正文：\n" + truncate(request.getContent(), 6000);
        String raw = aiService.chat(sys, user);
        return parseStringList(raw);
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        String q = request.resolveQuestion();
        if (!StringUtils.hasText(q)) {
            throw new ServiceException(400, "问题不能为空");
        }
        List<Article> articles = new ArrayList<>();
        Long scopedId = request.getArticleId();
        if (scopedId != null) {
            Article cur = articleMapper.selectById(scopedId);
            if (cur != null && Integer.valueOf(1).equals(cur.getStatus())) {
                articles.add(cur);
            }
        }
        int chunk = CONTEXT_CHUNK;
        if (articles.isEmpty()) {
            List<String> keywords = KeywordHelper.fromText(q);
            if (keywords.isEmpty()) {
                return refusalResponse();
            }
            articles = articleMapper.searchPublishedByKeywords(keywords, null, 3);
            if (articles == null || articles.isEmpty()) {
                articles = articleMapper.selectList(new QueryWrapper<Article>()
                        .eq("status", 1)
                        .orderByDesc("create_time")
                        .last("LIMIT 3"));
            }
            if (articles == null || articles.isEmpty()) {
                return refusalResponse();
            }
        } else {
            chunk = CONTEXT_CHUNK_SCOPED;
        }
        String sys = "你是博客问答助手。回答必须结合下面「参考资料」中的实际内容。"
                + "若用户问你能做什么、如何提问等元问题，请根据参考资料中的文章主题，简要说明可以围绕这些主题回答哪些类型的问题。"
                + "若参考资料与用户问题完全无关、或资料为空，请只回复：" + REFUSAL
                + "。不要编造参考资料中不存在的事实，表述需简洁。"
                + (articles.size() == 1 ? "当前用户正在阅读参考资料中的唯一一篇文章，请紧扣该文内容作答。" : "");
        StringBuilder ctx = new StringBuilder();
        List<ChatSourceDto> sources = new ArrayList<>();
        for (Article a : articles) {
            sources.add(new ChatSourceDto(a.getId(), a.getTitle()));
            ctx.append("---\n标题：").append(a.getTitle()).append("\n摘要：")
                    .append(nullToEmpty(a.getSummary())).append("\n正文片段：\n")
                    .append(truncate(nullToEmpty(a.getContent()), chunk)).append("\n");
        }
        String user = "参考资料：\n" + ctx + "\n用户问题：" + q;
        String answer = aiService.chat(sys, user);
        ChatResponse res = new ChatResponse();
        res.setAnswer(answer);
        res.setSources(sources);
        return res;
    }

    @Override
    public List<RecommendArticleDto> recommend(Long articleId) {
        if (articleId == null) {
            throw new ServiceException(400, "articleId 不能为空");
        }
        Article current = articleMapper.selectById(articleId);
        if (current == null) {
            throw new ServiceException(404, "文章不存在");
        }
        String blob = nullToEmpty(current.getTitle()) + " "
                + nullToEmpty(current.getSummary()) + " "
                + truncate(nullToEmpty(current.getContent()), 800);
        List<String> keywords = KeywordHelper.fromText(blob);
        if (keywords.isEmpty()) {
            return List.of();
        }
        List<Article> found = articleMapper.searchPublishedByKeywords(keywords, articleId, 3);
        if (found == null) {
            return List.of();
        }
        return found.stream().map(this::toRecommendDto).collect(Collectors.toList());
    }

    @Override
    public String weeklyReport(WeeklyReportRequest request) {
        if (request == null) {
            request = new WeeklyReportRequest();
        }
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDateTime from = weekStart.atStartOfDay();

        List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("status", 1)
                .ge("create_time", from)
                .orderByDesc("create_time")
                .last("LIMIT 25"));

        if (articles == null || articles.isEmpty()) {
            articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .eq("status", 1)
                    .orderByDesc("create_time")
                    .last("LIMIT 15"));
        }

        if (articles == null || articles.isEmpty()) {
            return "暂无已发布文章，无法生成周报。";
        }

        StringBuilder ctx = new StringBuilder();
        for (Article a : articles) {
            ctx.append("---\n标题：").append(nullToEmpty(a.getTitle()))
                    .append("\n摘要：").append(nullToEmpty(a.getSummary()))
                    .append("\n正文节选：\n")
                    .append(truncate(nullToEmpty(a.getContent()), WEEKLY_SNIPPET))
                    .append("\n发布时间：")
                    .append(a.getCreateTime() != null ? a.getCreateTime().toString() : "")
                    .append("\n");
        }

        String sys = "你是博客主编。根据给出的文章列表撰写周报，使用 Markdown。"
                + "结构建议：本周综述、主要更新（可按主题归纳）、简要数据观感（如篇数、主题分布）、下周写作建议。"
                + "只依据列表中的真实标题与摘要/节选内容归纳，不要编造不存在的文章。";

        String user = "以下为候选文章（优先自然周周一至当前已发布的稿件；若本周无稿则为站点近期已发布文章）：\n"
                + ctx;
        if (StringUtils.hasText(request.getFocus())) {
            user = user + "\n编排侧重点：" + request.getFocus().trim();
        }

        return aiService.chat(sys, user);
    }

    private ChatResponse refusalResponse() {
        ChatResponse r = new ChatResponse();
        r.setAnswer(REFUSAL);
        r.setSources(List.of());
        return r;
    }

    private RecommendArticleDto toRecommendDto(Article a) {
        RecommendArticleDto d = new RecommendArticleDto();
        d.setId(a.getId());
        d.setTitle(a.getTitle());
        d.setSummary(a.getSummary());
        d.setCover(a.getCover());
        d.setCreateTime(a.getCreateTime());
        return d;
    }

    private List<String> parseStringList(String raw) {
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        String s = raw.trim();
        int l = s.indexOf('[');
        int r = s.lastIndexOf(']');
        if (l >= 0 && r > l) {
            s = s.substring(l, r + 1);
        }
        try {
            List<String> list = objectMapper.readValue(s, new TypeReference<List<String>>() {});
            if (list == null) {
                return List.of();
            }
            LinkedHashSet<String> seen = new LinkedHashSet<>();
            for (String item : list) {
                if (StringUtils.hasText(item)) {
                    seen.add(item.trim());
                }
            }
            return new ArrayList<>(seen);
        } catch (Exception e) {
            return List.of();
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max);
    }

    private static String truncateTail(String s, int max) {
        if (s == null || s.length() <= max) {
            return s == null ? "" : s;
        }
        return s.substring(s.length() - max);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
