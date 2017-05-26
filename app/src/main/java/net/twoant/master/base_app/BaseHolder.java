package net.twoant.master.base_app;

import android.view.View;

/**
 * 其实可以看做MVP模式中的Presenter
 * @author Administrator
 *
 * @param <T>
 */
public abstract class BaseHolder<T> {
	public View holderView;//注意：一开始就将convertView转移到holder中用一个变量表示
	
	public BaseHolder(){
		//1.初始化holderView
		holderView = initHolderView();//需要holderView
		
		//2.设置tag
		holderView.setTag(this);
	}
	/**
	 * 初始化holderView
	 * @return
	 */
	public abstract View initHolderView();
	/**
	 * 绑定数据
	 */
	public abstract void bindData(T data);
	
	/**
	 * 获取holderView
	 * @return
	 */
	public View getHolderView(){
		return holderView;
	}
}
