package com.bambora.code.test.domain;

import com.google.gson.annotations.SerializedName;
import io.norberg.automatter.AutoMatter;

import java.util.Map;

@AutoMatter
public interface Deposit {
    @SerializedName("Username")
    String username();
    @SerializedName("Password")
    String password();
    @SerializedName("NotificationURL")
    String notificationURL();
    @SerializedName("EndUserID")
    String endUserId();
    @SerializedName("MessageID")
    String messageId();
    @SerializedName("Attributes")
    Map<String, Object> attributes();
}
