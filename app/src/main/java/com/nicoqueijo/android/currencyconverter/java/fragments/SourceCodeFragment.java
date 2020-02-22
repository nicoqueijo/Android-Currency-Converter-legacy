package com.nicoqueijo.android.currencyconverter.java.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.java.activities.MainActivity;

/**
 * Fragment used to view the source code of the project via an embedded browser window inside the
 * app. Implements OnScrollChangedListener to monitor the scroll position.
 */
public class SourceCodeFragment extends Fragment implements
        ViewTreeObserver.OnScrollChangedListener {

    public static final String TAG = SourceCodeFragment.class.getSimpleName();

    private Toolbar mToolbar;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static SourceCodeFragment newInstance() {
        return new SourceCodeFragment();
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
        View view = inflater.inflate(R.layout.fragment_source_code, container, false);
        initViews(view);
        setUpWebClientsAndSwipeLayout();
        handleBackPress();
        return view;
    }

    /**
     * Removes the Open-in-browser menu item when this Fragment's view is destroyed as it is no
     * longer needed and would be irrelevant when other Fragment's take over the content frame.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mToolbar.getMenu().removeItem(R.id.open_in_browser);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_open_in_browser, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sourceCodeIntent = new Intent(Intent.ACTION_VIEW);
        sourceCodeIntent.setData(Uri.parse("https://github.com/nicoqueijo/Android-Currency-Converter"));
        Intent sourceCodeChooser = Intent.createChooser(sourceCodeIntent,
                getString(R.string.launch_browser));
        startActivity(sourceCodeChooser);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the reference to the hosting activity and the toolbar. Notifies this Fragment that it
     * has a menu.
     */
    private void setUpFragment() {
        setHasOptionsMenu(true);
        MainActivity hostingActivity = (MainActivity) getActivity();
        mToolbar = hostingActivity.findViewById(R.id.open_in_browser);
    }

    /**
     * Initializes the views and configures the settings.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViews(View view) {
        mWebView = view.findViewById(R.id.web_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        WebSettings webViewSettings = mWebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setBuiltInZoomControls(true);
        webViewSettings.setDisplayZoomControls(false);
        mWebView.loadUrl("https://github.com/nicoqueijo");
        mWebView.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    /**
     * Sets up the web clients by implementing how the ProgressBar interacts with them.
     */
    private void setUpWebClientsAndSwipeLayout() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> mWebView.reload());
    }

    /**
     * Navigates backwards through the WebView if possible when back button pressed.
     * Credit: https://stackoverflow.com/a/47710213/5906793
     */
    private void handleBackPress() {
        mWebView.setOnKeyListener((v, keyCode, event) -> {
            MainActivity hostActivity = (MainActivity) getActivity();
            if (hostActivity.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                hostActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == MotionEvent.ACTION_UP
                    && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            return false;
        });
    }

    /**
     * Listens for the WebView scroll position and assures the SwipeRefreshLayout is only enabled
     * when the scroll position is at the top. This is to prevent the refresh feature to be
     * triggered when the user swipes down to scroll.
     */
    @Override
    public void onScrollChanged() {
        if (mWebView.getScrollY() == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }
}
