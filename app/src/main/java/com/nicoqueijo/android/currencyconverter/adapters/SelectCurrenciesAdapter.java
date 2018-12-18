package com.nicoqueijo.android.currencyconverter.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.fragments.SelectCurrenciesFragment;
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
public class SelectCurrenciesAdapter extends
        RecyclerView.Adapter<SelectCurrenciesAdapter.ViewHolder>
        implements Filterable, INameableAdapter {

    public static final String TAG = SelectCurrenciesAdapter.class.getSimpleName();

    private SelectCurrenciesFragment mSelectCurrenciesFragment;
    private ArrayList<Currency> mCurrencies;
    private ArrayList<Currency> mCurrenciesFull;

    /**
     * Constructor for the adapter.
     *
     * @param selectCurrenciesFragment the Fragment hosting this adapter's RecyclerView.
     */
    public SelectCurrenciesAdapter(Fragment selectCurrenciesFragment) {
        mSelectCurrenciesFragment = (SelectCurrenciesFragment) selectCurrenciesFragment;
        mCurrencies = new ArrayList<>(CurrenciesSingleton.getInstance(selectCurrenciesFragment
                .getContext()).getCurrencies());
        mCurrenciesFull = new ArrayList<>(CurrenciesSingleton.getInstance(selectCurrenciesFragment
                .getContext()).getCurrencies());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_selectable_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFlag.setImageResource(Utility.getDrawableResourceByName(mCurrencies.get(position)
                .getCurrencyCode().toLowerCase(), mSelectCurrenciesFragment.getContext()));
        holder.mCurrencyCode.setText(mCurrencies.get(position).getTrimmedCurrencyCode());
        holder.mCurrencyName.setText(Utility.getStringResourceByName(mCurrencies.get(position)
                .getCurrencyCode(), mSelectCurrenciesFragment.getContext()));
        boolean currencyIsSelected = mCurrencies.get(position).isSelected();
        holder.itemView.setClickable(!currencyIsSelected);
        holder.mCheckMark.setVisibility(currencyIsSelected ? View.VISIBLE : View.INVISIBLE);
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

        ImageView mFlag;
        TextView mCurrencyCode;
        TextView mCurrencyName;
        ImageView mCheckMark;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mFlag = itemView.findViewById(R.id.flag);
            mCurrencyCode = itemView.findViewById(R.id.currency_code);
            mCurrencyName = itemView.findViewById(R.id.currency_name);
            mCheckMark = itemView.findViewById(R.id.check_mark);
        }

        @Override
        public void onClick(View v) {
            Currency selectedCurrency = mCurrencies.get(getAdapterPosition());
            selectedCurrency.setSelected(true);
            mSelectCurrenciesFragment.sendActiveCurrency(selectedCurrency);
            mSelectCurrenciesFragment.getFragmentManager().popBackStack();

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
                            mSelectCurrenciesFragment.getContext()).toLowerCase();
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
