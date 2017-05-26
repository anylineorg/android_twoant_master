package net.twoant.master.ui.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.ToastUtil;
import com.hyphenate.easeui.model.GroupInfoBean;
import net.twoant.master.ui.chat.adapter.GroupsAdapter;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/2/23.
 * 用户已添加群组列表
 */
public class GroupsActivity extends ChatBaseActivity implements View.OnClickListener, Handler.Callback, SwipeRefreshLayout.OnRefreshListener {

    private final static int RESULT_CODE = 0xA;
    /**
     * 刷新
     */
    private final static int ID_REFRESH = 1;
    /**
     * 获取群列表失败
     */
    private final static int ID_FAIL = 2;

    private final static int GET_GROUP_INFO_SUCCESSFUL = 3;

    private InputMethodManager inputMethodManager;
    private SwipeRefreshLayout mSwipeLayout;
    protected List<EMGroup> mGroupList;
    private GroupsAdapter mGroupsAdapter;
    private Handler mHandler;
    //搜索 et
    private AppCompatEditText mEtQuery;
    //清除按钮
    private AppCompatImageButton mCleanInput;


    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_groups;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        mHandler = new Handler(this);
        initView();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        onRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_CODE == requestCode) {
            //进行刷新
            onRefresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        mSwipeLayout.setRefreshing(false);
        switch (msg.what) {

            case ID_REFRESH:
                if (msg.obj instanceof List) {
                    mGroupsAdapter.setDataBean((List<EMGroup>) msg.obj);
                    getGroupInfo((List<EMGroup>) msg.obj);
                }
                break;

            case ID_FAIL:
                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                }
                break;

            case GET_GROUP_INFO_SUCCESSFUL:
                if (msg.obj instanceof ArrayMap) {
                    mGroupsAdapter.setGroupInfoBean((ArrayMap<String, GroupInfoBean>) msg.obj);
                }
                break;
        }
        return true;
    }

    private void getGroupInfo(final List<EMGroup> groups) {
        if (null == groups || groups.isEmpty()) {
            return;
        }
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ArrayMap<String, GroupInfoBean> groupInfo = UserInfoDiskHelper.getInstance().getSpecifyGroupInfo(false, groups, null);
                if (null != groupInfo && !groupInfo.isEmpty() && null != mHandler) {
                    Message message = mHandler.obtainMessage();
                    message.obj = groupInfo;
                    message.what = GET_GROUP_INFO_SUCCESSFUL;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EMGroup> groupsFromServer = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = ID_REFRESH;
                        message.obj = groupsFromServer;
                        mHandler.sendMessage(message);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = ID_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "获取群组列表失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        })) {
            mSwipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_search_clear:
                mEtQuery.getText().clear();
                break;
            case net.twoant.master.R.id.ll_group:
                v.setClickable(false);
                Object tag = v.getTag();
                if (tag instanceof String) {
                    ChatActivity.startGroupActivityForResult(GroupsActivity.this, RESULT_CODE, (String) tag);
                }
                v.setClickable(true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        initSimpleToolbarData(this, getString(net.twoant.master.R.string.group_chat), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsActivity.this.finish();
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.twoant.master.R.id.create) {
                    startActivity(new Intent(GroupsActivity.this.mContext, NewGroupActivity.class));

                    return true;
                }
                return false;
            }
        });

        mEtQuery = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_query);
        mEtQuery.setHint("输入“群号/群名”查询当前列表");
        mEtQuery.addTextChangedListener(new TextWatcher() {
            private ArrayFilter mArrayFilter = new ArrayFilter();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 < s.length()) {
                    if (View.VISIBLE != mCleanInput.getVisibility()) {
                        mCleanInput.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (View.GONE != mCleanInput.getVisibility()) {
                        mCleanInput.setVisibility(View.GONE);
                    }
                }
                mArrayFilter.filter(s);
            }
        });
        mCleanInput = (AppCompatImageButton) findViewById(net.twoant.master.R.id.btn_search_clear);
        mCleanInput.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                        if (getCurrentFocus() != null)
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_1));
        mSwipeLayout = (SwipeRefreshLayout) findViewById(net.twoant.master.R.id.swipe_layout);
        mSwipeLayout.setColorSchemeResources(net.twoant.master.R.color.common_top_bar_primary);
        mSwipeLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupsAdapter = new GroupsAdapter(this, null, this);
        recyclerView.setAdapter(mGroupsAdapter);
    }

    private class ArrayFilter extends Filter {

        private final Object mLock = new Object();
        private ArrayList<EMGroup> mOriginalValues;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mGroupList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<EMGroup> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<EMGroup> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<EMGroup> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final EMGroup value = values.get(i);
                    if (null == value) {
                        continue;
                    }

                    if (value.getGroupName().toLowerCase().contains(prefixString)
                            || value.getGroupId().toLowerCase().contains(prefixString)) {
                        newValues.add(value);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mGroupList = (List<EMGroup>) results.values;
            if (mGroupsAdapter != null) {
                mGroupsAdapter.setDataBean(mGroupList);
            }
        }
    }
}
