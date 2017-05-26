package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.common_utils.ToastUtil;

import java.math.BigDecimal;

/**
 * Created by DZY on 2016/12/9.
 * 佛祖保佑   永无BUG
 */
public class AddRegistrationFee extends BaseActivity implements View.OnClickListener{
    private LinearLayout llContain,llSecond,llItemSecond2Contain;
    private String position;
    private TextView tvTitle,tvFirstFee,tvSecondFee,tvItemFirst,tvItemSecond;
    private EditText etFirstFee,etSecondFee,etPeople,etName,etSecond2,etItemName,etItemFirst,
                etItemSecond,etItemSecond2;
    private RelativeLayout rlSecondContain,rlItemSecondContain;
    private ScrollView scrollView;
    public String str;
    private TextView tv_addactivity_item;
    private StringBuilder stringBuilder;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_item;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        position = getIntent().getStringExtra("position");
        tv_addactivity_item = (TextView) findViewById(net.twoant.master.R.id.tv_addactivity_item);
        tv_addactivity_item.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_finish_publishdetail).setOnClickListener(this);
        llContain = (LinearLayout) findViewById(net.twoant.master.R.id.ll_contain_activityitem);
        tvTitle = (TextView) findViewById(net.twoant.master.R.id.tv_title);
        etName = (EditText) findViewById(net.twoant.master.R.id.et_activityname_activityitem);
        tvFirstFee = (TextView) findViewById(net.twoant.master.R.id.tv_fee_activityitem);
        etFirstFee = (EditText) findViewById(net.twoant.master.R.id.et_free_activityitem);
        etSecondFee = (EditText) findViewById(net.twoant.master.R.id.et_second_publishactivity);
        tvSecondFee = (TextView) findViewById(net.twoant.master.R.id.tv_secondfee_acitivityitem);
        etSecond2 = (EditText) findViewById(net.twoant.master.R.id.et_second2_activityitem);
        etPeople = (EditText) findViewById(net.twoant.master.R.id.et_people_activityitem);
        rlSecondContain = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_secondcontain_activityitem);
        llSecond = (LinearLayout) findViewById(net.twoant.master.R.id.ll_second2_activityitem);
        scrollView = (ScrollView) findViewById(net.twoant.master.R.id.scolloview_activityitem);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        stringBuilder = new StringBuilder();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.tv_addactivity_item://item "+"按钮
                final View inflate = View.inflate(AddRegistrationFee.this, net.twoant.master.R.layout.zy_activity_item_item, null);
                inflate.findViewById(net.twoant.master.R.id.iv_delete_activityitem).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llContain.removeView(inflate);
                    }
                });
                etItemName = (EditText) inflate.findViewById(net.twoant.master.R.id.et_item_name);
                tvItemFirst = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_item_first);
                etItemFirst = (EditText) inflate.findViewById(net.twoant.master.R.id.et_item_first);
                tvItemSecond = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_item_second);
                etItemSecond = (EditText) inflate.findViewById(net.twoant.master.R.id.et_item_second);
                etItemSecond2 = (EditText) inflate.findViewById(net.twoant.master.R.id.et_item_second2);
                rlItemSecondContain = (RelativeLayout) inflate.findViewById(net.twoant.master.R.id.rl_item_secondcontain);
                llItemSecond2Contain = (LinearLayout) inflate.findViewById(net.twoant.master.R.id.ll_item_secondcontain);
                switch(position){
                    case "0" :
                        initItemName("所需积分:","请输入积分",View.GONE,"",View.GONE,"");
                      break;
                    case "1" :
                        initItemName("活动费用:","请输入金额",View.VISIBLE,"积分数额:",View.VISIBLE,"为0可不填");
                      break;
                    case "2" :
                        initItemName("到账金额:","可到账110元",View.VISIBLE,"储值金额:",View.GONE,"储值满100元");
                        break;
                    case "3" :
                        initItemName("可用次数:","可用10次",View.VISIBLE,"充值金额:",View.GONE,"充值满100元");
                        break;
                    case "4" :
                        initItemName("红包金额:","可使用红包5元",View.VISIBLE,"消费金额:",View.GONE,"消费满100元");
                        break;
                }
                llContain.addView(inflate);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                break;
            //完成
            case net.twoant.master.R.id.btn_finish_publishdetail:
                switch(position){
                    case "0"://积分
                        String etName0 = etName.getText().toString().trim();
                        if (TextUtils.isEmpty(etName0)){
                            ToastUtil.showLong("项目名为空");
                            return;
                        }
                        String etFirstFee0 = etFirstFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etFirstFee0)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etPeople0 = etPeople.getText().toString().trim();
                        if (TextUtils.isEmpty(etPeople0)){
//                            ToastUtil.showLong("人数");
//                            return;
                            etPeople0 = "0";
                        }
                        str = etName0 + "_" +etFirstFee0 + "_" + etPeople0;
                        stringBuilder.append(str);
                        break;
                    case "1"://收费
                        String etName1 = etName.getText().toString().trim();
                        if (TextUtils.isEmpty(etName1)){
                            ToastUtil.showLong("项目名为空");
                            return;
                        }
                        String etFirstFee1 = etFirstFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etFirstFee1)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etSecondFee1 = etSecondFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etSecondFee1)){
//                            ToastUtil.showLong("费用空");
//                            return;
                            etSecondFee1 = "0";
                        }
                        String etSecond21 = etSecond2.getText().toString().trim();
                        if (TextUtils.isEmpty(etSecond21)){
//                            ToastUtil.showLong("费用空");
//                            return;
                            etSecond21 = "0";
                        }
                        String etPeople1 = etPeople.getText().toString().trim();
                        if (TextUtils.isEmpty(etPeople1)){
//                            ToastUtil.showLong("人数空");
//                            return;
                            etPeople1 = "0";
                        }
                        str = etName1 + "_" +etFirstFee1 + "_" + etSecondFee1 +"_" + etSecond21+"_" + etPeople1;
                        stringBuilder.append(str);
                        break;
                    case "2"://储值
                        String etName2 = etName.getText().toString().trim();
                        if (TextUtils.isEmpty(etName2)){
                            ToastUtil.showLong("项目名为空");
                            return;
                        }
                        String etFirstFee2 = etFirstFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etFirstFee2)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etSecondFee2 = etSecondFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etSecondFee2)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etPeople2 = etPeople.getText().toString().trim();
                        if (TextUtils.isEmpty(etPeople2)){
//                            ToastUtil.showLong("人数空");
//                            return;
                            etPeople2 = "0";
                        }
                        str = etName2 + "_" +etFirstFee2 + "_" + etSecondFee2 +"_" + etPeople2;
                        stringBuilder.append(str);
                        break;
                    case "3"://计次
                        String etName3 = etName.getText().toString().trim();
                        if (TextUtils.isEmpty(etName3)){
                            ToastUtil.showLong("项目名为空");
                            return;
                        }
                        String etFirstFee3 = etFirstFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etFirstFee3)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etSecondFee3 = etSecondFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etSecondFee3)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etPeople3 = etPeople.getText().toString().trim();
                        if (TextUtils.isEmpty(etPeople3)){
//                            ToastUtil.showLong("人数空");
//                            return;
                            etPeople3 = "0";
                        }
                        str = etName3 + "_" +etFirstFee3 + "_" + etSecondFee3 +"_" + etPeople3;
                        stringBuilder.append(str);
                        break;
                    case "4"://红包
                        String etName4 = etName.getText().toString().trim();
                        if (TextUtils.isEmpty(etName4)){
                            ToastUtil.showLong("项目名为空");
                            return;
                        }
                        String etFirstFee4 = etFirstFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etFirstFee4)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        String etSecondFee4 = etSecondFee.getText().toString().trim();
                        if (TextUtils.isEmpty(etSecondFee4)){
                            ToastUtil.showLong("费用空");
                            return;
                        }
                        //红包满 x - y
                        BigDecimal subtract = new BigDecimal(etSecondFee4).subtract(new BigDecimal(etFirstFee4));
                        if (subtract.intValue()<0) {
                            ToastUtil.showLong("满"+etSecondFee4+"元减"+etFirstFee4+"元不妥吧");
                            return;
                        }
                        String etPeople4 = etPeople.getText().toString().trim();
                        if (TextUtils.isEmpty(etPeople4)){
//                            ToastUtil.showLong("人数空");
//                            return;
                            etPeople4 = "0";
                        }
                        str = etName4 + "_" +etFirstFee4+"_"+etSecondFee4+"_"+etPeople4;
                        stringBuilder.append(str);
                        break;
                }

                int childCount = llContain.getChildCount();
                if (childCount > 4) {
                    for (int i =4 ; i<childCount ; i++){
                        View childAt = llContain.getChildAt(i);
                        System.out.println(childAt);
                        switch(position){
                            case "0":
                                EditText etname0 = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_name);
                                String itemname0 = etname0.getText().toString().trim();
                                if (TextUtils.isEmpty(itemname0)) {
                                    ToastUtil.showLong("子条目有名称为空");
                                    return;
                                }
                                TextView etsuoxujifen = (TextView) childAt.findViewById(net.twoant.master.R.id.et_item_first);
                                String suoxujifen = etsuoxujifen.getText().toString().trim();
                                if (TextUtils.isEmpty(suoxujifen)) {
                                    ToastUtil.showLong("子条目中有所需积分有空");
                                    return;
                                }
                                TextView etmineegexianzhi = (TextView) childAt.findViewById(net.twoant.master.R.id.et_item_people);
                                String mingexianzhi = etmineegexianzhi.getText().toString().trim();
                                if (TextUtils.isEmpty(mingexianzhi)) {
                                    ToastUtil.showLong("子条目中名额限制有空");
                                    return;
                                }
                                stringBuilder.append(","+itemname0+"_"+suoxujifen+"_"+mingexianzhi);
                                break;
                            case "1"://收费活动
                                EditText etName1 = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_name);
                                String itemname1 = etName1.getText().toString().trim();
                                if (TextUtils.isEmpty(itemname1)) {
                                    ToastUtil.showLong("子条目名称有空");
                                    return;
                                }
                                EditText ethuodong = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_first);
                                String huodongfeiyong = ethuodong.getText().toString().trim();
                                if (TextUtils.isEmpty(huodongfeiyong)) {
                                    ToastUtil.showLong("子条目有空");
                                    return;
                                }
                                EditText etjifenshue = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_second);
                                String jifenshue = etjifenshue.getText().toString().trim();
                                if (TextUtils.isEmpty(jifenshue)) {
//                                    ToastUtil.showLong("子条目有空");
//                                    return;
                                    jifenshue = "0";
                                }
                                EditText etkedikou = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_second2);
                                String kedikou = etkedikou.getText().toString().trim();
                                if (TextUtils.isEmpty(kedikou)) {
//                                    ToastUtil.showLong("子条目有空");
//                                    return;
                                    kedikou = "0";
                                }
                                EditText etmingexianzhi1 = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_people);
                                String mingexianzhi1 = etmingexianzhi1.getText().toString().trim();
                                if (TextUtils.isEmpty(mingexianzhi1)) {
//                                    ToastUtil.showLong("子条目中名额限制有空");
//                                    return;
                                    mingexianzhi1 = "0";
                                }
                                stringBuilder.append(","+itemname1+"_"+huodongfeiyong+"_"+jifenshue+"_"+kedikou+"_"+mingexianzhi1);
                                break;
                            case "2":
                                EditText etname2 = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_name);
                                String itemname2 = etname2.getText().toString().trim();
                                if (TextUtils.isEmpty(itemname2)) {
                                    ToastUtil.showLong("子条目名称有空");
                                    return;
                                }
                                EditText etdaozhangjine = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_first);
                                String daozhangjine = etdaozhangjine.getText().toString().trim();
                                if (TextUtils.isEmpty(daozhangjine)) {
                                    ToastUtil.showLong("子条目有空");
                                    return;
                                }
                                EditText etchuzhijine = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_second);
                                String chuzhijine = etchuzhijine.getText().toString().trim();
                                if (TextUtils.isEmpty(chuzhijine)) {
                                    ToastUtil.showLong("子条目有空");
                                    return;
                                }
                                EditText etmingexinzhi = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_people);
                                String mingexianzhi2 = etmingexinzhi.getText().toString().trim();
                                if (TextUtils.isEmpty(mingexianzhi2)) {
//                                    ToastUtil.showLong("子条目中名额限制有空");
//                                    return;
                                    mingexianzhi2 = "0";
                                }
                                stringBuilder.append(","+itemname2+"_"+daozhangjine+"_"+chuzhijine+"_"+mingexianzhi2);
                                break;
                            case "3":
                                EditText etNmae = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_name);
                                String itemname3 = etNmae.getText().toString().trim();
                                if (TextUtils.isEmpty(itemname3)) {
                                    ToastUtil.showLong("子条目名称有空");
                                    return;
                                }
                                EditText etkeyongcishu = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_first);
                                String keyongcishu = etkeyongcishu.getText().toString().trim();
                                if (TextUtils.isEmpty(keyongcishu)) {
                                    ToastUtil.showLong("子条目有空");
                                    return;
                                }
                                EditText etchongzhijine = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_second);
                                String chongzhijine = etchongzhijine.getText().toString().trim();
                                if (TextUtils.isEmpty(chongzhijine)) {
                                    ToastUtil.showLong("子条目有空");
                                    return;
                                }
                                EditText etmingexinazhi = (EditText) childAt.findViewById(net.twoant.master.R.id.et_item_people);
                                String mingexianzhi3 = etmingexinazhi.getText().toString().trim();
                                if (TextUtils.isEmpty(mingexianzhi3)) {
//                                    ToastUtil.showLong("子条目中名额限制有空");
//                                    return;
                                    mingexianzhi3 = "0";
                                }
                                stringBuilder.append(","+itemname3+"_"+keyongcishu+"_"+chongzhijine+"_"+mingexianzhi3);
                                break;
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("str",stringBuilder.toString());
                setResult(1,intent);
                finish();
                break;
        }
    }

    private void initData() {
        switch(position){
            case "0"://积分
                initName("积分活动费用","所需积分:","请输入积分",View.GONE,"",View.GONE,"");
                break;
            case "1"://收费
                initName("收费活动费用","活动费用:","请输入金额",View.VISIBLE,"积分数额:",View.VISIBLE,"为0可不填");
                break;
            case "2"://储值
                initName("储值活动费用","到账金额:","可到账110元",View.VISIBLE,"储值金额:",View.GONE,"储值满100元");
                break;
            case "3"://计次
                initName("计次活动费用","可用次数:","可用10次",View.VISIBLE,"充值金额:",View.GONE,"充值满100元");
                break;
            case "4"://红包
                initName("红包活动费用","红包金额:","可使用红包5元",View.VISIBLE,"消费金额:",View.GONE,"消费满100元");
                tv_addactivity_item.setVisibility(View.GONE);
                break;
        }
    }

    private void initName(String tvTitle1,String tvFee1,String etFirstFee1,int visibility,String secondFee,
                          int visibility1,String etSecondFee1){
        tvTitle.setText(tvTitle1);
        tvFirstFee.setText(tvFee1);
        etFirstFee.setHint(etFirstFee1);
        rlSecondContain.setVisibility(visibility);
        tvSecondFee.setText(secondFee);
        llSecond.setVisibility(visibility1);
        etSecondFee.setHint(etSecondFee1);
    }

    private void initItemName(String tvFee1,String etFirstFee1,int visibility,String secondFee,
                              int visibility1,String etItemSecond1){
        tvItemFirst.setText(tvFee1);
        etItemFirst.setHint(etFirstFee1);
        rlItemSecondContain.setVisibility(visibility);
        tvItemSecond.setText(secondFee);
        llItemSecond2Contain.setVisibility(visibility1);
        etItemSecond.setHint(etItemSecond1);
    }

}
