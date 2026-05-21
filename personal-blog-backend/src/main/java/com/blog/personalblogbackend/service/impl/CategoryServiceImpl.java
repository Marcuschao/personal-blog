package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.model.entity.Category;
import com.blog.personalblogbackend.mapper.CategoryMapper;
import com.blog.personalblogbackend.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getAllCategories() {
        return list(); // 使用 MyBatis-Plus 提供的 list 方法查询所有分类
    }
}
