package net.twoant.master.ui.convenient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J on 2017/2/16.
 */

public class MyGridViewAdapter extends BaseAdapter {
    private List<DataRow> mStrings = new ArrayList<>();
    private final LayoutInflater mInflater;

    public MyGridViewAdapter(Context context, List<DataRow> strings) {
        mInflater = LayoutInflater.from(context);
        this.mStrings = strings;
    }
    public void setDatas(List<DataRow> strings){
        this.mStrings = strings;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return mStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.xj_convenient_item_gridview,null,false);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(mStrings.get(position).getString("NM"));
//        viewHolder.mTextView.setText(strings[position]);
        return convertView;
    }



    class ViewHolder{
        TextView mTextView;
    }
}
