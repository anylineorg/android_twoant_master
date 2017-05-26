package net.twoant.master.ui.chat.holder;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/2/18.
 * 产品咨询viewHolder
 */

public class MerchantChatViewHolder extends BaseRecyclerViewHolder {

    private CircleImageView mIvHeaderImage;
    private AppCompatTextView mTvNickname;
    private AppCompatTextView mTvSignature;
    private AppCompatTextView mTvState;

    public MerchantChatViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    protected void initView(View itemView, int viewType) {
        this.mIvHeaderImage = (CircleImageView) itemView.findViewById(net.twoant.master.R.id.iv_header_image);
        this.mTvNickname = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_nickname);
        this.mTvSignature = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_signature);
        this.mTvState = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_state);
    }

    public CircleImageView getIvHeaderImage() {
        return mIvHeaderImage;
    }

    public AppCompatTextView getTvNickname() {
        return mTvNickname;
    }

    public AppCompatTextView getTvSignature() {
        return mTvSignature;
    }

    public AppCompatTextView getTvState() {
        return mTvState;
    }
}
