package net.twoant.master.ui.my_center.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.base_app.BaseActivity;

/**
 * Created by DZY on 2016/12/9.
 * 佛祖保佑   永无BUG
 */
public class ShowActionDetalActivity extends BaseActivity implements View.OnClickListener{
    private String position;
    private TextView tvTitle,tvFirstFee,tvSecondFee,itemTitle,itemPrice,itemPerson,itemIntegral,
            itemIntegral2Money;
    private RelativeLayout rlSecondContain;
    private LinearLayout llSecond,llContain;
    private String info;
    private String[] split;

    private TextView tvItemFirst,tvItemSecond,etItemFirst,etItemSecond,etItemName,etItemSecond2,etmingexinazhi;
    private RelativeLayout rlItemSecondContain;
    private LinearLayout llItemSecond2Contain;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_item_show;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        position = getIntent().getStringExtra("position");
        info = getIntent().getStringExtra("info");
        System.out.println(info);
        tvTitle = (TextView) findViewById(net.twoant.master.R.id.tv_title);
        tvFirstFee = (TextView) findViewById(net.twoant.master.R.id.tv_fee_activityitem);
        tvSecondFee = (TextView) findViewById(net.twoant.master.R.id.tv_secondfee_acitivityitem);
        itemTitle = (TextView) findViewById(net.twoant.master.R.id.et_activityname_activityitem_show);
        itemPrice = (TextView) findViewById(net.twoant.master.R.id.et_free_activityitem);
        itemPerson = (TextView) findViewById(net.twoant.master.R.id.et_people_activityitem);
        itemIntegral2Money = (TextView) findViewById(net.twoant.master.R.id.et_second2_activityitem);
        itemIntegral = (TextView) findViewById(net.twoant.master.R.id.et_second_publishactivity);
        llSecond = (LinearLayout) findViewById(net.twoant.master.R.id.ll_second2_activityitem);
        llContain = (LinearLayout) findViewById(net.twoant.master.R.id.ll_contain_activityitem);
        rlSecondContain = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_secondcontain_activityitem);
        findViewById(net.twoant.master.R.id.btn_finish_publishdetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        initData();
    }



    private void initData() {
        String[] split_item = info.split(",");
        split = split_item[0].split("_");
        itemTitle.setText(" "+split[0]);
        itemPrice.setText(" "+split[1]);
        switch(position){
            case "0"://积分
                initName("积分活动费用","所需积分:",View.GONE,"",View.GONE);
                String s = split[2];
                if ("0".equals(s)) {
                    s = "无限制";
                }
                itemPerson.setText(" "+s);
                break;
            case "1"://收费
                initName("收费活动费用","活动费用:",View.VISIBLE,"积分数额:",View.VISIBLE);
                itemIntegral.setText(" "+split[2]);
                itemIntegral2Money.setText(" "+split[3]);
                String s1 = split[4];
                if ("0".equals(s1)) {
                    s1 = "无限制";
                }
                itemPerson.setText(" "+s1);
                break;
            case "2"://储值
                initName("储值活动费用","到账金额:",View.VISIBLE,"储值金额:",View.GONE);
                itemIntegral.setText(" "+split[2]);
                String s2 = split[3];
                if ("0".equals(s2)) {
                    s2 = "无限制";
                }
                itemPerson.setText(" "+s2);
                break;
            case "3"://计次
                initName("计次活动费用","可用次数:",View.VISIBLE,"充值金额:",View.GONE);
                itemIntegral.setText(" "+split[2]);
                String s4 = split[3];
                if ("0".equals(s4)) {
                    s4 = "无限制";
                }
                itemPerson.setText(" "+s4);
                break;
            case "4"://红包
                initName("红包活动费用","红包金额:",View.VISIBLE,"消费金额:",View.GONE);
                itemIntegral.setText(" "+split[2]);
                String s3 = split[3];
                if ("0".equals(s3)) {
                    s3 = "无限制";
                }
                itemPerson.setText(" "+s3);
                break;
        }

        for (int i =1; i < split_item.length;i++){
            final View inflate = View.inflate(ShowActionDetalActivity.this, net.twoant.master.R.layout.zy_activity_item_item_show, null);
            tvItemFirst = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_item_first);
            tvItemSecond = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_item_second);
            rlItemSecondContain = (RelativeLayout) inflate.findViewById(net.twoant.master.R.id.rl_item_secondcontain);
            llItemSecond2Contain = (LinearLayout) inflate.findViewById(net.twoant.master.R.id.ll_item_secondcontain);

            etItemName = (TextView) inflate.findViewById(net.twoant.master.R.id.et_item_name);
            etItemFirst = (TextView) inflate.findViewById(net.twoant.master.R.id.et_item_first);
            etItemSecond = (TextView) inflate.findViewById(net.twoant.master.R.id.et_item_second);
            etItemSecond2 = (TextView) inflate.findViewById(net.twoant.master.R.id.et_item_second2);
            etmingexinazhi = (TextView) inflate.findViewById(net.twoant.master.R.id.et_item_people);
            String[] split = split_item[i].split("_");
            switch(position){
                case "0" :
                    initItemName("所需积分:",View.GONE,"",View.GONE);
                    etItemName.setText(" "+split[0]);
                    etItemFirst.setText(" "+split[1]);
                    etmingexinazhi.setText(" "+split[2]);
                    break;
                case "1" :
                    initItemName("活动费用:",View.VISIBLE,"积分数额:",View.VISIBLE);
                    etItemName.setText(" "+split[0]);
                    etItemFirst.setText(" "+split[1]);
                    etItemSecond.setText(" "+split[2]);
                    etItemSecond2.setText(" "+split[3]);
                    etmingexinazhi.setText(" "+split[4]);
                    break;
                case "2" :
                    initItemName("到账金额:",View.VISIBLE,"储值金额:",View.GONE);
                    etItemName.setText(" "+split[0]);
                    etItemFirst.setText(" "+split[1]);
                    etItemSecond.setText(" "+split[2]);
                    etmingexinazhi.setText(" "+split[3]);
                    break;
                case "3" :
                    initItemName("可用次数:",View.VISIBLE,"充值金额:",View.GONE);
                    etItemName.setText(" "+split[0]);
                    etItemFirst.setText(" "+split[1]);
                    etItemSecond.setText(" "+split[2]);
                    etmingexinazhi.setText(" "+split[3]);
                    break;
                case "4" :
                    initItemName("红包金额:",View.VISIBLE,"消费金额:",View.GONE);
                    etItemName.setText(" "+split[0]);
                    etItemFirst.setText(" "+split[1]);
                    etItemSecond.setText(" "+split[2]);
                    etmingexinazhi.setText(" "+split[3]);
                    break;
            }
            llContain.addView(inflate);
        }
    }

    private void initName(String tvTitle1,String tvFee1,int visibility,String secondFee, int visibility1){
        tvTitle.setText(tvTitle1);
        tvFirstFee.setText(tvFee1);
        rlSecondContain.setVisibility(visibility);
        tvSecondFee.setText(secondFee);
        llSecond.setVisibility(visibility1);
    }

    private void initItemName(String tvFee1,int visibility,String secondFee,int visibility1){
        tvItemFirst.setText(tvFee1);
        rlItemSecondContain.setVisibility(visibility);
        tvItemSecond.setText(secondFee);
        llItemSecond2Contain.setVisibility(visibility1);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.btn_finish_publishdetail:
                finish();
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }
}
