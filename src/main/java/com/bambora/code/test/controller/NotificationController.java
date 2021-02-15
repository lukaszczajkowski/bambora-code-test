package com.bambora.code.test.controller;

import com.bambora.code.test.domain.notification.Notification;
import com.bambora.code.test.domain.response.Response;
import com.bambora.code.test.security.NotificationHandler;
import com.bambora.code.test.utils.ResponseStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications/a2b63j23dj23883jhfhfh")
public class NotificationController {

    private final NotificationHandler notificationHandler;

    public NotificationController(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @PostMapping
    public String postNotification(@RequestBody String incomingNotification, Model model) {
        System.out.println(incomingNotification);

        Notification notification =
                notificationHandler.handleNotification(incomingNotification);

        Response notificationResponse =
                notificationHandler.prepareNotificationResponse(notification.getMethod(),
                        notification.getUUID(),
                        ResponseStatus.OK);

        String responseJson = notificationHandler.toJson(notificationResponse);
        model.addAttribute("responseJson", responseJson);
        return "response";
    }
}
