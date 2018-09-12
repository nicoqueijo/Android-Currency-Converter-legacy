package com.nicoqueijo.android.currencyconverter.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.fragments.ErrorFragment;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;

/**
 * Dialog to display an error message to the user.
 */
public class ErrorDialog extends DialogFragment {

    public static final String TAG = ErrorDialog.class.getSimpleName();

    private String mErrorMessage;
    private TextView mErrorMessageTextView;

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param errorMessage error message returned by the API call
     * @return a new instance of fragment
     */
    public static ErrorDialog newInstance(String errorMessage) {
        ErrorDialog errorDialog = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString(ErrorFragment.ARG_ERROR_MESSAGE, errorMessage);
        errorDialog.setArguments(args);
        return errorDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mErrorMessage = getArguments().getString(ErrorFragment.ARG_ERROR_MESSAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error, container, false);
        Utility.roundDialogCorners(this);
        mErrorMessageTextView = view.findViewById(R.id.error_message);
        mErrorMessageTextView.setText(mErrorMessage);
        return view;
    }
}
