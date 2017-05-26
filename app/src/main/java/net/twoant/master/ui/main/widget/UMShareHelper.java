package net.twoant.master.ui.main.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.ShareBoardConfig;

/**
 * Created by S_Y_H on 2016/12/15.
 * 友盟分享帮助类
 */

public class UMShareHelper {

    private ShareAction mShareAction;
    private CustomShareListener mCustomShareListener;
    private ShareBoardConfig mShareBoardConfig;

    public UMShareHelper(Activity activity) {
        mCustomShareListener = new CustomShareListener();
        init(activity);
    }

    private void init(Activity activity) {
        mShareAction = new ShareAction(activity).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(mCustomShareListener).withText("蚂蚁---分享");
    }

    /**
     * 在底部显示弹窗
     *
     * @param title             标题
     * @param titleIsVisibility 标题是否可见， 如果为 true 标题传 null 即可
     */
    public void showDialogAtBottom(String title, boolean titleIsVisibility) {
        if (mShareBoardConfig == null)
            mShareBoardConfig = new ShareBoardConfig();
        mShareBoardConfig.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        mShareBoardConfig.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        mShareBoardConfig.setCancelButtonVisibility(false);
        mShareBoardConfig.setTitleVisibility(titleIsVisibility);
        mShareBoardConfig.setIndicatorColor(Color.WHITE, Color.WHITE);
        mShareBoardConfig.setShareboardBackgroundColor(Color.WHITE);
        if (titleIsVisibility)
            mShareBoardConfig.setTitleText(title);
        mShareAction.open(mShareBoardConfig);
    }

    /**
     * 居中显示弹窗
     *
     * @param title           标题
     * @param btnIsVisibility 底部btn是否可见
     */
    public void showDialogAtCenter(String title, boolean btnIsVisibility) {
        if (mShareBoardConfig == null)
            mShareBoardConfig = new ShareBoardConfig();
        mShareBoardConfig.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
        mShareBoardConfig.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        mShareBoardConfig.setCancelButtonVisibility(btnIsVisibility);
        mShareBoardConfig.setTitleText(title);
        mShareBoardConfig.setShareboardBackgroundColor(Color.WHITE);
        mShareBoardConfig.setIndicatorColor(Color.WHITE, Color.WHITE);
        mShareAction.open(mShareBoardConfig);
    }

    /**
     * 设置各种内容
     * 要在showDialog方法之前调用
     */
    public UMShareHelper setText(String text, String title, String url) {
        if (text != null)
            mShareAction.withText(text);
        if (title != null)
            mShareAction.withTitle(title);
        if (url != null)
            mShareAction.withTargetUrl(url);
        return this;
    }


    /**
     * 在对应的activity 生命周期中调用
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(AiSouAppInfoModel.getAppContext()).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 在对应的activity 生命周期中调用
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    public void onConfigurationChanged() {
        mShareAction.close();
    }

    private static class CustomShareListener implements UMShareListener {

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                ToastUtil.showShort("收藏成功啦");
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST
                        && platform != SHARE_MEDIA.LINKEDIN
                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    ToastUtil.showShort("分享成功啦");
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST
                    && platform != SHARE_MEDIA.LINKEDIN
                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                ToastUtil.showShort("分享失败啦");
                if (t != null) {
                    LogUtils.e("分享失败,throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showShort("分享取消了");
        }
    }

}
