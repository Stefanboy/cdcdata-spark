package com.cdcdata.cdcdataweb.controller;

import com.cdcdata.cdcdataweb.dao.UserRepository;
import com.cdcdata.cdcdataweb.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/save")
    public User save(User user){
        return userRepository.save(user);
    }

    @GetMapping("/user/{id}")
    public User get(@PathVariable("id") Integer id){
        return userRepository.findById(id).get();
    }
}
