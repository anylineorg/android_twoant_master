package net.twoant.master.api;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;

public class MainActivity extends AppCompatActivity {
       WebView webview_ui;
        private ValueCallback<Uri> uploadMessage;
        private ValueCallback<Uri[]> uploadMessageAboveL;
        private final static int FILE_CHOOSER_RESULT_CODE = 10000;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.yh_activity_discover);
            ((AppCompatTextView) findViewById(R.id.tv_title_discover_activity)).setText("店铺管理");
            Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (webview_ui.canGoBack()){
                       webview_ui.goBack();
                   }else{
                       MainActivity.this.finish();
                   }
                }
            });
            webview_ui = (WebView) findViewById(R.id.web_view_container);
            assert webview_ui != null;
            WebSettings settings = webview_ui.getSettings();
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setJavaScriptEnabled(true);
            webview_ui.setWebChromeClient(new WebChromeClient() {

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
            webview_ui.setWebViewClient(new WebViewClient() {
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
            });
            String targetUrl = "http://sk.deepbit.cn/wap/mbr/tt/idx?_t="+ AiSouAppInfoModel.getInstance().getToken();
            webview_ui.loadUrl(targetUrl);
        }

        private void openImageChooserActivity() {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview_ui.canGoBack()) {
            webview_ui.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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
    }
