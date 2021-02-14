package com.bambora.code.test.domain;

import com.google.gson.annotations.SerializedName;
import io.norberg.automatter.AutoMatter;

import java.util.Map;

@AutoMatter
public interface Request {
    @SerializedName("Username")
    String username();
    @SerializedName("Password")
    String password();
    @SerializedName("Attributes")
    Map<String, Object> attributes();
}
