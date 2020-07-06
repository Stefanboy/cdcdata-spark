package com.cdcdata.cdcdataweb.controller;

import com.cdcdata.cdcdataweb.domain.People;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MethodController {

    @GetMapping("/json")
    public People json(){
        return new People("aaa","000000",null,new Date());
    }

    private Map<String,Object> params = new HashMap<>();
    @GetMapping(path = "/{grade_id}/{class_id}")
    public Object find(@PathVariable("grade_id") String gradeId,
                       @PathVariable("class_id") String classId){

        params.put("gradeId",gradeId);
        params.put("classId",classId);
        return params;
    }

    @GetMapping(value = "/query")
    public Object query(int from, int size){

        params.put("from",from);
        params.put("size",size);
        return params;
    }

    @GetMapping(value = "/query2")//key值既可以是page 也可以是from
    public Object query2(@RequestParam(defaultValue = "1",name = "page") int from, int size){

        params.put("from",from);
        params.put("size",size);
        return params;
    }

    @GetMapping(value = "/request")
    public Object request(HttpServletRequest request){
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        params.put("name",name);
        params.put("password",password);
        return params;
    }

    @PostMapping(value = "/post")
    public Object login(String name,String password){
        params.put("name",name);
        params.put("password",password);
        return params;
    }

    @DeleteMapping(value = "/del")
    public Object delete(String id){
        params.put("id",id);
        return params;
    }
}
