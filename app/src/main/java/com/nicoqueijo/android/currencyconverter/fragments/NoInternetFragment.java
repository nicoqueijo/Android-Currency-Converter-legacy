package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Fragment to notify the user when there is no internet and therefore exchange rates could not be
 * fetched.
 */
public class NoInternetFragment extends Fragment {

    public static final String TAG = NoInternetFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_internet, container, false);
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static NoInternetFragment newInstance() {
        NoInternetFragment noInternetFragment = new NoInternetFragment();
        Bundle args = new Bundle();
        return noInternetFragment;
    }

}
