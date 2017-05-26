package net.twoant.master.common_utils;

import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import net.twoant.master.app.AiSouAppInfoModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	/**
	 * 在主线程执行一段任务
	 * @author DZY
	 */
	public static void runOnUIThread(Runnable r){
		AiSouAppInfoModel.mHandler.post(r);
	}
	/**
	 * 移除子View
	 * @param child
	 */
	public static void removeSelfFromParent(View child){
		if(child!=null){
			ViewParent parent = child.getParent();
			if(parent!=null && parent instanceof ViewGroup){
				ViewGroup group = (ViewGroup) parent;
				group.removeView(child);//移除子View
			}
		}
	}
	
	public static Drawable getDrawable(int id){
		return AiSouAppInfoModel.getAppContext().getResources().getDrawable(id);
	}
	
	public static String getString(int id){
		return AiSouAppInfoModel.getAppContext().getResources().getString(id);
	}
	public static String[] getStringArray(int id){
		return AiSouAppInfoModel.getAppContext().getResources().getStringArray(id);
	}
	
	public static int getColor(int id){
		return AiSouAppInfoModel.getAppContext().getResources().getColor(id);
	}
	/**
	 * 获取dp资源，并且会自动将dp值转为px值
	 * @param id
	 * @return
	 */
	public static int getDimens(int id){
		return AiSouAppInfoModel.getAppContext().getResources().getDimensionPixelSize(id);
	}

	public static int getViewHeight(View view, boolean isHeight){
		int result;
		if(view==null)return 0;
		if(isHeight){
			int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			view.measure(h,0);
			result =view.getMeasuredHeight();
		}else{
			int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			view.measure(0,w);
			result =view.getMeasuredWidth();
		}
		return result;
	}

	/**
	 * 使用java正则表达式去掉多余的.与0
	 * @param s
	 * @return
	 */
	public static String subZeroAndDot(String s){
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s;
	}

	public static String getYzmFromSms(String smsBody) {
		Pattern pattern = Pattern.compile("\\d{6}");
		Matcher matcher = pattern.matcher(smsBody);

		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public static InputFilter[] get2DotEditextFilter(){
		return new InputFilter[]{new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if(source.equals(".") && dest.toString().length() == 0){
					return "0.";
				}
				if(dest.toString().contains(".")){
					int index = dest.toString().indexOf(".");
					int mlength = dest.toString().substring(index).length();
					if(mlength == 3){
						return "";
					}
				}
				return null;
			}
		}};
	}

}
