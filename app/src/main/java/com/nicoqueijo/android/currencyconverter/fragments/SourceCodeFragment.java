package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Dialog Fragment used to view the source code of the project.
 */
public class SourceCodeFragment extends Fragment {

    public static final String TAG = SourceCodeFragment.class.getSimpleName();

    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_source_code, container, false);
        mWebView = view.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("https://github.com/nicoqueijo");
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
