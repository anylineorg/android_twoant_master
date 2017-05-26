package net.twoant.master.ui.main.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.main.adapter.HistorySearchAdapter;
import net.twoant.master.ui.main.server.WriteDataIntentService;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by S_Y_H on 2017/3/15.
 * 用户历史选择弹窗
 */

public final class HistoryPopupWindow implements View.OnClickListener {

    /**
     * 用户历史选择弹窗
     */
    private PopupWindow mHistoryPopupWindow;

    /**
     * 用户弹窗是否 处于 显示 状态
     */
    private boolean isHistoryShowing;

    /**
     * 用户搜索历史数据
     */
    private ArrayList<String> mUserHistoryData;

    private Context mContext;

    private String mFileName;

    /**
     * 历史记录的适配器
     */
    private HistorySearchAdapter mHistorySearchAdapter;

    private AiSouAppInfoModel mAiSouAppInfoModel;

    private EditText mEditText;

    private IOnItemClickListener iOnItemClickListener;

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putStringArrayList("mUHD", mUserHistoryData);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        mUserHistoryData = bundle.getStringArrayList("mUHD");
    }

    public interface IOnItemClickListener {

        void onCleanClickListener(View view);

        void onItemClickListener(View view, String content);
    }

    public void setOnItemClickListener(HistoryPopupWindow.IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    /**
     * 重置数据
     */
    public void reset() {
        if (null != mHistoryPopupWindow && mHistoryPopupWindow.isShowing()) {
            mHistoryPopupWindow.dismiss();
            mHistoryPopupWindow = null;
        }
        cleanState();
        mContext = null;
        mEditText = null;
        mUserHistoryData = null;
        iOnItemClickListener = null;
    }

    public ArrayList<String> getUserHistoryData() {
        return this.mUserHistoryData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //清除 搜索 历史记录
            case net.twoant.master.R.id.btn_clean:
                File file = getHistoryFile(mContext, mFileName, mAiSouAppInfoModel);
                if (file.exists()) {
                    if (file.delete()) {
                        if (null != mUserHistoryData) {
                            mUserHistoryData.clear();
                        }
                        ToastUtil.showShort("清理成功");
                    }
                }

                if (mHistorySearchAdapter != null) {
                    mHistorySearchAdapter.setDataList(null);
                    mHistorySearchAdapter.notifyDataSetChanged();
                }

                if (null != iOnItemClickListener) {
                    iOnItemClickListener.onCleanClickListener(view);
                }
                break;
            //搜索记录 的 item 被点击后回调
            case net.twoant.master.R.id.tv_history_name:
                if (mHistoryPopupWindow != null) {
                    mHistoryPopupWindow.dismiss();
                }
                Object tag = view.getTag();
                if (tag instanceof Integer && null != iOnItemClickListener) {
                    iOnItemClickListener.onItemClickListener(view, mHistorySearchAdapter.getDataList().get((Integer) tag));
                }
                break;
        }
    }

    public HistoryPopupWindow(@Nullable ArrayList<String> historyData, String fileName) {
        this.mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
        this.mContext = AiSouAppInfoModel.getAppContext();
        this.mFileName = fileName;
        initHistoryPopupWindow(historyData);
    }

    public void setEditText(EditText editText) {
        cleanState();
        this.mEditText = editText;
        initEditData();
    }

    public void setUserHistoryData(ArrayList<String> historyData) {
        this.mUserHistoryData = historyData;
    }

    public ArrayList<String> addHistoryData(String temp) {
        if (mUserHistoryData == null) {
            mUserHistoryData = new ArrayList<>(12);
        }

        if (mUserHistoryData.contains(temp)) {
            mUserHistoryData.add(0, mUserHistoryData.remove(mUserHistoryData.indexOf(temp)));
        } else {
            mUserHistoryData.add(0, temp);
        }
        return mUserHistoryData;
    }

    public void saveUserHistoryData() {
        if (null != mUserHistoryData && !mUserHistoryData.isEmpty()) {
            WriteDataIntentService.startWriteHistoryUserData(mContext, mFileName, mUserHistoryData);
        }
    }

    private void cleanState() {
        if (null != mEditText) {
            mEditText.setOnFocusChangeListener(null);
            mEditText.setOnTouchListener(null);
        }
    }

    /**
     * 历史记录
     */
    @SuppressLint("InflateParams")
    private void initHistoryPopupWindow(@Nullable ArrayList<String> userHistoryData) {
        if (mHistoryPopupWindow == null) {
            final View view = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.yh_popup_window_history_list, null);
            view.findViewById(net.twoant.master.R.id.btn_clean).setOnClickListener(this);
            RecyclerView historyRecyclerView = (RecyclerView) view.findViewById(net.twoant.master.R.id.rv_history);
            historyRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.HORIZONTAL));
            historyRecyclerView.setAdapter(mHistorySearchAdapter = new HistorySearchAdapter(this));
            mHistoryPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            mHistoryPopupWindow.setAnimationStyle(net.twoant.master.R.style.DialogStyle_X);
            // 设置点击周围取消
            mHistoryPopupWindow.setOutsideTouchable(true);
            mHistoryPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isHistoryShowing = false;
                }
            });
            // 设置获取焦点
//            mHistoryPopupWindow.setFocusable(true);
            // 设置poPupwindow可以点后退键取消poPupwindow(5.0 以下)
            mHistoryPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, net.twoant.master.R.drawable.yh_shape_popup_window_bg));
        }
        if (null == userHistoryData) {
            readHistoryData();
        }
    }

    /**
     * 初始化 搜索框 数据
     */
    private void initEditData() {
        if (null == mEditText) {
            return;
        }

        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mHistorySearchAdapter != null && !isHistoryShowing && event.getAction() == MotionEvent.ACTION_DOWN) {
                    isHistoryShowing = true;
                    mHistorySearchAdapter.setDataList(mUserHistoryData);
                    mHistoryPopupWindow.showAsDropDown(v, 0, 0);

                }
                return false;
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mHistorySearchAdapter != null && !isHistoryShowing) {
                        isHistoryShowing = true;
                        mHistorySearchAdapter.setDataList(mUserHistoryData);
                        mHistoryPopupWindow.showAsDropDown(v, 0, 0);
                    }
                } else {
                    if (mHistoryPopupWindow != null) {
                        mHistoryPopupWindow.dismiss();
                    }
                    isHistoryShowing = false;
                }
            }
        });
    }

    /**
     * 读取用户搜索历史
     */
    @SuppressWarnings("unchecked")
    private void readHistoryData() {
        if (null != mUserHistoryData) {
            return;
        }

        final File file = getHistoryFile(mContext, mFileName, mAiSouAppInfoModel);

        if (!file.exists()) {
            return;
        }

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream inputStream = null;
                try {
                    inputStream = new ObjectInputStream(new FileInputStream(file));
                    Object readObject = inputStream.readObject();
                    if (readObject instanceof ArrayList) {
                        mUserHistoryData = (ArrayList<String>) readObject;
                        //倒序
                        Collections.reverse(mUserHistoryData);
                        //只保留十二条数据
                        if (mUserHistoryData.size() > 12) {
                            mUserHistoryData = (ArrayList<String>) mUserHistoryData.subList(0, 11);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("SearchToolbarHelper =" + e.toString());
                } finally {
                    CloseUtils.closeIO(inputStream);
                }
            }
        });
    }

    @NonNull
    public static File getHistoryFile(Context context, String name, AiSouAppInfoModel mAiSouAppInfoModel) {
        return new File(context.getCacheDir().getParent() + File.separator + name + "_history",
                null == mAiSouAppInfoModel ? "def" : mAiSouAppInfoModel.getUID() + ".ser");
    }
}
