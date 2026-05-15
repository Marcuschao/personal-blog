package com.blog.personalblogbackend.controller;

import com.blog.personalblogbackend.dto.diary.DiaryPublicDetailVo;
import com.blog.personalblogbackend.dto.diary.DiaryPublicListItemVo;
import com.blog.personalblogbackend.service.DiaryService;
import com.blog.personalblogbackend.support.PageResult;
import com.blog.personalblogbackend.support.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diary/public")
public class DiaryPublicController {

    @Autowired
    private DiaryService diaryService;

    @GetMapping
    public Result<PageResult<DiaryPublicListItemVo>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(diaryService.pagePublic(page, size));
    }

    @GetMapping("/{id:\\d+}")
    public Result<DiaryPublicDetailVo> get(@PathVariable Long id) {
        return Result.success(diaryService.getPublic(id));
    }
}
