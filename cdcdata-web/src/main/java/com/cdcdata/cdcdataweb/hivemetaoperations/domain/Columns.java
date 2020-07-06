package com.cdcdata.cdcdataweb.hivemetaoperations.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


public class Columns {

    private String columeName;
    private String typeName;
    private int integerIdx;

    public Columns() {
    }

    public Columns(String columeName, String typeName, int integerIdx) {
        this.columeName = columeName;
        this.typeName = typeName;
        this.integerIdx = integerIdx;
    }

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
