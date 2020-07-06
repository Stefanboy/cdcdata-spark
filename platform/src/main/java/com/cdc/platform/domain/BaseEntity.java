package com.cdc.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;

/**
 * 定义公共的方法
 */
@MappedSuperclass
public class BaseEntity {
    //创建时间
    private Long createTime;
    //是否删除
    @JsonIgnore //不返回
    private boolean isTrash = false;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
    }
}
