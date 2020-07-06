package com.cdcdata.cdcdataweb.domain;

import javax.persistence.*;

@Entity
@Table(name = "jpa_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//id的生成策略
    private Integer id;
    @Column(name = "last_name",length = 50)//将该字段映射成字段名last_name，长度改为50
    private String name;
    private String email;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
