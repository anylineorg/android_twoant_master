package net.twoant.master.ui.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.NetworkUtils;

/**
 * Created by S_Y_H on 2016/12/8.
 * 发现列表界面
 */
public class DiscoverActivity extends BaseActivity {

    private final static String ACTION_START = "DiscoverActivity_action_start";
    private final static String EXTRA_TITLE = "DiscoverActivity_extra_title";
    private final static String EXTRA_URL = "DiscoverActivity_extra_url";
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private CoordinatorLayout mCoordinatorLayout;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    public static void startActivity(Activity activity, @Nullable String title, @Nullable String url) {
        Intent intent = new Intent(activity, DiscoverActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.yh_activity_discover;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.discover_content_parent);
        String title = intent.getStringExtra(EXTRA_TITLE);
        if (title != null) {
            ((AppCompatTextView) findViewById(R.id.tv_title_discover_activity)).setText(title);
        }
        String url = intent.getStringExtra(EXTRA_URL);
        if (url == null) {
            url = ApiConstants.DISCOVER_URL;
        }
        if (!url.contains("http://")) {
            url = "http://" + url;
        }
        if (!url.contains("_t=")) {
            if (url.contains("?") && url.contains("=")) {
                url += "&_t=" + AiSouAppInfoModel.getInstance().getToken();
            } else {
                url += "?_t=" + AiSouAppInfoModel.getInstance().getToken();
            }
        }

        mWebView = (WebView) findViewById(net.twoant.master.R.id.web_view_container);
        setWeb();
        mWebView.requestFocus();
        mWebView.setWebChromeClient(new WebChromeClient() {

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) { // Handle
                //网络异常后,webview加载一个本地页面,避免网络恢复的瞬间,显示异常页面
                // mWebView.loadUrl("file:///android_asset/start.html");

                // mWebView.setVisibility(View.GONE);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //  errorImg.setVisibility(View.GONE);
                return true;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //    view.loadUrl(request.getUrl().toString());
                return false;

            }
        });
        mWebView.loadUrl(url);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()){
                    mWebView.goBack();
                }else{
                    DiscoverActivity.this.finish();
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(net.twoant.master.R.id.pb_progressBar);
        mProgressBar.setMax(100);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                mProgressBar.setProgress(progress * 100);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }
    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }


    private void setWeb() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.clearCache(true);
        mWebView.clearHistory();
    }

    @Override
    protected void onDestroy() {
        try {
            mWebView.destroy();
        } catch (Exception e) {
            try {
                if (mCoordinatorLayout != null) {
                    mCoordinatorLayout.removeAllViews();
                    mWebView.destroy();
                }
            } catch (Exception e1) {
                //empty
            }
        }
        super.onDestroy();
    }
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private void loadingError(String hint) {
        mCoordinatorLayout.removeViewAt(mCoordinatorLayout.getChildCount() - 1);
        View inflate = getLayoutInflater().inflate(R.layout.yh_error_hint, mCoordinatorLayout);
        ((AppCompatTextView) inflate.findViewById(R.id.tv_hint_info)).setText(hint);
    }


    private class MyWebViewClient extends WebViewClient {

        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                loadingError(NetworkUtils.isNetworkConnected() ? error.getDescription().toString() : "网络链接失败");
            } else {
                super.onReceivedError(view, request, error);
            }

        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            loadingError(NetworkUtils.isNetworkConnected() ? description : "网络链接失败");
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //    view.loadUrl(request.getUrl().toString());
            return false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                String host = request.getUrl().getHost();
//                if (ApiConstants.BASE.equals(host) || ApiConstants.GUO.equals(host)) {
//                    // This is my web site, so do not override; let my WebView load the page
//                    return false;
//                }
//                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//                Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
//                startActivity(intent);
//                return true;
//            }
            //  return false;
            // return super.shouldOverrideUrlLoading(view, request);
        }

//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//            if (Uri.parse(url).getHost().equals(ApiConstants.OWN_URL)) {
//                // This is my web site, so do not override; let my WebView load the page
//                return false;
//            }
//            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
//            return true;
//        }

    }


}