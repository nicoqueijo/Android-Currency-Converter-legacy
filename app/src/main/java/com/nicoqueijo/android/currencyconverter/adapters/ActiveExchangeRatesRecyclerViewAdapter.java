package com.nicoqueijo.android.currencyconverter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActiveExchangeRatesRecyclerViewAdapter extends
        RecyclerView.Adapter<ActiveExchangeRatesRecyclerViewAdapter.ViewHolder> {

    private List<Integer> mFlags;
    private List<String> mCurrencies;

    public ActiveExchangeRatesRecyclerViewAdapter() {
        mFlags = new ArrayList<>(Arrays.asList(R.drawable.usdusd,
                R.drawable.usdeur, R.drawable.usdars, R.drawable.usduyu));
        mCurrencies = new ArrayList<>(Arrays.asList("usdusd", "usdeur", "usdars", "usduyu"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_active_exchange_rate, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFlag.setImageResource(mFlags.get(position));
        holder.mCurrency.setText(mCurrencies.get(position));
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mFlag;
        public TextView mCurrency;

        public ViewHolder(View itemView) {
            super(itemView);
            mFlag = itemView.findViewById(R.id.flag);
            mCurrency = itemView.findViewById(R.id.currency);
        }
    }
}
