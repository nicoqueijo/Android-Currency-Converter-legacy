package com.nicoqueijo.android.currencyconverter.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Fragment used to view the source code of the project via an embedded browser window inside the
 * app. Implements OnScrollChangedListener to monitor the scroll position.
 */
public class SourceCodeFragment extends Fragment implements
        ViewTreeObserver.OnScrollChangedListener {

    public static final String TAG = SourceCodeFragment.class.getSimpleName();

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static SourceCodeFragment newInstance() {
        return new SourceCodeFragment();
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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });
    }

    /**
     * Listens to the back button press and navigates backwards through the WebView if possible
     * when pressed.
     * Credit: https://stackoverflow.com/a/47710213/5906793
     */
    private void handleBackPress() {
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
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
