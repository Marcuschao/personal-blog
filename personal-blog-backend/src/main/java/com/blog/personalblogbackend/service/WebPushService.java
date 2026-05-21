package com.blog.personalblogbackend.service;

public interface WebPushService {

    boolean isOperational();

    void notifyNewArticle(Long articleId, String articleTitle);

    void sendCustom(String title, String body, String url);

    void sendForArticle(Long articleId);
}
