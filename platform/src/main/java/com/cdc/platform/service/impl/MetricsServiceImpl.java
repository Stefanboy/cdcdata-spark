package com.cdc.platform.service.impl;

import com.cdc.platform.domain.HDFSSummary;
import com.cdc.platform.domain.YARNSummary;
import com.cdc.platform.repository.HDFSSummaryRepository;
import com.cdc.platform.repository.YARNSummaryRepository;
import com.cdc.platform.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired//注入
    private HDFSSummaryRepository hdfsSummaryRepository;

    @Autowired
    private YARNSummaryRepository yarnSummaryRepository;


    @Override
    public void addHDFSSummary(HDFSSummary hdfsSummary) {
        hdfsSummaryRepository.save(hdfsSummary);
    }

    @Override
    public HDFSSummary findHDFSSummary(Long time) {
        return hdfsSummaryRepository.findTopByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(time);
    }

    @Override
    public List<HDFSSummary> HDFSSummarys(Long start, Long end) {
        return hdfsSummaryRepository.findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(start,end);
    }

    @Override
    public void addYARNSummary(YARNSummary yarnSummary) {
        yarnSummaryRepository.save(yarnSummary);
    }
}
