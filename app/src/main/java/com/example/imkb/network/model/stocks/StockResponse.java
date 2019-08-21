package com.example.imkb.network.model.stocks;

import com.example.imkb.network.model.Status;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StockResponse {
    @SerializedName("stocks")
    @Expose
    private ArrayList<Stocks> stocks;
    @SerializedName("status")
    @Expose
    private Status status;

    public ArrayList<Stocks> getStocks() {
        return stocks;
    }

    public void setStocks(ArrayList<Stocks> stocks) {
        this.stocks = stocks;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



}
