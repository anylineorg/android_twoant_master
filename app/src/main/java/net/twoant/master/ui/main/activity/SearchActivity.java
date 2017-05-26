package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.ui.main.adapter.SectionsPagerAdapter;
import net.twoant.master.ui.main.fragment.ActivityFragment;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.HomePageSearchHelper;
import net.twoant.master.ui.main.widget.search_toolbar.HomeSearchToolbarSetter;
import net.twoant.master.ui.main.widget.search_toolbar.IOnSearchListener;
import net.twoant.master.ui.main.widget.search_toolbar.SearchToolbar;

/**
 * Created by S_Y_H on 2017/1/3.
 * 搜索页
 */
public class SearchActivity extends BaseActivity implements ViewPager.OnPageChangeListener, IOnSearchListener, ActivityFragment.IOnTypeAndCategoryChangeListener {

    private final static String ACTION_START = "SearchActivity_action_start";
    private final static String EXTRA_KEYWORD = "SearchActivity_extra_keyword";
    private final static String EXTRA_TYPE = "SearchActivity_extra_type";
    private int mType;
    private String mKeyword;
    private SearchToolbar mSearchToolbar;
    private TabLayout mTabLayout;
    private ActivityFragment[] mSearchFragments;
    private ActivityFragment mCurrentSelectFragments;

    public static void startActivity(Activity activity, String type, String keyword) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_KEYWORD, keyword);
        intent.putExtra(EXTRA_TYPE, type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.yh_activity_search;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mType", mType);
        outState.putString("mKey", mKeyword);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mType = savedInstanceState.getInt("mType");
        mKeyword = savedInstanceState.getString("mKey");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        BaseConfig.checkState(intent, ACTION_START);
        mKeyword = intent.getStringExtra(EXTRA_KEYWORD);
        mType = getTypeFromName(intent.getStringExtra(EXTRA_TYPE));

    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mKeyword = intent.getStringExtra(EXTRA_KEYWORD);
        mType = getTypeFromName(intent.getStringExtra(EXTRA_TYPE));
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        mSearchFragments = getParameter(mType, mKeyword);
        mCurrentSelectFragments = mSearchFragments[0];
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), mSearchFragments, getTitle(mType)));
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(this);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);
        initToolbarData();


        findViewById(R.id.btn_back_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCurrentSelectFragments) {
                    mCurrentSelectFragments.scrollToPosition(0, false);
                }
            }
        });
    }

    private void initToolbarData() {
        mSearchToolbar = (SearchToolbar) findViewById(R.id.st_tool_bar);
        HomePageSearchHelper homePageSearchHelper = new HomePageSearchHelper();
        homePageSearchHelper.setOnSearchListener(this);
        mSearchToolbar.setSearchHelperBase(new HomeSearchToolbarSetter(this), homePageSearchHelper);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mSearchToolbar) {
            mSearchToolbar.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSearchToolbar) {
            mSearchToolbar.onDestroy();
        }
    }

    @Override
    public void onChangeListener(int type, int category, @NonNull String keyword) {
        if (mType == type && mKeyword != null && mKeyword.equals(keyword))
            return;
        this.mType = type;
        this.mKeyword = keyword;
        refresh();
    }

    /**
     * 刷新 tab 、ViewPager
     */
    private void refresh() {
        for (int i = 0; i < mSearchFragments.length; ++i) {
            mSearchFragments[i].replaceTypeAndCategory(mType, getCategoryFromType(mType)[i], mKeyword);
        }
        String[] text = getTitle(mType);
        for (int i = 0, count = mTabLayout.getTabCount(); i < count; ++i) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null && text != null && text.length > i)
                tab.setText(text[i]);
        }
    }

    @Override
    public void onSearchListener(String name, String keyword) {
        this.mType = getTypeFromName(name);
        this.mKeyword = keyword;
        refresh();
    }

    /**
     * 获取 控制器类型
     *
     * @param name 分类名
     * @return 控制器类型
     */
    private int getTypeFromName(String name) {
        switch (name) {
            case HomePageSearchHelper.GOODS:
                return IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW;

            case HomePageSearchHelper.ACTIVITY:
                return IRecyclerViewConstant.TYPE_ACTIVITY;

            case HomePageSearchHelper.MERCHANT:
                return IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW;

            case HomePageSearchHelper.INFORMATION:
                return IRecyclerViewConstant.TYPE_MESSAGE;

            case HomePageSearchHelper.FRIEND:
                return IRecyclerViewConstant.TYPE_FRIEND;

            default:
                return -1;
        }
    }

    /**
     * @param type 控制器类型
     * @return 分类类型
     */
    private int[] getCategoryFromType(int type) {
        switch (type) {
            case IRecyclerViewConstant.TYPE_ACTIVITY:
                return new int[]{
                        IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEARBY
                        , IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEW
                        , IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_FAVOURITE};

            case IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW:
                return new int[]{
                        IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_NEARBY,
                        IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_COMMENT,
                        IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_SALES};
            case IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW:
                return new int[]{
                        IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY,
                        IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW,
                        IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE};

            case IRecyclerViewConstant.TYPE_MESSAGE:
                return new int[]{
                        IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_NEW,
                        IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_FOLLOW,
                        IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_COMMENT};

            case IRecyclerViewConstant.TYPE_FRIEND:
                return new int[]{
                        IRecyclerViewConstant.CATEGORY_FRIEND_SEARCH_CONTACTS,
                        IRecyclerViewConstant.CATEGORY_FRIEND_SEARCH_GROUP,
                        IRecyclerViewConstant.CATEGORY_FRIEND_SEARCH_CHAT_HISTORY};

            default:
                throw new IllegalArgumentException("无法找到 该 type");
        }
    }

    /**
     * 获取对应的Fragment
     *
     * @param type    类型
     * @param keyword 关键字
     * @return 三个类型的 Fragment[]
     */
    private ActivityFragment[] getParameter(int type, String keyword) {

        switch (type) {
            case IRecyclerViewConstant.TYPE_ACTIVITY:
                return new ActivityFragment[]{
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEARBY, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEW, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_FAVOURITE, keyword)};
            case IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW:
                return new ActivityFragment[]{
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW, IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_NEARBY, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW, IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_COMMENT, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW, IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_SALES, keyword)};
            case IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW:
                return new ActivityFragment[]{
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW, IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW, IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW, IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE, keyword)};

            case IRecyclerViewConstant.TYPE_MESSAGE:
                return new ActivityFragment[]{ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MESSAGE, IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_NEW, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MESSAGE, IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_FOLLOW, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_MESSAGE, IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_COMMENT, keyword)};

            case IRecyclerViewConstant.TYPE_FRIEND:
                return new ActivityFragment[]{
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_FRIEND, IRecyclerViewConstant.CATEGORY_FRIEND_SEARCH_CONTACTS, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_FRIEND, IRecyclerViewConstant.CATEGORY_FRIEND_SEARCH_GROUP, keyword),
                        ActivityFragment.newInstance(IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_FRIEND, IRecyclerViewConstant.CATEGORY_FRIEND_SEARCH_CHAT_HISTORY, keyword)};

            default:
                throw new IllegalArgumentException("无法找到 该 type");
        }
    }

    private String[] getTitle(int type) {

        switch (type) {
            case IRecyclerViewConstant.TYPE_ACTIVITY:
                return new String[]{getString(R.string.search_nearby),
                        getString(R.string.search_new_state),
                        getString(R.string.search_hot)};

            case IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW:
                return new String[]{getString(R.string.search_nearby),
                        getString(R.string.search_bargain),
                        getString(R.string.search_browse)};

            case IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW:
                return new String[]{getString(R.string.search_nearby),
                        getString(R.string.search_new_state),
                        getString(R.string.search_hot)};

            case IRecyclerViewConstant.TYPE_MESSAGE:
                return new String[]{getString(R.string.search_new_state),
                        getString(R.string.search_hot),
                        getString(R.string.search_comment)};

            case IRecyclerViewConstant.TYPE_FRIEND:
                return new String[]{getString(R.string.search_contact),
                        getString(R.string.search_group),
                        getString(R.string.search_history)};
            default:
                throw new IllegalArgumentException("无法找到 该 type");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentSelectFragments = mSearchFragments[position];
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (ViewPager.SCROLL_STATE_DRAGGING == state && null != mSearchToolbar) {
            mSearchToolbar.cleanFocus();
        }
    }

}
