package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.common.support.PageResult;
import com.blog.personalblogbackend.model.vo.search.ArticleSearchHitVo;
import com.blog.personalblogbackend.model.dto.search.SearchPageQuery;

public interface SearchService {

    PageResult<ArticleSearchHitVo> searchPublished(SearchPageQuery query);
}
