package com.nicoqueijo.android.currencyconverter.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;

/**
 * Groups common fields and methods that both the Volley and Connection error Fragments use in
 * an attempt to promote code reusability.
 */
public abstract class ErrorFragment extends Fragment {

    public static final String TAG = ErrorFragment.class.getSimpleName();

    public MainActivity hostingActivity;
    public Toolbar mToolbar;
    public ImageView mRefreshMenuItem;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        initMenu(menu, inflater);
    }

    /**
     * Gets the reference to the hosting activity and the toolbar. Notifies this Fragment that it
     * has a menu.
     */
    public void setUpFragment() {
        hostingActivity = (MainActivity) getActivity();
        mToolbar = hostingActivity.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
    }

    /**
     * Sets up the menu for this Fragment.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater used to instantiate menu XML files into Menu objects.
     */
    public void initMenu(Menu menu, MenuInflater inflater) {
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
     * When the refresh menu item is clicked, the drawer is closed (if open) and the hosting
     * activity retries to fetch data. If there is no internet a Snackbar is shown.
     */
    public void processRefreshClick() {
        hostingActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
        if (Utility.isNetworkAvailable(hostingActivity)) {
            hostingActivity.appLaunchSetup();
        } else {
            showNoInternetSnackbar();
        }
    }

    /**
     * Shows the Snackbar notifying there is no internet connection.
     */
    public void showNoInternetSnackbar() {
        Snackbar snackbar = Snackbar.make(this.getView(), R.string.no_internet,
                Snackbar.LENGTH_SHORT);
        Utility.styleSnackbar(snackbar, getContext());
        snackbar.show();
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
