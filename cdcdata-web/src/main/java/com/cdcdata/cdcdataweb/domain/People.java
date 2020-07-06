package com.cdcdata.cdcdataweb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class People {
    @JsonProperty("account") //改别名
    private String name;
    @JsonIgnore//忽略该字段 级该字段不输出
    private String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)//如果该字段为null不返回
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date birthday;//格式化日期

    public People() {
    }

    public People(String name, String password, String phone, Date birthday) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
