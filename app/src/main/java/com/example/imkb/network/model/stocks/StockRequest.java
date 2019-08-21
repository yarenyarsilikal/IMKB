package com.example.imkb.network.model.stocks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockRequest {
    @SerializedName("period")
    @Expose
    private String period;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
