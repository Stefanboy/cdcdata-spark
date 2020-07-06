package com.cdc.platform.service;

import com.cdc.platform.domain.HDFSSummary;
import com.cdc.platform.domain.YARNSummary;

import java.util.List;

/**
 * 本次只监控hdfs 其实yarn也需要监控(以后再说)
 */
public interface MetricsService {
    public void addHDFSSummary(HDFSSummary hdfsSummary);

    public HDFSSummary findHDFSSummary(Long time);

    public List<HDFSSummary> HDFSSummarys(Long start,Long end);

    public void addYARNSummary(YARNSummary yarnSummary);

}
