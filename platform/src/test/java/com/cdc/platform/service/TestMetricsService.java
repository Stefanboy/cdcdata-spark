package com.cdc.platform.service;

import com.cdc.platform.domain.HDFSSummary;
import com.cdc.platform.repository.HDFSSummaryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMetricsService {

    @Autowired
    HDFSSummaryRepository hdfsSummaryRepository;

    @Test
    public void findByTime(){

        HDFSSummary bean = hdfsSummaryRepository.findTopByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc( 1592126765031L);
        System.out.println(bean);
    }
    @Test
    public void findByTime2(){

        List<HDFSSummary> beans = hdfsSummaryRepository.findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(1589611226012L, 1592126765031L);
        for (HDFSSummary bean : beans) {
            System.out.println(bean);
        }

    }
}
