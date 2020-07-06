package com.cdc.platform.repository;

import com.cdc.platform.domain.HDFSSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//参数1 对象 参数2 主键类型
public interface HDFSSummaryRepository extends JpaRepository<HDFSSummary,Long> {

    HDFSSummary findTopByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(Long time);
    List<HDFSSummary> findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(Long start,Long end);
}
