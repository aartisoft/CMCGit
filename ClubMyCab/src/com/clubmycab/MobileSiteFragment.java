package com.clubmycab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MobileSiteFragment extends Fragment {
	
	private static final String TAG = "com.clubmycab.MobileSiteFragment";
	
	private String mURL;
	private String mUberRequestID, mUberUsername, mUberPassword;
    
    private WebView mWebView;
    private ProgressBar mProgressBar;
     
    public static final String ARGUMENTS_MOBILE_SITE_URL = "com.clubmycab.MobileSiteURL";
    public static final String ARGUMENTS_UBER_REQUEST_ID = "com.clubmycab.MobileSiteUberReqID";
    public static final String ARGUMENTS_UBER_USERNAME = "com.clubmycab.MobileSiteUberUsername";
    public static final String ARGUMENTS_UBER_PASSWORD = "com.clubmycab.MobileSiteUberPassword";
     
    public static MobileSiteFragment newInstance(String url, String uberReqID, String uberUsername, String uberPassword) {
         
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENTS_MOBILE_SITE_URL, url);
        args.putString(ARGUMENTS_UBER_REQUEST_ID, uberReqID);
        args.putString(ARGUMENTS_UBER_USERNAME, uberUsername);
        args.putString(ARGUMENTS_UBER_PASSWORD, uberPassword);
         
        MobileSiteFragment mobileSiteFragment = new MobileSiteFragment();
        mobileSiteFragment.setArguments(args);
         
        return mobileSiteFragment;
         
    }
     
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//         
//        inflater.inflate(R.menu.menu_refresh, menu);
//         
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            ImageView imageView = (ImageView)getActivity().findViewById(android.R.id.home);
//            imageView.setBackgroundColor(getResources().getColor(android.R.color.white));
//            imageView.setPadding(4, 4, 4, 4);
//        } else {
//            // TODO
//        }
//         
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//     
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//         
//        if (item.getItemId() == R.id.menu_item_refresh) {
//            mWebView.loadUrl(mURL);
//        } else if (item.getItemId() == android.R.id.home) {
//            getActivity().finish();
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//         
//        return true;
//    }
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        setRetainInstance(true);
//        setHasOptionsMenu(true);
         
        mURL = getArguments().getString(ARGUMENTS_MOBILE_SITE_URL);
//        mURL = "http://www.google.com";
        mUberRequestID = getArguments().getString(ARGUMENTS_UBER_REQUEST_ID, "");
        mUberUsername = getArguments().getString(ARGUMENTS_UBER_USERNAME, "");
        mUberPassword = getArguments().getString(ARGUMENTS_UBER_PASSWORD, "");
        
        getActivity().setResult(Activity.RESULT_CANCELED);   //default value to return
    }
     
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         
        View view = (View)inflater.inflate(R.layout.fragment_mobile_site, container, false);
         
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBarMobileSite);
         
        mWebView = (WebView)view.findViewById(R.id.webViewMobileSite);
         
        mWebView.getSettings().setJavaScriptEnabled(true);
         
//        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebViewClient(new WebViewClient() {
        	
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	Log.d(TAG, "shouldOverrideUrlLoading url : " + url);
            	if (url.contains("uberapi.php?code=") && !mUberRequestID.isEmpty()) {
					url += ("&requestid=" + mUberRequestID);
					mWebView.loadUrl(url.replace("https", "http"));    //needs to be removed for production
					Log.d(TAG, "shouldOverrideUrlLoading mUberRequestID url : " + url);
					return true;
				} else if (url.contains("processing.php") && !mUberRequestID.isEmpty()) {
					Intent intent = new Intent();
					intent.putExtra(ARGUMENTS_UBER_REQUEST_ID, mUberRequestID);
					getActivity().setResult(Activity.RESULT_OK, intent);
					getActivity().finish();
				}
                return false;
            }
            
//            @Override
//            public void onPageFinished(WebView view, String url) {
//            	// TODO Auto-generated method stub
//            	super.onPageFinished(view, url);
//            	
//            	Log.d(TAG, "onPageFinished url : " + url);
//            	
//            	if (url.equals("https://login.uber.com/login")) {
////            		String email="";
////                    String password="";
//            		view.loadUrl("javascript: {" +
//            	            "document.getElementById('email').value = '"+mUberUsername+"';document.getElementById('password').value='"+mUberPassword+"';};");
//            		
////            		view.loadUrl("javascript: {"  +
////            	            "var frms = document.getElementsByClassName('btn btn--huge btn--full');" +
////            	            "frms[0].click();};");
//				}
//            }
        });
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
         
//        mWebView.setOnTouchListener(new View.OnTouchListener() {
//             
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                WebView.HitTestResult hitTestResult = ((WebView)v).getHitTestResult();
////              Log.d("PayinWebAPIFragment", "hitTestResult : " + hitTestResult);
//                if (hitTestResult != null) {
////                  Log.d("PayinWebAPIFragment", "getExtra : " + hitTestResult.getExtra() + " getType : " + hitTestResult.getType());
//                    if ((hitTestResult.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) && (hitTestResult.getExtra().contains("FundTransfer/updatelimit.jsp#"))) {
////                      mWebView.loadUrl(hitTestResult.getExtra());
//                        if (!isAdded()) {
//                            return false;
//                        }
//                        getActivity().finish();
//                    }
//                }
//                 
//                return false;
//            }
//        });
         
        return view;
    }

}
