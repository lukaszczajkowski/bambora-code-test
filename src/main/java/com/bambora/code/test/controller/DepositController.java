package com.bambora.code.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/deposit")
public class DepositController {

    private final String redirectUrl;
    private final String username;
    private final String password;

    public DepositController(@Value("${trustly.api-url}") String redirectUrl,
                             @Value("${trustly.api-username}") String username,
                             @Value("${trustly.api-password}") String password) {
        this.redirectUrl = redirectUrl;
        this.username = username;
        this.password = password;
    }

    @PostMapping
    public RedirectView makeDeposit(@RequestParam(value = "amount") String amount) {

        return new RedirectView("https://www.baeldung.com/spring-redirect-and-forward");
    }

    @RequestMapping("/success")
    public String success() {
        return "success";
    }

    @RequestMapping("/failure")
    public String failure() {
        return "failure";
    }
}
