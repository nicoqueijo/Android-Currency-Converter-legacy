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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Dialog Fragment used to view the source code of the project.
 */
public class SourceCodeFragment extends Fragment {

    public static final String TAG = SourceCodeFragment.class.getSimpleName();

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_source_code, container, false);
        mWebView = view.findViewById(R.id.web_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        WebSettings webViewSettings = mWebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setBuiltInZoomControls(true);
        webViewSettings.setDisplayZoomControls(false);
        mWebView.loadUrl("https://github.com/nicoqueijo");

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

        // https://stackoverflow.com/a/47710213/5906793
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

}
