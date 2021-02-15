/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Trustly Group AB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bambora.code.test.security;

import com.bambora.code.test.domain.notification.Notification;
import com.bambora.code.test.domain.notification.notificationsdata.CreditData;
import com.bambora.code.test.domain.response.Response;
import com.bambora.code.test.requestbuilders.NotificationResponse;
import com.bambora.code.test.utils.Method;
import com.bambora.code.test.utils.NotificationDeserializer;
import com.bambora.code.test.utils.ResponseStatus;
import com.bambora.code.test.utils.exceptions.TrustlySignatureException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandler {
    private final SignatureHandler signatureHandler;

    public NotificationHandler(SignatureHandler signatureHandler) {
        this.signatureHandler = signatureHandler;
    }

    public Notification handleNotification(final String notificationJson) {
        final NotificationDeserializer deserializer = new NotificationDeserializer();

        deserializer.registerDataType(Method.CREDIT.toString(), CreditData.class);

        final Gson gson = new GsonBuilder().registerTypeAdapter(Notification.class, deserializer)
                .create();

        final Notification notification = gson.fromJson(notificationJson, Notification.class);

        verifyNotification(notification);

        return notification;
    }

    private void verifyNotification(final Notification notification) {
        if (!signatureHandler.verifyNotificationSignature(notification)) {
            throw new TrustlySignatureException("Incoming data signature is not valid");
        }
    }

    /**
     * Creates a response for an incoming notification.
     * @param method method of the notification
     * @param uuid UUID of the incoming notification
     * @param status OK/FAIL
     * @return Notification response
     */
    public Response prepareNotificationResponse(final Method method, final String uuid, final ResponseStatus status) {
        final Response response = new NotificationResponse.Build(method, uuid, status)
                .getResponse();

        signatureHandler.signNotificationResponse(response);

        return response;
    }

    public String toJson(final Response response) {
        return new Gson().toJson(response);
    }
}
