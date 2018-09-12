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
import com.nicoqueijo.android.currencyconverter.fragments.SelectExchangeRatesFragment;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView that displays all the exchange rates available for selection.
 * Implements Filterable to search and filter the long list of exchange rates.
 * Implements INameableAdapter to return the first char of the current element being scrolled.
 */
public class SelectExchangeRatesAdapter extends
        RecyclerView.Adapter<SelectExchangeRatesAdapter.ViewHolder>
        implements Filterable, INameableAdapter {

    public static final String TAG = SelectExchangeRatesAdapter.class.getSimpleName();

    private SelectExchangeRatesFragment mSelectExchangeRatesFragment;
    private List<Currency> mCurrencies;
    private List<Currency> mCurrenciesFull;

    /**
     * Constructor for the adapter.
     *
     * @param selectExchangeRatesFragment the selectExchangeRatesFragment fragment hosting this RecyclerView
     * @param allCurrencies               the list of all available currencies
     */
    public SelectExchangeRatesAdapter(SelectExchangeRatesFragment selectExchangeRatesFragment,
                                      List<Currency> allCurrencies) {
        mSelectExchangeRatesFragment = selectExchangeRatesFragment;
        mCurrencies = new ArrayList<>(allCurrencies);
        mCurrenciesFull = new ArrayList<>(allCurrencies);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selectable_exchange_rate, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFlag.setImageResource(Utility.getDrawableResourceByName(mCurrencies.get(position)
                .getCurrencyCode().toLowerCase(), mSelectExchangeRatesFragment.getContext()));
        holder.mCurrencyCode.setText(mCurrencies.get(position).getTrimmedCurrencyCode());
        holder.mCurrencyName.setText(Utility.getStringResourceByName(mCurrencies.get(position)
                .getCurrencyCode(), mSelectExchangeRatesFragment.getContext()));
        boolean currencyIsSelected = mCurrencies.get(position).isSelected();
        holder.itemView.setClickable(!currencyIsSelected);
        holder.mCheck.setVisibility(currencyIsSelected ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    /**
     * Retrieves the first character of the element in the current position of the adapter.
     *
     * @param position index of the adapter.
     * @return first character of element at this position.
     */
    @Override
    public Character getCharacterForElement(int position) {
        Character firstCharacter;
        try {
            firstCharacter = mCurrencies.get(position).getCurrencyCode()
                    .charAt(Currency.CURRENCY_CODE_STARTING_INDEX);
        } catch (IndexOutOfBoundsException exception) {
            firstCharacter = ' ';
        }
        return firstCharacter;
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
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
            mCheck = itemView.findViewById(R.id.check_mark);
        }

        @Override
        public void onClick(View v) {
            Currency selectedCurrency = mCurrencies.get(getAdapterPosition());
            selectedCurrency.setSelected(true);
            mSelectExchangeRatesFragment.sendActiveCurrency(selectedCurrency);
            mSelectExchangeRatesFragment.getFragmentManager().popBackStack();

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
                            mSelectExchangeRatesFragment.getContext()).toLowerCase();
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
