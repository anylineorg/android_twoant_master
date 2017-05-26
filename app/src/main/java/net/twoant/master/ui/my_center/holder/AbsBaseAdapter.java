package net.twoant.master.ui.my_center.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.twoant.master.base_app.BaseHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2016/12/2.
 */

public abstract class AbsBaseAdapter<T> extends BaseAdapter {

    private BaseHolder holder;
    private List<T> mData = new ArrayList<>();

    public AbsBaseAdapter(List<T> data) {
        this.mData = data;
    }

    public List<T> getData() {
        return mData;
    }

    /**
     * 更新AbsListView的数据
     *
     * @param newData 新数据
     */
    public void notifyDataSetChanged(List<T> newData) {
        this.mData = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            holder = (BaseHolder) convertView.getTag();
        } else {
            holder = onCreateViewHolder(parent, getItemViewType(position));
        }
        holder.bindData(getItem(position));
        return holder.holderView;
    }

    /**
     * 创建ViewHolder
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    protected abstract BaseHolder onCreateViewHolder(ViewGroup parent, int viewType);
}
