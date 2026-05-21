package com.blog.personalblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.personalblogbackend.model.entity.FriendLink;
import com.blog.personalblogbackend.mapper.FriendLinkMapper;
import com.blog.personalblogbackend.service.FriendLinkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Override
    public List<FriendLink> listEnabledPublic() {
        return list(new LambdaQueryWrapper<FriendLink>()
                .eq(FriendLink::getStatus, 1)
                .orderByAsc(FriendLink::getSortOrder)
                .orderByAsc(FriendLink::getId));
    }
}
