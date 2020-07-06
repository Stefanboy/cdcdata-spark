package com.cdcdata.cdcdataweb.dao;

import com.cdcdata.cdcdataweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
//JpaRepository<User,Integer> 泛型1表示保存的东西是什么 泛型2表示主键是什么类型
//JpaRepository可以点进去看看里面封装的方法
public interface UserRepository extends JpaRepository<User,Integer> {
}
