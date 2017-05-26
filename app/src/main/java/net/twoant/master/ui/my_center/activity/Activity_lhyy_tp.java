package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;

/**
 * Created by Administrator on 2017/1/6 0006.
 */
public class Activity_lhyy_tp extends LongBaseActivity {


   TextView save,title;
    WebView web;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_lhyy_tp;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        web = (WebView) findViewById(net.twoant.master.R.id.web);
        save = (TextView) findViewById(net.twoant.master.R.id.tv_Save);
        title = (TextView) findViewById(net.twoant.master.R.id.tv_Title);
        title.setText("联合运营协议");
        save.setText("我同意");
        save.setTextColor(getResources().getColor(net.twoant.master.R.color.red_f9));
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_lhyy_tp.this, Activity_lhyy.class));
                Activity_lhyy_tp.this.finish();
            }
        });
        web.loadUrl(ApiConstants.TIP + "&user=" + AiSouAppInfoModel.getInstance().getUID()
        + "&_t=" + AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken()
                +"&_cc="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode()
                +"&_ac="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
