package com.blog.personalblogbackend.service.impl;

import com.blog.personalblogbackend.model.entity.Tag;
import com.blog.personalblogbackend.mapper.TagMapper;
import com.blog.personalblogbackend.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public List<Tag> getAllTags() {
        return list(); // 使用 MyBatis-Plus 提供的 list 方法查询所有标签
    }
}
