package net.twoant.master.base_app;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BasePagerAdapter<T> extends PagerAdapter{
	protected ArrayList<T> list;
	
	
	public BasePagerAdapter(ArrayList<T> list) {
		super();
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==object;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
		container.removeView((View) object);
	}

}
