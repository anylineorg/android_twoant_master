package net.twoant.master.ui.chat.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.app.ChatMode;
import net.twoant.master.ui.chat.widget.CustomCheckBox;

/**
 * 交流中心的 设置界面
 */

public class ChatSettingsActivity extends ChatBaseActivity implements OnClickListener {

    private AppCompatCheckBox mCbNotification;
    private AppCompatCheckBox mCbNotificationSound;
    private AppCompatCheckBox mCbNotificationVibrate;//震动
    private AppCompatCheckBox mCbSwitchSpeaker;//使用扬声器
    private AppCompatCheckBox mCbExitDeleteMsg;//退群清空
    private AppCompatCheckBox mCbAutoAcceptInvite;//自动同意群邀请

    private ChatMode mSettingsModel;
    private CustomCheckBox mCbSwitchSound;
    private CustomCheckBox mCbSwitchVibrate;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_fragment_conversation_settings;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initSimpleToolbarData(this, "设置", new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatSettingsActivity.this.finish();
            }
        });
        mSettingsModel = ChatHelper.getModel();
        initView();
        initState();
    }

    private void initView() {
        findViewById(net.twoant.master.R.id.tv_black_list).setOnClickListener(this);
        CustomCheckBox cbSwitchNotification = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_notification);
        cbSwitchNotification.setOnClickListener(this);
        mCbNotification = cbSwitchNotification.getCheckBox();
        mCbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbSwitchSound.setVisibility(View.VISIBLE);
                    mCbSwitchVibrate.setVisibility(View.VISIBLE);
                } else {
                    mCbSwitchSound.setVisibility(View.GONE);
                    mCbSwitchVibrate.setVisibility(View.GONE);
                }
            }
        });

        mCbSwitchSound = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_sound);
        mCbSwitchSound.setOnClickListener(this);
        mCbNotificationSound = mCbSwitchSound.getCheckBox();

        mCbSwitchVibrate = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_vibrate);
        mCbSwitchVibrate.setOnClickListener(this);
        mCbNotificationVibrate = mCbSwitchVibrate.getCheckBox();

        CustomCheckBox cbSwitchSpeaker = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_speaker);
        cbSwitchSpeaker.setOnClickListener(this);
        mCbSwitchSpeaker = cbSwitchSpeaker.getCheckBox();

        CustomCheckBox cbSwitchDeleteMsgWhenExitGroup = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_delete_msg_when_exit_group);
        cbSwitchDeleteMsgWhenExitGroup.setOnClickListener(this);
        mCbExitDeleteMsg = cbSwitchDeleteMsgWhenExitGroup.getCheckBox();

        CustomCheckBox cbSwitchAutoAcceptGroupInvitation = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_auto_accept_group_invitation);
        cbSwitchAutoAcceptGroupInvitation.setOnClickListener(this);
        mCbAutoAcceptInvite = cbSwitchAutoAcceptGroupInvitation.getCheckBox();
    }

    private void initState() {
        mCbNotification.setChecked(mSettingsModel.getSettingMsgNotification());
        mCbNotificationSound.setChecked(mSettingsModel.getSettingMsgSound());
        mCbNotificationVibrate.setChecked(mSettingsModel.getSettingMsgVibrate());
        mCbSwitchSpeaker.setChecked(mSettingsModel.getSettingMsgSpeaker());
        mCbExitDeleteMsg.setChecked(mSettingsModel.isDeleteMessagesAsExitGroup());
        mCbAutoAcceptInvite.setChecked(mSettingsModel.isAutoAcceptGroupInvitation());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_black_list://黑名单
                BlacklistActivity.startActivity(ChatSettingsActivity.this);
                break;
            case net.twoant.master.R.id.cb_switch_notification://接受新消息通知
                if (mCbNotification.isChecked()) {
                    mCbNotification.setChecked(false);
                    mSettingsModel.setSettingMsgNotification(false);
                } else {
                    mCbNotification.setChecked(true);
                    mSettingsModel.setSettingMsgNotification(true);
                }
                break;
            case net.twoant.master.R.id.cb_switch_sound://声音
                if (mCbNotificationSound.isChecked()) {
                    mCbNotificationSound.setChecked(false);
                    mSettingsModel.setSettingMsgSound(false);
                } else {
                    mCbNotificationSound.setChecked(true);
                    mSettingsModel.setSettingMsgSound(true);
                }
                break;

            case net.twoant.master.R.id.cb_switch_vibrate://震动
                if (mCbNotificationVibrate.isChecked()) {
                    mCbNotificationVibrate.setChecked(false);
                    mSettingsModel.setSettingMsgVibrate(false);
                } else {
                    mCbNotificationVibrate.setChecked(true);
                    mSettingsModel.setSettingMsgVibrate(true);
                }
                break;

            case net.twoant.master.R.id.cb_switch_speaker://使用扬声器
                if (mCbSwitchSpeaker.isChecked()) {
                    mCbSwitchSpeaker.setChecked(false);
                    mSettingsModel.setSettingMsgSpeaker(false);
                } else {
                    mCbSwitchSpeaker.setChecked(true);
                    mSettingsModel.setSettingMsgSpeaker(true);
                }
                break;

            case net.twoant.master.R.id.cb_switch_delete_msg_when_exit_group://退出群组清空
                if (mCbExitDeleteMsg.isChecked()) {
                    mCbExitDeleteMsg.setChecked(false);
                    mSettingsModel.setDeleteMessagesAsExitGroup(false);
                } else {
                    mCbExitDeleteMsg.setChecked(true);
                    mSettingsModel.setDeleteMessagesAsExitGroup(true);
                }
                break;

            case net.twoant.master.R.id.cb_switch_auto_accept_group_invitation://自动群邀请
                if (mCbAutoAcceptInvite.isChecked()) {
                    mCbAutoAcceptInvite.setChecked(false);
                    mSettingsModel.setAutoAcceptGroupInvitation(false);
                } else {
                    mCbAutoAcceptInvite.setChecked(true);
                    mSettingsModel.setAutoAcceptGroupInvitation(true);
                }
                break;
        }

    }

}
