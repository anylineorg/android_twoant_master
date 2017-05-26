package net.twoant.master.base_app;

import android.os.Bundle;

import com.google.gson.Gson;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;


public abstract class LongLongBaseActivity extends TakePhotoBaseActivity {

	public static Gson gson = new Gson();
	public static Map<String,String> map = new HashMap<String,String>();

	@Override
	protected void subOnCreate(Bundle savedInstanceState) {
		this.initData();
		requestNetData();
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

	protected void initData() {

	}

	protected void requestNetData() {

	}
}
