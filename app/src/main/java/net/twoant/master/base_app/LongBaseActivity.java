package net.twoant.master.base_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.widget.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;


public abstract class LongBaseActivity extends AppCompatActivity {
	/**
	 * 上下文
	 */
	protected Context mContext;
	public static int page = 1;
	public static Gson gson = new Gson();
	public static Map<String,String> map = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseConfig.onCreate(this);
		doBeforeSetContentView();
		setContentView(getLayoutId());
		mContext = this;
		this.subOnCreate(savedInstanceState);
		this.initData();
		requestNetData();
//		initScrollBack();
	}
	public boolean isNull(String string){

		if (null==string || string.equals("null") || string=="null" || string.length()==0){
			return true;
		}
		return false;
	}

	public void LongHttp(int id, String name, Map<String,String> map, StringCallback callback){
		OkHttpUtils.head().addHeader("Connection", "close");
		map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
		map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
		map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
		OkHttpUtils.post().id(id).url(ApiConstants.GUO + name).params(map).build().execute(callback);
	}
	public void LongHttp(String name, Map<String,String> map, StringCallback callback){
		OkHttpUtils.head().addHeader("Connection", "close");
		map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
		map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
		map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
		OkHttpUtils.post().url(ApiConstants.GUO + name).params(map).build().execute(callback);
	}
	public void LongHttp(String url, String name, Map<String,String> map, StringCallback callback){
		OkHttpUtils.head().addHeader("Connection", "close");
		map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
		map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
		map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());

		OkHttpUtils.post().url(url).params(map).build().execute(callback);
	}public void LongHttpN(String url, Map<String,String> map, StringCallback callback){
		OkHttpUtils.head().addHeader("Connection", "close");
		OkHttpUtils.post().url(url).params(map).build().execute(callback);
	}
	public void LongHttpGet(String url, StringCallback callback){
		url = url+"&_t="+ AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken()
				+"&_cc="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode()
				+"&_ac="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode();
		System.out.println(url);
		OkHttpUtils.head().addHeader("Connection", "close");
		OkHttpUtils.get().url(url).build().execute(callback);
	}
	/**
	 * 获取布局文件的资源layout id
	 */
	protected abstract @LayoutRes
	int getLayoutId();

	/**
	 * 子类实现方法，实现方法后，子类无需再重写 OnCreate（Bundle savedInstanceState）方法;
	 */
	protected abstract void subOnCreate(Bundle savedInstanceState);

	protected void initData() {

	}
	protected void requestNetData() {

	}

	/**
	 * 该方法在setContentView 之前调用
	 * 设置layout前的配置
	 */
	private void doBeforeSetContentView() {
		// 默认着色状态栏
		SetStatusBarColor();
	}
	/**
	 * 着色状态栏（4.4以上系统有效）
	 */
	private void SetStatusBarColor() {
		StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, getStateBarColorRes()));
	}
	/**
	 * 若子类需要设置 自己的状态栏颜色，覆写该方法即可
	 * @return 颜色资源id
	 */
	protected @ColorRes
	int getStateBarColorRes() {
		return BaseConfig.getDefStateBarColor();
	}

	/**
	 * Activity的普通跳转
	 * @param context
	 */
	public void intent2Activity(Class<? extends Activity> context){
		Intent intent = new Intent(this,context);
		startActivity(intent);
	}

	/**
	 * Toast提示
	 * @param text
	 */
	public void showToast(String text){
		showToast(this, text, Toast.LENGTH_SHORT);
	}

	private void showToast(Context context , CharSequence text , int duration){
		Toast.makeText(context, text, duration).show();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		BaseConfig.onRestart(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		BaseConfig.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		BaseConfig.onStart(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseConfig.onResume(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		BaseConfig.onStop(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseConfig.onDestroy(this);
	}
}
