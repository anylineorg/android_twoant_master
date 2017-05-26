package net.twoant.master.ui.chat.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import com.hyphenate.easeui.model.GroupInfoBean;
import net.twoant.master.ui.chat.activity.GroupSimpleDetailActivity;
import net.twoant.master.ui.chat.activity.UserProfileActivity;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.chat.domain.InviteMessage;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/2/24.
 * 新好友 、通知适配器
 */

public class NewFriendMessageAdapter extends BaseRecyclerAdapter<InviteMessage, NewFriendMessageAdapter.NewFriendMessageViewHolder> implements View.OnClickListener {

    private InviteMessageDao mMessageDao;
    private SimpleDateFormat mSimpleDateFormat;
    private HintDialogUtil mHintDialog;
    private Date mDate;
    private ArrayMap<String, GroupInfoBean> mGroupInfoMap;
    private ArrayMap<String, EaseUser> mUserInfoMap;

    public NewFriendMessageAdapter(Activity context) {
        super(context);
        this.mMessageDao = new InviteMessageDao(context);
    }

    public void setGroupInfoMap(ArrayMap<String, GroupInfoBean> groupInfoMap) {
        this.mGroupInfoMap = groupInfoMap;
        if (null != this.mGroupInfoMap && !this.mGroupInfoMap.isEmpty()) {
            this.notifyDataSetChanged();
        }
    }

    public void setmUserInfoMap(ArrayMap<String, EaseUser> userInfoMap) {
        this.mUserInfoMap = userInfoMap;
        if (null != mUserInfoMap && !mUserInfoMap.isEmpty()) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    protected void onBindViewHolder(NewFriendMessageViewHolder holder, int viewType, int position) {
        InviteMessage inviteMessage = mDataList.get(position);
        if (null != inviteMessage) {
            //初始化公有数据
            initUserOrGroupCommonData(holder, inviteMessage);

            if (View.GONE != holder.mIvMsgState.getVisibility()) {
                holder.mIvMsgState.setVisibility(View.GONE);
            }

            InviteMessage.InviteMesageStatus status = inviteMessage.getStatus();
            AppCompatButton btnDisagree = holder.mBtnDisagree;

            AppCompatButton btnAgree = holder.mBtnAgree;
            btnDisagree.setTag(position);
            btnAgree.setTag(position);

            LinearLayoutCompat behaviour = holder.mLlBehaviour;//同意、拒绝 父布局

            //远程用户已同意
            if (status == InviteMessage.InviteMesageStatus.BEAGREED) {

                if (View.VISIBLE != behaviour.getVisibility()) {
                    behaviour.setVisibility(View.VISIBLE);
                }

                if (View.GONE != btnDisagree.getVisibility()) {
                    btnDisagree.setVisibility(View.GONE);
                }

                if (btnAgree.isEnabled()) {
                    btnAgree.setEnabled(false);
                }

                btnAgree.setText("已添加");

                EaseUser easeUser = null == mUserInfoMap ? null : mUserInfoMap.get(inviteMessage.getFrom());
                String temp = mContext.getResources().getString(net.twoant.master.R.string.Has_agreed_to_your_friend_request);
                if (null != easeUser) {
                    holder.mTvMessage.setText(TextUtils.isEmpty(easeUser.getNickname()) ?
                            "信息: " + easeUser.getUsername() + temp : easeUser.getNickname() + "(" +
                            easeUser.getUsername() + ")" + temp);
                } else {
                    holder.mTvMessage.setText(inviteMessage.getFromUserNickName() == null ?
                            "信息: " + inviteMessage.getFrom() + temp : inviteMessage.getFromUserNickName() + temp);
                }
            } else if (status == InviteMessage.InviteMesageStatus.BEINVITEED ||
                    status == InviteMessage.InviteMesageStatus.BEAPPLYED ||
                    status == InviteMessage.InviteMesageStatus.GROUPINVITATION) { //被邀请、远程用户申请加入、收到远程用户的群组邀请

                if (View.VISIBLE != btnAgree.getVisibility()) {
                    btnAgree.setVisibility(View.VISIBLE);
                }

                if (!btnAgree.isEnabled()) {
                    btnAgree.setEnabled(true);
                }

                btnAgree.setText(mContext.getResources().getString(net.twoant.master.R.string.agree));

                if (View.VISIBLE != btnDisagree.getVisibility()) {
                    btnDisagree.setVisibility(View.VISIBLE);
                }

                if (!btnDisagree.isEnabled()) {
                    btnDisagree.setEnabled(true);
                }

                btnDisagree.setText(mContext.getResources().getString(net.twoant.master.R.string.refuse));

                //添加理由
                String reason = inviteMessage.getReason();

                if (status == InviteMessage.InviteMesageStatus.BEINVITEED) {
                    if (TextUtils.isEmpty(reason)) {
                        holder.mTvMessage.setText(net.twoant.master.R.string.Request_to_add_you_as_a_friend);
                    } else {
                        holder.mTvMessage.setText(String.valueOf("信息: " + reason));
                    }
                } else if (status == InviteMessage.InviteMesageStatus.BEAPPLYED) { //申请进群
                    if (TextUtils.isEmpty(reason)) {
                        holder.mTvMessage.setText(String.valueOf(mContext.getResources().getString(net.twoant.master.R.string.Apply_to_the_group_of) + inviteMessage.getGroupName()));
                    } else {
                        reason = mContext.getResources().getString(net.twoant.master.R.string.Apply_to_the_group_of) +
                                inviteMessage.getGroupName() + "\n附加信息: " + reason;
                        holder.mTvMessage.setText(reason);
                    }
                } else {//status == InviteMessage.InviteMesageStatus.GROUPINVITATION
                    EaseUser easeUser = null == mUserInfoMap ? null : mUserInfoMap.get(inviteMessage.getFrom());
                    String inviteUser = null != easeUser ?
                            TextUtils.isEmpty(easeUser.getNickname()) ? easeUser.getUsername() : easeUser.getNickname() + "(" +
                                    easeUser.getUsername() + ")"
                            : inviteMessage.getFrom();

                    if (TextUtils.isEmpty(reason)) {
                        holder.mTvMessage.setText(String.valueOf("群邀请: " + inviteUser + "邀请您入" + inviteMessage.getGroupName()));
                    } else {
                        reason = inviteUser + "邀请您加入" + inviteMessage.getGroupName();
                        holder.mTvMessage.setText(reason);
                    }
                }

                btnAgree.setOnClickListener(this);
                btnDisagree.setOnClickListener(this);

            } else if (status == InviteMessage.InviteMesageStatus.AGREED) {
                btnAgree.setText(mContext.getResources().getString(net.twoant.master.R.string.Has_agreed_to));
                if (btnAgree.isEnabled()) {
                    btnAgree.setEnabled(false);
                }
                if (View.GONE != btnDisagree.getVisibility()) {
                    btnDisagree.setVisibility(View.GONE);
                }
                holder.mTvMessage.setText(String.valueOf("消息: " + inviteMessage.getReason()));
            } else if (status == InviteMessage.InviteMesageStatus.REFUSED) {
                btnDisagree.setText(mContext.getResources().getString(net.twoant.master.R.string.Has_refused_to));
                if (btnDisagree.isEnabled()) {
                    btnDisagree.setEnabled(false);
                }
                if (View.VISIBLE != holder.mIvMsgState.getVisibility()) {
                    holder.mIvMsgState.setVisibility(View.VISIBLE);
                }
                if (View.GONE != btnAgree.getVisibility()) {
                    btnAgree.setVisibility(View.GONE);
                }
                holder.mTvMessage.setText("已拒绝添加请求");
            } else if (status == InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED) {
                EaseUser easeUser = null == mUserInfoMap ? null : mUserInfoMap.get(inviteMessage.getGroupInviter());

                String str = easeUser == null ?
                        inviteMessage.getGroupInviter() + mContext.getResources().getString(net.twoant.master.R.string.accept_join_group) + inviteMessage.getGroupName()
                        : easeUser.getNickname() + " (" + easeUser.getUsername() + ") "
                        + mContext.getResources().getString(net.twoant.master.R.string.accept_join_group) + inviteMessage.getGroupName();

                holder.mTvMessage.setText(str);

                if (View.GONE != behaviour.getVisibility()) {
                    behaviour.setVisibility(View.GONE);
                }

            } else if (status == InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED) {
                EaseUser easeUser = null == mUserInfoMap ? null : mUserInfoMap.get(inviteMessage.getGroupInviter());

                String str = easeUser == null ?
                        inviteMessage.getGroupInviter() + mContext.getResources().getString(net.twoant.master.R.string.refuse_join_group) + inviteMessage.getGroupName()
                        : easeUser.getNickname() + " (" + easeUser.getUsername() + ") "
                        + mContext.getResources().getString(net.twoant.master.R.string.refuse_join_group) + inviteMessage.getGroupName();

                holder.mTvMessage.setText(str);

                if (View.VISIBLE != holder.mIvMsgState.getVisibility()) {
                    holder.mIvMsgState.setVisibility(View.VISIBLE);
                }
                if (View.GONE != behaviour.getVisibility()) {
                    behaviour.setVisibility(View.GONE);
                }
            }
        }

    }

    private void initUserOrGroupCommonData(NewFriendMessageViewHolder holder, InviteMessage inviteMessage) {
        if (null != inviteMessage.getGroupId()) {//群
            if (View.VISIBLE != holder.mTvGroupName.getVisibility()) {
                holder.mTvGroupName.setVisibility(View.VISIBLE);
            }

            holder.mTvAccount.setText(String.valueOf("群号: " + inviteMessage.getGroupId()));
            holder.mTvGroupName.setText(String.valueOf("群名: " + inviteMessage.getGroupName()));

            if (!holder.mIvHeaderAvatar.isClickable()) {
                holder.mIvHeaderAvatar.setClickable(true);
            }

            GroupInfoBean groupInfoBean = null == mGroupInfoMap ? null : mGroupInfoMap.get(inviteMessage.getGroupId());
            if (null != groupInfoBean) {
                ImageLoader.getImageFromNetwork(holder.mIvHeaderAvatar, groupInfoBean.getGroupAvatar(), mContext, net.twoant.master.R.drawable.ic_def_circle);
            } else {
                holder.mIvHeaderAvatar.setImageResource(net.twoant.master.R.drawable.ic_def_circle);
            }

            String from = null == inviteMessage.getGroupInviter() ? inviteMessage.getFrom() : inviteMessage.getGroupInviter();
            EaseUser easeUser = null == mUserInfoMap ? null : mUserInfoMap.get(from);
            if (null != easeUser) {
                holder.mTvNickname.setText(TextUtils.isEmpty(easeUser.getNickname()) ? easeUser.getUsername() : easeUser.getNickname() + " (" + easeUser.getUsername() + ")");
            } else {
                holder.mTvNickname.setText(String.valueOf("蚂蚁号: " + from));
            }

            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(inviteMessage.getGroupId() + "-" + inviteMessage.getGroupName());
            holder.mIvHeaderAvatar.setTag(net.twoant.master.R.id.new_friend, from);
            holder.mIvHeaderAvatar.setOnClickListener(this);
        } else {
            if (View.GONE != holder.mTvGroupName.getVisibility()) {
                holder.mTvGroupName.setVisibility(View.GONE);
            }

            if (holder.mIvHeaderAvatar.isClickable()) {
                holder.mIvHeaderAvatar.setClickable(false);
            }

            EaseUser easeUser = null == mUserInfoMap ? null : mUserInfoMap.get(inviteMessage.getFrom());
            if (null != easeUser) {
                ImageLoader.getImageFromNetwork(holder.mIvHeaderAvatar, easeUser.getAvatar(), mContext, net.twoant.master.R.drawable.ic_def_circle);
                holder.mTvNickname.setText(TextUtils.isEmpty(easeUser.getNickname()) ? easeUser.getUsername() : easeUser.getNickname());
            } else {
                holder.mTvNickname.setText(null == inviteMessage.getFromUserNickName() ? inviteMessage.getFrom() : inviteMessage.getFromUserNickName());
                holder.mIvHeaderAvatar.setImageResource(net.twoant.master.R.drawable.ic_def_circle);
            }

            holder.mTvAccount.setText(String.valueOf("蚂蚁号: " + inviteMessage.getFrom()));
            holder.itemView.setTag(inviteMessage.getFrom());
            holder.itemView.setOnClickListener(this);
        }
        holder.mTime.setText(decodeTime(inviteMessage.getTime()));
    }

    /**
     * 转换日期
     */
    private String decodeTime(long time) {
        if (null == mSimpleDateFormat) {
            mSimpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.CHINA);
        }

        if (null == mDate) {
            mDate = new Date();
        }

        mDate.setTime(time);
        return "时间: " + mSimpleDateFormat.format(mDate);
    }

    /**
     * 初始化 弹窗
     */
    private void initAndShowLoadingDialog(String hint) {
        if (null == mHintDialog) {
            mHintDialog = new HintDialogUtil(mContext);
        }
        mHintDialog.showLoading(hint, true, false);
    }

    /**
     * 接受 invitation
     */
    private void acceptInvitation(int pos) {
        if (null == mDataList || mDataList.size() < pos) {
            return;
        }

        initAndShowLoadingDialog(mContext.getResources().getString(net.twoant.master.R.string.Are_agree_with));
        UserInfoDiskHelper.getInstance().execute(new AcceptInvitation(mDataList.get(pos), pos));
    }

    /**
     * 接受 邀请
     */
    private class AcceptInvitation implements Runnable {

        private InviteMessage mInviteMessage;
        private int mPosition;

        private AcceptInvitation(InviteMessage inviteMessage, int position) {
            this.mInviteMessage = inviteMessage;
            this.mPosition = position;
        }

        @Override
        public void run() {

            try {
                if (mInviteMessage.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {//accept be friends
                    EMClient.getInstance().contactManager().acceptInvitation(mInviteMessage.getFrom());
                } else if (mInviteMessage.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) { //accept application to join group
                    EMClient.getInstance().groupManager().acceptApplication(mInviteMessage.getFrom(), mInviteMessage.getGroupId());
                } else if (mInviteMessage.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                    EMClient.getInstance().groupManager().acceptInvitation(mInviteMessage.getGroupId(), mInviteMessage.getGroupInviter());
                }
                mInviteMessage.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                // update database
                final ContentValues values = new ContentValues();
                values.put(InviteMessageDao.COLUMN_NAME_STATUS, mInviteMessage.getStatus().ordinal());
                mMessageDao.updateMessage(mInviteMessage.getId(), values);
                if (null != mContext) {
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (null != mHintDialog) {
                                mHintDialog.dismissDialog();
                            }
                            View view = NewFriendMessageAdapter.this.getVisibilityViewAtPosition(mPosition);
                            if (null != view) {
                                AppCompatButton agree = (AppCompatButton) view.findViewById(net.twoant.master.R.id.btn_disagree);
                                if (null != agree) {
                                    agree.setText(net.twoant.master.R.string.Has_agreed_to);
                                    agree.setEnabled(false);
                                }
                                agree = (AppCompatButton) view.findViewById(net.twoant.master.R.id.btn_agree);

                                if (null != agree) {
                                    agree.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }

            } catch (final Exception e) {
                if (null != mContext) {
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (null != mHintDialog) {
                                mHintDialog.dismissDialog();
                            }
                            ToastUtil.showShort(net.twoant.master.R.string.Agree_with_failure);
                        }
                    });
                }

            }

        }
    }

    /**
     * 拒绝 邀请
     */
    private class RefuseInvitation implements Runnable {
        private InviteMessage mInviteMessage;
        private int mPosition;

        private RefuseInvitation(InviteMessage inviteMessage, int position) {
            this.mInviteMessage = inviteMessage;
            this.mPosition = position;
        }

        @Override
        public void run() {
            // call api
            try {
                if (mInviteMessage.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {//decline the invitation
                    EMClient.getInstance().contactManager().declineInvitation(mInviteMessage.getFrom());
                } else if (mInviteMessage.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) { //decline application to join group
                    EMClient.getInstance().groupManager().declineApplication(mInviteMessage.getFrom(), mInviteMessage.getGroupId(), "");
                } else if (mInviteMessage.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                    EMClient.getInstance().groupManager().declineInvitation(mInviteMessage.getGroupId(), mInviteMessage.getGroupInviter(), "");
                }
                mInviteMessage.setStatus(InviteMessage.InviteMesageStatus.REFUSED);
                // update database
                ContentValues values = new ContentValues();
                values.put(InviteMessageDao.COLUMN_NAME_STATUS, mInviteMessage.getStatus().ordinal());
                mMessageDao.updateMessage(mInviteMessage.getId(), values);
                if (null != mContext) {
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (null != mHintDialog) {
                                mHintDialog.dismissDialog();
                            }

                            View view = NewFriendMessageAdapter.this.getVisibilityViewAtPosition(mPosition);
                            if (null != view) {
                                AppCompatButton btn = (AppCompatButton) view.findViewById(net.twoant.master.R.id.btn_disagree);
                                btn.setText(net.twoant.master.R.string.Has_refused_to);
                                btn.setEnabled(false);
                                btn = (AppCompatButton) view.findViewById(net.twoant.master.R.id.btn_agree);
                                btn.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            } catch (final Exception e) {
                if (null != mContext) {
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ToastUtil.showShort(net.twoant.master.R.string.Refuse_with_failure);
                        }
                    });
                }

            }
        }
    }

    /**
     * 拒绝请求
     */
    private void refuseInvitation(int position) {
        if (null == mDataList || mDataList.size() < position) {
            return;
        }

        initAndShowLoadingDialog(mContext.getResources().getString(net.twoant.master.R.string.Are_refuse_with));
        UserInfoDiskHelper.getInstance().execute(new RefuseInvitation(mDataList.get(position), position));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new NewFriendMessageViewHolder(layoutInflater.inflate(net.twoant.master.R.layout.yh_row_invite_msg, parent, false), viewType);
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_agree:
                if (tag instanceof Integer) {
                    acceptInvitation((Integer) tag);
                }
                break;

            case net.twoant.master.R.id.btn_disagree:
                if (tag instanceof Integer) {
                    refuseInvitation((Integer) tag);
                }
                break;
            case net.twoant.master.R.id.ll_invitation:
                if (tag instanceof String) {
                    String[] split = ((String) tag).split("-");
                    if (2 == split.length) {
                        GroupSimpleDetailActivity.startActivity(mContext, split[0], split[1]);
                    } else {
                        UserProfileActivity.startActivity(mContext, (String) tag);
                    }
                }
                break;

            case net.twoant.master.R.id.iv_header_avatar:
                tag = v.getTag(net.twoant.master.R.id.new_friend);
                if (tag instanceof String) {
                    UserProfileActivity.startActivity(mContext, (String) tag);
                }
                break;
        }
    }


    static class NewFriendMessageViewHolder extends BaseRecyclerViewHolder {

        private CircleImageView mIvHeaderAvatar;
        private AppCompatTextView mTime;
        private AppCompatButton mBtnDisagree;
        private AppCompatButton mBtnAgree;
        private AppCompatTextView mTvNickname;
        private AppCompatImageView mIvMsgState;
        private AppCompatTextView mTvGroupName;
        private AppCompatTextView mTvMessage;
        private AppCompatTextView mTvAccount;
        private LinearLayoutCompat mLlBehaviour;

        NewFriendMessageViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mTime = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_time);
            this.mIvHeaderAvatar = (CircleImageView) itemView.findViewById(net.twoant.master.R.id.iv_header_avatar);
            this.mBtnDisagree = (AppCompatButton) itemView.findViewById(net.twoant.master.R.id.btn_disagree);
            this.mBtnAgree = (AppCompatButton) itemView.findViewById(net.twoant.master.R.id.btn_agree);
            this.mTvNickname = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_nickname);
            this.mIvMsgState = (AppCompatImageView) itemView.findViewById(net.twoant.master.R.id.iv_msg_state);
            this.mTvGroupName = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_groupName);
            this.mTvMessage = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_message);
            this.mTvAccount = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_account);
            this.mLlBehaviour = (LinearLayoutCompat) itemView.findViewById(net.twoant.master.R.id.ll_behaviour);
        }
    }

}

