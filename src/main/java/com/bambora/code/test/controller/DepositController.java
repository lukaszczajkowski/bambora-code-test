package com.bambora.code.test.controller;

import com.bambora.code.test.domain.DepositBuilder;
import com.bambora.code.test.domain.Request;
import com.bambora.code.test.domain.RequestBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

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
    public RedirectView makeDeposit(@RequestBody String amount , ModelAndView modelAndView) {
        Request request = buildRequest(amount);
        modelAndView.addObject(request);
        return new RedirectView("redirect:/" + redirectUrl);
    }

    private Request buildRequest(String amount) {
        Map<String, Object> attributes = setAttributes(amount);
        return null;
    }

    private Map<String, Object> setAttributes(String amount) {
        Map<String, Object> attributes = new HashMap<>();
        return attributes;
    }

}
