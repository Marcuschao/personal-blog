package com.blog.personalblogbackend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogSiteProperties {
    private String siteUrl = "http://localhost:5173";
    /** 前端部署子路径，如 /pblog；根路径部署留空 */
    private String siteBasePath = "";
    private String siteTitle = "晓晓博客";
    private String siteDescription = "个人博客";
    private boolean notifyMailEnabled = false;
    private String notifyFrom = "";
    private PushSettings push = new PushSettings();

    @Data
    public static class PushSettings {
        private boolean enabled = false;
        private String publicKey = "";
        private String privateKey = "";
        private String subject = "mailto:admin@localhost";
    }

    /** 站点对外完整 URL，如 https://example.com/pblog/article/1 */
    public String resolvePublicUrl(String path) {
        String origin = siteUrl != null ? siteUrl.replaceAll("/$", "") : "";
        String basePath = siteBasePath != null ? siteBasePath.trim() : "";
        if (!basePath.isEmpty()) {
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }
            basePath = basePath.replaceAll("/$", "");
        }
        String p = path != null ? path : "/";
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return origin + basePath + p;
    }
}
