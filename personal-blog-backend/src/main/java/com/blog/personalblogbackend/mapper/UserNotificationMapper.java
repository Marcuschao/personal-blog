package com.blog.personalblogbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.personalblogbackend.model.entity.UserNotification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserNotificationMapper extends BaseMapper<UserNotification> {
}
