package net.twoant.master.ui.chat.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.model.GroupInfoBean;

import net.twoant.master.R;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/2/24.
 * 群组列表 适配器
 */

public class GroupsAdapter extends BaseRecyclerAdapter<EMGroup, GroupsAdapter.GroupsViewHolder> {

    private View.OnClickListener mOnClickListener;
    private Map<String, GroupInfoBean> mGroupInfoBean;

    public GroupsAdapter(Activity context, @Nullable ILoadingData iLoadingData, @Nullable View.OnClickListener onClickListener) {
        super(context, iLoadingData);
        this.mOnClickListener = onClickListener;
    }

    public void setGroupInfoBean(Map<String, GroupInfoBean> groupInfoBean) {
        this.mGroupInfoBean = groupInfoBean;
        if (null != mGroupInfoBean) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    protected void onBindViewHolder(GroupsViewHolder holder, int viewType, int position) {
        EMGroup emGroup = mDataList.get(position);
        if (emGroup != null) {
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setTag(emGroup.getGroupId());
            holder.mTvGroupName.setText(emGroup.getGroupName());
            GroupInfoBean groupInfoBean = null == mGroupInfoBean ? null : mGroupInfoBean.get(emGroup.getGroupId());
            if (null != groupInfoBean) {
                ImageLoader.getImageFromNetwork(holder.mIvGroupAvatar, groupInfoBean.getGroupAvatar()
                        , mContext, R.drawable.ic_def_circle);
            } else {
                holder.mIvGroupAvatar.setImageResource(R.drawable.ic_def_circle);
            }
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new GroupsViewHolder(layoutInflater.inflate(R.layout.yh_row_group, parent, false), viewType);
    }

    static class GroupsViewHolder extends BaseRecyclerViewHolder {

        private CircleImageView mIvGroupAvatar;
        private AppCompatTextView mTvGroupName;

        GroupsViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mIvGroupAvatar = (CircleImageView) itemView.findViewById(R.id.iv_group_avatar);
            this.mTvGroupName = (AppCompatTextView) itemView.findViewById(R.id.tv_group_name);
        }
    }
}
