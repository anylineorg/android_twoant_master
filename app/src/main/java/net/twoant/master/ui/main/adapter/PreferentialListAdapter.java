package net.twoant.master.ui.main.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.interfaces.IOnItemSelectListener;
import net.twoant.master.widget.entry.DataRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by S_Y_H on 2017/4/15.
 * 优惠列表，包括红包、储值卡
 */

public class PreferentialListAdapter extends BaseRecyclerAdapter<DataRow,
        PreferentialListAdapter.PreferentialListViewHolder> {

    private String mRedPacketContent;
    private ForegroundColorSpan mColorPrimarySpan;
    private String mRedPacketLimit;
    private String mStoredActionBalance;
    private String mRedPacketInvalid;
    private String mStoredActionSumPrice;
    private SimpleDateFormat mRedPacketTime;
    private SimpleDateFormat mServerTime;
    private int mCurrentCheckedPosition = -1;
    private CheckedListener mCheckedListener;
    private ItemClickListener mItemClickListener;
    private IOnItemSelectListener<DataRow> iOnItemSelectListener;


    public PreferentialListAdapter(Activity context) {
        super(context);
        mRedPacketContent = mContext.getString(net.twoant.master.R.string.payment_red_packet_description);
        mRedPacketLimit = mContext.getString(net.twoant.master.R.string.payment_red_packet_limit);
        mColorPrimarySpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, net.twoant.master.R.color.colorPrimary));
        mStoredActionBalance = mContext.getString(net.twoant.master.R.string.payment_stored_balance);
        mRedPacketInvalid = mContext.getString(net.twoant.master.R.string.payment_red_packet_invalid);
        mStoredActionSumPrice = mContext.getString(net.twoant.master.R.string.payment_stored_sum);
    }

    public void setOnItemSelectListener(IOnItemSelectListener<DataRow> iOnItemSelectListener) {
        this.iOnItemSelectListener = iOnItemSelectListener;
    }

    @Override
    protected void onBindViewHolder(PreferentialListViewHolder holder, int viewType, int position) {
        DataRow redPacketBean = mDataList.get(position);
        if (null != redPacketBean) {
            switch (redPacketBean.getInt("SORT_ID")) {
                case 12:
                    holder.mTvContent.setText(redPacketBean.getString("ACTIVITY_TITLE"));
                    holder.mTvTitle.setText(String.format(mRedPacketContent, StringUtils.subZeroAndDot(redPacketBean.getStringDef("BALANCE", "0"))));
                    String limitPrice = StringUtils.subZeroAndDot(redPacketBean.getStringDef("ACTIVE_VAL", "100000"));
                    SpannableString limit = new SpannableString(String.format(mRedPacketLimit, limitPrice));
                    limit.setSpan(mColorPrimarySpan, 1, limitPrice.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.mTvLimit.setText(limit);
                    try {
                        holder.mTvDate.setText(String.format(mRedPacketInvalid, getFormatDate(redPacketBean.getString("END_TIME"))));
                    } catch (ParseException e) {
                        holder.mTvDate.setText(net.twoant.master.R.string.payment_get_red_packet_limit_fail);
                    }
                    break;

                case 21:
                    holder.mTvContent.setText(redPacketBean.getString("ACTIVITY_TITLE"));
                    holder.mTvTitle.setText(String.format(mStoredActionSumPrice, StringUtils.subZeroAndDot(redPacketBean.getStringDef("VAL", "0"))));
                    String price = StringUtils.subZeroAndDot(redPacketBean.getStringDef("BALANCE", "100000"));
                    SpannableString spannableString = new SpannableString(String.format(mStoredActionBalance, price));
                    spannableString.setSpan(mColorPrimarySpan, spannableString.length() - price.length() - 1, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.mTvLimit.setText(spannableString);
                    holder.mTvDate.setText(redPacketBean.getStringDef("SORT_NM", ""));
                    break;
                default:
                    return;
            }
            if (null == mCheckedListener) {
                mCheckedListener = new CheckedListener();
            }
            holder.mCbSelect.setOnCheckedChangeListener(mCheckedListener);
            if (null == mItemClickListener) {
                mItemClickListener = new ItemClickListener();
            }
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mItemClickListener);
            holder.mCbSelect.setTag(position);
            holder.mCbSelect.setChecked(position == mCurrentCheckedPosition);
        }
    }

    public void setDataBean(List<DataRow> redDataRows, DataRow redPacketBeanHigh) {
        if (null != redDataRows) {
            mDataList = redDataRows;
            if (null == redPacketBeanHigh) {
                mCurrentCheckedPosition = -1;
                return;
            }
            DataRow temp;
            String id = redPacketBeanHigh.getStringDef("ID", "-1");
            for (int i = 0, size = redDataRows.size(); i < size; ++i) {
                temp = redDataRows.get(i);
                if (null == temp) {
                    continue;
                }
                if (id.equals(temp.getString("ID"))) {
                    mCurrentCheckedPosition = i;
                    break;
                }
            }
        }
    }

    public void addDataBean(List<DataRow> dataRows) {
        if (null != dataRows) {
            mDataList.addAll(dataRows);
        }
    }

    public void setCurrentPosition(int currentPosition) {
        this.mCurrentCheckedPosition = currentPosition;
    }

    private class CheckedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getTag() instanceof Integer) {
                final int tag = (int) buttonView.getTag();
                if (isChecked) {
                    if (tag == mCurrentCheckedPosition) {
                        return;
                    }
                    if (-1 < mCurrentCheckedPosition && mDataList.size() > mCurrentCheckedPosition) {
                        notifyItemChanged(mCurrentCheckedPosition);
                    }
                    mCurrentCheckedPosition = tag;
                    if (null != iOnItemSelectListener) {
                        iOnItemSelectListener.onItemSelectListener(mDataList.size() > tag ? mDataList.get(tag) : null);
                    }
                } else {
                    if (mCurrentCheckedPosition == tag && null != iOnItemSelectListener) {
                        mCurrentCheckedPosition = -1;
                        iOnItemSelectListener.onItemSelectListener(null);
                    }
                }
            }
        }
    }

    private class ItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof Integer) {
                PreferentialListViewHolder visibilityViewHolder = getVisibilityViewHolder((int) v.getTag());
                if (null != visibilityViewHolder) {
                    AppCompatCheckBox cbSelect = visibilityViewHolder.mCbSelect;
                    if (cbSelect.isChecked()) {
                        cbSelect.setChecked(false);
                    } else {
                        cbSelect.setChecked(true);
                    }
                }
            }
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new PreferentialListViewHolder(layoutInflater.inflate(net.twoant.master.R.layout.yh_item_preferentia, parent, false), viewType);
    }

    private String getFormatDate(String date) throws ParseException {
        if (null == mRedPacketTime) {
            //"2017-01-26 12:00:00"
            mServerTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
            mRedPacketTime = new SimpleDateFormat("yy-MM-dd hh", Locale.CHINA);
        }
        return mRedPacketTime.format(mServerTime.parse(date));
    }

    static class PreferentialListViewHolder extends BaseRecyclerViewHolder {

        private AppCompatTextView mTvTitle;
        private AppCompatTextView mTvLimit;
        private AppCompatTextView mTvContent;
        private AppCompatTextView mTvDate;
        private AppCompatCheckBox mCbSelect;

        PreferentialListViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mTvTitle = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_title);
            this.mTvLimit = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_limit);
            this.mTvContent = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_content);
            this.mTvDate = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_date);
            this.mCbSelect = (AppCompatCheckBox) itemView.findViewById(net.twoant.master.R.id.cb_select);

        }
    }

}
