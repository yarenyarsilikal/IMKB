package com.example.imkb.network.model.stockdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockDetailRequest {
    @SerializedName("id")
    @Expose
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
