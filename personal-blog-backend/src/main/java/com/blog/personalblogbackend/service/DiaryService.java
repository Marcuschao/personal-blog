package com.blog.personalblogbackend.service;

import com.blog.personalblogbackend.model.vo.diary.DiaryPublicDetailVo;
import com.blog.personalblogbackend.model.vo.diary.DiaryPublicListItemVo;
import com.blog.personalblogbackend.model.dto.diary.DiarySaveRequest;
import com.blog.personalblogbackend.model.vo.diary.DiaryVo;
import com.blog.personalblogbackend.common.support.PageResult;

public interface DiaryService {

    PageResult<DiaryVo> pageMine(Long userId, int page, int size, String month, String tag);

    DiaryVo getMine(Long userId, Long id);

    Long create(Long userId, DiarySaveRequest req);

    void update(Long userId, Long id, DiarySaveRequest req);

    void delete(Long userId, Long id);

    PageResult<DiaryPublicListItemVo> pagePublic(int page, int size);

    DiaryPublicDetailVo getPublic(Long id);
}
