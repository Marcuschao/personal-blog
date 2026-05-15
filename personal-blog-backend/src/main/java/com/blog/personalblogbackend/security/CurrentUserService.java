package com.blog.personalblogbackend.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.personalblogbackend.entity.User;
import com.blog.personalblogbackend.exception.ServiceException;
import com.blog.personalblogbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    @Autowired
    private UserMapper userMapper;

    public Long requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new ServiceException(401, "未登录");
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", auth.getName()));
        if (user == null) {
            throw new ServiceException(401, "用户不存在");
        }
        return user.getId();
    }
}
