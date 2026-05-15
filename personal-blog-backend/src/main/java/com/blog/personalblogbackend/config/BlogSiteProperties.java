package com.blog.personalblogbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogSiteProperties {
    private String siteUrl = "http://localhost:5173";
    private String siteTitle = "晓晓博客";
    private String siteDescription = "个人博客";
    private boolean notifyMailEnabled = false;
    private String notifyFrom = "";
}
