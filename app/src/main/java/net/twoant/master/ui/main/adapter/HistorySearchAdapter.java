package net.twoant.master.ui.main.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.R;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/1/13.
 * 搜索历史记录 适配器
 */

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.HistorySearchViewHolder> {

    private ArrayList<String> mDataList;

    private final View.OnClickListener mOnclickListener;

    public HistorySearchAdapter(View.OnClickListener onclick) {
        this.mOnclickListener = onclick;
    }

    @Override
    public HistorySearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistorySearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.yh_popup_window_history_item, parent, false));
    }

    public void setDataList(ArrayList<String> strings) {
        this.mDataList = strings;
        this.notifyDataSetChanged();
    }

    /**
     * 移动item ， 例如：{a, b, c, d, e} 交换（4， 0） -交换后- {e, a, b, c, d}
     */
    public ArrayList<String> getDataList() {
        return mDataList;
    }


    @Override
    public void onBindViewHolder(HistorySearchViewHolder holder, int position) {
        AppCompatTextView title = holder.mTitle;
        title.setText(mDataList.get(position));
        title.setOnClickListener(mOnclickListener);
        title.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class HistorySearchViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView mTitle;

        HistorySearchViewHolder(View itemView) {
            super(itemView);
            mTitle = (AppCompatTextView) itemView.findViewById(R.id.tv_history_name);
        }
    }
}
