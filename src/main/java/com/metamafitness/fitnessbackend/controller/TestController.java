package com.metamafitness.fitnessbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/trainers")
    String helloWorld() {
        return "am a trainer hahaha";
    }

    @GetMapping("/test")
    String fun() {
        return "am a user hahaha";
    }
}
