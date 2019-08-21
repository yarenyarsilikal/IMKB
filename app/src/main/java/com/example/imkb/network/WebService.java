package com.example.imkb.network;

import com.example.imkb.network.model.handshake.HandShakeRequest;
import com.example.imkb.network.model.handshake.HandShakeResponse;
import com.example.imkb.network.model.stockdetail.StockDetailRequest;
import com.example.imkb.network.model.stockdetail.StockDetailResponse;
import com.example.imkb.network.model.stocks.StockRequest;
import com.example.imkb.network.model.stocks.StockResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WebService {

    @POST("api/handshake/start")
    Call<HandShakeResponse> getInitializeParams(@Body HandShakeRequest handShakeRequest);

    @POST("api/stocks/list")
    Call<StockResponse> getStocks(@Header("X-VP-Authorization") String auth, @Body StockRequest stockRequest);

    @POST("/api/stocks/detail")
    Call<StockDetailResponse> getStockDetail(@Header("X-VP-Authorization") String auth, @Body StockDetailRequest stockDetailRequest);
}
