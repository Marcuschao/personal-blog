package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.common.support.PageResult;
import com.blog.personalblogbackend.common.support.Result;
import com.blog.personalblogbackend.model.vo.search.ArticleSearchHitVo;
import com.blog.personalblogbackend.model.dto.search.SearchPageQuery;
import com.blog.personalblogbackend.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public Result<PageResult<ArticleSearchHitVo>> search(SearchPageQuery query) {
        return Result.success(searchService.searchPublished(query));
    }
}
