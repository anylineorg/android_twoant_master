package net.twoant.master.ui.chat.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.chat.activity.UserProfileActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.control.ControlUtils;

import java.util.Collection;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/3/9.
 * 组成员列表 适配器
 */

public class GroupMemberListAdapter extends BaseRecyclerAdapter<String, GroupMemberListAdapter.GroupMemberViewHolder> implements View.OnClickListener, AppCompatRadioButton.OnCheckedChangeListener {

    private ArrayMap<String, EaseUser> mUsersMap;
    private HashSet<String> mUIDSet;
    private String mModeratorId;
    private String mSignature;
    private boolean isRemoveMember;

    public GroupMemberListAdapter(Activity context, String moderatorId) {
        super(context);
        this.mModeratorId = moderatorId;
        mSignature = context.getString(net.twoant.master.R.string.info_signature_hint);
    }

    public void setModeIsRemoved(boolean isRemoved) {
        this.isRemoveMember = isRemoved;
        if (isRemoveMember && null == mUIDSet) {
            mUIDSet = new HashSet<>(null == mDataList ? 2 : mDataList.size());
        }
        this.notifyDataSetChanged();
    }

    public boolean getIsRemovedMemberState() {
        return this.isRemoveMember;
    }

    public void setUserInfoData(ArrayMap<String, EaseUser> users) {
        if (null == this.mUsersMap) {
            this.mUsersMap = users;
        } else {
            this.mUsersMap.putAll((SimpleArrayMap<String, EaseUser>) users);
        }
        if (null != mUsersMap && !mUsersMap.isEmpty()) {
            this.notifyDataSetChanged();
        }
    }

    @Nullable
    public Collection<String> getDeleteMembers() {
        return this.mUIDSet;
    }

    public boolean doRemoveOperation() {
        if (null != mDataList && null != mUIDSet && !mUIDSet.isEmpty()) {
            mDataList.removeAll(mUIDSet);
            isRemoveMember = false;
            this.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    protected void onBindViewHolder(GroupMemberViewHolder holder, int viewType, int position) {
        String uid = mDataList.get(position);
        if (!TextUtils.isEmpty(uid)) {
            AppCompatTextView hintInfo = holder.mTvHintInfo;
            if (uid.equals(mModeratorId)) {
                if (hintInfo.getVisibility() != View.VISIBLE) {
                    hintInfo.setVisibility(View.VISIBLE);
                }
            } else {
                if (hintInfo.getVisibility() != View.GONE) {
                    hintInfo.setVisibility(View.GONE);
                }
            }

            EaseUser easeUser;
            if (null != mUsersMap && null != (easeUser = mUsersMap.get(uid))) {
                holder.mTvNickname.setText(String.valueOf(easeUser.getNickname() + " (" + easeUser.getUsername() + ")"));
                ImageLoader.getImageFromNetwork(holder.mIvHeaderImage, easeUser.getAvatar(), mContext, net.twoant.master.R.drawable.ic_def_circle);
                holder.mTvSignature.setText(ControlUtils.isNull(easeUser.getSignature()) ? mSignature : easeUser.getSignature());
            } else {
                holder.mIvHeaderImage.setImageResource(net.twoant.master.R.drawable.ic_def_circle);
                holder.mTvNickname.setText(uid);
                holder.mTvSignature.setText(mSignature);
            }

            AppCompatRadioButton radioButton = holder.mRbDeleteMember;
            if (isRemoveMember) {

                if (uid.equals(mModeratorId)) {
                    if (radioButton.getVisibility() != View.GONE) {
                        radioButton.setVisibility(View.GONE);
                    }
                    if (radioButton.isChecked()) {
                        radioButton.setChecked(false);
                    }
                    holder.itemView.setTag(uid);
                    radioButton.setTag(null);
                } else {
                    if (radioButton.getVisibility() != View.VISIBLE) {
                        radioButton.setVisibility(View.VISIBLE);
                    }

                    holder.itemView.setTag(position);
                    radioButton.setTag(uid);
                    radioButton.setOnCheckedChangeListener(this);

                    if (null != mUIDSet && mUIDSet.contains(uid)) {
                        radioButton.setChecked(true);
                    } else {
                        radioButton.setChecked(false);
                    }
                }
            } else {
                if (radioButton.getVisibility() != View.GONE) {
                    radioButton.setVisibility(View.GONE);
                }
                holder.itemView.setTag(uid);
            }
            holder.itemView.setOnClickListener(this);
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new GroupMemberViewHolder(layoutInflater.inflate(net.twoant.master.R.layout.yh_item_group_member_list, parent, false), viewType);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Object tag = buttonView.getTag();
        if (tag instanceof String) {
            if (isChecked) {
                mUIDSet.add((String) tag);
            } else {
                mUIDSet.remove(tag);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof String) {
            UserProfileActivity.startActivity(mContext, (String) tag);
        } else if (tag instanceof Integer) {
            GroupMemberViewHolder viewHolder = getVisibilityViewHolder((int) tag);
            AppCompatRadioButton member = viewHolder.mRbDeleteMember;
            if (null != member) {
                member.setChecked(!member.isChecked());
            }
        }
    }

    class GroupMemberViewHolder extends BaseRecyclerViewHolder {

        private AppCompatRadioButton mRbDeleteMember;
        private CircleImageView mIvHeaderImage;
        private AppCompatTextView mTvNickname;
        private AppCompatTextView mTvSignature;
        private AppCompatTextView mTvHintInfo;

        private GroupMemberViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mRbDeleteMember = (AppCompatRadioButton) itemView.findViewById(net.twoant.master.R.id.rb_delete_member);
            this.mIvHeaderImage = (CircleImageView) itemView.findViewById(net.twoant.master.R.id.iv_header_image);
            this.mTvNickname = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_nickname);
            this.mTvSignature = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_signature);
            this.mTvHintInfo = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_hint_info);
        }
    }
}
