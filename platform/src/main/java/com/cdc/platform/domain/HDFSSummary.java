package com.cdc.platform.domain;

import javax.persistence.*;

//映射到数据库的类
@Entity
@Table(name = "cdcdata_hdfs_summary")
//该类其实就是对应hsfs界面的summary
public class HDFSSummary extends BaseEntity{

    @Id //主键自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long total;//总空间
    private Long dfsUsed;//用了多少
    private Float percentUsed;//百分比
    private Long dfsFree;//剩下多少
    private Float freeFercentUsed;//剩余占比
    private Long blocks;//总的block数量
    private Long files;//总问价数量
    private Long missingBlocks;//丢了多少个块

    @Override
    public String toString() {
        return "HDFSSummary{" +
                "id=" + id +
                ", total=" + total +
                ", dfsUsed=" + dfsUsed +
                ", percentUsed=" + percentUsed +
                ", dfsFree=" + dfsFree +
                ", freeFercentUsed=" + freeFercentUsed +
                ", blocks=" + blocks +
                ", files=" + files +
                ", missingBlocks=" + missingBlocks +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getDfsUsed() {
        return dfsUsed;
    }

    public void setDfsUsed(Long dfsUsed) {
        this.dfsUsed = dfsUsed;
    }

    public Float getPercentUsed() {
        return percentUsed;
    }

    public void setPercentUsed(Float percentUsed) {
        this.percentUsed = percentUsed;
    }

    public Long getDfsFree() {
        return dfsFree;
    }

    public void setDfsFree(Long dfsFree) {
        this.dfsFree = dfsFree;
    }

    public Float getFreeFercentUsed() {
        return freeFercentUsed;
    }

    public void setFreeFercentUsed(Float freeFercentUsed) {
        this.freeFercentUsed = freeFercentUsed;
    }

    public Long getBlocks() {
        return blocks;
    }

    public void setBlocks(Long blocks) {
        this.blocks = blocks;
    }

    public Long getFiles() {
        return files;
    }

    public void setFiles(Long files) {
        this.files = files;
    }

    public Long getMissingBlocks() {
        return missingBlocks;
    }

    public void setMissingBlocks(Long missingBlocks) {
        this.missingBlocks = missingBlocks;
    }
}
