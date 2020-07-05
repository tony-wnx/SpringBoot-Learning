package com.tony.quickstart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author： wnx
 * @date： 2020/7/5 21:33
 * @description： TODO
 * @version: 1.0
 */
@RestController
public class HelloWorldController {

    @GetMapping("hello")
    public String hello(){
        return "Hello World";
    }
}
