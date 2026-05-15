package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.support.Result;
import com.blog.personalblogbackend.dto.comment.CommentCreateRequest;
import com.blog.personalblogbackend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentSubmitController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public Result<String> submit(@Valid @RequestBody CommentCreateRequest body) {
        commentService.submit(body);
        return Result.success("提交成功，审核通过后可见");
    }
}
