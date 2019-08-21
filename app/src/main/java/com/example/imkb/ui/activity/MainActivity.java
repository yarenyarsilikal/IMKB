package com.example.imkb.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.imkb.R;
import com.example.imkb.network.Connection;
import com.example.imkb.network.WebService;
import com.example.imkb.network.model.handshake.HandShakeRequest;
import com.example.imkb.network.model.handshake.HandShakeResponse;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_AES_KEY = "aes_key";
    public static final String KEY_AES_IV = "aes_iv";
    public static final String KEY_AUTH = "auth";


    private String aesKey;
    private String aesIv;
    private String auth;

    private HandShakeRequest handShakeRequest = new HandShakeRequest();

    //region LIFECYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeResources();
        getConnection();
    }
    //endregion

    //region INITIALIZATION METHODS
    private void initializeViews() {
        Button button = findViewById(R.id.btn_open);
        button.setOnClickListener(this);
    }

    private void initializeResources() {
        String uuid = UUID.randomUUID().toString();
        handShakeRequest.setDeviceId(uuid);
        String systemVersion = Build.VERSION.RELEASE;
        handShakeRequest.setSystemVersion(systemVersion);
        handShakeRequest.setPlatformName("Android");
        String deviceModel = Build.MODEL;
        handShakeRequest.setDeviceModel(deviceModel);
        String manifacturer = Build.MANUFACTURER;
        handShakeRequest.setManifacturer(manifacturer);
    }
    //endregion

    //region FLOW METHODS

    @Override
    public void onClick(View v) {
        startStockListActivity();
    }

    //endregion

    //region STORY METHODS

    private void startStockListActivity() {
        if(isNetworkAvailable(getApplicationContext())){
            Intent intent = new Intent(MainActivity.this, StockListActivity.class);
            intent.putExtra(KEY_AES_KEY, aesKey);
            intent.putExtra(KEY_AES_IV, aesIv);
            intent.putExtra(KEY_AUTH, auth);
            MainActivity.this.startActivity(intent);
        }else{
            showToast(getApplicationContext(), getResources().getString(R.string.no_network_error_message));
        }

    }

    private void getConnection() {
        WebService webService = Connection.getClient().create(WebService.class);

        Call<HandShakeResponse> call = webService.getInitializeParams(handShakeRequest);
        call.enqueue(new Callback<HandShakeResponse>() {
            @Override
            public void onResponse(Call<HandShakeResponse> call, Response<HandShakeResponse> response) {
                if (response.body() != null) {
                    aesKey = response.body().getAesKey();
                    aesIv = response.body().getAesIV();
                    auth = response.body().getAuthorization();
                }
            }

            @Override
            public void onFailure(Call<HandShakeResponse> call, Throwable t) {
                showToast(getApplicationContext(), getResources().getString(R.string.error_message));
            }
        });
    }
    //endregion


}
