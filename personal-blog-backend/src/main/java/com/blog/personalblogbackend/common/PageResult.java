package com.blog.personalblogbackend.common;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private List<T> records;
    private long total;
    private long size;
    private long current;

    public PageResult(List<T> records, long total, long size, long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
    }

    public static <T> PageResult<T> build(List<T> records, long total, long size, long current) {
        return new PageResult<>(records, total, size, current);
    }

    // 方便 Mybatis-Plus IPage 转换为 PageResult
    public static <T> PageResult<T> build(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }
}
