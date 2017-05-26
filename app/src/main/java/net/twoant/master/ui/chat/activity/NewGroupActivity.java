package net.twoant.master.ui.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.base_app.TakePhotoBaseActivity;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.FileUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TResult;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

/**
 * Created by S_Y_H on 2017/3/1.
 * 创建群聊
 */
public class NewGroupActivity extends TakePhotoBaseActivity {

    private AppCompatEditText mEditGroupName;
    private AppCompatEditText mEditGroupIntroduction;
    private AppCompatCheckBox mCbPublic;
    private AppCompatCheckBox mCbMemberInvite;
    private HintDialogUtil mHintDialog;
    private AppCompatImageView mIvGroupImage;
    private int mScreenWidth;
    private File mImageParentPath;
    private ListViewDialog mBottomDialog;
    private String mGroupImageCompressPath;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_new_group;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initToolbarData();
        mScreenWidth = DisplayDimensionUtils.getScreenWidth();
        mHintDialog = new HintDialogUtil(this);
        initView();
        initBottomDialogData();
    }

    private void initView() {
        this.mEditGroupName = (AppCompatEditText) findViewById(net.twoant.master.R.id.edit_group_name);
        this.mEditGroupIntroduction = (AppCompatEditText) findViewById(net.twoant.master.R.id.edit_group_introduction);
        this.mCbPublic = (AppCompatCheckBox) findViewById(net.twoant.master.R.id.cb_public);
        this.mCbMemberInvite = (AppCompatCheckBox) findViewById(net.twoant.master.R.id.cb_member_inviter);
        this.mIvGroupImage = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_group_img);
        ViewGroup.LayoutParams layoutParams = mIvGroupImage.getLayoutParams();
        layoutParams.height = (int) (mScreenWidth / 5.0F * 3 + .5F);
        mIvGroupImage.setLayoutParams(layoutParams);
        mIvGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomDialog.showDialog(true, true);
            }
        });
        mCbMemberInvite.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbMemberInvite.setText(net.twoant.master.R.string.join_need_owner_approval);
                } else {
                    mCbMemberInvite.setText(net.twoant.master.R.string.Open_group_members_invited);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_create, menu);
        MenuItem item = menu.findItem(net.twoant.master.R.id.create);
        if (item != null)
            item.setTitle("下一步");
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK && 0 == requestCode) {

            if (!mHintDialog.isShowing()) {
                mHintDialog.showLoading(net.twoant.master.R.string.Is_to_create_a_group_chat, false, false);
            } else {
                return;
            }

            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final String groupName = mEditGroupName.getText().toString().trim();
                    String desc = mEditGroupIntroduction.getText().toString();
                    String[] members = data.getStringArrayExtra("newmembers");
                    try {
                        EMGroupOptions option = new EMGroupOptions();
                        option.maxUsers = 500;

                        String reason = NewGroupActivity.this.getString(net.twoant.master.R.string.invite_join_group);
                        String nickname = ChatHelper.getUserProfileManager().getCurrentUserInfo().getNickname();
                        reason = TextUtils.isEmpty(nickname) ?
                                EMClient.getInstance().getCurrentUser() + reason + groupName :
                                nickname + "(" + EMClient.getInstance().getCurrentUser() + ")" + reason + groupName;

                        if (mCbPublic.isChecked()) {
                            option.style = mCbMemberInvite.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                        } else {
                            option.style = mCbMemberInvite.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                        }

                        EMGroup group = EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);

                        if (null != group && updateGroupAvatar(mGroupImageCompressPath, group.getGroupId())) {
                            imageUploadState();
                        } else {
                            imageUploadState();
                        }

                    } catch (final HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (null != mHintDialog) {
                                    mHintDialog.showError(UserInfoUtil.getErrorDescription(e, getString(net.twoant.master.R.string.Failed_to_create_groups)), true, true);
                                }
                            }
                        });
                    }
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void imageUploadState() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (null != mHintDialog) {
                    mHintDialog.dismissDialog();
                }
                ToastUtil.showShort("创建完成");
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void initToolbarData() {
        ChatBaseActivity.initSimpleToolbarData(this, getString(net.twoant.master.R.string.chat_create_group), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGroupActivity.this.finish();
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.twoant.master.R.id.create) {
                    String name = mEditGroupName.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        new EaseAlertDialog(NewGroupActivity.this, net.twoant.master.R.string.Group_name_cannot_be_empty).show();
                    } else {
                        // select from contact list
                        startActivityForResult(new Intent(NewGroupActivity.this,
                                GroupPickContactsActivity.class).putExtra("groupName", name), 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void takeSuccess(TResult result) {
        mGroupImageCompressPath = result.getImage().getCompressPath();
        if (ImageView.ScaleType.FIT_XY != mIvGroupImage.getScaleType()) {
            mIvGroupImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        ImageLoader.getImageFromLocation(mIvGroupImage, mGroupImageCompressPath, this, net.twoant.master.R.drawable.ic_def_small);
    }

    public static boolean updateGroupAvatar(String path, String groupId) {
        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();

        String imagePath = null;
        if (null != instance && !TextUtils.isEmpty(path)) {
            File file = new File(path);
            String fileName = file.getName();
            if (!fileName.contains(".")) {
                fileName += ".jpg";
            }

            Response upload = null;
            try {
                upload = OkHttpUtils.post().url(ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE)
                        .addParams("_t", instance.getAiSouUserBean().getLoginToken())
                        .addParams("_ac", instance.getAiSouLocationBean().getCurrentAddressCode())
                        .addParams("_cc", instance.getAiSouLocationBean().getCurrentCityCode())
                        .addFile("file", fileName, file)
                        .build().execute();

                if (upload.isSuccessful()) {
                    DataRow msg = DataRow.parseJson(upload.body().string());
                    if (msg != null && msg.getBoolean("result", false)) {
                        if ((msg = msg.getRow("data")) != null) {
                            imagePath = msg.getString("FILE_URL");
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.e("NewGroupActivity", "upload img  fail =" + e);
            } finally {
                if (null != upload) {
                    upload.close();
                }
            }

            if (TextUtils.isEmpty(imagePath)) {
                return false;
            }

            Response createGroupInfo = null;
            try {
                createGroupInfo = OkHttpUtils.post().url(ApiConstants.CHAT_CREATE_GROUP_INFO)
                        .addParams("code", groupId)
                        .addParams("_t", instance.getAiSouUserBean().getLoginToken())
                        .addParams("_ac", instance.getAiSouLocationBean().getCurrentAddressCode())
                        .addParams("_cc", instance.getAiSouLocationBean().getCurrentCityCode())
                        .addParams("avatar", imagePath)
                        .build().execute();

                if (createGroupInfo.isSuccessful()) {

                    final String string = createGroupInfo.body().string();
                    Log.e("NewGroupActivity", "result: " + string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != createGroupInfo) {
                    createGroupInfo.close();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 选择照片
     */

    private void initBottomDialogData() {
        String path = AiSouAppInfoModel.getInstance().getUID() + File.separator + "group";
        if (FileUtils.sdCardExists()) {
            File externalCacheDir = this.getExternalCacheDir();
            if (null != externalCacheDir)
                mImageParentPath = new File(externalCacheDir.getAbsolutePath() + File.separator + path);
        } else {
            mImageParentPath = new File(getCacheDir().getAbsolutePath(), path);
        }
        mBottomDialog = new ListViewDialog(mContext);
        mBottomDialog.setInitData(getString(net.twoant.master.R.string.merchant_dialog_cancel), getResources().getStringArray(net.twoant.master.R.array.bottom_dialog));
        mBottomDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                File file;
                if (null != mImageParentPath) {
                    file = new File(mImageParentPath, System.currentTimeMillis() + ".jpg");
                    if (!file.getParentFile().exists())
                        if (!file.getParentFile().mkdirs()) {
                            ToastUtil.showShort(net.twoant.master.R.string.merchant_dialog_folder_create_fail);
                            return;
                        }
                } else {
                    ToastUtil.showShort(net.twoant.master.R.string.merchant_dialog_folder_create_fail);
                    return;
                }

                configCompress(150 * 1024, mScreenWidth, (int) (mScreenWidth / 5.0F * 3 + .5F), true);
                if (position == 0) {
                    startGetPhoto(true, 1, file, mScreenWidth, (int) (mScreenWidth / 5.0F * 3 + .5F));
                } else {
                    startGetPhoto(false, 1, file, mScreenWidth, (int) (mScreenWidth / 5.0F * 3 + .5F));
                }
            }
        });
    }
}
