package com.holland.graph_robot.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping
@RestController
public class IndexController {
    @Value("${spring.application.name}")
    private String name;

    @PostMapping("/test")
    public ResponseEntity<?> test(String a) {
        return ResponseEntity.ok().body("=>" + a + "::OK\n" + new Date());
    }

}
