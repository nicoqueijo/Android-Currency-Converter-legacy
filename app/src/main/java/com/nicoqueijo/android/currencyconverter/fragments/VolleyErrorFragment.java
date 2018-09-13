package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.dialogs.VolleyErrorDialog;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;

/**
 * Fragment to notify the user that an error has occurred when trying to fetch the data.
 */
public class VolleyErrorFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = VolleyErrorFragment.class.getSimpleName();

    public static final String ARG_ERROR_MESSAGE = "arg_error_message";

    private MainActivity hostingActivity;
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
        setupFragment();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        initMenu(menu, inflater);
    }

    /**
     * Gets the reference to the hosting activity and the toolbar. Notifies this fragment that it
     * has a menu.
     */
    private void setupFragment() {
        hostingActivity = (MainActivity) getActivity();
        mToolbar = hostingActivity.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
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
     * Sets up the menu for this fragment.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater used to instantiate menu XML files into Menu objects.
     */
    private void initMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        mRefreshMenuItem = (ImageView) menu.findItem(R.id.refresh).getActionView();
        mRefreshMenuItem.setImageResource(R.drawable.ic_refresh);
        mRefreshMenuItem.setPadding(24, 24, 24, 24);
        mRefreshMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRefreshClick();
            }
        });
    }

    /**
     * When the refresh menu item is clicked, the drawer is closed (if open), a rotating animation
     * is done on the menu item, and the hosting activity retries to fetch data. If there is no
     * internet a Snackbar is shown.
     */
    private void processRefreshClick() {
        hostingActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        if (Utility.isNetworkAvailable(hostingActivity)) {
            Animation animRotate = AnimationUtils.loadAnimation(hostingActivity, R.anim.rotate);
            mRefreshMenuItem.startAnimation(animRotate);
            hostingActivity.appLaunchSetup();
        } else {
            showNoInternetSnackbar();
        }
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

    /**
     * Shows the Snackbar notifying there is no internet connection.
     */
    private void showNoInternetSnackbar() {
        Snackbar.make(this.getView(), R.string.no_internet, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Removes the Refresh menu item when this Fragment's view is destroyed as it is no longer
     * needed and would be irrelevant when other Fragment's take over the content frame.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mToolbar.getMenu().removeItem(R.id.refresh);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
