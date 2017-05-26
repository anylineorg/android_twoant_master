package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/2/7.
 * 报表 ViewHolder
 */

public class ReportStatementViewHolder extends BaseRecyclerViewHolder {

    private CircleImageView mIvHeadImg;
    private AppCompatTextView mTvNickname;
    private AppCompatTextView mTvPhoneNumber;
    private RecyclerView mListView;

    public ReportStatementViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    protected void initView(View itemView, int viewType) {
        this.mIvHeadImg = (CircleImageView) itemView.findViewById(R.id.iv_head_img);
        this.mTvNickname = (AppCompatTextView) itemView.findViewById(R.id.tv_nickname);
        this.mTvPhoneNumber = (AppCompatTextView) itemView.findViewById(R.id.tv_phone_number);
        this.mListView = (RecyclerView) itemView.findViewById(R.id.rv_recycler_view);
    }

    public CircleImageView getIvHeadImg() {
        return mIvHeadImg;
    }

    public AppCompatTextView getTvNickname() {
        return mTvNickname;
    }

    public AppCompatTextView getTvPhoneNumber() {
        return mTvPhoneNumber;
    }

    public RecyclerView getListView() {
        return mListView;
    }
}