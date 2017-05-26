package net.twoant.master.ui.chat.adapter;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.domain.EaseUser;

import net.twoant.master.R;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/3/7.
 * 群组的黑名单列表
 */

public class GroupBlackListAdapter extends BaseRecyclerAdapter<EaseUser, GroupBlackListAdapter.GroupBlackListViewHolder> {

    private View.OnClickListener mOnClickListener;

    public GroupBlackListAdapter(Activity context, View.OnClickListener onClickListener) {
        super(context);
        this.mOnClickListener = onClickListener;
    }

    static class GroupBlackListViewHolder extends BaseRecyclerViewHolder {

        private CircleImageView mIvHeaderImage;
        private AppCompatTextView mTvNickname;
        private AppCompatTextView mTvSignature;
        private AppCompatButton mBtnRemove;

        private GroupBlackListViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mIvHeaderImage = (CircleImageView) itemView.findViewById(R.id.iv_header_image);
            this.mTvNickname = (AppCompatTextView) itemView.findViewById(R.id.tv_nickname);
            this.mTvSignature = (AppCompatTextView) itemView.findViewById(R.id.tv_signature);
            this.mBtnRemove = (AppCompatButton) itemView.findViewById(R.id.btn_remove);
        }
    }

    @Override
    protected void onBindViewHolder(GroupBlackListViewHolder holder, int viewType, int position) {
        EaseUser easeUser = mDataList.get(position);
        if (null != easeUser) {
            holder.mTvNickname.setText(easeUser.getNickname());
            holder.mTvSignature.setText(easeUser.getSignature());
            holder.mBtnRemove.setTag(position);
            holder.mBtnRemove.setOnClickListener(mOnClickListener);
            ImageLoader.getImageFromNetwork(holder.mIvHeaderImage, easeUser.getAvatar(), mContext, R.drawable.ic_def_circle);
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new GroupBlackListViewHolder(layoutInflater.inflate(R.layout.yh_item_chat_group_black_list, parent, false), viewType);
    }
}
