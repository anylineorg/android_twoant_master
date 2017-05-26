package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.DisplayDimensionUtils;

import java.util.Arrays;

/**
 * Created by S_Y_H on 2017/3/15.
 * 搜索栏的type 类型选择
 */

public final class SearchTypePopupWindow {

    private ListPopupWindow mListPopupWindow;
    private IOnTypeClickListener iOnTypeClickListener;
    private Context mContext;
    private String mType;
    private String[] mTypes;

    public SearchTypePopupWindow(String currentType, String... types) {
        this.mContext = AiSouAppInfoModel.getAppContext();
        if (0 < types.length) {
            this.mTypes = types;
            this.mType = currentType;
        }
    }

    public String getCurrentType() {
        return this.mType;
    }

    public String[] getTypes() {
        return this.mTypes;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString("mT", mType);
        bundle.putStringArray("mTs", mTypes);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        mType = bundle.getString("mT");
        mTypes = bundle.getStringArray("mTs");
    }

    public interface IOnTypeClickListener {
        void onTypeClickListener(SearchTypePopupWindow popupWindow, View view, String type, String[] types);
    }

    public void setOnTypeClickListener(SearchTypePopupWindow.IOnTypeClickListener iOnTypeClickListener) {
        this.iOnTypeClickListener = iOnTypeClickListener;
    }

    /**
     * 显示类型选择popupWindow
     *
     * @param view 显示的位置
     */
    public void show(View view) {
        if (mListPopupWindow == null) {
            initTypeSelectPopupWindow();
        }
        if (mListPopupWindow.isShowing()) {
            return;
        }
        mListPopupWindow.setAnchorView(view);
        mListPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        mListPopupWindow.setContentWidth(DisplayDimensionUtils.dpToPx(60));
        mListPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        mListPopupWindow.show();
    }

    public void dismiss() {
        if (null != mListPopupWindow && mListPopupWindow.isShowing()) {
            mListPopupWindow.dismiss();
        }
    }

    public boolean syncData(String type, String... types) {
        if (types.length >= mTypes.length) {
            for (int i = 0, size = mTypes.length; i < size; ++i) {
                mTypes[i] = types[i];
            }
            mType = type;
            return true;
        }
        return false;
    }

    /**
     * 初始化选择类型的popupWindow
     */
    private void initTypeSelectPopupWindow() {
        mListPopupWindow = new ListPopupWindow(mContext);
        ArrayAdapter<String> typeArrayAdapter = new ArrayAdapter<>(mContext, net.twoant.master.R.layout.yh_popup_window_select_type,
                net.twoant.master.R.id.tv_type_popup_window_select_list, mTypes);
        mListPopupWindow.setAdapter(typeArrayAdapter);
        mListPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, net.twoant.master.R.drawable.yh_shape_popup_window_bg));
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != iOnTypeClickListener) {
                    iOnTypeClickListener.onTypeClickListener(SearchTypePopupWindow.this, view, mTypes[position], mTypes);
                }
                replace(mTypes[position], position);
                mListPopupWindow.dismiss();
            }
        });
        mListPopupWindow.setModal(true);
    }

    /**
     * @param type     活动类型
     * @param position 下标
     */
    private void replace(String type, int position) {
        int index = Arrays.binarySearch(mTypes, type);
        if (index >= 0) {
            mTypes[index] = mType;
            mType = type;
        } else {
            mTypes[position] = mType;
            mType = type;
        }
    }
}
