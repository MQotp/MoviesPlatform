package com.moviesPlatform.backend.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestSecuredController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from a secured endpoint!";
    }
}
