package com.cdc.platform.domain;

import javax.persistence.*;

@Entity
@Table(name = "cdcata_yarn_summary")
public class YARNSummary extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer liveNodeManagerNums;//活的NodeManager数量
    private Integer deadNodeManagerNums;//死的NodeManager数量
    private Integer unhealthNodeManagerNums;//不健康的NodeManager数量

    private Integer submittedApps;//提交的应用数
    private Integer pendingApps;//pending的应用数
    private Integer killedApps;//杀掉的应用数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLiveNodeManagerNums() {
        return liveNodeManagerNums;
    }

    public void setLiveNodeManagerNums(Integer liveNodeManagerNums) {
        this.liveNodeManagerNums = liveNodeManagerNums;
    }

    public Integer getDeadNodeManagerNums() {
        return deadNodeManagerNums;
    }

    public void setDeadNodeManagerNums(Integer deadNodeManagerNums) {
        this.deadNodeManagerNums = deadNodeManagerNums;
    }

    public Integer getUnhealthNodeManagerNums() {
        return unhealthNodeManagerNums;
    }

    public void setUnhealthNodeManagerNums(Integer unhealthNodeManagerNums) {
        this.unhealthNodeManagerNums = unhealthNodeManagerNums;
    }

    public Integer getSubmittedApps() {
        return submittedApps;
    }

    public void setSubmittedApps(Integer submittedApps) {
        this.submittedApps = submittedApps;
    }

    public Integer getPendingApps() {
        return pendingApps;
    }

    public void setPendingApps(Integer pendingApps) {
        this.pendingApps = pendingApps;
    }

    public Integer getKilledApps() {
        return killedApps;
    }

    public void setKilledApps(Integer killedApps) {
        this.killedApps = killedApps;
    }
}
