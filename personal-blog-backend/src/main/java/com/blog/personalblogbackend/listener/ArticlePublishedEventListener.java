package com.blog.personalblogbackend.listener;

import com.blog.personalblogbackend.config.BlogSiteProperties;
import com.blog.personalblogbackend.event.ArticlePublishedEvent;
import com.blog.personalblogbackend.service.BlogMailService;
import com.blog.personalblogbackend.service.SubscriberService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class ArticlePublishedEventListener {

    private final SubscriberService subscriberService;
    private final BlogMailService blogMailService;
    private final BlogSiteProperties blogSiteProperties;

    public ArticlePublishedEventListener(SubscriberService subscriberService,
                                         BlogMailService blogMailService,
                                         BlogSiteProperties blogSiteProperties) {
        this.subscriberService = subscriberService;
        this.blogMailService = blogMailService;
        this.blogSiteProperties = blogSiteProperties;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onArticlePublished(ArticlePublishedEvent event) {
        List<String> emails = subscriberService.listActiveEmails();
        if (emails.isEmpty()) {
            return;
        }
        String base = blogSiteProperties.getSiteUrl().replaceAll("/$", "");
        String url = base + "/article/" + event.articleId();
        String summary = event.summary() != null ? event.summary() : "";
        String subject = "[" + blogSiteProperties.getSiteTitle() + "] 新文章：" + event.title();
        String body = event.title() + "\n\n" + summary + "\n\n阅读：" + url + "\n";
        for (String email : emails) {
            blogMailService.sendIfConfigured(email, subject, body);
        }
    }
}
