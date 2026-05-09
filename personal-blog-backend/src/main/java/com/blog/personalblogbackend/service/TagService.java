package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TagService extends IService<Tag> {

    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<Tag> getAllTags();
}
