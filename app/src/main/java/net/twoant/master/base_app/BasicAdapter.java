package net.twoant.master.base_app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BasicAdapter<T> extends BaseAdapter {
	protected List<T> list;
	public BasicAdapter(List<T> list) {
		super();
		this.list = list;
	}

	@Override
	public int getCount() {
		return null == list ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1.初始化holder
		BaseHolder<T> holder = null;
		if (convertView == null) {
			holder = getHolder(position);// 需要一个不固定的holder
		} else {
			holder = (BaseHolder) convertView.getTag();
		}
		// 3.绑定数据
		holder.bindData(list.get(position));

		// 4.增加炫酷动画
		// 一开始缩小
//		ViewHelper.setScaleX(holder.getHolderView(), 0.5f);
//		ViewHelper.setScaleY(holder.getHolderView(), 0.5f);
		// 执行放大动画
//		ViewPropertyAnimator.animate(holder.getHolderView()).scaleX(1f).setDuration(400)
//				.setInterpolator(new OvershootInterpolator()).start();
//		ViewPropertyAnimator.animate(holder.getHolderView()).scaleY(1f).setDuration(400)
//				.setInterpolator(new OvershootInterpolator()).start();

		return holder.getHolderView();
	}

	protected abstract BaseHolder<T> getHolder(int position);

}
