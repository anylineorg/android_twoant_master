package net.twoant.master.base_app;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

/**
 * Created by S_Y_H on 2016/11/14.22:25
 * 基类fragment 适用于简单的fragment 类
 * 该类不适用于 FragmentPagerAdapter
 */
public abstract class BaseFragment extends Fragment {

    /**
     * @return 子类的资源布局id
     */
    protected abstract
    @LayoutRes
    int getLayoutRes();

    /**
     * 子类 实现该方法，无需在覆写 onViewCreate（）和onViewCreated（）两个方法，
     *
     * @param view getLayoutRes() 初始化后的view
     */
    protected abstract void onViewCreate(View view, @Nullable Bundle savedInstanceState);

    /**
     * 该方法 子类不应该再覆写
     */
    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }



    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreate(view, savedInstanceState);
    }

    public void LongHttp(int id, String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().id(id).url(ApiConstants.GUO + name).params(map).build().execute(callback);
    }
    public void LongHttp(String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.GUO + name).params(map).build().execute(callback);
    }
    public void LongHttp(String url, String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
    public void LongHttpGet(String url, StringCallback callback){
        url = url+"&_t="+ AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken()
                +"&_cc="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode()
                +"&_ac="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode();
        OkHttpUtils.get().url(url).build().execute(callback);
    }
}
