package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.GreetingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GreetingService {
    private final String greetingPrefix;

    public GreetingService(@Value("${dompetku.greeting-prefix}") String greetingPrefix) {
        this.greetingPrefix = greetingPrefix;
    }

    public GreetingResponse greet(String name) {
        String message = greetingPrefix + ", " + name + "!";
        return new GreetingResponse(message, LocalDateTime.now());
    }

}
