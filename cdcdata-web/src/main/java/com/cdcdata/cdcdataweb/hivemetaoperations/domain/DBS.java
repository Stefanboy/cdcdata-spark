package com.cdcdata.cdcdataweb.hivemetaoperations.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DBS")
public class DBS {

    @Id
    private Long dbId;
    private String desc;
    private String dbLocationUri;
    private String name;
    private String ownerName;
    private String ownerType;

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDbLocationUri() {
        return dbLocationUri;
    }

    public void setDbLocationUri(String dbLocationUri) {
        this.dbLocationUri = dbLocationUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }
}
