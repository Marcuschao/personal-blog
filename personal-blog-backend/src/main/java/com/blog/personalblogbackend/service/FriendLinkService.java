package com.blog.personalblogbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.personalblogbackend.model.entity.FriendLink;

import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {

    List<FriendLink> listEnabledPublic();
}
