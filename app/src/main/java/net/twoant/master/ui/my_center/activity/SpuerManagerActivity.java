package net.twoant.master.ui.my_center.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;

/**
 * Created by DZY on 2017/1/13.
 * 佛祖保佑   永无BUG
 */

public class SpuerManagerActivity extends LongBaseActivity {

    public TextView tvTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.zy_supermanager_activity;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        tvTitle = (TextView) findViewById(R.id.tv_title_tool_bar);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(ApiConstants.BASE+"wap/adm/idx"+"?_t="+ AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken()
                +"&_cc="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode()
                +"&_ac="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        tvTitle.setText("超级管理员页面");
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
