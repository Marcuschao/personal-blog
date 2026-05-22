package com.blog.personalblogbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.personalblogbackend.model.dto.comment.CommentCreateRequest;
import com.blog.personalblogbackend.model.vo.comment.CommentPublicVo;
import com.blog.personalblogbackend.model.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    List<CommentPublicVo> listApprovedForArticle(Long articleId);

    void submit(CommentCreateRequest req);

    void deleteComment(Long id);

    com.baomidou.mybatisplus.core.metadata.IPage<Comment> adminPage(int page, int size, Integer status, Long userId);
}
