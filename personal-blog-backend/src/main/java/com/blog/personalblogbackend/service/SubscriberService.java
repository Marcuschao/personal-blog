package com.blog.personalblogbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.personalblogbackend.dto.subscribe.SubscribeRequest;
import com.blog.personalblogbackend.entity.Subscriber;

import java.util.List;

public interface SubscriberService extends IService<Subscriber> {

    int STATUS_PENDING = 0;
    int STATUS_ACTIVE = 1;

    void subscribe(SubscribeRequest req);

    boolean confirmSubscribe(String token);

    List<String> listActiveEmails();
}
