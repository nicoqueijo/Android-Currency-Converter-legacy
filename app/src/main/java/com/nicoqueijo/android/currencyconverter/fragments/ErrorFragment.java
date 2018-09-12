package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.dialogs.ErrorDialog;

/**
 * Fragment to notify the user that an error has occurred.
 */
public class ErrorFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ErrorFragment.class.getSimpleName();

    public static final String ARG_ERROR_MESSAGE = "arg_error_message";

    private Toolbar mToolbar;
    private ImageView mRefreshMenuItem;
    private String mErrorMessage;
    private Button mShowButton;

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
        mToolbar = getActivity().findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_refresh);
        mRefreshMenuItem = (ImageView) mToolbar.getMenu().findItem(R.id.refresh).getActionView();
        mRefreshMenuItem.setImageResource(R.drawable.ic_refresh);
        mRefreshMenuItem.setPadding(24, 24, 24, 24);
        mShowButton = view.findViewById(R.id.show_button);
        mShowButton.setOnClickListener(this);
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
