package net.twoant.master.widget;

import android.app.Activity;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by S_Y_H on 2016/11/25.
 * 带listView的弹窗
 */

public final class ListViewDialog extends BaseDialog {

    private View mView;
    private ListView mListView;
    private AppCompatButton mBtn;
    private int mColor = -1;
    private IOnItemClickListener iOnItemClickListener;

    /**
     * +
     * 显示底部dialog
     *
     * @param context 必须是activity实例
     */
    public ListViewDialog(Activity context) {
        this(context, Gravity.BOTTOM, true);
    }

    /**
     * @param context 必须是activity实例
     * @param gravity 选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     */
    public ListViewDialog(Activity context, int gravity, boolean showBtn) {
        super(context, gravity, true);
        mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.yh_popup_window_select_view, null);
        mAlertDialog.setView(mView);
        initData();
        mBtn.setVisibility(showBtn ? View.VISIBLE : View.GONE);
    }

    /**
     * item 点击回调接口
     */
    public interface IOnItemClickListener {
        void onItemClickListener(int position, View v);
    }

    /**
     * 设置listView item 点击回调
     *
     * @param clickListener 监听对象
     */
    public <D> void setOnItemClickListener(IOnItemClickListener clickListener) {
        if (iOnItemClickListener == null) {
            iOnItemClickListener = clickListener;
            if (mListView == null) initData();
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (iOnItemClickListener != null)
                        iOnItemClickListener.onItemClickListener(position, view);
                    dismiss();
                }
            });
        }
    }

    @NonNull
    public AppCompatButton getBottomBtn() {
        return this.mBtn;
    }

    /**
     * 设置底部的字体颜色
     */
    public void setTextColor(@ColorRes int color) {
        initData();
        int mColor = ContextCompat.getColor(mActivity, color);
        mBtn.setTextColor(mColor);
    }

    /**
     * 设置所有的字体颜色
     */
    public void setBottomColor(@ColorRes int color) {
        initData();
        mColor = ContextCompat.getColor(mActivity, color);
        mBtn.setTextColor(mColor);
        if (mListView.getAdapter() == null) {
            return;
        }
        setListChildColor();
    }

    /**
     * @param bottomHintInfo 底部btn 文字信息
     * @param listViewData   适配器填充的数据
     * @param <T>            适配器填充的数据 类型
     */
    @SafeVarargs
    public final <T> void setInitData(String bottomHintInfo, T... listViewData) {
        setInitData(Arrays.asList(listViewData), bottomHintInfo);
    }

    /**
     * 在activity 生命周期onDestroy中调用该方法
     */
    public void onDestroy() {
        mActivity = null;
        dismiss();
        mAlertDialog = null;
        iOnItemClickListener = null;
    }

    /**
     * 设置 listView 数据
     *
     * @param listViewData   listView 数据
     * @param bottomHintInfo 底部btn 文字信息
     * @param <T>            适配器填充的数据
     */
    public <T> void setInitData(List<T> listViewData, String bottomHintInfo) {
        initData();
        mBtn.setText(bottomHintInfo);
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(mActivity, net.twoant.master.R.layout.yh_item_simple_popup_window, net.twoant.master.R.id.tv_item_name_simple_popup_window, listViewData);
        mListView.setAdapter(adapter);
        if (mColor != -1)
            setListChildColor();
    }


    /**
     * 初始化数据
     */
    private void initData() {
        if (mListView == null || mBtn == null) {
            mListView = (ListView) mView.findViewById(net.twoant.master.R.id.lv_list_view_popup_window);
            mBtn = (AppCompatButton) mView.findViewById(net.twoant.master.R.id.btn_popup_window_select_view);
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }


    /**
     * 设置所有的字体颜色
     */
    private void setListChildColor() {
        int count = mListView.getChildCount();
        for (int i = 0; i < count; ++i) {
            View view = mListView.getChildAt(i);
            if (view instanceof AppCompatTextView) {
                ((AppCompatTextView) view).setTextColor(mColor);
            }
        }
    }

}
