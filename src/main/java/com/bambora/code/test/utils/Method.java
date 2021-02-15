package com.bambora.code.test.utils;

import com.google.gson.annotations.SerializedName;

public enum Method {
    @SerializedName("Deposit")
    DEPOSIT("Deposit");

    private final String jsonName;

    Method(final String s) {
        jsonName = s;
    }

    public boolean equalsName(final String otherName) {
        return otherName != null && jsonName.equals(otherName);
    }

    public String toString() {
        return jsonName;
    }

}
