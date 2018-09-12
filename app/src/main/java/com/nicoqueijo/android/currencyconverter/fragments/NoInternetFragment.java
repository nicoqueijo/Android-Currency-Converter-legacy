package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Fragment to notify the user when there is no internet and therefore exchange rates could not be
 * fetched.
 */
public class NoInternetFragment extends Fragment {

    public static final String TAG = NoInternetFragment.class.getSimpleName();

    private Toolbar mToolbar;
    private ImageView mRefreshMenuItem;

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static NoInternetFragment newInstance() {
        return new NoInternetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);
        mToolbar = getActivity().findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_refresh);
        mRefreshMenuItem = (ImageView) mToolbar.getMenu().findItem(R.id.refresh).getActionView();
        mRefreshMenuItem.setImageResource(R.drawable.ic_refresh);
        mRefreshMenuItem.setPadding(24, 24, 24, 24);
        return view;
    }

    /**
     * Removes the Refresh menu item when this Fragment's view is destroyed as it is no longer
     * needed and would be irrelevant when other Fragment's take over the content frame.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mToolbar.getMenu().removeItem(R.id.refresh);
    }
}
