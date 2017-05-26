package net.twoant.master.ui.chat.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.ToastUtil;
import com.hyphenate.easeui.model.GroupInfoBean;
import net.twoant.master.ui.chat.adapter.GroupListAdapter;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/2/25
 * 加入公共群聊
 */
public class PublicGroupsActivity extends ChatBaseActivity implements View.OnClickListener, BaseRecyclerAdapter.IRefreshData, Handler.Callback {

    /**
     * 群组信息集合
     */
    private final static String EXTRA_GROUP_INFO_LIST = "E_G_I_L";

    /**
     * 每页的数量
     */
    private final static int PAGE_SIZE = 20;
    /**
     * 获取数据成功
     */
    private final static int CODE_SUCCESS = 1;
    /**
     * 获取数据失败
     */
    private final static int CODE_FAIL = 2;
    /**
     * 每有更多
     */
    private final static int CODE_NO_MORE = 3;

    /**
     * 搜索 群组 成功
     */
    private final static int CODE_SEARCH_SUCCESS = 4;

    /**
     * 搜索 群组失败
     */
    private final static int CODE_SEARCH_FAIL = 5;
    /**
     * 搜索群组 不存在
     */
    private final static int CODE_SEARCH_NOT_EXIST = 6;
    /**
     * 获取群信息成功
     */
    private static final int CODE_GET_GROUP_INFO_SUCCESSFUL = 7;

    /**
     * 过滤器
     */
    private final GroupsFilter fGroupFilter = new GroupsFilter();

    private GroupListAdapter mGroupListAdapter;
    private AppCompatEditText mEtQuery;
    private AppCompatImageButton mBtnSearchClear;
    private Handler mHandler;
    private String mCursor;//游标
    private boolean isLoadingFinish = false;//是否加载完成
    /**
     * 搜索弹窗
     */
    private AlertDialog mSearchAlertDialog;
    /**
     * 搜索的群号
     */
    private AppCompatEditText mSearchKey;
    /**
     * 搜索限制，防止点击多次
     */
    private boolean mLimitCount;
    private ContentLoadingProgressBar mProgressBar;
    private LinearLayoutCompat mGroupParentLayout;


    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_public_groups;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        mHandler = new Handler(this);
        initView();
    }


    /**
     * 初始化 View
     */
    private void initView() {
        initSimpleToolbarData(this, getString(net.twoant.master.R.string.Open_group_chat), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicGroupsActivity.this.finish();
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.twoant.master.R.id.find) {
                    initSearchDialog();
                }
                return false;
            }
        });
        this.mEtQuery = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_query);
        mEtQuery.setHint("输入“群号/群名”查询当前列表");
        this.mBtnSearchClear = (AppCompatImageButton) findViewById(net.twoant.master.R.id.btn_search_clear);
        mBtnSearchClear.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mGroupListAdapter = new GroupListAdapter(this, this));
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_1));
        mEtQuery.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (View.VISIBLE != mBtnSearchClear.getVisibility()) {
                        mBtnSearchClear.setVisibility(View.VISIBLE);
                    }
                } else if (View.GONE != mBtnSearchClear.getVisibility()) {
                    mBtnSearchClear.setVisibility(View.GONE);
                }
                fGroupFilter.filter(s);
            }
        });
    }

    @Override
    public void getData() {
        loadingData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mGroupListAdapter && mGroupListAdapter.getDataList().size() > 1) {
            mGroupListAdapter.refreshData();
        }
    }

    @Override
    public boolean canLoadingData() {
        return !isLoadingFinish;
    }

    @Override
    public void refreshData() {
        isLoadingFinish = false;
        mCursor = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_find, menu);
        MenuItem item = menu.findItem(net.twoant.master.R.id.find);
        if (null != item) {
            item.setTitle("搜索");
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_SUCCESS:
                if (msg.obj instanceof List) {
                    getGroupInfo((List<EMGroupInfo>) msg.obj);
                    mGroupListAdapter.setDataBean((List<EMGroupInfo>) msg.obj, mGroupListAdapter.getDataList().size() == 1);
                }
                break;

            case CODE_NO_MORE:
                isLoadingFinish = false;
                mGroupListAdapter.showErrorLoadingHint("没有更多");
                break;
            case CODE_FAIL:
                if (msg.obj instanceof String) {
                    mGroupListAdapter.showErrorLoadingHint((String) msg.obj);
                }
                break;
            case CODE_SEARCH_SUCCESS:
                mProgressBar.hide();
                mSearchKey.getText().clear();
                mGroupParentLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                mLimitCount = false;
                if (msg.obj instanceof EMGroupInfo) {
                    mGroupListAdapter.setDataBean(false, (EMGroupInfo) msg.obj);
                    checkAndAdd((EMGroupInfo) msg.obj);
                    if (null != mSearchAlertDialog && mSearchAlertDialog.isShowing()) {
                        mSearchAlertDialog.dismiss();
                    }
                }
                break;
            case CODE_SEARCH_FAIL:
                mProgressBar.hide();
                if (View.GONE != mProgressBar.getVisibility()) {
                    mProgressBar.setVisibility(View.GONE);
                }
                mGroupParentLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                mLimitCount = false;
                ToastUtil.showShort(getResources().getString(net.twoant.master.R.string.group_search_failed) + " : " + getString(net.twoant.master.R.string.connect_failuer_toast));
                break;
            case CODE_SEARCH_NOT_EXIST:
                mProgressBar.hide();
                mSearchKey.getText().clear();
                if (View.GONE != mProgressBar.getVisibility()) {
                    mProgressBar.setVisibility(View.GONE);
                }
                mGroupParentLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                mLimitCount = false;
                ToastUtil.showShort(getResources().getString(net.twoant.master.R.string.group_not_existed));
                break;

            case CODE_GET_GROUP_INFO_SUCCESSFUL:
                if (msg.obj instanceof ArrayMap) {
                    mGroupListAdapter.setGroupInfo((ArrayMap<String, GroupInfoBean>) msg.obj);
                }
                break;
        }
        return true;
    }

    private void getGroupInfo(final List<EMGroupInfo> info) {
        if (null == info || info.isEmpty()) {
            return;
        }

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ArrayMap<String, GroupInfoBean> groupInfo = UserInfoDiskHelper.getInstance().getSpecifyGroupInfo(info, false, null);
                if (null != groupInfo && !groupInfo.isEmpty() && null != mHandler) {
                    Message message = mHandler.obtainMessage();
                    message.what = CODE_GET_GROUP_INFO_SUCCESSFUL;
                    message.obj = groupInfo;
                    mHandler.sendMessage(message);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mSearchAlertDialog && mSearchAlertDialog.isShowing()) {
            mSearchAlertDialog.dismiss();
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_search_clear:
                mEtQuery.getText().clear();
                break;
            case net.twoant.master.R.id.btn_cancel:
                if (mSearchAlertDialog.isShowing()) {
                    MainActivity.closeIME(false, mSearchKey);
                    mSearchAlertDialog.dismiss();
                }
                mLimitCount = false;
                break;
            case net.twoant.master.R.id.btn_search:
                searchGroup();
                break;
        }
    }


    /**
     * 初始化搜索弹窗
     */
    @SuppressLint("InflateParams")
    private void initSearchDialog() {
        if (null == mSearchAlertDialog) {
            View inflate = getLayoutInflater().inflate(net.twoant.master.R.layout.yh_dialog_search, null);
            mGroupParentLayout = (LinearLayoutCompat) inflate.findViewById(net.twoant.master.R.id.ll_group);
            mSearchKey = (AppCompatEditText) inflate.findViewById(net.twoant.master.R.id.et_info);
            inflate.findViewById(net.twoant.master.R.id.btn_cancel).setOnClickListener(this);
            mProgressBar = (ContentLoadingProgressBar) inflate.findViewById(net.twoant.master.R.id.pb_progressBar);
            inflate.findViewById(net.twoant.master.R.id.btn_search).setOnClickListener(this);
            mSearchAlertDialog = new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle)
                    .setView(inflate).create();
            mSearchAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    MainActivity.closeIME(true, mSearchKey);
                }
            });

            mSearchAlertDialog.setCanceledOnTouchOutside(false);
            mSearchAlertDialog.setCancelable(true);

            mSearchAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLimitCount = false;
                }
            });

            mSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (null != event && event.getAction() == KeyEvent.KEYCODE_ENTER) {
                        switch (event.getAction()) {
                            case KeyEvent.ACTION_UP:
                                searchGroup();
                                return true;
                            default:
                                return true;
                        }
                    }
                    return false;
                }
            });
        }
        //创建匹配集合
        mEtQuery.setText("");

        if (View.GONE != mProgressBar.getVisibility()) {
            mProgressBar.setVisibility(View.GONE);
        }

        mGroupParentLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mSearchAlertDialog.show();
    }

    /**
     * 检查是否包含指定的信息， 没有就添加到集合中
     *
     * @param em mess
     */
    private void checkAndAdd(EMGroupInfo em) {
        if (null != fGroupFilter.mOriginalValues) {
            final ArrayList<EMGroupInfo> values = fGroupFilter.mOriginalValues;
            boolean isContains = false;
            String groupId = (em).getGroupId();
            for (EMGroupInfo info : values) {
                if (null != info && info.getGroupId().equals(groupId)) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                fGroupFilter.mOriginalValues.add(0, em);
            }
        }
    }

    /**
     * 根据群id 搜索
     */
    private void searchGroup() {
        if (mLimitCount) {
            return;
        }

        if (View.VISIBLE != mProgressBar.getVisibility()) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        mProgressBar.show();

        mLimitCount = true;
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup groupFromServer = EMClient.getInstance().groupManager().getGroupFromServer(mSearchKey.getText().toString());
                    EMGroupInfo groupInfo = new EMGroupInfo(groupFromServer.getGroupId(), groupFromServer.getGroupName());
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = groupInfo;
                        message.what = CODE_SEARCH_SUCCESS;
                        mHandler.sendMessage(message);
                    }
                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        if (e.getErrorCode() == EMError.GROUP_INVALID_ID) {
                            mHandler.sendEmptyMessage(CODE_SEARCH_NOT_EXIST);
                        } else {
                            mHandler.sendEmptyMessage(CODE_SEARCH_FAIL);
                        }
                    }
                }
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadingData() {
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMCursorResult<EMGroupInfo> result = EMClient.getInstance().groupManager().getPublicGroupsFromServer(PAGE_SIZE, mCursor);
                    if (null != result) {
                        List<EMGroupInfo> data = result.getData();
                        if (null != data && !data.isEmpty()) {
                            if (null != mHandler) {
                                Message message = mHandler.obtainMessage();
                                mCursor = result.getCursor();
                                message.what = CODE_SUCCESS;
                                message.obj = data;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            if (null != mHandler)
                                mHandler.sendEmptyMessage(CODE_NO_MORE);
                        }
                    } else {
                        if (null != mHandler)
                            mHandler.sendEmptyMessage(CODE_NO_MORE);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "加载失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    /**
     * 群组列表过滤
     */
    private class GroupsFilter extends Filter {

        private final Object mLock = new Object();
        private ArrayList<EMGroupInfo> mOriginalValues;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final CustomFilterResults results = new CustomFilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(mGroupListAdapter.getDataList());
                    }
                }
            }

            if (TextUtils.isEmpty(prefix)) {
                final ArrayList<EMGroupInfo> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.isShowOriginalValues = true;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<EMGroupInfo> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<EMGroupInfo> newValues = new ArrayList<>();

                for (int i = 0; i < count; ++i) {
                    final EMGroupInfo value = values.get(i);

                    if (value == null) {
                        continue;
                    }

                    if (value.getGroupName().toLowerCase().contains(prefixString)
                            || value.getGroupId().toLowerCase().contains(prefixString)) {
                        newValues.add(value);
                    }
                }
                results.isShowOriginalValues = false;
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;

        }

        private class CustomFilterResults extends FilterResults {
            private boolean isShowOriginalValues;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (null != mGroupListAdapter && results instanceof CustomFilterResults) {
                if (((CustomFilterResults) results).isShowOriginalValues) {
                    mGroupListAdapter.setDataBean(true, (List<EMGroupInfo>) results.values);
                } else {
                    mGroupListAdapter.setDataBean(false, (List<EMGroupInfo>) results.values);
                }
            }

        }

    }

}
