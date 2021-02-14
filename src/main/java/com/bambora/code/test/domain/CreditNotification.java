package com.bambora.code.test.domain;

import com.bambora.code.test.utils.Currency;
import com.google.gson.annotations.SerializedName;
import io.norberg.automatter.AutoMatter;

import java.util.Map;

@AutoMatter
public interface CreditNotification {

    @SerializedName("messageid")
    String messageId();
    @SerializedName("notificationid")
    String notificationId();
    @SerializedName("orderid")
    String orderId();
    @SerializedName("enduserid")
    String endUserId();
    String amount();
    Currency currency();
    String timestamp();
    Map<String, Object> attributes();
}
