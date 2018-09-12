package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.dialogs.ErrorDialog;

/**
 * Fragment to notify the user that an error has occurred.
 */
public class ErrorFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ErrorFragment.class.getSimpleName();

    public static final String ARG_ERROR_MESSAGE = "arg_error_message";

    private String mErrorMessage;
    private Button mShowHideButton;

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param errorMessage error message returned by the API call
     * @return a new instance of fragment
     */
    public static ErrorFragment newInstance(String errorMessage) {
        ErrorFragment errorFragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        errorFragment.setArguments(args);
        return errorFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mShowHideButton = view.findViewById(R.id.show_hide_button);
        mShowHideButton.setOnClickListener(this);
    }

    /**
     * Shows a dialog with the error message (as returned by the API call) upon click.
     */
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment errorDialog = ErrorDialog.newInstance(mErrorMessage);
        errorDialog.show(fragmentManager, ErrorDialog.TAG);
    }
}
