package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Fragment to notify the user when there is no internet and therefore exchange rates could not be
 * fetched.
 */
public class ConnectionErrorFragment extends ErrorFragment {

    public static final String TAG = ConnectionErrorFragment.class.getSimpleName();

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static ConnectionErrorFragment newInstance() {
        return new ConnectionErrorFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_connection, container, false);
    }
}
