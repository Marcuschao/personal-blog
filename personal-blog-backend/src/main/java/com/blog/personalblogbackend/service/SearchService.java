package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.support.PageResult;
import com.blog.personalblogbackend.dto.search.ArticleSearchHitVo;
import com.blog.personalblogbackend.dto.search.SearchPageQuery;

public interface SearchService {

    PageResult<ArticleSearchHitVo> searchPublished(SearchPageQuery query);
}
