package net.twoant.master.ui.my_center.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.main.activity.RechargeActivity;
import net.twoant.master.widget.entry.DataRow;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6 0006.
 */
public class Activity_lhyy extends LongBaseActivity {
    private ConvenientBanner convenientBanner;
    private WebView lhyy_report;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_lhyy;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        convenientBanner = (ConvenientBanner) findViewById(net.twoant.master.R.id.vp_adviewpage);

        lhyy_report = (WebView) findViewById(net.twoant.master.R.id.lhyy_report);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity_lhyy.this.finish();
            }
        });
        TextView title = (TextView) findViewById(net.twoant.master.R.id.tv_Title);
        title.setText("联合运营");
        TextView save = (TextView) findViewById(net.twoant.master.R.id.tv_Save);
        save.setVisibility(View.VISIBLE);
        save.setTextColor(getResources().getColor(net.twoant.master.R.color.red_f9));
        save.setText("充值");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RechargeActivity.startActivity(Activity_lhyy.this, RechargeActivity.TYPE_COMBINED);
//                startActivity(new Intent(Activity_lhyy.this, Activity_lhyy_pay.class));
            }
        });
        //设置编码
        lhyy_report.getSettings().setDefaultTextEncodingName("utf-8");
        //支持js
        lhyy_report.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    List<String> list;

    @Override
    protected void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sort", "9");
        LongHttp(ApiConstants.LHYY, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                List<DataRow> row = null;
                try {
                    row = DataRow.parseJson(response).getSet("data").getRows();
                } catch (Exception e) {
                    Log.e("==", e.getMessage());
                }
                list = new ArrayList<String>();
                for (int i = 0; i < row.size(); i++) {
                    DataRow r = row.get(i);
                    list.add(r.getString("IMG_URL"));
                }
                convenientBanner.setPages(
                        new CBViewHolderCreator<NetImageHolderView>() {

                            @Override
                            public NetImageHolderView createHolder() {
                                return new NetImageHolderView();
                            }
                        }, list)
                        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                        .setPageIndicator(new int[]{net.twoant.master.R.drawable.ic_page_indicator, net.twoant.master.R.drawable.ic_page_indicator_focused})
                        //设置指示器的方向
                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
                convenientBanner.startTurning(5000);
            }
        });


    }
}
