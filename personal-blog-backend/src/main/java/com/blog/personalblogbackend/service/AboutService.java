package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.model.entity.About;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AboutService extends IService<About> {

    /**
     * 获取关于我内容
     * @return 关于我实体
     */
    About getAboutContent();

    /**
     * 更新关于我内容
     * @param about 关于我实体
     * @return 是否成功
     */
    boolean updateAboutContent(About about);
}
