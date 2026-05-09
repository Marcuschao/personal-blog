package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.entity.Article;
import com.blog.personalblogbackend.entity.ArticleTag;
import com.blog.personalblogbackend.entity.Tag;
import com.blog.personalblogbackend.mapper.ArticleMapper;
import com.blog.personalblogbackend.mapper.TagMapper;
import com.blog.personalblogbackend.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.personalblogbackend.dto.ArticlePageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public IPage<Article> getArticlePage(ArticlePageQuery query) {
        Page<Article> page = new Page<>(query.getPage(), query.getSize());
        return articleMapper.selectArticleVOPage(page, query.getCategoryId(), query.getTagId(), query.getKeyword());
    }

    @Override
    public Article getArticleDetail(Long id) {
        return articleMapper.selectArticleVOById(id);
    }

    @Override
    @Transactional
    public boolean createArticle(Article article, List<String> tagNames) {
        // 1. 插入文章
        articleMapper.insert(article);

        // 2. 处理标签
        handleArticleTags(article.getId(), tagNames);
        return true;
    }

    @Override
    @Transactional
    public boolean updateArticle(Article article, List<String> tagNames) {
        // 1. 更新文章
        articleMapper.updateById(article);

        // 2. 处理标签 (先删除旧的，再插入新的)
        articleMapper.deleteArticleTagsByArticleId(article.getId());
        handleArticleTags(article.getId(), tagNames);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long id) {
        // 1. 删除文章-标签关联
        articleMapper.deleteArticleTagsByArticleId(id);
        // 2. 删除文章
        articleMapper.deleteById(id);
        return true;
    }

    private void handleArticleTags(Long articleId, List<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
            return;
        }

        List<Long> tagIdsToInsert = new ArrayList<>();
        for (String tagName : tagNames) {
            if (!StringUtils.hasText(tagName)) continue;

            Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().eq("name", tagName));
            if (tag == null) {
                // 标签不存在，则创建新标签
                tag = new Tag();
                tag.setName(tagName);
                tagMapper.insert(tag);
            }
            tagIdsToInsert.add(tag.getId());
        }

        if (!CollectionUtils.isEmpty(tagIdsToInsert)) {
            articleMapper.insertArticleTags(articleId, tagIdsToInsert);
        }
    }
}
