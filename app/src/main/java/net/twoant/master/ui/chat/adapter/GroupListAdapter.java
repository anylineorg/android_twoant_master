package net.twoant.master.ui.chat.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeui.model.GroupInfoBean;

import net.twoant.master.R;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.chat.activity.GroupSimpleDetailActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/2/25.
 * 添加群组的 适配器
 */

public class GroupListAdapter extends BaseRecyclerAdapter<EMGroupInfo, GroupListAdapter.GroupListViewHolder> implements View.OnClickListener {

    private Map<String, GroupInfoBean> mGroupInfoBean;

    public GroupListAdapter(Activity context, @Nullable ILoadingData iLoadingData) {
        super(context, iLoadingData);
    }

    public void setGroupInfo(Map<String, GroupInfoBean> map) {
        if (null == this.mGroupInfoBean) {
            this.mGroupInfoBean = map;
        } else if (null != map) {
            this.mGroupInfoBean.putAll(map);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public List<EMGroupInfo> setDataBean(List<EMGroupInfo> data, boolean isRefresh) {
        if (isRefresh && null != mGroupInfoBean) {
            mGroupInfoBean.clear();
        }
        return super.setDataBean(data, isRefresh);
    }

    @Override
    protected void onBindViewHolder(GroupListViewHolder holder, int viewType, int position) {
        EMGroupInfo emGroupInfo = mDataList.get(position);
        if (null != emGroupInfo) {
            holder.mTvGroupName.setText(emGroupInfo.getGroupName());
            holder.itemView.setTag(emGroupInfo.getGroupId() + "-" + emGroupInfo.getGroupName());
            holder.itemView.setOnClickListener(this);
            if (null != mGroupInfoBean) {
                GroupInfoBean groupInfoBean = mGroupInfoBean.get(emGroupInfo.getGroupId());
                if (null != groupInfoBean) {
                    ImageLoader.getImageFromNetwork(holder.mIvGroupAvatar,
                            groupInfoBean.getGroupAvatar(), mContext, R.drawable.ic_def_circle);
                } else {
                    holder.mIvGroupAvatar.setImageResource(R.drawable.ic_def_circle);
                }
            }
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new GroupListViewHolder(layoutInflater.inflate(R.layout.yh_row_group, parent, false), viewType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_group:
                Object tag = v.getTag();
                if (tag instanceof String) {
                    String[] split = ((String) tag).split("-");
                    if (2 == split.length) {
                        GroupSimpleDetailActivity.startActivity(mContext, split[0], split[1]);
                    }
                }
                break;
        }
    }

    static class GroupListViewHolder extends BaseRecyclerViewHolder {

        private AppCompatTextView mTvGroupName;
        private CircleImageView mIvGroupAvatar;

        GroupListViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mIvGroupAvatar = (CircleImageView) itemView.findViewById(R.id.iv_group_avatar);
            this.mTvGroupName = (AppCompatTextView) itemView.findViewById(R.id.tv_group_name);
        }
    }

}
