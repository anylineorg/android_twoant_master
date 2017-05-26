package net.twoant.master.ui.chat.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
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
 * Created by S_Y_H on 2017/3/14.
 * 用户黑名单列表
 */

public class UserBlackListAdapter extends BaseRecyclerAdapter<EaseUser, UserBlackListAdapter.UserBlackViewHolder> {

    private View.OnClickListener mOnClickListener;

    public UserBlackListAdapter(Activity context, @Nullable ILoadingData iLoadingData, View.OnClickListener onClickListener) {
        super(context, iLoadingData);
        this.mOnClickListener = onClickListener;
    }

    @Override
    protected void onBindViewHolder(UserBlackViewHolder holder, int viewType, int position) {
        EaseUser easeUser = mDataList.get(position);
        if (null != easeUser) {
            ImageLoader.getImageFromNetwork(holder.mIvHeaderImage, easeUser.getAvatar(), mContext, R.drawable.ic_def_circle);
            holder.mTvNickname.setText(easeUser.getNickname());
            holder.mTvSignature.setText(easeUser.getSignature());
            String username = easeUser.getUsername();
            holder.itemView.setTag(username);
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mBtnRemove.setTag(username);
            holder.mBtnRemove.setTag(R.id.chat_current_click_id, position);
            holder.mBtnRemove.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new UserBlackViewHolder(layoutInflater.inflate(R.layout.yh_item_user_black_list, parent, false), viewType);
    }

    static class UserBlackViewHolder extends BaseRecyclerViewHolder {

        private CircleImageView mIvHeaderImage;
        private AppCompatTextView mTvNickname;
        private AppCompatTextView mTvSignature;
        private AppCompatButton mBtnRemove;

        private UserBlackViewHolder(View itemView, int viewType) {
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

}
