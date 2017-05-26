package net.twoant.master.ui.charge.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.HomeBaseFragment;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.BankCardActivity;
import net.twoant.master.ui.charge.activity.IntegralActivity;
import net.twoant.master.ui.charge.activity.MyActivityActivity;
import net.twoant.master.ui.charge.activity.Order4Activity;
import net.twoant.master.ui.charge.activity.WalletActivity;
import net.twoant.master.ui.charge.zxing.android.CaptureActivity;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.my_center.activity.Activity_lhyy_tp;
import net.twoant.master.ui.my_center.activity.ManagerShopList;
import net.twoant.master.ui.my_center.activity.NetImageHolderView;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/15.
 * 首页的首付款 fragment 的界面
 */

public class ChargeFragment extends HomeBaseFragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    private RelativeLayout mRlHuodongCharge;
    private RelativeLayout mRlAdposition;

    private TextView yue,jifen,hongbao,bank;

    private ConvenientBanner convenientBanner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Map map = new HashMap();
    public List list;

    @Override
    protected int getLayoutRes() {
        return R
                .layout.zy_fragment_charge;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView(view);
    }

    @Override
    protected void onUserVisible() {
        //开始自动翻页
        convenientBanner.startTurning(5000);
        longHttp(ApiConstants.BASE + "/mbr/fi/usr/ac/jv", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                try{
                    DataRow data1 = DataRow.parseJson(response).getRow("data");
                    yue.setText(data1.getString("PURSE_BALANCE"));
                    jifen.setText(data1.getString("SCORE_BALANCE"));
                    hongbao.setText(data1.getString("VOUCHER_CNT"));
                    bank.setText(data1.getString("BANKCARD_CNT"));
                }catch (Exception e){
//                    ToastUtil.showShort(response);
                }
            }
        });
    }

    @Override
    protected void onUserInvisible() {
        //停止翻页
        convenientBanner.stopTurning();
    }



    private void initView(View view) {
        view.findViewById(R.id.ll_scan_scan_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_activitymanage_charge).setOnClickListener(this);
        mRlAdposition = (RelativeLayout) view.findViewById(R.id.rl_adposition);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh_home_fragment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        view.findViewById(R.id.ll_jifen_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_balance_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_hongbao_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_card_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_sellermanage_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_1_charfragment).setOnClickListener(this);
        view.findViewById(R.id.ll_2_charfragment).setOnClickListener(this);
        view.findViewById(R.id.ll_3_charfragment).setOnClickListener(this);
//        view.findViewById(R.id.ll_4_charfragment).setOnClickListener(this);
        view.findViewById(R.id.ll_collectmoney_chage).setOnClickListener(this);
        view.findViewById(R.id.ll_transationlist_charge).setOnClickListener(this);
        view.findViewById(R.id.ll_unit_charge).setOnClickListener(this);
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.vp_adviewpage);
        yue = (TextView) view.findViewById(R.id.tv_yue_charge);
        jifen = (TextView) view.findViewById(R.id.tv_jifen_charge);
        hongbao = (TextView) view.findViewById(R.id.tv_hongbao_charge);
        bank = (TextView) view.findViewById(R.id.tv_bank_charge);

        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        requestNet();
    }

    @Override
    public void onStart() {
        super.onStart();
        longHttp(ApiConstants.BASE + "/mbr/fi/usr/ac/jv", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try{
                    DataRow data1 = DataRow.parseJson(response).getRow("data");
                    yue.setText(data1.getString("PURSE_BALANCE"));
                    jifen.setText(data1.getString("SCORE_BALANCE"));
                    hongbao.setText(data1.getString("VOUCHER_CNT"));
                    bank.setText(data1.getString("BANKCARD_CNT"));
                }catch (Exception e){
//                    ToastUtil.showShort(response);
                }
            }
        });
    }

    private void requestNet() {
        onStart();
        longHttp(ApiConstants.CHARGE_AD, map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                DataRow dataRow = DataRow.parseJson(response);
                List<DataRow> row = dataRow.getSet("data").getRows();
//                List<DataRow> row = DataRow.parse(response).getSet("data").getRows();
                MainActivity.checkState(getActivity(), dataRow);
                list = new ArrayList<>();
                for (int i=0;i<row.size();i++){
                    DataRow r = row.get(i);
                    list.add(r.getString("AD_PICURL"));
                }
                convenientBanner.setPages(
                        new CBViewHolderCreator<NetImageHolderView>() {

                            @Override
                            public NetImageHolderView createHolder() {
                                return new NetImageHolderView();
                            }
                        }, list)
                        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                        .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                        //设置指示器的方向
                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
                convenientBanner.startTurning(5000);

            }
        });
    }

    private void longHttp(String url, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
    private void longHttpGet(String url, StringCallback callback){
        url = url+"&_t="+ AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken()+"&_cc="
                +AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode()
                +"&_ac="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode();
        OkHttpUtils.get().url(url).build().execute(callback);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ll_1_charfragment:
                Order4Activity.startActivity(getActivity(),0);
                break;
            case R.id.ll_2_charfragment:
                Order4Activity.startActivity(getActivity(),1);
                break;
            case R.id.ll_3_charfragment:
                Order4Activity.startActivity(getActivity(),0);
//                Order4Activity.startActivity(getActivity(),2);
                break;
//            case R.id.ll_4_charfragment:
//                Order4Activity.startActivity(getActivity(),1);
//                Order4Activity.startActivity(getActivity(),3);
//                break;
            case R.id.ll_sellermanage_charge:
                Intent intent = new Intent(getActivity(), ManagerShopList.class);
                ManagerShopList.from_mymessage="1";
                startActivity(intent);
                break;
            case R.id.ll_scan_scan_charge:
                startActivity(new Intent(getActivity(),CaptureActivity.class));
                break;
            case R.id.ll_card_charge://银行卡
                startActivity(new Intent(getActivity(), BankCardActivity.class));
                break;
            case R.id.ll_jifen_charge:
                startActivity(new Intent(getActivity(), IntegralActivity.class));
                break;
            case R.id.ll_hongbao_charge://钱包
                startActivity(new Intent(getActivity(), RedPacketActivity.class));
                break;
            case R.id.ll_activitymanage_charge:
                MyActivityActivity.startActivity(getActivity());
                break;
            case R.id.ll_balance_charge:
                startActivity(new Intent(getActivity(), WalletActivity.class));
                break;
            //为我的管理的商家
            case R.id.ll_collectmoney_chage:
                //    startActivity(new Intent(getActivity(), ManagerShopList.class));

                intent = new Intent(getActivity(), ManagerShopList.class);
                ManagerShopList.from_mymessage="0";
                getActivity().startActivity(intent);
                break;
            case R.id.ll_transationlist_charge:
                startActivity(new Intent(getActivity(), TransationRecordActivity.class));
                break;
            case R.id.ll_unit_charge:
                if(AiSouAppInfoModel.getInstance().getAiSouUserBean().isAuthentication()) {
                    startActivity(new Intent(getActivity(), Activity_lhyy_tp.class));
                }else{
                    ToastUtil.showLong("您还未实名认证");
                }
                break;

        }
    }

    @Override
    public void onRefresh() {
        requestNet();
        swipeRefreshLayout.setRefreshing(false);
    }

}
