package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.dialogs.VolleyErrorDialog;

/**
 * Fragment to notify the user that an error has occurred when trying to fetch the data.
 */
public class VolleyErrorFragment extends ErrorFragment implements View.OnClickListener {

    public static final String TAG = VolleyErrorFragment.class.getSimpleName();

    public static final String ARG_ERROR_MESSAGE = "arg_error_message";

    private String mErrorMessage;
    private Button mShowButton;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @param errorMessage error message returned by the API call
     * @return a new instance of Fragment
     */
    public static VolleyErrorFragment newInstance(String errorMessage) {
        VolleyErrorFragment volleyErrorFragment = new VolleyErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        volleyErrorFragment.setArguments(args);
        return volleyErrorFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpFragment();
        if (getArguments() != null) {
            mErrorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        initViewsAndListeners(view);
        return view;
    }

    /**
     * Initializes the views and sets the onClick listener for the button.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViewsAndListeners(View view) {
        mShowButton = view.findViewById(R.id.show_button);
        mShowButton.setOnClickListener(this);
    }

    /**
     * Shows a dialog with the error message (as returned by the API call) upon click.
     */
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment errorDialog = VolleyErrorDialog.newInstance(mErrorMessage);
        errorDialog.show(fragmentManager, VolleyErrorDialog.TAG);
    }
}
