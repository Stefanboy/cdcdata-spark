package com.cdcdata.cdcdataweb.hivemetaoperations.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "columnsV2")
public class ColumnsV2 {
    @Id
    @Column(name = "CD_ID",length = 20)
    private Long id;
    @Column(name = "COMMENT",length = 256)
    private String comment;
    @Column(name = "COLUME_NAME",length = 128)
    private String columeName;
    @Column(name = "TYPE_NAME",length = 32672)
    private String typeName;
    @Column(name = "INTEGER_IDX",length = 11)
    private int integerIdx;

    public String getColumeName() {
        return columeName;
    }

    public void setColumeName(String columeName) {
        this.columeName = columeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getIntegerIdx() {
        return integerIdx;
    }

    public void setIntegerIdx(int integerIdx) {
        this.integerIdx = integerIdx;
    }
}
