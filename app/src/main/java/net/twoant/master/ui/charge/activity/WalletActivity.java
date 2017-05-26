package net.twoant.master.ui.charge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.holder.WalletHolder;
import net.twoant.master.ui.main.activity.RechargeActivity;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.timer.TimePickerView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/10.
 * 佛祖保佑   永无BUG
 */

public class WalletActivity extends LongBaseActivity implements View.OnClickListener {
    TimePickerView pvTime;
    private TextView funds, qb_mouth;
    private TextView funds_, qb_mouth_;//本页自带的控件
    private ImageView mouth;
    private ListView listView;
    private List<DataRow> mDataRow;
    private View mHeadView;
    private View mFloatBarInLvHeader;
    private View mFloatBar;
    private AppCompatImageButton img;
    private boolean flag_1 = false;
    private boolean flag_2 = true;
    private boolean btnFlag = false;
    private TranslateAnimation translateAnimationHide;
    private TranslateAnimation translateAnimation;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_wallet;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        listView = (ListView) findViewById(net.twoant.master.R.id.qb_listview);
        qb_mouth_ = (TextView) findViewById(net.twoant.master.R.id.qb_mouth);
        findViewById(net.twoant.master.R.id.qb_claner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
        // ListView 第一个头部控件（效果图中的红色区域）
        mHeadView = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.item_headview_wallet, listView, false);
        funds = (TextView) mHeadView.findViewById(net.twoant.master.R.id.funds);
        mHeadView.findViewById(net.twoant.master.R.id.qb_czhi).setOnClickListener(this);
        mHeadView.findViewById(net.twoant.master.R.id.qb_tx).setOnClickListener(this);
        mHeadView.findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);

        listView.addHeaderView(mHeadView);
        // ListView 顶部隐藏的浮动栏
        mFloatBar = findViewById(net.twoant.master.R.id.float_bar);
        // ListView 第二个头部控件（浮动栏）
        mFloatBarInLvHeader = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.wallet_floatbar, listView, false);
        qb_mouth = (TextView) mFloatBarInLvHeader.findViewById(net.twoant.master.R.id.qb_mouth);
        mouth = (ImageView) mFloatBarInLvHeader.findViewById(net.twoant.master.R.id.qb_claner);
        listView.addHeaderView(mFloatBarInLvHeader);
        img = (AppCompatImageButton) findViewById(net.twoant.master.R.id.fab_back_top);
        translateAnimationHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 2f);
        translateAnimationHide.setDuration(500);
        translateAnimationHide.setFillAfter(true);
        translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 2f,
                Animation.RELATIVE_TO_SELF, 0f);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        img.startAnimation(translateAnimation);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(0);
                btnFlag = true;
                img.startAnimation(translateAnimationHide);
                flag_1 = false;
                flag_2 = true;
            }
        });
        // 监听 ListView 滑动事件
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int oldVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                 /* 判断ListView头部中的浮动栏(mFloatBarInLvHeader)当前是否可见
                  * 来决定隐藏或显示浮动栏(mFloatBar)*/
                if (firstVisibleItem >= 1) {
                    mFloatBar.setVisibility(View.VISIBLE);
                } else {
                    mFloatBar.setVisibility(View.GONE);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                }
                if (firstVisibleItem > oldVisibleItem) {
                    if (flag_1) {
                        img.setVisibility(View.VISIBLE);
                        img.startAnimation(translateAnimationHide);
                        img.setEnabled(false);
                        flag_1 = false;
                        flag_2 = true;
                    }
                }
                if (firstVisibleItem < oldVisibleItem) {
                    if (!btnFlag) {
                        if (flag_2) {
                            img.setVisibility(View.VISIBLE);
                            img.startAnimation(translateAnimation);
                            img.setEnabled(true);
                            flag_1 = true;
                            flag_2 = false;
                            btnFlag = false;
                        }
                    } else {
                        btnFlag = false;
                    }
                }
                oldVisibleItem = firstVisibleItem;
            }
        });
        //pvTime
        mouth.setOnClickListener(this);

        // 时间选择器qb_mouth
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M");
        qb_mouth.setText(sdf.format(new Date()));
        qb_mouth_.setText(sdf.format(new Date()));
        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                String t = getTime(date);
                int tp = Integer.parseInt(t.split("-")[1]);
                tp++;
                String time = t.split("-")[0] + "-" + tp;
                int tempYear = Integer.parseInt(t.split("-")[0]);
                if (tp == 13) {
                    tp = 1;
                    time = tempYear + 1 + "-" + tp;
                }
                qb_mouth.setText(time);
                qb_mouth_.setText(time);
                initData();
            }
        });
        mHeadView.findViewById(net.twoant.master.R.id.qb_tx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalletActivity.this, BankCardActivity.class);
                intent.putExtra("accont_price", funds.getText() + "");
                intent.putExtra("bank_card", funds.getText() + "");
                startActivity(intent);
            }
        });
    }

    class MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new WalletHolder();
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    HashMap<String, String> map = new HashMap<>();

    @Override
    protected void initData() {
        requestNetForRecord();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNetForRecord();
    }

    public void requestNetForBalance() {
        LongHttp(ApiConstants.USERPRICEBAG, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                String string = DataRow.parseJson(response).getRow("data").getString("PURSE_BALANCE");
                if (!TextUtils.isEmpty(string)) {
                    funds.setText(string);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.qb_czhi://充值钱包
                RechargeActivity.startActivity(WalletActivity.this, RechargeActivity.TYPE_WALLET);
//                startActivity(new Intent(WalletActivity.this,GetCashActivity.class));
                break;
            case net.twoant.master.R.id.qb_tx://提现钱包
//                startActivity(new Intent(WalletActivity.this,WithdrawalsActivity.class));
                break;
            case net.twoant.master.R.id.qb_claner:
                pvTime.show();
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }

    private void requestNetForRecord() {
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        requestNetForBalance();
        String s = qb_mouth.getText() + "";
        String[] split = s.split("-");
        String s1 = split[0];
        String s2 = split[1];
        if (Integer.parseInt(s2) <= 9) {
            s = s1 + "-0" + s2;
        }
        map.put("ym", s);
        LongHttp(ApiConstants.USERPRICEBAGW, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
//                response = "{\"result\":true,\"code\":\"200\",\"data\":[{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.02\",\"LOG_DATE\":\"2017-01-25\",\"SORT_ID\":2211,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-25 10:31:52\",\"FK_ID\":296,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":-1,\"SORT_TITLE\":\"积分充值\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":19,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户购买积分钱包出帐\"},{\"VAL\":\"0.01\",\"LOG_DATE\":\"2017-01-19\",\"SORT_ID\":1210,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-19 03:03:15\",\"FK_ID\":55,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":1,\"SORT_TITLE\":\"用户钱包充值入帐钱包\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":21,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户钱包充值入帐钱包\"},{\"VAL\":\"0.01\",\"LOG_DATE\":\"2017-01-19\",\"SORT_ID\":1210,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-01-19 12:01:44\",\"FK_ID\":53,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_AVATAR\":\"/avatar/1485448859204555fd6c8.jpg\",\"FLAG\":1,\"SORT_TITLE\":\"用户钱包充值入帐钱包\",\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":20,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户钱包充值入帐钱包\"}],\"success\":true,\"type\":\"list\",\"message\":null}";
                mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                listView.setAdapter(new MyAdapter(mDataRow));
            }
        });
    }
}
