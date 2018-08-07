package com.nicoqueijo.android.currencyconverter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.List;

public class ActiveExchangeRatesRecyclerViewAdapter extends
        RecyclerView.Adapter<ActiveExchangeRatesRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = ActiveExchangeRatesRecyclerViewAdapter.class.getSimpleName();

    Context mContext;
    List<Currency> mCurrencies;

    public ActiveExchangeRatesRecyclerViewAdapter(Context context, List<Currency> currencies) {
        mContext = context;
        mCurrencies = currencies;
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
        holder.mCurrency.setText(mCurrencies.get(position).getCurrencyCode().substring(3));
        holder.mFlag.setImageResource(Utility
                .getDrawableResourceByName(mCurrencies
                        .get(position)
                        .getCurrencyCode()
                        .toLowerCase(), mContext));

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
            mCurrency = itemView.findViewById(R.id.currency_code);
        }
    }
}
