package com.bambora.code.test.controller;

import com.bambora.code.test.domain.request.Request;
import com.bambora.code.test.domain.response.Response;
import com.bambora.code.test.requestbuilders.Deposit;
import com.bambora.code.test.security.NotificationHandler;
import com.bambora.code.test.domain.notification.notificationsdata.CreditData;
import com.bambora.code.test.security.SignedAPI;
import com.bambora.code.test.utils.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/deposit")
public class DepositController {

    private final NotificationHandler notificationHandler;
    private final SignedAPI signedAPI;
    private final String redirectUrl;
    private final String username;
    private final String password;
    private final String notificationUrl;
    private final String successUrl;
    private final String failUrl;

    public DepositController(SignedAPI signedAPI,
                             NotificationHandler notificationHandler,
                             @Value("${trustly.api-url}") String redirectUrl,
                             @Value("${trustly.api-username}") String username,
                             @Value("${trustly.api-password}") String password) {
        this.signedAPI = signedAPI;
        this.notificationHandler = notificationHandler;
        this.redirectUrl = redirectUrl;
        this.username = username;
        this.password = password;
        this.notificationUrl = "https://bambora-code-test-lc.herokuapp.com/notifications/a2b63j23dj23883jhfhfh";
        this.successUrl = "http://bambora-code-test-lc.herokuapp.com/deposit/success";
        this.failUrl = "http://bambora-code-test-lc.herokuapp.com/deposit/failure";
    }

    @PostMapping
    public RedirectView makeDeposit(@RequestParam(value = "amount") String amount) {
        if (Double.parseDouble(amount) <= 0) {
            throw new IllegalArgumentException("Amount cannot be lower or equal 0");
        }
        Request deposit = buildDeposit(amount);
        Response response = signedAPI.sendRequest(deposit);
        String iframeUrl = (String) ((Map<String, Object>) response.getResult().getData()).get("url");
        return new RedirectView(iframeUrl);
    }

    @RequestMapping("/success")
    public String success() {
        return "success";
    }

    @RequestMapping("/failure")
    public String failure() {
        return "failure";
    }

    private Request buildDeposit(String amount) {
        return new Deposit.Build(notificationUrl, "user@email.com", signedAPI.newMessageID(), Currency.SEK, "Steve", "Smith", "steve@smith.com")
                .locale("sv_SE")
                .amount(amount)
                .mobilePhone("070-1234567")
                .nationalIdentificationNumber("400915-6094")
                .successURL(successUrl)
                .failURL(failUrl)
                .getRequest();
    }
}
