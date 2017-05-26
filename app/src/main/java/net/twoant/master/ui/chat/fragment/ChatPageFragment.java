package net.twoant.master.ui.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.domain.EaseUser;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.FragmentViewPagerAdapter;
import net.twoant.master.base_app.HomeBaseFragment;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.ui.chat.activity.AddContactActivity;
import net.twoant.master.ui.chat.activity.ChatSettingsActivity;
import net.twoant.master.ui.chat.activity.NewGroupActivity;
import net.twoant.master.ui.chat.activity.PublicGroupsActivity;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.my_center.fragment.EditDataActivity;
import net.twoant.master.widget.entry.DataRow;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/18.
 * 首页的 聊天中心 fragment
 */

public class ChatPageFragment extends HomeBaseFragment implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack {
    private final static int CODE_MODIFY_INFO = 1;

    private final Fragment[] fFragments = new Fragment[]{EaseConversationListFragment.newInstance()
            , EaseContactListFragment.newInstance(), DynamicFragment.newInstance()};
    private TabLayout mTabLayout;
    private ArrayList<Integer> mHasMessPosition = new ArrayList<>(3);
    /**
     * 头像
     */
    private CircleImageView mImageView;
    private ViewPager mChatViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void refreshList(int position) {
        switch (position) {
            case 0:
                Fragment conversationListFragment = fFragments[position];
                if (conversationListFragment != null && conversationListFragment.isVisible() && conversationListFragment instanceof EaseConversationListFragment) {
                    ((EaseConversationListFragment) conversationListFragment).refresh();
                }
            case 1:
                Fragment fragment = fFragments[position];
                if (fragment != null && fragment instanceof EaseContactListFragment) {
                    ((EaseContactListFragment) fragment).refresh();
                }
        }
    }

    /**
     * 切换显示页
     */
    public void switchPage(int position) {
        if (isVisibility && null != mChatViewPager) {
            int childCount = mChatViewPager.getChildCount();
            if (0 <= position && position < childCount && position != mChatViewPager.getCurrentItem()) {
                mChatViewPager.setCurrentItem(position);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.yh_menu_chat_home, menu);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.yh_fragment_chat;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initToolbar(view);
        initTabData(view);
        initHeaderData(view);
    }

    @Override
    public void onResponse(String response, int id) {
        DataRow dataRow = DataRow.parseJson(response);
        dataRow=dataRow.getRow("data");
        if (dataRow==null){
            dataRow=DataRow.parseJson(response).getRow("DATA");
        }
        if (dataRow != null) {

            String imageUrl = BaseConfig.getCorrectImageUrl(dataRow.getStringDef("IMG_FILE_PATH", ""));
            if (mImageView != null)
                ImageLoader.getImageFromNetwork(mImageView
                        , imageUrl
                        , mImageView.getContext()
                        , R.drawable.ic_def_circle);
            final EaseUser easeUser = new EaseUser(dataRow.getString("CODE"));
            easeUser.setNickname(dataRow.getStringDef("NM", ""));
            easeUser.setAvatar(imageUrl);
            easeUser.setAvatarMd5(MD5Util.getMD5ToHex(imageUrl));
            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    ChatHelper.getUserProfileManager().updateCurrentUserInfo(easeUser);
                }
            });
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    /**
     * 初始化头像数据
     */
    private void initHeaderData(View view) {
        mImageView = (CircleImageView) view.findViewById(R.id.iv_header_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EditDataActivity.class);
                ChatPageFragment.this.startActivityForResult(intent, CODE_MODIFY_INFO);
            }
        });

        String avatar = ChatHelper.getUserProfileManager().getCurrentUserInfo().getAvatar();

        File file;
        if (TextUtils.isEmpty(avatar) || !(file = new File(avatar)).exists() || 0 >= file.length()) {
            initUserInfo();
        } else {

            try {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (null != bitmap) {
                    mImageView.setImageBitmap(bitmap);
                } else {
                    ImageLoader.getImageFromLocation(mImageView
                            , file
                            , mActivity
                            , R.drawable.ic_def_circle);
                }
            } catch (OutOfMemoryError error) {
                LogUtils.e("ChatPageFragment loading header img oom");
                ImageLoader.getImageFromLocation(mImageView
                        , file
                        , mActivity
                        , R.drawable.ic_def_circle);
            }


        }
    }

    private void initUserInfo() {
        UserInfoUtil.getUserInfo(AiSouAppInfoModel.getInstance().getToken(), this);
    }


    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CODE_MODIFY_INFO == requestCode && Activity.RESULT_OK == resultCode) {
            initUserInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化 toolbar 数据
     */
    private void initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    //创建群
                    case R.id.create_group:
                        startActivity(new Intent(ChatPageFragment.this.mContext, NewGroupActivity.class));
                        return true;
                    //加好友
                    case R.id.add_friends:
                        startActivity(new Intent(ChatPageFragment.this.mContext, AddContactActivity.class));
                        return true;
                    //加群
                    case R.id.add_group:
                        startActivity(new Intent(ChatPageFragment.this.mContext, PublicGroupsActivity.class));
                        return true;
                    //设置
                    case R.id.setting:
                        startActivity(new Intent(ChatPageFragment.this.mContext, ChatSettingsActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * 更新当前未读的消息数量
     */
    private void updateUnreadLabel() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity) {
            ((MainActivity) activity).updateUnreadLabel(getUnreadMsgCountTotal());
        }
    }

    /**
     * 获取未读消息数量
     */
    private int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal;
        int chatRoomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatRoomUnreadMsgCount = chatRoomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatRoomUnreadMsgCount;
    }

    /**
     * 初始化 tab 数据
     */
    private void initTabData(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.chat_title_tab_layout);
        mChatViewPager = (ViewPager) view.findViewById(R.id.chat_view_pager);
        FragmentViewPagerAdapter viewPagerAdapter = new FragmentViewPagerAdapter(getChildFragmentManager(), null);
        viewPagerAdapter.setFragmentList(fFragments);
        mChatViewPager.setAdapter(viewPagerAdapter);
        mChatViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mChatViewPager, true);
        mTabLayout.removeAllTabs();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mTabLayout.addTab(getTab(mTabLayout, "消息", false), true);
        mTabLayout.addTab(getTab(mTabLayout, "联系人", true));
        mTabLayout.addTab(getTab(mTabLayout, "动态", true));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            private int mPrimaryColor = ContextCompat.getColor(ChatPageFragment.this.mContext, R.color.colorPrimary);
            private int mWhiteColor = ContextCompat.getColor(ChatPageFragment.this.mContext, R.color.whiteTextColor);

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MainActivity.closeIME(false, mView);
                changeTabState(tab, true);
                changeHintState(tab.getPosition(), tab, true);
            }

            /**
             *
             * @param tab 当前tab
             * @param isSelect 是否是选择 为选中状态
             */
            private void changeTabState(TabLayout.Tab tab, boolean isSelect) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    ((AppCompatTextView) customView.findViewById(R.id.tv_tab_name)).setTextColor(isSelect ? mPrimaryColor : mWhiteColor);
                    customView.setEnabled(!isSelect);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabState(tab, false);
                changeHintState(tab.getPosition(), tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                changeHintState(tab.getPosition(), tab, true);

            }
        });
    }


    /**
     * @param tabLayout tab 布局
     * @param title     显示的标题
     * @return tab 实例
     */
    @NonNull
    private TabLayout.Tab getTab(TabLayout tabLayout, String title, boolean enable) {
        TabLayout.Tab tab = tabLayout.newTab().setCustomView(R.layout.yh_tab_chat);
        final View customView = tab.getCustomView();
        if (customView != null) {
            customView.setEnabled(enable);
            AppCompatTextView textView = (AppCompatTextView) customView.findViewById(R.id.tv_tab_name);
            textView.setText(title);
            textView.setTextColor(enable ? ContextCompat.getColor(ChatPageFragment.this.mContext, R.color.whiteTextColor)
                    : ContextCompat.getColor(ChatPageFragment.this.mContext, R.color.colorPrimary));
        }
        return tab;
    }

    /**
     * 显示提示 点点
     *
     * @param position 那个位置上显示
     * @param isShow   是否显示
     */
    public void setHintPoint(int position, boolean isShow) {
        if (mTabLayout != null) {
            final TabLayout.Tab tabAt = mTabLayout.getTabAt(position);
            if (isShow) {
                if (!mHasMessPosition.contains(position))
                    mHasMessPosition.add(position);
            } else {
                mHasMessPosition.remove(Integer.valueOf(position));
            }
            changeHintState(position, tabAt, true);
        }
    }

    /**
     * 切换 提示的点点 状态
     *
     * @param isSelect true 判断tabLayout选中的当前位置 是否与 position 相同，false 不判断
     */
    private void changeHintState(int position, TabLayout.Tab tabAt, boolean isSelect) {
        View customView;
        if (mTabLayout != null && tabAt != null && (customView = tabAt.getCustomView()) != null
                && (customView = (customView.findViewById(R.id.cb_mess_hint))) instanceof AppCompatCheckBox) {


            if (mHasMessPosition.contains(position)) {

                if (customView.getVisibility() != View.VISIBLE) {
                    customView.setVisibility(View.VISIBLE);
                }
                boolean b = isSelect && mTabLayout.getSelectedTabPosition() == position;
                if (b != ((AppCompatCheckBox) customView).isChecked()) {
                    ((AppCompatCheckBox) customView).setChecked(b);
                }

            } else {
                if (customView.getVisibility() != View.GONE) {
                    customView.setVisibility(View.GONE);
                }
            }
        }
    }
}
