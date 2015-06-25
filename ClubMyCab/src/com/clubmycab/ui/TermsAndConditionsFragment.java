package com.clubmycab.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.clubmycab.R;
import com.clubmycab.utility.Log;

public class TermsAndConditionsFragment extends Fragment {
	
	private static final String TAG = "com.clubmycab.TNCFragment";
	
	public static final String ARGUMENTS_TNC_SITE_URL = "com.clubmycab.TNCSiteURL";
	
	private String mURL;
	
	private WebView mWebView;
    private ProgressBar mProgressBar;
	
	public static TermsAndConditionsFragment newInstance(String url) {
		
		Bundle args = new Bundle();
        args.putSerializable(ARGUMENTS_TNC_SITE_URL, url);
        
		TermsAndConditionsFragment tncFragment = new TermsAndConditionsFragment();
		tncFragment.setArguments(args);
		
		return tncFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mURL = getArguments().getString(ARGUMENTS_TNC_SITE_URL);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = (View)inflater.inflate(R.layout.fragment_tnc, container, false);
		
		mProgressBar = (ProgressBar)view.findViewById(R.id.progressBarTNC);
		
		mWebView = (WebView)view.findViewById(R.id.webViewTNC);
        
//        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
         
        mWebView.setWebViewClient(new WebViewClient());
		
        Log.d(TAG, "onCreateView : " + mURL);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
 
                }
            }
        });
        mWebView.loadUrl(mURL);
        
		return view;
	}

}
