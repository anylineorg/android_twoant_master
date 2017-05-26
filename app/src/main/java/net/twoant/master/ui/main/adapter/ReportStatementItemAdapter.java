package net.twoant.master.ui.main.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by S_Y_H on 2017/1/2.
 * 活动报表 item 适配器
 */

public class ReportStatementItemAdapter extends RecyclerView.Adapter<ReportStatementItemAdapter.ReportStatementItemViewHolder> {

    private final static int TYPE_COMMON = 1;
    private final static int TYPE_SAVE_RECORDED = 2;
    private String mUseInfo;
    private String mUnusedInfo;
    private String mSum;

    /**
     * 子项目适配器ViewHolder
     */
    static class ReportStatementItemViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView mTvTitle;

        private AppCompatTextView mTvState;

        private AppCompatTextView mTvUsed;
        private AppCompatTextView mTvUnused;

        private ReportStatementItemViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_COMMON) {
                this.mTvState = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_state);
                this.mTvTitle = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_title);
            } else {
                this.mTvTitle = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_title);
                this.mTvUsed = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_used);
                this.mTvUnused = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_unused);
            }
        }
    }

    private List<DataRow> mDataRows;
    private int mActivityKind;
    private Context mContext;
    //橘色
    private int mCurrentTextColor;
    //黑色
    private int mPrimaryTextColor;

    public ReportStatementItemAdapter(DataSet rows, int kind) {
        if (rows != null)
            this.mDataRows = rows.getRows();
        this.mActivityKind = kind;
        mContext = AiSouAppInfoModel.getAppContext();
        mCurrentTextColor = ContextCompat.getColor(mContext, net.twoant.master.R.color.currentSelectColor);
        mPrimaryTextColor = ContextCompat.getColor(mContext, net.twoant.master.R.color.principalTitleTextColor);
    }


   public void refreshData(DataSet items, int activityKind) {
        if (items != null)
            mDataRows = items.getRows();
        this.mActivityKind = activityKind;
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDataRows == null ? 0 : mDataRows.size();
    }

    @Override
    public ReportStatementItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMON) {
            return new ReportStatementItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_report_other, parent, false), viewType);
        }
        return new ReportStatementItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_report_saved_recorded, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ReportStatementItemViewHolder holder, int position) {
        DataRow row = mDataRows.get(position);
        if (row == null) return;

        if (holder.getItemViewType() == TYPE_COMMON) {
            holder.mTvTitle.setText(row.getString("ACTIVITY_ITEM_TITLE"));
            AppCompatTextView mTvState = holder.mTvState;
            if (row.getBoolean("USE_STATUS", false)) {
                mTvState.setText("已使用");
                mTvState.setTextColor(mPrimaryTextColor);
            } else {
                mTvState.setText("未使用");
                mTvState.setTextColor(mCurrentTextColor);
            }
        } else {
            String val = row.getString("VAL");
            if (mActivityKind == IRecyclerViewConstant.KIND_METER_ACTIVITY) {
                if (mSum == null) {
                    mSum = mContext.getString(net.twoant.master.R.string.report_item_recorded_sum_price);
                }
                holder.mTvTitle.setText(String.format(mSum, val));

                if (mUseInfo == null) {
                    mUseInfo = mContext.getString(net.twoant.master.R.string.report_item_recorded_used_price);
                }
                holder.mTvUsed.setText(String.format(mUseInfo, StringUtils.subZeroAndDot(new BigDecimal(val).subtract(new BigDecimal(val = row.getString("BALANCE"))).toString())));

                if (mUnusedInfo == null) {
                    mUnusedInfo = mContext.getString(net.twoant.master.R.string.report_item_recorded_unused_price);
                }
                holder.mTvUnused.setText(String.format(mUnusedInfo, val));
            } else {
                if (mSum == null) {
                    mSum = mContext.getString(net.twoant.master.R.string.report_item_saved_sum_price);
                }
                holder.mTvTitle.setText(String.format(mSum, val));

                if (mUseInfo == null) {
                    mUseInfo = mContext.getString(net.twoant.master.R.string.report_item_saved_used_price);
                }
                holder.mTvUsed.setText(String.format(mUseInfo, StringUtils.subZeroAndDot(new BigDecimal(val).subtract(new BigDecimal(val = row.getString("BALANCE"))).toString())));

                if (mUnusedInfo == null) {
                    mUnusedInfo = mContext.getString(net.twoant.master.R.string.report_item_saved_unused_price);
                }
                holder.mTvUnused.setText(String.format(mUnusedInfo, val));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (IRecyclerViewConstant.KIND_METER_ACTIVITY == mActivityKind ||
                IRecyclerViewConstant.KIND_SAVED_ACTIVITY == mActivityKind) {
            return TYPE_SAVE_RECORDED;
        }
        return TYPE_COMMON;
    }
}

