package com.blog.personalblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.personalblogbackend.captcha.MathCaptchaStore;
import com.blog.personalblogbackend.dto.comment.CommentCreateRequest;
import com.blog.personalblogbackend.dto.comment.CommentPublicVo;
import com.blog.personalblogbackend.entity.Article;
import com.blog.personalblogbackend.entity.Comment;
import com.blog.personalblogbackend.exception.ServiceException;
import com.blog.personalblogbackend.mapper.CommentMapper;
import com.blog.personalblogbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;

    @Autowired
    private MathCaptchaStore mathCaptchaStore;
    @Autowired
    private com.blog.personalblogbackend.mapper.ArticleMapper articleMapper;

    @Override
    public List<CommentPublicVo> listApprovedForArticle(Long articleId) {
        List<Comment> list = list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, STATUS_APPROVED)
                .orderByAsc(Comment::getCreateTime));
        return list.stream()
                .map(c -> new CommentPublicVo(c.getId(), c.getParentId(), c.getAuthor(), c.getContent(), c.getCreateTime()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void submit(CommentCreateRequest req) {
        if (!mathCaptchaStore.verifyAndConsume(req.getCaptchaId(), req.getCaptchaAnswer())) {
            throw new ServiceException(400, "验证码错误或已过期");
        }
        Article a = articleMapper.selectById(req.getArticleId());
        if (a == null || a.getStatus() == null || a.getStatus() != 1) {
            throw new ServiceException(404, "文章不存在或未发布");
        }
        if (req.getParentId() != null) {
            Comment parent = getById(req.getParentId());
            if (parent == null || !parent.getArticleId().equals(req.getArticleId())) {
                throw new ServiceException(400, "回复目标无效");
            }
        }
        Comment c = new Comment();
        c.setArticleId(req.getArticleId());
        c.setParentId(req.getParentId());
        c.setAuthor(req.getAuthor().trim());
        c.setEmail(req.getEmail().trim().toLowerCase());
        c.setContent(req.getContent().trim());
        c.setStatus(STATUS_PENDING);
        c.setCreateTime(LocalDateTime.now());
        save(c);
    }

    @Override
    public IPage<Comment> adminPage(int page, int size, Integer status) {
        LambdaQueryWrapper<Comment> q = new LambdaQueryWrapper<Comment>().orderByDesc(Comment::getCreateTime);
        if (status != null) {
            q.eq(Comment::getStatus, status);
        }
        return page(new Page<>(page, size), q);
    }
}
