package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.helpers.Constants;

/**
 * Fragment to notify the user that an error has occurred.
 */
public class ErrorFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ErrorFragment.class.getSimpleName();

    private String mErrorMessage;
    private Button mShowHideButton;
    private TextView mErrorMessageTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mErrorMessage = getArguments().getString(Constants.ARG_ERROR_MESSAGE);
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
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static ErrorFragment newInstance(String errorMessage) {
        ErrorFragment errorFragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_ERROR_MESSAGE, errorMessage);
        errorFragment.setArguments(args);
        return errorFragment;
    }

    /**
     * Initializes the views and sets the onClick listener for the button.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViewsAndListeners(View view) {
        mShowHideButton = view.findViewById(R.id.show_hide_button);
        mErrorMessageTextView = view.findViewById(R.id.error_message);
        mErrorMessageTextView.setText(mErrorMessage);
        mErrorMessageTextView.setVisibility(View.GONE);
        mShowHideButton.setOnClickListener(this);
    }

    /**
     * Toggles between hiding and showing the error message.
     */
    @Override
    public void onClick(View v) {
        if (mShowHideButton.getText().toString().equals(getResources()
                .getString(R.string.show))) {
            mShowHideButton.setText(R.string.hide);
            mErrorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mShowHideButton.setText(R.string.show);
            mErrorMessageTextView.setVisibility(View.GONE);
        }
    }
}
