package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.common.support.PageResult;
import com.blog.personalblogbackend.model.entity.SensitiveWord;

import java.util.List;

public interface SensitiveWordService {
    boolean contains(String text);

    String replace(String text);

    List<String> findAll(String text);

    PageResult<SensitiveWord> page(long page, long size);

    SensitiveWord add(String word);

    void delete(Long id);

    void refresh();
}
