package net.twoant.master.ui.main.presenter;

import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;

import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.ui.main.interfaces.IStartRequestNetwork;

import java.util.LinkedList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by S_Y_H on 2016/12/1.
 * 首页网络请求控制类
 */

public final class HomePagerHttpControl implements HttpConnectedUtils.IOnStartNetworkCallBack {

    private static HomePagerHttpControl sHomePagerHttpControl;
    private SimpleArrayMap<Integer, IStartRequestNetwork> mAdapters = new SimpleArrayMap<>(10);
    private SimpleArrayMap<Integer, LinkedList<String>> mTags = new SimpleArrayMap<>(10);

    private static HttpConnectedUtils sHttpConnectUtil;


    private HomePagerHttpControl() {
        sHttpConnectUtil = HttpConnectedUtils.getInstance(this);
    }

    public static HomePagerHttpControl getInstance() {
        if (sHomePagerHttpControl == null) {
            synchronized (HomePagerHttpControl.class) {
                if (sHomePagerHttpControl == null) {
                    sHomePagerHttpControl = new HomePagerHttpControl();
                }
            }
        }
        return sHomePagerHttpControl;
    }

    public void startNetwork(int id, IStartRequestNetwork adapter, Map<String, String> parameter, String url) {
        sHttpConnectUtil.initData();
        this.mAdapters.put(id, adapter);
        int hashCode = adapter.hashCode();
        LinkedList<String> integers = mTags.get(hashCode);
        if (integers != null)
            integers.add(String.valueOf(id));
        else {
            integers = new LinkedList<>();
            integers.add(String.valueOf(id));
            mTags.put(hashCode, integers);
        }
        sHttpConnectUtil.startNetworkGetString(url, id, parameter);
    }

    public void cancelRequest(Object tag) {
        sHttpConnectUtil.cancelRequest(mTags.get(tag.hashCode()));
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        IStartRequestNetwork adapter = this.mAdapters.remove(id);
        if (adapter != null) {
            LinkedList<String> arrayList = mTags.get(adapter.hashCode());
            if (arrayList != null)
                arrayList.remove(String.valueOf(id));
            adapter.loadingDataSuccessful(response, id);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        IStartRequestNetwork adapter = this.mAdapters.remove(id);
        if (adapter != null) {
            LinkedList<String> arrayList = mTags.get(adapter.hashCode());
            if (arrayList != null)
                arrayList.remove(String.valueOf(id));
            adapter.loadingDataFail(NetworkUtils.getNetworkStateDescription(call, e, "加载失败，点击重试。"), id);
        }
    }
}
