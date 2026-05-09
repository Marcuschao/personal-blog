package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
}
