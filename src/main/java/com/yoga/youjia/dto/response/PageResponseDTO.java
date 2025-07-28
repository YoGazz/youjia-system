package com.yoga.youjia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 *
 * @param <T> 列表项类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    
    /**
     * 数据列表
     */
    private List<T> content;
    
    /**
     * 当前页码（从0开始）
     */
    private int page;
    
    /**
     * 每页大小
     */
    private int size;
    
    /**
     * 总记录数
     */
    private long totalElements;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 是否为第一页
     */
    private boolean first;
    
    /**
     * 是否为最后一页
     */
    private boolean last;
}
