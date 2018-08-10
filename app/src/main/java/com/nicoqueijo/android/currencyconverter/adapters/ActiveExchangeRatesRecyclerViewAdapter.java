package com.nicoqueijo.android.currencyconverter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.algorithms.CurrencyConversion;
import com.nicoqueijo.android.currencyconverter.helpers.Constants;
import com.nicoqueijo.android.currencyconverter.helpers.CustomEditText;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.List;

public class ActiveExchangeRatesRecyclerViewAdapter extends
        RecyclerView.Adapter<ActiveExchangeRatesRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = ActiveExchangeRatesRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private List<Currency> mActiveCurrencies;
    private boolean onBind;

    public ActiveExchangeRatesRecyclerViewAdapter(Context context,
                                                  List<Currency> activeCurrencies) {
        mContext = context;
        mActiveCurrencies = activeCurrencies;
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
        onBind = true;
        Currency currentCurrency = mActiveCurrencies.get(position);
        holder.mCurrencyCode.setText(currentCurrency.getCurrencyCode()
                .substring(Constants.CURRENCY_CODE_STARTING_INDEX));
        holder.mFlag.setImageResource(Utility.getDrawableResourceByName(currentCurrency
                .getCurrencyCode().toLowerCase(), mContext));
        if (currentCurrency.getConversionValue() == Constants.ZERO) {
            holder.mConversionValue.setText(Constants.EMPTY_STRING);
        } else {
            holder.mConversionValue.setText(String.valueOf(currentCurrency.getConversionValue()));
        }
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return mActiveCurrencies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        ImageView mFlag;
        TextView mCurrencyCode;
        CustomEditText mConversionValue;

        ViewHolder(View itemView) {
            super(itemView);
            mFlag = itemView.findViewById(R.id.flag);
            mCurrencyCode = itemView.findViewById(R.id.currency_code);
            mConversionValue = itemView.findViewById(R.id.conversion_value);
            mConversionValue.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                mConversionValue.setText(text.substring(0, text.length() - 1));
                mConversionValue.setSelection(mConversionValue.getText().length());
                return;
            }
            if (s.length() > Constants.ZERO && !s.toString().equals(".")) {
                mActiveCurrencies.get(getAdapterPosition())
                        .setConversionValue(Double.parseDouble(s.toString()));
                for (int i = 0; i < mActiveCurrencies.size(); i++) {
                    if (!onBind) {
                        if (i == getAdapterPosition()) {
                            continue;
                        }
                        Currency focusedCurrency = mActiveCurrencies.get(getAdapterPosition());
                        Currency ithCurrency = mActiveCurrencies.get(i);
                        double amount = focusedCurrency.getConversionValue();
                        double fromRate = focusedCurrency.getExchangeRate();
                        double toRate = ithCurrency.getExchangeRate();
                        double convertedCurrency = CurrencyConversion
                                .currencyConverter(amount, fromRate, toRate);
                        ithCurrency.setConversionValue(convertedCurrency);
                        notifyItemChanged(i);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 1) {
                if (s.toString().startsWith(".")) {
                    s.insert(0, "0");
                } else if (s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        }
    }
}
