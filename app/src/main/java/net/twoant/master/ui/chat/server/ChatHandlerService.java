package net.twoant.master.ui.chat.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.ui.chat.Model.LoginUserBean;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;

/**
 * Created by S_Y_H on 2017/3/2.
 * 处理环信相关事宜的服务
 */

public class ChatHandlerService extends Service {

    /**
     * 登录环信
     */
    private final static String ACTION_LOGIN = "chs_al";
    private final static String EXTRA_LOGIN = "chs_el";
    private final static String TAG = "ChatHandlerService";

    public static void startService(Context context, LoginUserBean loginUserBean) {
        Intent intent = new Intent(context, ChatHandlerService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(EXTRA_LOGIN, loginUserBean);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_LOGIN:
                    startLoginChat((LoginUserBean) intent.getParcelableExtra(EXTRA_LOGIN));
                    return Service.START_REDELIVER_INTENT;
            }
        } else {
            LogUtils.d(TAG, "startService: intent is null ");
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void startLoginChat(final LoginUserBean loginBean) {
        final AiSouAppInfoModel aiSouAppInfoModel = AiSouAppInfoModel.getInstance();
        if (null != loginBean && null != aiSouAppInfoModel) {
            if (!aiSouAppInfoModel.isChatLogin()) {
                EMClient instance = EMClient.getInstance();

                if (null != instance) {
                    try{

                        instance.logout(true);
                    }catch (Exception e){

                    }
                    instance.login(loginBean.getUid(), loginBean.getPassword(), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            LogUtils.e(TAG + "login successful");

                            EaseUser easeUser = new EaseUser(loginBean.getUid());
                            easeUser.setAvatar(loginBean.getAvatar());
                            easeUser.setNickname(loginBean.getNickName());
                            easeUser.setAvatarMd5(MD5Util.getMD5ToHex(loginBean.getAvatar()));
                            ChatHelper.getUserProfileManager().updateCurrentUserInfo(easeUser);

                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            AiSouAppInfoModel appInfoModel = AiSouAppInfoModel.getInstance();
                            if (null != appInfoModel) {
                                appInfoModel.setChatLogin(true);
                                appInfoModel.setChatLoginDescription(null);
                            }
                            ChatHandlerService.this.stopSelf();
                        }

                        @Override
                        public void onError(int i, String s) {
                            LogUtils.e(TAG + "login onError");
                            AiSouAppInfoModel appInfoModel = AiSouAppInfoModel.getInstance();
                            if (null != appInfoModel) {
                                appInfoModel.setChatLogin(false);
                                appInfoModel.setChatLoginDescription(UserInfoUtil.getErrorDescription(i, "登录失败"));
                            }
                            ChatHandlerService.this.stopSelf();
                        }

                        @Override
                        public void onProgress(int i, String s) {
                        }
                    });
                }
            }

        }
    }
}
