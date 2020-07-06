package com.cdc.platform.schedule;

import com.cdc.platform.domain.HDFSSummary;
import com.cdc.platform.domain.HadoopMetrics;
import com.cdc.platform.domain.YARNSummary;
import com.cdc.platform.service.MetricsService;
import com.cdc.platform.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 调度器
 */
@Component
public class HadoopJmxScheduler {
    //@Value("${cdcdata.hadoop.nn.uri}")
    private String nnUri = "http://jd:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeInfo";


    //@Value("${cdcdata.hadoop.rm.uri}")
    //private String rmUri;

    private String rmUri = "http://jd:38088/jmx?qry=Hadoop:service=ResourceManager,name=ClusterMetrics";
    private String queueUri = "http://jd:38088/jmx?qry=Hadoop:service=ResourceManager,name=QueueMetrics,q0=root";
    //public static final String JMXURL = "http://%s/jmx?qry=%s";
    //public static final String NAMENODEINFO = "Hadoop:service=NameNode,name=NameNodeInfo";
    //public static final String RESOURCEMANAGERINFO = "Hadoop:service=ResourceManager,name=ClusterMetrics";
    //public static final String RESOURCEMANAGERINFO = "Hadoop:service=ResourceManager,name=ClusterMetrics";
    //public static final String QUEUEINFO = "Hadoop:service=ResourceManager,name=QueueMetrics,q0=root";
    //Hadoop:service=ResourceManager,name=ClusterMetrics
    //Hadoop:service=ResourceManager,name=QueueMetrics,q0=root

    @Autowired
    MetricsService metricsService;


    @Scheduled(cron = "*/5 * * * * *")
    public void metrics(){
        try {

            //saveHDFS();
            saveYARN();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void saveYARN() throws Exception{
        HadoopMetrics metrics = HttpClient.httpGet(rmUri);
        YARNSummary yarnSummary = new YARNSummary();

        if(metrics.getValue("tag.ClusterMetrics").toString().equals("ResourceManager")){

            yarnSummary.setLiveNodeManagerNums((int)metrics.getValue("NumActiveNMs"));
            yarnSummary.setDeadNodeManagerNums((int)metrics.getValue("NumLostNMs"));
            yarnSummary.setUnhealthNodeManagerNums((int)metrics.getValue("NumUnhealthyNMs"));


        }

        metrics = HttpClient.httpGet(queueUri);
        if(metrics.getValue("modelerType").toString().equals("QueueMetrics,q0=root")){
            yarnSummary.setSubmittedApps((int)metrics.getValue("AppsSubmitted"));
            yarnSummary.setPendingApps((int)metrics.getValue("AppsPending"));
            yarnSummary.setKilledApps((int)metrics.getValue("AppsKilled"));

        }

        yarnSummary.setTrash(false);
        yarnSummary.setCreateTime(System.currentTimeMillis());

        metricsService.addYARNSummary(yarnSummary);
    }

    private void saveHDFS() throws Exception{
        HadoopMetrics metrics = HttpClient.httpGet(nnUri);

        HDFSSummary hdfsSummary = new HDFSSummary();
        hdfsSummary.setTotal(Long.parseLong(metrics.getValue("Total").toString()));
        hdfsSummary.setDfsUsed(Long.parseLong(metrics.getValue("Used").toString()));
        hdfsSummary.setPercentUsed(Float.parseFloat(metrics.getValue("PercentUsed").toString()));
        hdfsSummary.setDfsFree(Long.parseLong(metrics.getValue("Free").toString()));
        hdfsSummary.setFiles(Long.parseLong(metrics.getValue("TotalFiles").toString()));
        hdfsSummary.setBlocks(Long.parseLong(metrics.getValue("TotalBlocks").toString()));
        hdfsSummary.setPercentUsed(Float.parseFloat(metrics.getValue("PercentRemaining").toString()));

        hdfsSummary.setTrash(false);
        hdfsSummary.setCreateTime(System.currentTimeMillis());
        metricsService.addHDFSSummary(hdfsSummary);
    }

}
