package com.nicoqueijo.android.currencyconverter.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.common.collect.Lists;
import com.nicoqueijo.android.currencyconverter.databinding.RowSelectableCurrencyBinding;
import com.nicoqueijo.android.currencyconverter.fragments.SelectableCurrenciesFragment;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.nicoqueijo.android.currencyconverter.singletons.CurrenciesSingleton;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView that displays all the exchange rates available for selection.
 * Implements Filterable to search and filter the long list of exchange rates.
 * Implements INameableAdapter to return the first char of the current element being scrolled.
 */
public class SelectableCurrenciesAdapter extends
        RecyclerView.Adapter<SelectableCurrenciesAdapter.ViewHolder>
        implements Filterable, INameableAdapter {

    public static final String TAG = SelectableCurrenciesAdapter.class.getSimpleName();

    private SelectableCurrenciesFragment mSelectableCurrenciesFragment;
    private ArrayList<Currency> mCurrencies;
    private ArrayList<Currency> mCurrenciesFull;

    /**
     * Constructor for the adapter.
     *
     * @param selectableCurrenciesFragment the Fragment hosting this adapter's RecyclerView.
     */
    public SelectableCurrenciesAdapter(Fragment selectableCurrenciesFragment) {
        mSelectableCurrenciesFragment = (SelectableCurrenciesFragment) selectableCurrenciesFragment;
        mCurrencies = Lists.newArrayList(CurrenciesSingleton
                .getInstance(selectableCurrenciesFragment.getContext()).getCurrencies());
        mCurrenciesFull = Lists.newArrayList(CurrenciesSingleton
                .getInstance(selectableCurrenciesFragment.getContext()).getCurrencies());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowSelectableCurrencyBinding binding = RowSelectableCurrencyBinding.inflate(inflater,
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mCurrencies.get(position));
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RowSelectableCurrencyBinding mBinding;

        ViewHolder(RowSelectableCurrencyBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(Currency currency) {
            mBinding.setCurrency(currency);
        }

        @Override
        public void onClick(View v) {
            Currency selectedCurrency = mCurrencies.get(getAdapterPosition());
            selectedCurrency.setSelected(true);
            mSelectableCurrenciesFragment.sendActiveCurrency(selectedCurrency);
            mSelectableCurrenciesFragment.getFragmentManager().popBackStack();
        }
    }

    @Override
    public Filter getFilter() {
        return currenciesFilter;
    }

    /**
     * Performs the filtering based on the user input in the hosting Fragment's SearchView.
     * Credit: https://www.youtube.com/watch?v=sJ-Z9G0SDhc
     */
    private Filter currenciesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Currency> filteredList = Lists.newArrayList();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mCurrenciesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                String currencyCode;
                String currencyName;
                for (Currency currency : mCurrenciesFull) {
                    currencyCode = currency.getTrimmedCurrencyCode().toLowerCase();
                    currencyName = Utility.getStringResourceByName(currency.getCurrencyCode(),
                            mSelectableCurrenciesFragment.getContext()).toLowerCase();
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

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCurrencies.clear();
            mCurrencies.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
