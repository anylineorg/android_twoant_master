package net.twoant.master.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.common_utils.HttpConnectedUtils;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * Created by S_Y_H on 2016/12/8.
 * 显示推送的信息
 */
public class PushInfoDetailActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_merchant_entered_agreement;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("消息");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1 < AppManager.getBackSize()) {
                    PushInfoDetailActivity.this.finish();
                } else {
                    MainActivity.startActivityNewTask(PushInfoDetailActivity.this);
                    PushInfoDetailActivity.this.finish();
                }

            }
        });
        Intent intent = getIntent();
        final ArrayMap<String, String> arrayMap = new ArrayMap<>(3);
        if (intent != null) {
            ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title)).setText(intent.getStringExtra(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_content)).setText(intent.getStringExtra(JPushInterface.EXTRA_ALERT));
            String stringExtra = intent.getStringExtra(JPushInterface.EXTRA_MSG_ID);
            arrayMap.put("id", stringExtra == null ? "" : stringExtra);
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                HttpConnectedUtils.getInstance(new HttpConnectedUtils.IOnStartNetworkSimpleCallBack() {

                    @Override
                    public void onResponse(String response, int id) {
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                }).startNetworkGetString(0, arrayMap, ApiConstants.MESSAGE_READ);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (1 < AppManager.getBackSize()) {
            PushInfoDetailActivity.this.finish();
        }
    }
}
