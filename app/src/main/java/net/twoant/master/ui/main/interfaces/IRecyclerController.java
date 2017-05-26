package net.twoant.master.ui.main.interfaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2016/12/1.
 * 首页的RecyclerView控制器
 */

public interface IRecyclerController<AP, VH> {

    VH onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * @param state           状态码
     * @param viewType        视图类型
     * @param holder          ViewHolder
     * @param position        所处适配器位置
     * @param onClickListener 点击监听
     */
    void onBindViewHolder(int state, int viewType, VH holder, int position, View.OnClickListener onClickListener);

    int getItemCount();

    String getUrl(int category);

    /**
     * @param adapter   适配器
     * @param response  网络请求的数据
     * @param isRefresh 是否是刷新
     */
    void decodeResponseData(AP adapter, String response, boolean isRefresh, int id);

    /**
     * 暂时写死，上传类型，只能为String
     */
    Map<String, String> getParameter(int category);

    int getItemViewType(int position);

    /**
     * 是否继续加载
     *
     * @return true 继续加载，主要用于控制图片的加载
     */
    boolean canLoadingData();

    /**
     * 网络请求失败
     *
     * @param id      请求id
     * @param adapter 适配器
     * @return true 显示默认信息， false 自己处理
     */
    boolean requestFail(int id, BaseRecyclerNetworkAdapter adapter);

    void onClickListener(View view, HomePagerHttpControl control, AP adapter);

    /**
     * 刷新
     *
     * @param keyword 关键字
     */
    void refreshData(String keyword);

    /**
     * 请求加载数据
     */
    void requestLoadingData();

    void onDestroy();

    void onResume();

    void onStop();

    void onPause();

    List getDataList();

    /**
     * 绑定适配器
     */
    void bindingAdapter(AP adapter);
}
