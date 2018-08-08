package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.adapters.SelectExchangeRatesRecyclerViewAdapter;
import com.nicoqueijo.android.currencyconverter.helpers.Constants;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.List;


public class SelectExchangeRatesDialog extends DialogFragment {

    public static final String TAG = SelectExchangeRatesDialog.class.getSimpleName();

    List<Currency> mCurrencies;

    private RecyclerView mRecyclerView;
    private SelectExchangeRatesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private SearchView mSearchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrencies = getArguments().getParcelableArrayList(Constants.ARG_CURRENCIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_exchange_rate, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_select_rates);
        mToolbar = view.findViewById(R.id.toolbar_search);
        mToolbar.inflateMenu(R.menu.menu_search);
        mSearchView = (SearchView) mToolbar.getMenu().findItem(R.id.search).getActionView();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_GO);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mAdapter = new SelectExchangeRatesRecyclerViewAdapter(getContext(), mCurrencies);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    public static SelectExchangeRatesDialog newInstance(ArrayList<Currency> currencies) {
        SelectExchangeRatesDialog selectExchangeRatesDialog = new SelectExchangeRatesDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.ARG_CURRENCIES, currencies);
        selectExchangeRatesDialog.setArguments(args);
        return selectExchangeRatesDialog;
    }
}
