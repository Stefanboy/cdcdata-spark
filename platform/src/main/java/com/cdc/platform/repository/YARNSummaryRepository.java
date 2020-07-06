package com.cdc.platform.repository;

import com.cdc.platform.domain.YARNSummary;
import org.springframework.data.jpa.repository.JpaRepository;

//参数1 对象 参数2 主键类型
public interface YARNSummaryRepository extends JpaRepository<YARNSummary,Long> {

}
