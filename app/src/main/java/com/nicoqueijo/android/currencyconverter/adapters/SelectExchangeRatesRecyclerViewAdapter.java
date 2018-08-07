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

public class SelectExchangeRatesRecyclerViewAdapter extends
        RecyclerView.Adapter<SelectExchangeRatesRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = SelectExchangeRatesRecyclerViewAdapter.class.getSimpleName();

    Context mContext;
    List<Currency> mCurrencies;

    public SelectExchangeRatesRecyclerViewAdapter(Context context, List<Currency> currencies) {
        mContext = context;
        mCurrencies = currencies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_exchange_rate, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFlag.setImageResource(Utility
                .getDrawableResourceByName(mCurrencies
                        .get(position)
                        .getCurrencyCode()
                        .toLowerCase(), mContext));
        holder.mCurrencyCode.setText(mCurrencies.get(position).getCurrencyCode().substring(3));
        holder.mCurrencyName.setText(Utility.getStringResourceByName(mCurrencies.get(position)
                .getCurrencyCode(), mContext));
        if (mCurrencies.get(position).isSelected()) {
            holder.mCheck.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mFlag;
        public TextView mCurrencyCode;
        public TextView mCurrencyName;
        public ImageView mCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            mFlag = itemView.findViewById(R.id.flag);
            mCurrencyCode = itemView.findViewById(R.id.currency_code);
            mCurrencyName = itemView.findViewById(R.id.currency_name);
            mCheck = itemView.findViewById(R.id.check);
        }
    }
}
