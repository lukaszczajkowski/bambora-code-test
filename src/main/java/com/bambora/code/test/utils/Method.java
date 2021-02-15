package com.bambora.code.test.utils;

import com.google.gson.annotations.SerializedName;

public enum Method {
    @SerializedName("Deposit")
    DEPOSIT("Deposit"),
    @SerializedName("ViewAutomaticSettlementDetailsCSV")
    VIEW_AUTOMATIC_SETTLEMENT_DETAILS_CSV("ViewAutomaticSettlementDetailsCSV"),
    @SerializedName("credit")
    CREDIT("credit");

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
