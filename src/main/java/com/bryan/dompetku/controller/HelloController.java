package com.bryan.dompetku.controller;

import com.bryan.dompetku.dto.GreetingResponse;
import com.bryan.dompetku.service.GreetingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hello")
public class HelloController {
    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping
    public GreetingResponse hello(@RequestParam(defaultValue = "Dunia") String name) {
        return greetingService.greet(name);
    }

    @GetMapping("/{name}")
    public GreetingResponse helloByPath(@PathVariable String name){
        return greetingService.greet(name);
    }
}