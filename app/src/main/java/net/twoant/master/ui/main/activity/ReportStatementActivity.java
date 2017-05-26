package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.adapter.ReportStatementBottomSheetAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.control.ReportStatementControl;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by S_Y_H on 2017/1/3.
 * 活动报表
 */
public class ReportStatementActivity extends BaseActivity implements HttpConnectedUtils.IOnStartNetworkCallBack, View.OnClickListener {

    private final static String INTENT_ACTION = "report_action";

    private final static String EXTRA_ACTION_ID = "action_id";
    //活动种类
    private final static String EXTRA_ACTION_KIND = "action_kind";

    //统计所有
    private final static int ID_STATISTICS_ALL = 1;

    private final static int ID_STATISTICS_ITEM = 2;

    private HttpConnectedUtils mHttpConnectedUtils;

    //底部 start
    private AppCompatTextView mTvSumPeople;
    private AppCompatTextView mTvSumPrice;
    private AppCompatTextView mTvUnused;
    private AppCompatTextView mTvUsed;
    //底部 end

    private int mActivityKind;
    private String mActionId;
    private BaseRecyclerNetworkAdapter mStatementAdapter;
    private HintDialogUtil mHintDialogUtil;

    private AppCompatImageView mIvUnused;
    private AppCompatImageView mIvUsed;
    private BottomSheetBehavior<NestedScrollView> mSheetBehavior;

    //底部 sheet start
    private NestedScrollView mNestedScrollView;
    private AppCompatTextView mBottomTitle;
    //底部sheet end

    private boolean isUsed;
    private boolean isUnused;
    private ReportStatementBottomSheetAdapter mBottomSheetAdapter;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("isUsed", isUsed);
        outState.putBoolean("isUnused", isUnused);
        outState.putInt("mActivityKind", mActivityKind);
        outState.putString("mActionId", mActionId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isUsed = savedInstanceState.getBoolean("isUsed");
        isUnused = savedInstanceState.getBoolean("isUnused");
        mActivityKind = savedInstanceState.getInt("mActivityKind");
        mActionId = savedInstanceState.getString("mActionId");
    }

    public static void startActivity(Activity activity, String actionId, int activityKind) {
        Intent intent = new Intent(activity, ReportStatementActivity.class);
        intent.setAction(INTENT_ACTION);
        intent.putExtra(EXTRA_ACTION_ID, actionId);
        intent.putExtra(EXTRA_ACTION_KIND, activityKind);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_report_statement;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, INTENT_ACTION);
        mActionId = intent.getStringExtra(EXTRA_ACTION_ID);
        mActivityKind = intent.getIntExtra(EXTRA_ACTION_KIND, -1);

        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("活动财务报表");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportStatementActivity.this.finish();
            }
        });

        initView();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mHintDialogUtil = new HintDialogUtil(ReportStatementActivity.this).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ReportStatementActivity.this.finish();
                    }
                });
                mHintDialogUtil.showLoading("加载中...", true, false);
                mHttpConnectedUtils = HttpConnectedUtils.getInstance(ReportStatementActivity.this);
                ArrayMap<String, String> arrayMap = new ArrayMap<>();
                arrayMap.put("id", mActionId);
                mHttpConnectedUtils.startNetworkGetString(ReportStatementActivity.ID_STATISTICS_ALL, arrayMap, ApiConstants.ACTIVITY_MERCHANT_REPORT_SUM);
                if (mActivityKind == IRecyclerViewConstant.KIND_SAVED_ACTIVITY || mActivityKind == IRecyclerViewConstant.KIND_METER_ACTIVITY) {
                    return;
                }
                mHttpConnectedUtils.startNetworkGetString(ReportStatementActivity.ID_STATISTICS_ITEM, arrayMap, ApiConstants.ACTIVITY_MERCHANT_REPORT_ITEM_SUM);
            }
        });
    }

    private void initView() {
        RecyclerView rvRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        switch (mActivityKind) {
            case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
            case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
            case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                ViewStub viewStubReportOther = (ViewStub) findViewById(net.twoant.master.R.id.view_stub_report_other);
                View other = viewStubReportOther.inflate();
                this.mTvSumPeople = (AppCompatTextView) other.findViewById(net.twoant.master.R.id.tv_sum);
                other.findViewById(net.twoant.master.R.id.rl_unused).setOnClickListener(this);
                other.findViewById(net.twoant.master.R.id.rl_used).setOnClickListener(this);
                this.mTvUnused = (AppCompatTextView) other.findViewById(net.twoant.master.R.id.tv_unused);
                this.mTvUsed = (AppCompatTextView) other.findViewById(net.twoant.master.R.id.tv_used);
                this.mIvUnused = (AppCompatImageView) other.findViewById(net.twoant.master.R.id.iv_unused);
                this.mIvUsed = (AppCompatImageView) other.findViewById(net.twoant.master.R.id.iv_used);

                ViewStub mBottomSheetViewStub = (ViewStub) findViewById(net.twoant.master.R.id.view_stub_report_bottom);
                View inflate = mBottomSheetViewStub.inflate();
                this.mNestedScrollView = (NestedScrollView) inflate.findViewById(net.twoant.master.R.id.nsv_nested_scroll_view);
                this.mBottomTitle = (AppCompatTextView) inflate.findViewById(net.twoant.master.R.id.tv_title);
                RecyclerView bottomRecyclerView = (RecyclerView) inflate.findViewById(net.twoant.master.R.id.rv_recycler_view);
                bottomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mBottomSheetAdapter = new ReportStatementBottomSheetAdapter();
                bottomRecyclerView.setNestedScrollingEnabled(false);
                bottomRecyclerView.setAdapter(mBottomSheetAdapter);
                inflate.findViewById(net.twoant.master.R.id.btn_close_bottom).setOnClickListener(this);
                mSheetBehavior = BottomSheetBehavior.from(mNestedScrollView);
                mSheetBehavior.setHideable(true);

                if (mNestedScrollView.getVisibility() != View.GONE) {
                    mNestedScrollView.setVisibility(View.GONE);
                }

                if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    mNestedScrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSheetBehavior.setSkipCollapsed(true);
                            mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }, 1000);
                }

                mSheetBehavior.setBottomSheetCallback(mBottomSheetCallback);

                break;
            //记次
            case IRecyclerViewConstant.KIND_METER_ACTIVITY:
            case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                ViewStub viewStubReportSaved = (ViewStub) findViewById(net.twoant.master.R.id.view_stub_report_saved);
                View view = viewStubReportSaved.inflate();
                this.mTvSumPeople = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_sum_people);
                this.mTvSumPrice = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_sum_price);
                this.mTvUnused = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_unused);
                this.mTvUsed = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_used);
                break;
        }

        rvRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mStatementAdapter = new BaseRecyclerNetworkAdapter(this, new ReportStatementControl(mActionId, mActivityKind));
        rvRecyclerView.setAdapter(mStatementAdapter);
        rvRecyclerView.setHasFixedSize(true);
        rvRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_5));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStatementAdapter != null) {
            mStatementAdapter.onDestroy();
        }

        if (mHttpConnectedUtils != null) {
            mHttpConnectedUtils.cancelRequest();
            mHttpConnectedUtils = null;
        }

        if (mHintDialogUtil != null) {
            mHintDialogUtil.dismissDialog();
        }
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        DataRow dataRow = DataRow.parseJson(response);
        if (dataRow != null && dataRow.getBoolean("result", false)) {
            MainActivity.checkState(this, dataRow);
            if (id == ID_STATISTICS_ALL) {
                dataRow = dataRow.getRow("data");
                if (dataRow == null) return;

                if (mActivityKind == IRecyclerViewConstant.KIND_METER_ACTIVITY) {
                    mTvSumPeople.setText(String.format(getString(net.twoant.master.R.string.report_sum), dataRow.getStringDef("QTY_SUM", "0")));
                    String val_sum = dataRow.getStringDef("VAL_SUM", "0");
                    mTvSumPrice.setText(String.format(getString(net.twoant.master.R.string.report_recorded_sum_price), val_sum));
                    mTvUsed.setText(String.format(getString(net.twoant.master.R.string.report_recorded_used_price), StringUtils.subZeroAndDot(new BigDecimal(val_sum).subtract(new BigDecimal(val_sum = dataRow.getStringDef("BALANCE_SUM", "0"))).setScale(2, RoundingMode.HALF_DOWN).toString())));
                    mTvUnused.setText(String.format(getString(net.twoant.master.R.string.report_recorded_unused_price), val_sum));
                    mHintDialogUtil.dismissDialog();
                    return;
                }

                if (mActivityKind == IRecyclerViewConstant.KIND_SAVED_ACTIVITY) {
                    mTvSumPeople.setText(String.format(getString(net.twoant.master.R.string.report_sum), dataRow.getStringDef("QTY_SUM", "0")));
                    String val_sum = dataRow.getStringDef("VAL_SUM", "0");
                    mTvSumPrice.setText(String.format(getString(net.twoant.master.R.string.report_saved_sum_price), val_sum));
                    mTvUsed.setText(String.format(getString(net.twoant.master.R.string.report_saved_used_price), StringUtils.subZeroAndDot(new BigDecimal(val_sum).subtract(new BigDecimal(val_sum = dataRow.getStringDef("BALANCE_SUM", "0"))).setScale(2, RoundingMode.HALF_DOWN).toString())));
                    mTvUnused.setText(String.format(getString(net.twoant.master.R.string.report_saved_unused_price), val_sum));
                    mHintDialogUtil.dismissDialog();
                    return;
                }

                mTvSumPeople.setText(String.format(getString(net.twoant.master.R.string.report_sum), dataRow.getStringDef("QTY_SUM", "0")));
                String val_sum = dataRow.getStringDef("VAL_SUM", "0");
                mTvUsed.setText(String.format(getString(net.twoant.master.R.string.report_used), new BigDecimal(val_sum).subtract(new BigDecimal(val_sum = dataRow.getStringDef("BALANCE_SUM", "0")))));
                mTvUnused.setText(String.format(getString(net.twoant.master.R.string.report_unused), val_sum));

            } else {
                DataSet data = dataRow.getSet("data");
                if (data == null) return;
                int size = data.size();
                if (size > 0) {
                    mBottomSheetAdapter.setDataRows(data);
                }
            }
            mHintDialogUtil.dismissDialog();
        } else {
            mHintDialogUtil.showError("数据异常", true, false);
        }
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                if (isUnused) {
                    isUnused = false;
                    mIvUnused.animate().rotation(0).start();
                    return;
                }

                if (isUsed) {
                    mIvUsed.animate().rotation(0).start();
                    isUsed = false;
                }

                if (mNestedScrollView.getVisibility() != View.GONE) {
                    mNestedScrollView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void onError(Call call, Exception e, int id) {
        mHintDialogUtil.showError(NetworkUtils.getNetworkStateDescription(call, e, "获取失败"), true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case net.twoant.master.R.id.rl_unused:
                if (mIvUnused == null)
                    return;

                if (isUsed) {
                    closeOrOpenUsed();
                    return;
                }
                closeOrOpenUnused();
                break;

            case net.twoant.master.R.id.rl_used:
                if (mIvUsed == null)
                    return;

                if (isUnused) {
                    closeOrOpenUnused();
                    return;
                }
                closeOrOpenUsed();
                break;

            case net.twoant.master.R.id.btn_close_bottom:
                if (isUsed) {
                    closeOrOpenUsed();
                } else if (isUnused) {
                    closeOrOpenUnused();
                } else {
                    if (mSheetBehavior != null) {
                        if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                            mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                }
                break;
        }
    }

    private void openOrClose(boolean isOpen, View view, String mess) {
        if (isOpen) {
            mBottomTitle.setText(mess);
            if (mSheetBehavior != null) {
                if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    if (mSheetBehavior.getSkipCollapsed())
                        mSheetBehavior.setSkipCollapsed(false);
                    mNestedScrollView.setVisibility(View.VISIBLE);
                    mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
            view.animate().rotation(180).start();
        } else {
            if (mSheetBehavior != null) {
                if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
            view.animate().rotation(0).start();
        }
    }

    private void closeOrOpenUsed() {
        if (isUsed) {
            isUsed = false;
            openOrClose(false, mIvUsed, "已使用");
        } else {
            isUsed = true;
            openOrClose(true, mIvUsed, "已使用");
            mBottomSheetAdapter.refreshDataState(ReportStatementBottomSheetAdapter.TYPE_USED);
        }
    }


    private void closeOrOpenUnused() {
        if (isUnused) {
            isUnused = false;
            openOrClose(false, mIvUnused, "未使用");
        } else {
            isUnused = true;
            openOrClose(true, mIvUnused, "未使用");
            mBottomSheetAdapter.refreshDataState(ReportStatementBottomSheetAdapter.TYPE_UNUSED);
        }
    }
}
