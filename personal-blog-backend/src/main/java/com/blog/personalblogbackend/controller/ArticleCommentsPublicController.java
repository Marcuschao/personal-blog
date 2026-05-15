package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.support.Result;
import com.blog.personalblogbackend.dto.comment.CommentPublicVo;
import com.blog.personalblogbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleCommentsPublicController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{articleId}/comments")
    public Result<List<CommentPublicVo>> listForArticle(@PathVariable Long articleId) {
        return Result.success(commentService.listApprovedForArticle(articleId));
    }
}
