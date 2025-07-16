package com.yoga.youjia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 *
 * @param <T> 列表项类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> list;       // 数据列表
    private long total;         // 总记录数
    private int pages;          // 总页数
    private int current;        // 当前页码
    private int size;           // 每页大小
}
