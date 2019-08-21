package com.example.imkb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imkb.R;
import com.example.imkb.network.Aes;
import com.example.imkb.network.Connection;
import com.example.imkb.network.WebService;
import com.example.imkb.network.model.stockdetail.GraphicData;
import com.example.imkb.network.model.stockdetail.StockDetailRequest;
import com.example.imkb.network.model.stockdetail.StockDetailResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockDetailActivity extends BaseActivity {

    private TextView tvSymbole;
    private TextView tvLowest;
    private TextView tvHighest;
    private TextView tvPrice;
    private TextView tvMaximum;
    private TextView tvDifference;
    private TextView tvMinimum;
    private TextView tvCount;
    private TextView tvVolume;
    private TextView tvOffer;
    private TextView tvBid;
    private ImageView ivArrow;
    private LineChart lineChart;
    private List<GraphicData> graphicData;

    private String stockId;
    private String aesKey;
    private String aesIv;
    private String auth;

    //region LIFECYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        initializeViews();
        initializeResources();

        showProgressDialog();
        getStockDetail(stockId);
    }
    //endregion

    //region INITIALIZATION METHODS
    private void initializeViews() {
        tvSymbole = findViewById(R.id.tv_detail_symbole);
        tvLowest = findViewById(R.id.tv_detail_lowest);
        tvHighest = findViewById(R.id.tv_detail_highest);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvMaximum = findViewById(R.id.tv_detail_maximum);
        tvMinimum = findViewById(R.id.tv_detail_minimum);
        tvDifference = findViewById(R.id.tv_detail_difference);
        tvCount = findViewById(R.id.tv_detail_count);
        tvVolume = findViewById(R.id.tv_detail_volume);
        tvBid = findViewById(R.id.tv_detail_bid);
        tvOffer = findViewById(R.id.tv_detail_offer);
        ivArrow = findViewById(R.id.iv_detail_arrow);
        lineChart = findViewById(R.id.linechart);
        lineChart.setTouchEnabled(false);
        lineChart.setPinchZoom(false);

    }

    private void initializeResources() {
        Intent intent = getIntent();
        handleIntent(intent);
        graphicData = new ArrayList<>();
    }
    //endregion


    //region STORY METHODS

    private void handleIntent(Intent intent) {
        aesKey = intent.getStringExtra(MainActivity.KEY_AES_KEY);
        aesIv = intent.getStringExtra(MainActivity.KEY_AES_IV);
        auth = intent.getStringExtra(MainActivity.KEY_AUTH);
        stockId = String.valueOf(intent.getIntExtra(StockListActivity.KEY_STOCKID, 0));
    }

    private void getStockDetail(String id) {
        WebService webService = Connection.getClient().create(WebService.class);
        String encrypted = Aes.encrypt(aesKey, aesIv, id);

        StockDetailRequest stockDetailRequest = new StockDetailRequest();
        stockDetailRequest.setId(encrypted);

        Call<StockDetailResponse> call = webService.getStockDetail(auth, stockDetailRequest);
        call.enqueue(new Callback<StockDetailResponse>() {
            @Override
            public void onResponse(Call<StockDetailResponse> call, Response<StockDetailResponse> response) {
                if (response.body() != null) {

                    tvSymbole.setText(Aes.decrypt(aesKey, aesIv, response.body().getSymbol()));
                    tvLowest.setText(Double.toString(response.body().getLowest()));
                    tvHighest.setText(Double.toString(response.body().getHighest()));
                    tvPrice.setText(Double.toString(response.body().getPrice()));
                    tvMaximum.setText(Double.toString(response.body().getMaximum()));
                    tvMinimum.setText(Double.toString(response.body().getMinimum()));
                    tvDifference.setText(Double.toString(response.body().getDifference()));
                    tvCount.setText(Integer.toString(response.body().getCount()));
                    tvVolume.setText(Double.toString(response.body().getVolume()));
                    tvBid.setText(Double.toString(response.body().getBid()));
                    tvOffer.setText(Double.toString(response.body().getOffer()));
                    if (response.body().getUp()) {
                        ivArrow.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up));
                    } else if (response.body().getDown()) {
                        ivArrow.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_down));
                    }

                    graphicData = response.body().getGraphicData();
                    if (graphicData.size() != 0) {
                        drawLineChart(graphicData);
                    } else {
                        lineChart.setVisibility(View.INVISIBLE);
                    }
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<StockDetailResponse> call, Throwable t) {
                hideProgressDialog();
                if (isNetworkAvailable(getApplicationContext())) {
                    showToast(getApplicationContext(), getResources().getString(R.string.error_message));
                } else {
                    showToast(getApplicationContext(), getResources().getString(R.string.no_network_error_message));
                }
            }
        });
    }

    private void drawLineChart(List<GraphicData> graphicDataList) {
        ArrayList<Entry> values = new ArrayList<>();

        for (GraphicData graphicData : graphicDataList) {
            values.add(new Entry((float) graphicData.getDay(), graphicData.getValue().floatValue()));
        }
        LineDataSet lineDataSet = new LineDataSet(values, getResources().getString(R.string.label_arrow));

        lineDataSet.setFillAlpha(110);
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
