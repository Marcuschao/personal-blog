package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.support.PageResult;
import com.blog.personalblogbackend.dto.ArticlePageQuery;
import com.blog.personalblogbackend.dto.search.ArticleSearchHitVo;
import com.blog.personalblogbackend.dto.search.SearchPageQuery;
import com.blog.personalblogbackend.entity.Article;
import com.blog.personalblogbackend.service.ArticleService;
import com.blog.personalblogbackend.service.SearchService;
import com.blog.personalblogbackend.util.HighlightUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ArticleService articleService;

    @Override
    public PageResult<ArticleSearchHitVo> searchPublished(SearchPageQuery query) {
        ArticlePageQuery pq = new ArticlePageQuery();
        pq.setKeyword(StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null);
        pq.setPage(query.getPage() != null ? query.getPage() : 1);
        pq.setSize(query.getSize() != null ? query.getSize() : 10);

        IPage<Article> page = articleService.getArticlePage(pq);
        String kw = pq.getKeyword() != null ? pq.getKeyword() : "";

        List<ArticleSearchHitVo> hits = page.getRecords().stream().map(a -> {
            ArticleSearchHitVo vo = new ArticleSearchHitVo();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setSummary(a.getSummary());
            vo.setCover(a.getCover());
            vo.setCreateTime(a.getCreateTime());
            vo.setUpdateTime(a.getUpdateTime());
            vo.setHighlightTitle(HighlightUtils.highlight(a.getTitle(), kw));
            vo.setHighlightSummary(HighlightUtils.highlight(
                    StringUtils.hasText(a.getSummary()) ? a.getSummary() : "", kw));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.build(hits, page.getTotal(), page.getSize(), page.getCurrent());
    }
}
