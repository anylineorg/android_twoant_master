package net.twoant.master.ui.main.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/1/3.
 * 活动报表的 底部 RecyclerView 的适配器
 */

public class ReportStatementBottomSheetAdapter extends RecyclerView.Adapter<ReportStatementBottomSheetAdapter.ReportStatementBottomSheetViewHolder> {

    public final static int TYPE_USED = 1;
    public final static int TYPE_UNUSED = 2;

    private List<DataRow> mDataRows;
    private List<DataRow> mUsedData;
    private List<DataRow> mUnusedData;
    private int mIsUsed;

    public void setDataRows(DataSet dataSet) {
        if (dataSet != null && dataSet.size() > 0) {
            mDataRows = dataSet.getRows();
            mUsedData = new ArrayList<>();
            mUnusedData = new ArrayList<>();
            for (DataRow dataRow : mDataRows) {
                //未使用
                if (1 == new BigDecimal(dataRow.getStringDef("BALANCE_SUM", "0")).compareTo(BigDecimal.ZERO)) {
                    mUnusedData.add(dataRow);
                }
                // 总数 - 余额 = 已使用
                if (1 == new BigDecimal(dataRow.getStringDef("VAL_SUM", "0")).compareTo(new BigDecimal(dataRow.getStringDef("BALANCE_SUM", "0")))) {
                    mUsedData.add(dataRow);
                }
            }
            mDataRows = null;
        }
    }

    public void refreshDataState(int isUsed) {
        if (mIsUsed == isUsed) return;

        mIsUsed = isUsed;
        if (isUsed == TYPE_USED)
            this.mDataRows = mUsedData;
        else
            this.mDataRows = mUnusedData;
        this.notifyDataSetChanged();
    }

    @Override
    public ReportStatementBottomSheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReportStatementBottomSheetViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_report_bottom_sheet, parent, false));
    }

    @Override
    public void onBindViewHolder(ReportStatementBottomSheetViewHolder holder, int position) {
        DataRow dataRow = mDataRows.get(position);
        if (dataRow != null) {
            holder.mTvTitle.setText(dataRow.getString("ACTIVITY_ITEM_TITLE"));
            if (mIsUsed == TYPE_USED) {
                holder.mTvNumber.setText(StringUtils.subZeroAndDot(new BigDecimal(dataRow.getStringDef("VAL_SUM", "0")).subtract(new BigDecimal(dataRow.getStringDef("BALANCE_SUM", "0")).setScale(2, RoundingMode.HALF_DOWN)).toString()));
            } else {
                holder.mTvNumber.setText(dataRow.getString("BALANCE_SUM"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataRows == null ? 0 : mDataRows.size();
    }


    static class ReportStatementBottomSheetViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView mTvTitle;
        private AppCompatTextView mTvNumber;

        ReportStatementBottomSheetViewHolder(View itemView) {
            super(itemView);
            this.mTvTitle = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_title);
            this.mTvNumber = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_number);

        }
    }

}
