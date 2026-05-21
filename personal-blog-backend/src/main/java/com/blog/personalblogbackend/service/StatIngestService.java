package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.model.dto.stat.PageViewRequest;

public interface StatIngestService {

    void recordPageView(PageViewRequest request);
}
