package net.twoant.master.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.bean.ActionDetailBean;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by S_Y_H on 2016/12/23.
 * 活动详情的费用项目适配器
 */

public class ActivityDetailItemAdapter extends RecyclerView.Adapter<ActivityDetailItemAdapter.ActivityDetailViewHolder> implements View.OnClickListener, AppCompatCheckBox.OnCheckedChangeListener {

    private List<ActionDetailBean.DataBean.ITEMSBean> mDataSet;
    private SparseIntArray fCheckBoxSelectCollection;
    private final SparseArray<ActivityDetailViewHolder> fViewHolders = new SparseArray<>(20);
    private final int mPrimaryColor;
    private final int mGreyColor;
    private final int mEnableColor;
    private final int mBlackColor;
    private String mNumHint;
    /**
     * 活动类型
     */
    private int mActivityKind;

    public void setDataAndKind(@NonNull List<ActionDetailBean.DataBean.ITEMSBean> dataSet, int kind) {
        this.mDataSet = dataSet;
        mActivityKind = kind;
        fCheckBoxSelectCollection = new SparseIntArray(dataSet.size());
        this.notifyDataSetChanged();
    }

    public ActivityDetailItemAdapter() {
        Context context = AiSouAppInfoModel.getAppContext();
        mPrimaryColor = ContextCompat.getColor(context, R.color.currentSelectColor);
        mGreyColor = ContextCompat.getColor(context, R.color.subordinationTitleTextColor);
        mNumHint = context.getString(R.string.action_limit);
        mEnableColor = ContextCompat.getColor(context, R.color.subordinationContentTextColor);
        mBlackColor = ContextCompat.getColor(context, R.color.principalTitleTextColor);
    }

    /**
     * @return 当前活动类型
     */
    public int getActivityKind() {
        return this.mActivityKind;
    }

    @Override
    public ActivityDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.yh_item_dialog_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ActivityDetailViewHolder holder, int position) {
        ActionDetailBean.DataBean.ITEMSBean row = mDataSet.get(position);

        if (row == null) return;

        AppCompatTextView tvHint = holder.mTvHint;
        int mTvHintVisibility = tvHint.getVisibility();
        AppCompatTextView tvPrice = holder.mTvPrice;
        switch (mActivityKind) {

            case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
                if (mTvHintVisibility != View.GONE)
                    tvHint.setVisibility(View.GONE);
                tvPrice.setText(String.valueOf(row.getSCORE() + " 积分"));
                break;

            case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
                BigDecimal bigDecimal = new BigDecimal(row.getSCORE());
                if (!bigDecimal.equals(BigDecimal.ZERO)) {
                    if (mTvHintVisibility != View.VISIBLE)
                        tvHint.setVisibility(View.VISIBLE);
                    String val = row.getVAL();
                    String price = row.getPRICE();
                    if (val != null && price != null) {
                        BigDecimal subtract = new BigDecimal(val).subtract(new BigDecimal(price));
                        tvHint.setText(String.valueOf(StringUtils.subZeroAndDot(bigDecimal.toString()) + "积分可抵扣￥" + subtract.toString()));
                    }
                } else {
                    if (mTvHintVisibility != View.GONE)
                        tvHint.setVisibility(View.GONE);
                }
                tvPrice.setText(String.valueOf("￥" + row.getPRICE()));
                break;

            case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                if (mTvHintVisibility != View.VISIBLE)
                    tvHint.setVisibility(View.VISIBLE);

                String price = row.getPRICE();
                price = price == null ? "" : price;
                SpannableString savePrice = new SpannableString("储值￥" + price);
                savePrice.setSpan(new ForegroundColorSpan(mPrimaryColor), 2, savePrice.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                tvHint.setText(savePrice);

                String priceAll = row.getVAL();
                priceAll = priceAll == null ? "" : priceAll;
                SpannableString spannableString = new SpannableString("到账￥" + priceAll);
                spannableString.setSpan(new ForegroundColorSpan(mGreyColor), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvPrice.setText(spannableString);
                break;

            case IRecyclerViewConstant.KIND_METER_ACTIVITY:
                if (mTvHintVisibility != View.VISIBLE)
                    tvHint.setVisibility(View.VISIBLE);

                tvHint.setText(String.valueOf(row.getVAL() + "次"));
                tvPrice.setText(String.valueOf("￥" + row.getPRICE()));
                break;

            case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                break;
        }

        AppCompatTextView tvTitle = holder.mTvTitle;
        tvTitle.setText(row.getTITLE());

        AppCompatCheckBox cbSelect = holder.mCbSelect;

        cbSelect.setTag(position);
        cbSelect.setOnCheckedChangeListener(this);

        if (-1 != fCheckBoxSelectCollection.get(position, -1))
            cbSelect.setChecked(true);
        else
            cbSelect.setChecked(false);
            /*
             * 买的价格  （积分活动为积分加买的价格）（储值：充200（price）送100（val - price））
             * （记次 200元（price） 10次（val）
             */
        //row.getString("PRICE");//
        //row.getString("SCORE");//积分
        //row.getString("VAL");//原价

        String limit = row.getJOIN_QTY_LIMIT();
        BigDecimal sum = new BigDecimal(limit == null ? "0" : limit);
        if (0 == sum.compareTo(BigDecimal.ZERO)) {
            holder.mTvSupplement.setText("无人数限制");
            holder.mTvSupplement.setTextColor(mGreyColor);
            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(position);
            tvTitle.setTextColor(mBlackColor);
            cbSelect.setEnabled(true);
            return;
        }
        String qty = row.getJOIN_QTY();
        if (!(1 == sum.subtract(new BigDecimal(qty == null ? "0" : qty)).compareTo(BigDecimal.ZERO))) {
            holder.mTvSupplement.setText(String.format(mNumHint, qty, limit));
            tvHint.setTextColor(mEnableColor);
            tvPrice.setTextColor(mEnableColor);
            tvTitle.setTextColor(mEnableColor);
            holder.mTvSupplement.setTextColor(mEnableColor);
            holder.mTvSupplement.setText(String.format(mNumHint, qty, limit));
            cbSelect.setEnabled(false);

        } else {
            holder.mTvSupplement.setText(String.format(mNumHint, qty, limit));
            holder.mTvSupplement.setTextColor(mGreyColor);
            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(position);
            tvTitle.setTextColor(mBlackColor);
            cbSelect.setEnabled(true);
        }
    }

    /**
     * @return 返回选中的列表项
     */
    public ArrayList<ActionDetailBean.DataBean.ITEMSBean> getDataRow() {
        ArrayList<ActionDetailBean.DataBean.ITEMSBean> arrayList = new ArrayList<>();
        for (int i = 0, size = fCheckBoxSelectCollection.size(); i < size; ++i) {
            arrayList.add(mDataSet.get(fCheckBoxSelectCollection.keyAt(i)));
        }
        return arrayList;
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Object tag = buttonView.getTag();
        if (tag instanceof Integer) {
            Integer position = (Integer) tag;
            int value = fCheckBoxSelectCollection.get(position, -1);

            if (isChecked) {
                if (-1 == value) {
                    ActionDetailBean.DataBean.ITEMSBean row = mDataSet.get(position);
                    String temp;
                    if (row != null && BigDecimal.ZERO.compareTo(new BigDecimal(row.getJOIN_QTY_LIMIT())) != 0) {

                        if (!(1 == new BigDecimal((temp = row.getJOIN_QTY_LIMIT()) == null ? "0" : temp)
                                .subtract(new BigDecimal((temp = row.getJOIN_QTY()) == null ? "0" : temp))
                                .compareTo(BigDecimal.ZERO))) {
                            ToastUtil.showShort("参加人数已满");
                            buttonView.setChecked(false);
                            return;
                        }
                    }
                    fCheckBoxSelectCollection.put(position, position);
                }
            } else {
                if (-1 != value)
                    fCheckBoxSelectCollection.delete(position);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(ActivityDetailViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        fViewHolders.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewDetachedFromWindow(ActivityDetailViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        fViewHolders.delete(holder.getAdapterPosition());
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof Integer) {
            int position = (Integer) tag;
            ActivityDetailViewHolder viewHolder = fViewHolders.get(position);
            if (viewHolder != null) {

                if (-1 == fCheckBoxSelectCollection.get(position, -1)) {
                    viewHolder.mCbSelect.setChecked(true);
                } else {
                    viewHolder.mCbSelect.setChecked(false);
                }
            }
        }
    }

    static class ActivityDetailViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView mTvTitle;
        private AppCompatTextView mTvHint;
        private AppCompatTextView mTvPrice;
        private AppCompatCheckBox mCbSelect;
        private AppCompatTextView mTvSupplement;

        private ActivityDetailViewHolder(View itemView) {
            super(itemView);
            this.mTvTitle = (AppCompatTextView) itemView.findViewById(R.id.tv_title);
            this.mTvHint = (AppCompatTextView) itemView.findViewById(R.id.tv_hint);
            this.mTvPrice = (AppCompatTextView) itemView.findViewById(R.id.tv_price);
            this.mCbSelect = (AppCompatCheckBox) itemView.findViewById(R.id.cb_select);
            this.mTvSupplement = (AppCompatTextView) itemView.findViewById(R.id.tv_supplement);
        }
    }

    /*
    @Override
    public void onBindViewHolder(ActivityDetailViewHolder holder, int position) {
        DataRow row = mDataSet.getRow(position);
        if (row != null) {
            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(position);
            AppCompatTextView mTvHint = holder.mTvHint;
            int mTvHintVisibility = mTvHint.getVisibility();
            switch (mActivityKind) {
                case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
                    if (mTvHintVisibility != GONE)
                        mTvHint.setVisibility(GONE);
                    holder.mTvPrice.setText(String.valueOf(row.getString("SCORE") + " 积分"));
                    break;
                case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
                    int score = row.getInt("SCORE");
                    if (score != 0) {
                        if (mTvHintVisibility != View.VISIBLE)
                            mTvHint.setVisibility(View.VISIBLE);
                        BigDecimal val = row.getDecimal("VAL");
                        BigDecimal price = row.getDecimal("PRICE");
                        if (val != null && price != null) {
                            BigDecimal subtract = val.subtract(price);
                            mTvHint.setText(String.valueOf(score + "积分可抵扣￥" + subtract.toString()));
                        }
                    }
                    if (mTvHintVisibility != View.GONE)
                        mTvHint.setVisibility(GONE);
                    holder.mTvPrice.setText(String.valueOf("￥" + row.getString("PRICE")));
                    break;
                case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                    if (mTvHintVisibility != View.VISIBLE)
                        mTvHint.setVisibility(View.VISIBLE);

                    String price = row.getString("PRICE");
                    price = price == null ? "" : price;
                    SpannableString savePrice = new SpannableString("储值￥" + price);
                    savePrice.setSpan(new ForegroundColorSpan(mPrimaryColor), 2, 2 + price.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    mTvHint.setText(savePrice);

                    String priceAll = row.getString("VAL");
                    priceAll = priceAll == null ? "" : priceAll;
                    SpannableString spannableString = new SpannableString("到账￥" + priceAll);
                    spannableString.setSpan(new ForegroundColorSpan(mGreyColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.mTvPrice.setText(spannableString);
                    break;
                case IRecyclerViewConstant.KIND_METER_ACTIVITY:
                    if (mTvHintVisibility != View.VISIBLE)
                        mTvHint.setVisibility(View.VISIBLE);

                    mTvHint.setText(String.valueOf(row.getString("VAL") + "次"));
                    holder.mTvPrice.setText(String.valueOf("￥" + row.getString("PRICE")));
                    break;
                case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                    break;
            }

            holder.mTvTitle.setText(row.getString("TITLE"));

            AppCompatCheckBox mCbSelect = holder.mCbSelect;
            if (-1 != fCheckBoxSelectCollection.get(position, -1))
                mCbSelect.setChecked(true);
            else
                mCbSelect.setChecked(false);
            mCbSelect.setTag(position);
            mCbSelect.setOnCheckedChangeListener(this);

             // 买的价格  （积分活动为积分加买的价格）（储值：充200（price）送100（val - price））
             // （记次 200元（price） 10次（val）

            //row.getString("PRICE");//
            //row.getString("SCORE");//积分
            //row.getString("VAL");//原价
        }
    }*/

}
