package com.nicoqueijo.android.currencyconverter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.fragments.SelectExchangeRatesDialog;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.List;

public class SelectExchangeRatesRecyclerViewAdapter extends RecyclerView.Adapter
        <SelectExchangeRatesRecyclerViewAdapter.ViewHolder> implements Filterable {

    public static final String TAG = SelectExchangeRatesRecyclerViewAdapter.class.getSimpleName();

    private SelectExchangeRatesDialog mDialog;
    private List<Currency> mCurrencies;
    private List<Currency> mCurrenciesFull;

    public SelectExchangeRatesRecyclerViewAdapter(SelectExchangeRatesDialog dialog,
                                                  List<Currency> allCurrencies) {
        mDialog = dialog;
        mCurrencies = new ArrayList<>(allCurrencies);
        mCurrenciesFull = new ArrayList<>(allCurrencies);
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
        holder.mFlag.setImageResource(Utility.getDrawableResourceByName(mCurrencies.get(position)
                .getCurrencyCode().toLowerCase(), mDialog.getContext()));
        holder.mCurrencyCode.setText(mCurrencies.get(position).getTrimmedCurrencyCode());
        holder.mCurrencyName.setText(Utility.getStringResourceByName(mCurrencies.get(position)
                .getCurrencyCode(), mDialog.getContext()));
        boolean currencyIsSelected = mCurrencies.get(position).isSelected();
        holder.itemView.setClickable(!currencyIsSelected);
        holder.mCheck.setVisibility(currencyIsSelected ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mFlag;
        TextView mCurrencyCode;
        TextView mCurrencyName;
        ImageView mCheck;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mFlag = itemView.findViewById(R.id.flag);
            mCurrencyCode = itemView.findViewById(R.id.currency_code);
            mCurrencyName = itemView.findViewById(R.id.currency_name);
            mCheck = itemView.findViewById(R.id.check);
        }

        @Override
        public void onClick(View v) {
            Currency selectedCurrency = mCurrencies.get(getAdapterPosition());
            selectedCurrency.setSelected(true);
            mDialog.sendActiveCurrency(selectedCurrency);
            mDialog.dismiss();
        }
    }

    @Override
    public Filter getFilter() {
        return currenciesFilter;
    }

    /**
     * Performs the filtering based on the user input in the hosting fragment's SearchView.
     * Credit: https://www.youtube.com/watch?v=sJ-Z9G0SDhc
     */
    private Filter currenciesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Currency> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mCurrenciesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                String currencyCode;
                String currencyName;
                for (Currency currency : mCurrenciesFull) {
                    currencyCode = currency.getTrimmedCurrencyCode().toLowerCase();
                    currencyName = Utility.getStringResourceByName(currency.getCurrencyCode(),
                            mDialog.getContext()).toLowerCase();
                    if (currencyCode.contains(filterPattern) ||
                            currencyName.contains(filterPattern)) {
                        filteredList.add(currency);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCurrencies.clear();
            mCurrencies.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
