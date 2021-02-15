package com.bambora.code.test.security;

import com.bambora.code.test.domain.request.Request;
import com.bambora.code.test.domain.response.Response;
import com.bambora.code.test.utils.Method;
import com.bambora.code.test.utils.exceptions.TrustlyConnectionException;
import com.bambora.code.test.utils.exceptions.TrustlyDataException;
import com.bambora.code.test.utils.exceptions.TrustlySignatureException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

@Component
public class SignedAPI {
    private final SignatureHandler signatureHandler;
    private final String apiUrl;

    public SignedAPI(SignatureHandler signatureHandler,
                     @Value("${trustly.api-url}")String apiUrl) {
        this.signatureHandler = signatureHandler;
        this.apiUrl = apiUrl;
    }

    /**
     * Sends given request to Trustly.
     * @param request Request to send to Trustly API
     * @return Response generated from the request.
     */
    public Response sendRequest(final Request request) {
        final GsonBuilder gsonBuilder = new GsonBuilder();

        if (request.getMethod() == Method.VIEW_AUTOMATIC_SETTLEMENT_DETAILS_CSV) {
            gsonBuilder.serializeNulls();
        }

        final Gson gson = gsonBuilder.create();

        signatureHandler.insertCredentials(request);
        signatureHandler.signRequest(request);

        final String jsonResponse = newHttpPost(gson.toJson(request, Request.class));

        return handleJsonResponse(jsonResponse, request.getUUID());
    }

    /**
     * Sends a POST data to Trustly server.
     * @param request String representation of a request.
     * @return String representation of a response.
     */
    private String newHttpPost(final String request) {
        try {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpPost httpPost = new HttpPost(apiUrl);
            final StringEntity jsonRequest = new StringEntity(request, "UTF-8");
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(jsonRequest);

            final HttpResponse result = httpClient.execute(httpPost);
            return EntityUtils.toString(result.getEntity(), "UTF-8");
        }
        catch (final IOException e) {
            throw new TrustlyConnectionException("Failed to send request.", e);
        }
    }

    /**
     * Deserializes and verifies incoming response.
     * @param responseJson response from Trustly.
     * @param requestUUID UUID from the request that resulted in the response.
     * @return Response object
     */
    private Response handleJsonResponse(final String responseJson, final String requestUUID) {
        final Gson gson = new Gson();
        final Response response = gson.fromJson(responseJson, Response.class);
        verifyResponse(response, requestUUID);
        return response;
    }

    private void verifyResponse(final Response response, final String requestUUID) {
        if (!signatureHandler.verifyResponseSignature(response)) {
            throw new TrustlySignatureException("Incoming data signature is not valid");
        }
        if(response.getUUID() != null && !response.getUUID().equals(requestUUID) ) {
            throw new TrustlyDataException("Incoming data signature is not valid");
        }
    }

    /**
     * Generates a random messageID. Good for testing.
     * @return return a random generated messageid.
     */
    public String newMessageID() {
        final SecureRandom random = new SecureRandom();

        return new BigInteger(130, random).toString(32);
    }
}
