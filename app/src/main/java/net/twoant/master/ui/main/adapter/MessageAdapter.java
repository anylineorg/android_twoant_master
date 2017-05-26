package net.twoant.master.ui.main.adapter;

import android.app.Activity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.ui.main.activity.MessageDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/1/3.
 * 消息 适配器
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> implements View.OnClickListener {

    private List mDataSet;
    private Activity mActivity;

    public MessageAdapter(Activity activity) {
        this.mActivity = activity;
        mDataSet = new ArrayList();
        for (int i = 0; i < 10; ++i) {
            mDataSet.add("测试" + i);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.mIvImg.setImageResource(net.twoant.master.R.drawable.ic_def_large);
        holder.mTvTitle.setText("标题测试" + position);
        holder.mTvContent.setText("内容测试" + position);
        AppCompatTextView hintNumber = holder.mTvHintNumber;
        if (hintNumber.getVisibility() != View.VISIBLE)
            hintNumber.setVisibility(View.VISIBLE);
        hintNumber.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    @Override
    public void onClick(View v) {
        MessageDetailActivity.startActivity(mActivity);
    }

    public void onDestroy() {
        mActivity = null;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView mIvImg;
        private AppCompatTextView mTvHintNumber;
        private AppCompatTextView mTvTitle;
        private AppCompatTextView mTvContent;

        MessageViewHolder(View itemView) {
            super(itemView);
            this.mIvImg = (AppCompatImageView) itemView.findViewById(net.twoant.master.R.id.iv_img);
            this.mTvHintNumber = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_hint_number);
            this.mTvTitle = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_title);
            this.mTvContent = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_content);
        }

    }

}
