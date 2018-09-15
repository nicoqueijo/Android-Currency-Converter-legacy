package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.adapters.SelectCurrenciesAdapter;
import com.nicoqueijo.android.currencyconverter.interfaces.ICommunicator;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;

/**
 * Fragment used to search, filter, and add exchange rates to the ActiveCurrenciesFragment.
 */
public class SelectCurrenciesFragment extends Fragment {

    public static final String TAG = SelectCurrenciesFragment.class.getSimpleName();

    ArrayList<Currency> mAllCurrencies;

    private MainActivity hostingActivity;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SelectCurrenciesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DragScrollBar mDragScrollBar;

    /**
     * * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param allCurrencies the list of all available currencies.
     * @return a new instance of fragment
     */
    public static SelectCurrenciesFragment newInstance(ArrayList<Currency> allCurrencies) {
        SelectCurrenciesFragment selectCurrenciesFragment = new SelectCurrenciesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ActiveCurrenciesFragment.ARG_ALL_CURRENCIES, allCurrencies);
        selectCurrenciesFragment.setArguments(args);
        return selectCurrenciesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpFragment();
        if (getArguments() != null) {
            mAllCurrencies = getArguments().getParcelableArrayList(ActiveCurrenciesFragment
                    .ARG_ALL_CURRENCIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_currency, container, false);
        initViewsAdaptersAndListeners(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        initMenu(menu, inflater);
    }

    /**
     * Hides the keyboard when app returns to focus. The keyboard pops up in the first place
     * because the Fragment under this one has a list of EditTexts and when it is started again
     * one of the EditText's will have the focus and force the keyboard to show.
     */
    @Override
    public void onStart() {
        super.onStart();
        hostingActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Removes the Search menu item when this Fragment's view is destroyed as it is no longer
     * needed and would be irrelevant when other Fragment's take over the content frame.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mToolbar.getMenu().removeItem(R.id.search);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the reference to the hosting activity and the toolbar. Notifies this fragment that it
     * has a menu.
     */
    private void setUpFragment() {
        setHasOptionsMenu(true);
        hostingActivity = (MainActivity) getActivity();
        mToolbar = hostingActivity.findViewById(R.id.toolbar);
    }

    /**
     * Sets up the menu for this fragment.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater used to instantiate menu XML files into Menu objects.
     */
    public void initMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
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
    }

    /**
     * Initializes the views and sets up the adapters.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViewsAdaptersAndListeners(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_select_currencies);
        mDragScrollBar = view.findViewById(R.id.drag_scroll_bar);
        mDragScrollBar.setIndicator(new AlphabetIndicator(getContext()), true);
        mAdapter = new SelectCurrenciesAdapter(this, mAllCurrencies);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    /**
     * Passes the newly selected currency to the ActiveCurrenciesFragment via an interface.
     *
     * @param currency the new currency that was selected
     */
    public void sendActiveCurrency(Currency currency) {
        ICommunicator communicator = (ICommunicator) getActivity();
        communicator.passSelectedCurrency(currency);
    }
}
