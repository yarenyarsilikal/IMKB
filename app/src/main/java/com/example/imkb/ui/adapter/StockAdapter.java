
package com.example.imkb.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imkb.R;
import com.example.imkb.network.Aes;
import com.example.imkb.network.model.stocks.Stocks;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> implements Filterable {

    private ItemClickListener itemClickListener;
    private List<Stocks> stockList;
    private List<Stocks> stocksListFull;
    private Context context;
    private String aesKey;
    private String aesIv;

    public StockAdapter(Context context, List<Stocks> stockList, String aesKey, String aesIv, ItemClickListener itemClickListener) {
        this.context = context;
        this.stockList = stockList;
        this.aesKey = aesKey;
        this.aesIv = aesIv;
        this.itemClickListener = itemClickListener;
        stocksListFull = new ArrayList<>(stockList);
    }

    @NonNull
    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stock, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockAdapter.ViewHolder viewHolder, int i) {
        viewHolder.llStock.setOnClickListener(v -> itemClickListener.onStockClicked(i, stockList));
        viewHolder.tvSymbol.setText(Aes.decrypt(aesKey, aesIv, stockList.get(i).getSymbol()));
        viewHolder.tvPrice.setText(stockList.get(i).getPrice().toString());
        viewHolder.tvDifference.setText(stockList.get(i).getDifference().toString());
        viewHolder.tvVolume.setText(stockList.get(i).getVolume().toString());
        viewHolder.tvBid.setText(stockList.get(i).getBid().toString());
        viewHolder.tvOffer.setText(stockList.get(i).getOffer().toString());

        if (stockList.get(i).getIsUp()) {
            viewHolder.ivArrow.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_arrow_up));
        } else if (stockList.get(i).getIsDown()) {
            viewHolder.ivArrow.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_arrow_down));
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    @Override
    public Filter getFilter() {
        return stockFilter;
    }

    private Filter stockFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Stocks> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(stocksListFull);
            } else {
                String filter = charSequence.toString().toLowerCase().trim();
                for (Stocks stocks : stocksListFull) {
                    if (Aes.decrypt(aesKey, aesIv, stocks.getSymbol()).toLowerCase().contains(filter)) {
                        filteredList.add(stocks);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stockList.clear();
            stockList.addAll((List<Stocks>) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llStock;
        private TextView tvSymbol;
        private TextView tvPrice;
        private TextView tvDifference;
        private TextView tvVolume;
        private TextView tvBid;
        private TextView tvOffer;
        private ImageView ivArrow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llStock = itemView.findViewById(R.id.ll_stock);
            tvSymbol = itemView.findViewById(R.id.tv_symbol);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDifference = itemView.findViewById(R.id.tv_difference);
            tvVolume = itemView.findViewById(R.id.tv_volume);
            tvBid = itemView.findViewById(R.id.tv_bid);
            tvOffer = itemView.findViewById(R.id.tv_offer);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
        }

    }

    public interface ItemClickListener {
        void onStockClicked(int position, List<Stocks> stocksList);
    }
}



