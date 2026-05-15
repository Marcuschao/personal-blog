package com.blog.personalblogbackend.event;

public record ArticlePublishedEvent(Long articleId, String title, String summary) {}
