package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.ShopPaySuccessActivtity;
import net.twoant.master.ui.charge.holder.WaitOnclickCancelListener;
import net.twoant.master.ui.charge.holder.WaitOnclickPayListener;
import net.twoant.master.ui.charge.holder.WaitPayAdapter;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.CancelOrderDialog;
import net.twoant.master.widget.ErrorPayPasswordDialog;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.PayDialog;
import net.twoant.master.widget.SetPasswordDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import net.twoant.master.wxapi.RechargeSuccessActivtity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class WaitPayFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private HintDialogUtil hintDialogUtil;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DataRow> rowsList;
    private CancelOrderDialog cancelOrderDialog;
    private PayDialog payDialog;
    private PassViewDialog passViewDialog;
    private String shopName;
    private String cash_price;
    private DataRow data;
    public int post;
    public DataRow bindData;
    private String caorderId;
    public LinearLayoutManager linearLayoutManager;
    private ErrorPayPasswordDialog errorPayPasswordDialog;

    private long RECYCLE_VIEW_POSITION = 0;
    private long RECYCLE_VIEW_TEMP_POSITION = 0;
    private boolean flag = true;

    @Override
    protected int getLayoutRes() {
        return R.layout.zy_fragment_waitpay;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.lv_myactivity1);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RECYCLE_VIEW_TEMP_POSITION += dy;
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_waitpay);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.aquaEmbellishColor, R.color.pickerview_timebtn_pre);
        swipeRefreshLayout.setOnRefreshListener(this);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        hintDialogUtil = new HintDialogUtil(getActivity());
        rowsList = new ArrayList<>();
        initData();
    }

    private void initData() {
        cancelOrderDialog = new CancelOrderDialog(getActivity(), Gravity.CENTER,false);
        errorPayPasswordDialog = new ErrorPayPasswordDialog(getActivity(),Gravity.CENTER);
        /**
         * 自定义输入密码框输密码的回调
         * */
        passViewDialog = new PassViewDialog(getActivity(), Gravity.BOTTOM, true);
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                hintDialogUtil.showLoading();
                PayHttpUtils.isPassword(AiSouAppInfoModel.getInstance().getUID(), strPassword,new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hintDialogUtil.dismissDialog();
                        ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow dataRow = DataRow.parseJson(response);
                        boolean success = (boolean) dataRow.get("success");
                        if (success) {
                            hintDialogUtil.showLoading(R.string.use_activity);
                            //密码验证成功
                            ShopPaySuccessActivtity.startActivity(getActivity(),data.getString("TOTAL_PRICE"),shopName,"平台支付");
                        }else{
                            hintDialogUtil.dismissDialog();
                            passViewDialog.clearn();
                            passViewDialog.cancel();
                            errorPayPasswordDialog.showDialog(true,true);
                            errorPayPasswordDialog.setReTry(new CancelCenterDialog.IOnClickListener() {
                                @Override
                                public void onClickListener(View v) {
                                    passViewDialog.showDialog(true,true);
                                }
                            });
//                            ToastUtil.showLong("支付密码错误,请重新输入");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        RECYCLE_VIEW_TEMP_POSITION = 0;

        hintDialogUtil.showLoading();
        getSourceNet();
    }

    void getSourceNet(){
        HashMap<String,String> map = new HashMap<>();
        String uid = AiSouAppInfoModel.getInstance().getUID();
        map.put("user",uid );
        LongHttp(ApiConstants.PERSON_WATI_PAY, "",map,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                swipeRefreshLayout.setRefreshing(false);
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                hintDialogUtil.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
                DataRow dataRow = DataRow.parseJson(response);
                DataSet data = dataRow.getSet("data");
                rowsList.clear();
                rowsList = data.getRows();
                WaitPayAdapter waitPayAdapter = new WaitPayAdapter();
                waitPayAdapter.setDataBean(rowsList, getActivity(), new WaitOnclickCancelListener() {
                    @Override
                    public void onClickListener(String orderId) {
                        caorderId = orderId;
                        cancelOrderDialog.showDialog(false,true);
                        cancelOrderDialog.setOnClickListener(new CancelOrderDialog.IOnClickListener() {
                            @Override
                            public void onClickListener(View v) {
                                requestNetCancel(caorderId);
                                flag = true;
                            }
                        });
                    }
                }, new WaitOnclickPayListener() {
                    @Override
                    public void onClickListener(String orderId) {
                        requestNetPay(orderId);
                        flag = true;
                    }
                });
                linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(waitPayAdapter);
                waitPayAdapter.notifyDataSetChanged();
                if (flag) {
                    RECYCLE_VIEW_POSITION = RECYCLE_VIEW_TEMP_POSITION;
                }
                recyclerView.scrollBy(0, (int) RECYCLE_VIEW_POSITION);
                hintDialogUtil.dismissDialog();
            }
        });
    }

    @Override
    public void onRefresh() {
        RECYCLE_VIEW_POSITION = 0;
        getSourceNet();
    }


    private void requestNetPay(String order) {
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("id",order);
        LongHttp(ApiConstants.WAITPAY_PAY, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                data = dataRow.getRow("data");
                cash_price = data.getString("CASH_PRICE");
                shopName = data.getString("SHOP_SELLER_NM");
                String orderId = data.getString("ID");
                if ("0.0".equals(cash_price) || "0".equals(cash_price)) {
                    hintDialogUtil.showLoading();
                    //获取支付密码状态  0：未设置
                    PayHttpUtils.getPayPasswordState(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            hintDialogUtil.dismissDialog();
                            ToastUtil.showLong("获取支付密码状态失败:"+e);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            LogUtils.d(response);
                            hintDialogUtil.dismissDialog();
                            DataRow dataRow = DataRow.parseJson(response);
                            String data = (String) dataRow.get("data");
                            LogUtils.d(data);
                            if (data.equals("0")) {
                                //没有支付密码，去设置
                                SetPasswordDialog setPasswordDialog = new SetPasswordDialog(getActivity(),Gravity.CENTER,false);
                                setPasswordDialog.showDialog(true,true);
                                return;
                            }else{
                                //输入平台的支付密码
                                passViewDialog.clearn();
                                passViewDialog.showDialog(true,true);
                                passViewDialog.setTile(shopName);
                            }
                        }
                    });
                }else {
                    payDialog = new PayDialog(getActivity(),Gravity.BOTTOM,true,orderId,false);
                    payDialog.setNeedPayMoney(cash_price +"");
                    payDialog.showDialog(false,true);
                    RechargeSuccessActivtity.paymoney = data.getString("TOTAL_PRICE");
                }
            }
        });
    }

    private void requestNetCancel(String id) {
        cancelOrderDialog.dismiss();
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("id",id);
        LongHttp(ApiConstants.PERSON_WATI_PAY_DELETE, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                String result = DataRow.parseJson(response).getString("result");
                if (result.contains("true")) {
                    cancelOrderDialog.dismiss();
                    ToastUtil.showLong("已取消");
                    getSourceNet();
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        passViewDialog.dismiss();
        hintDialogUtil.dismissDialog();
        if (null!=payDialog) {
            payDialog.dismiss();
        }
        RECYCLE_VIEW_POSITION = RECYCLE_VIEW_TEMP_POSITION;
        RECYCLE_VIEW_TEMP_POSITION = 0;
        System.out.println(RECYCLE_VIEW_POSITION);
        flag = false;
    }

}



