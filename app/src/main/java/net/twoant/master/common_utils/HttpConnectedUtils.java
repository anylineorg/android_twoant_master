package net.twoant.master.common_utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.twoant.master.app.AiSouAppInfoModel;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/21.
 * 网络请求工具类
 */

public class HttpConnectedUtils {

    private MyStringCallBack fStringCallBack;
    private String mToken;
    private String mCityCode;
    private String mAddressCode;

    /**
     * 设置提示信息
     *
     * @param loading 网络请求进行时的 加载时的信息
     * @param error   网络请求失败时的 提示信息
     */
    public void setHintInfo(String loading, String error) {
        if (fStringCallBack != null) {
            fStringCallBack.setHintInfo(loading, error);
        }
    }

    private HttpConnectedUtils(IOnStartNetworkSimpleCallBack callBack) {
        initData();
        fStringCallBack = new MyStringCallBack(callBack);
    }

    public void initData() {
        mToken = AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken();
        mToken = TextUtils.isEmpty(mToken) ? "" : mToken;
        mCityCode = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode();
        mCityCode = TextUtils.isEmpty(mCityCode) ? "" : mCityCode;
        mAddressCode = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode();
        mAddressCode = TextUtils.isEmpty(mAddressCode) ? "" : mAddressCode;
    }

    public void setCityAndAddressCode(String cityCode, String addressCode) {
        if (!TextUtils.isEmpty(cityCode)) {
            this.mCityCode = cityCode;
        }
        if (!TextUtils.isEmpty(addressCode)) {
            this.mAddressCode = addressCode;
        }
    }


    private HttpConnectedUtils() {
        initData();
        fStringCallBack = null;
    }

    public static HttpConnectedUtils getInstance(IOnStartNetworkSimpleCallBack callBack) {
        return new HttpConnectedUtils(callBack);
    }

    public static HttpConnectedUtils getInstance() {
        return new HttpConnectedUtils();
    }

    public void onDestroy() {
        if (fStringCallBack != null) {
            fStringCallBack.onDestroy();
            fStringCallBack = null;
        }
    }


    /**
     * 调用该方法必须是通过调用 {@see #getInstance(IOnStartNetworkSimpleCallBack) }进行获取实例
     *
     * @param id        id
     * @param parameter 参数
     * @param url       网址
     */
    public void startNetworkGetString(int id, @NonNull Map<String, String> parameter, String url) {
        if (fStringCallBack == null) {
            throw new IllegalArgumentException("需要调用带参的getInstance方法");
        }
        fStringCallBack.fStack.add(id);
        parameter.put("_t", mToken);
        parameter.put("_cc", mCityCode);
        parameter.put("_ac", mAddressCode);
        OkHttpUtils.post().url(url).tag(id).id(id).params(parameter).build().execute(fStringCallBack);
    }

    /**
     * 调用该方法必须是通过调用 {@see #getInstance(IOnStartNetworkSimpleCallBack) }进行获取实例
     * tag 为 string 类型
     *
     * @param id        id
     * @param parameter 参数
     * @param url       网址
     */
    public void startNetworkGetString(String url, int id, @NonNull Map<String, String> parameter) {
        if (fStringCallBack == null) {
            throw new IllegalArgumentException("需要调用带参的getInstance方法");
        }
        fStringCallBack.fStack.add(id);
        parameter.put("_t", mToken);
        parameter.put("_cc", mCityCode);
        parameter.put("_ac", mAddressCode);
        OkHttpUtils.post().url(url).tag(String.valueOf(id)).id(id).params(parameter).build().execute(fStringCallBack);
    }

    /**
     * 不带token ， 用于登录
     */
    public void startNetworkGetStringWithoutToken(int id, @NonNull Map<String, String> parameter, String url) {
        if (fStringCallBack == null) {
            throw new IllegalArgumentException("需要调用带参的getInstance方法");
        }
        fStringCallBack.fStack.add(id);
        OkHttpUtils.post().url(url).tag(id).id(id).params(parameter).build().execute(fStringCallBack);
    }


    /**
     * 下载文件
     */
    public void startNetworkGetFile(int id, String url, Callback fileCallBack) {
        OkHttpUtils.get().url(url).tag(id).id(id).build().execute(fileCallBack);
    }

    /**
     * 取消所有 网络链接, 需要在onDestroy中进行调用，因为会清空实例。
     */
    public void cancelRequest() {
        if (fStringCallBack != null)
            fStringCallBack.cancelRequest();
    }

    public void cancelRequest(List<String> ids) {
        if (ids == null) return;
        OkHttpUtils instance = OkHttpUtils.getInstance();
        for (String integer : ids)
            instance.cancelTag(integer);
        ids.clear();
    }


    /**
     * 调用该方法必须是通过调用 {@see #getInstance(IOnStartNetworkSimpleCallBack) }进行获取实例
     * * 该方法 只适合上传图片
     *
     * @param id  id
     * @param key 参数
     * @param url 网址
     */
    public void startNetworkUploading(int id, String url, String key, File file) {
        if (fStringCallBack == null) {
            throw new IllegalArgumentException("需要调用带参的getInstance方法");
        }
        fStringCallBack.fStack.add(id);
        String kk=AiSouAppInfoModel.getInstance().getToken();
        ToastUtil.showLong(kk);
        OkHttpUtils.post().url(url).addParams("_t", "8u79yhilgvubhpnijo9").addParams("_tk", "8u79yhilgvubhpnijo9").addParams("_ac", mAddressCode)
                .addParams("_cc", mCityCode).addFile(key, getName(file.getName()), file)
                .tag(String.valueOf(id)).id(id).build().execute(fStringCallBack);
    }

    /**
     * 调用该方法必须是通过调用 {@see #getInstance(IOnStartNetworkSimpleCallBack) }进行获取实例
     * 该方法 只适合上传图片
     *
     * @param id        id
     * @param parameter 参数
     * @param url       网址
     */
    public void startNetworkUploading(int id, String url, Map<String, String> parameter, Map<String, File> files) {
        if (fStringCallBack == null) {
            throw new IllegalArgumentException("需要调用带参的getInstance方法");
        }
        fStringCallBack.fStack.add(id);
        PostFormBuilder builder = OkHttpUtils.post().url(url);
        if (files != null) {
            Set<String> strings = files.keySet();
            for (String string : strings) {
                File file = files.get(string);
                builder.addFile(string, getName(file.getName()), file);
            }
        }
        if (parameter != null) {
            parameter.put("_t", mToken);
            parameter.put("_cc", mCityCode);
            parameter.put("_ac", mAddressCode);
            builder.params(parameter);
        }
        builder.tag(String.valueOf(id)).id(id).build().execute(fStringCallBack);
    }

    private String getName(String name) {
        if (name != null && !name.contains(".")) {
            name += ".jpg";
        }
        return name;
    }

    private static class MyStringCallBack extends Callback<String> {
        private final Stack<Integer> fStack = new Stack<>();
        private HintDialogUtil mHintDialog;
        private IOnStartNetworkSimpleCallBack fCallBack;
        /**
         * 加载中的提示信息
         */
        private String mLoadingInfo;
        /**
         * 加载失败的提示信息
         */
        private String mErrorInfo;

        private void setHintInfo(String loading, String error) {
            this.mLoadingInfo = loading;
            this.mErrorInfo = error;
        }

        MyStringCallBack(IOnStartNetworkSimpleCallBack onStartNetworkCallBack) {
            this.fCallBack = onStartNetworkCallBack;
        }

        void cancelRequest() {
            for (Integer tag : fStack) {
                OkHttpUtils.getInstance().cancelTag(tag);
            }
            fStack.clear();
        }

        void onDestroy() {
            this.cancelRequest();
            this.mHintDialog = null;
            this.fCallBack = null;

        }


        @Override
        public String parseNetworkResponse(Response response, int id) throws Exception {
            return response.body().string();
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            if (fStack.size() != 0)
                fStack.pop();
            if (fCallBack != null) {
                if (mHintDialog != null) {
                    mHintDialog.showError(mErrorInfo != null ? mErrorInfo : AiSouAppInfoModel.getAppContext().getString(net.twoant.master.R.string.dialog_loading_fail));
                }
                fCallBack.onError(call, e, id);
            }
        }

        @Override
        public void onResponse(String response, int id) {
            if (fStack.size() != 0)
                fStack.pop();
            if (fCallBack != null) {
                if (mHintDialog != null) {
                    mHintDialog.dismissDialog();
                }
                fCallBack.onResponse(response, id);
            }
        }

        public void onBefore(Request request, int id) {
            if (fCallBack instanceof IOnStartNetworkCallBack) {
                IOnStartNetworkCallBack callBack = (IOnStartNetworkCallBack) fCallBack;
                if (mHintDialog == null)
                    mHintDialog = callBack.getHintDialog();
                if (mHintDialog != null) {
                    mHintDialog.showLoading(mLoadingInfo != null ? mLoadingInfo : AiSouAppInfoModel.getAppContext().getString(net.twoant.master.R.string.dialog_loading), true, false);
                }
                callBack.onBefore(request, id);
            }
        }
    }

    public interface IOnStartNetworkSimpleCallBack {
        void onResponse(String response, int id);

        void onError(Call call, Exception e, int id);
    }

    public interface IOnStartNetworkCallBack extends IOnStartNetworkSimpleCallBack {

        void onBefore(Request request, int id);

        /**
         * 使用默认的加载提示，null 为不使用。
         *
         * @return HintDialogUtils实例
         */
        @Nullable
        HintDialogUtil getHintDialog();
    }
}
