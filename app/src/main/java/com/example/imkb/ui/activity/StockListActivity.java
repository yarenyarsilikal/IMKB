package com.example.imkb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imkb.R;
import com.example.imkb.network.Aes;
import com.example.imkb.network.Connection;
import com.example.imkb.network.WebService;
import com.example.imkb.network.model.stocks.StockRequest;
import com.example.imkb.network.model.stocks.StockResponse;
import com.example.imkb.network.model.stocks.Stocks;
import com.example.imkb.ui.adapter.StockAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, StockAdapter.ItemClickListener {

    public static final String KEY_STOCKID = "stockid";

    final String periodAll = "all";
    final String periodIncreasing = "increasing";
    final String periodDecreasing = "decreasing";
    final String periodVol30 = "volume30";
    final String periodVol50 = "volume50";
    final String periodVol100 = "volume100";

    private StockRequest stockRequest;
    private ArrayList<Stocks> stockList;
    private RecyclerView rvStocks;
    private StockAdapter stockAdapter;

    private String period;
    private String aesKey;
    private String aesIv;
    private String auth;

    private SearchView searchView;

    //region LIFECYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressDialog();
        initializeViews();
        initializeResources();

        getStockList(period);
    }
    //endregion

    //region INITIALIZATION METHODS
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        rvStocks = findViewById(R.id.rv_stock);

        searchView = findViewById(R.id.searchview);
        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (stockAdapter != null)
                    stockAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void initializeResources() {
        Intent intent = getIntent();
        aesKey = intent.getStringExtra(MainActivity.KEY_AES_KEY);
        aesIv = intent.getStringExtra(MainActivity.KEY_AES_IV);
        auth = intent.getStringExtra(MainActivity.KEY_AUTH);
        period = periodAll;
        stockList = new ArrayList<>();
    }

    private void initializeStocksAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvStocks.setLayoutManager(layoutManager);

        stockAdapter = new StockAdapter(this, stockList, aesKey, aesIv, this);
        rvStocks.setAdapter(stockAdapter);
    }
    //endregion

    //region FLOW METHODS

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                getStockList(periodAll);
                break;
            case R.id.nav_increasing:
                getStockList(periodIncreasing);
                break;
            case R.id.nav_decreasing:
                getStockList(periodDecreasing);
                break;
            case R.id.nav_volume30:
                getStockList(periodVol30);
                break;
            case R.id.nav_volume50:
                getStockList(periodVol50);
                break;
            case R.id.nav_volume100:
                getStockList(periodVol100);
                break;
            default:


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onStockClicked(int position, List<Stocks> stocksList) {
        Stocks stock = stocksList.get(position);
        startStockDetailActivity(stock);
    }

    //endregion


    //region STORY METHODS

    private void startStockDetailActivity(Stocks stock){
        Intent intent = new Intent(StockListActivity.this, StockDetailActivity.class);
        intent.putExtra(KEY_STOCKID, stock.getId());
        intent.putExtra(MainActivity.KEY_AES_KEY, aesKey);
        intent.putExtra(MainActivity.KEY_AES_IV, aesIv);
        intent.putExtra(MainActivity.KEY_AUTH, auth);
        startActivity(intent);
    }

    private void getStockList(String period) {
        WebService webService = Connection.getClient().create(WebService.class);
        String encrypted = Aes.encrypt(aesKey, aesIv, period);

        stockRequest = new StockRequest();
        stockRequest.setPeriod(encrypted);

        Call<StockResponse> call = webService.getStocks(auth, stockRequest);
        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStocks() != null && !response.body().getStocks().isEmpty()) {
                        stockList.clear();
                        stockList.addAll(response.body().getStocks());
                        initializeStocksAdapter();
                        hideProgressDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                hideProgressDialog();
                if (isNetworkAvailable(getApplicationContext())) {
                    showToast(getApplicationContext(), getResources().getString(R.string.error_message));
                } else {
                    showToast(getApplicationContext(), getResources().getString(R.string.no_network_error_message));
                }
            }
        });
    }
    //endregion
}
