package com.nicoqueijo.android.currencyconverter.java.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.java.fragments.VolleyErrorFragment;
import com.nicoqueijo.android.currencyconverter.java.helpers.Utility;

/**
 * Dialog to display an error message to the user.
 */
public class VolleyErrorDialog extends DialogFragment {

    public static final String TAG = VolleyErrorDialog.class.getSimpleName();

    private String mErrorMessage;
    private TextView mErrorMessageTextView;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @param errorMessage error message returned by the API call
     * @return a new instance of Fragment
     */
    public static VolleyErrorDialog newInstance(String errorMessage) {
        VolleyErrorDialog volleyErrorDialog = new VolleyErrorDialog();
        Bundle args = new Bundle();
        args.putString(VolleyErrorFragment.ARG_ERROR_MESSAGE, errorMessage);
        volleyErrorDialog.setArguments(args);
        return volleyErrorDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mErrorMessage = getArguments().getString(VolleyErrorFragment.ARG_ERROR_MESSAGE);
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
