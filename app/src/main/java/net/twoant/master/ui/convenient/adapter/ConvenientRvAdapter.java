package net.twoant.master.ui.convenient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.adapter.control.SearchConventionControl;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guo15 on 2017/3/9.
 */

public class ConvenientRvAdapter extends RecyclerView.Adapter {
    private ViewGroup.LayoutParams mLayoutParams;
    private String mString = "";
    private List<String> imagList = new ArrayList<>();
    private String mString1;
    private String mImgUrl;
    private Context mContext;
    private final int mWidth;
    private String mConvenientinfo_content;
    private String mTitle;

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    List<DataRow> mData = new ArrayList<>();

    public ConvenientRvAdapter(Context context, List<DataRow> mData) {
        this.mData = mData;
        mContext = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
    }

    public void setDatas(List<DataRow> infoListBeen) {
        mData = infoListBeen;
        notifyDataSetChanged();
    }

    public void addDatas(List<DataRow> infoListBeen) {
        mData.addAll(infoListBeen);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.xj_item_convenient_fragment, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        DataRow mDataRow = mData.get(position);
        if (null != mDataRow) {
            viewHolder.mLinearLayout.setVisibility(View.VISIBLE);
            viewHolder.tv_hint.setVisibility(View.GONE);
            mTitle = mDataRow.getString("TITLE");
            mTitle = mTitle.replace("&#126","~");
            viewHolder.mTitle.setText(EmojiUtil.getEmjoiStrig(mTitle));
            if ("null".equals(mDataRow.getString("FROM_SHOP_ID"))) {
                viewHolder.pub_type.setText("个人发布");
            } else {
                viewHolder.pub_type.setText("商家发布");
            }
            viewHolder.mData.setText(mDataRow.getString("ADD_TIME"));
            String click = mDataRow.getString("CLICK");
            if (!"null".equals(click)) {
                viewHolder.mClick.setText("阅览:" + click);
            } else {
                viewHolder.mClick.setText("阅览:0");
            }

            viewHolder.mUid.setText("ID:" + mDataRow.getInt("FROM_USER_AISOU_ID"));
            mConvenientinfo_content = EmojiUtil.getEmjoiStrig(mDataRow.getString("CONTENT"));
            mString = SearchConventionControl.getContent(mConvenientinfo_content);
            mString = mString.replace("&lt;","<");
            mString = mString.replace("&gt;",">");
            mString = mString.replace("&#126","~");
//            try {
                if (!"".equals(mString)) {
                    viewHolder.mContent.setVisibility(View.VISIBLE);
//                    EmojiUtil.handlerEmojiText(viewHolder.mContent, emjoiStrig, AiSouAppInfoModel.getAppContext(), CommonUtil.getDimens(R.dimen.px_40), CommonUtil.getDimens(R.dimen.px_40));
                    viewHolder.mContent.setText(mString);
                } else {
                    viewHolder.mContent.setVisibility(View.GONE);
                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            imagList = SearchConventionControl.getImageUrl(mConvenientinfo_content);

            if (null!=imagList){
            if (imagList.size() == 0) {
                viewHolder.iv_pic1.setVisibility(View.GONE);
                viewHolder.iv_pic2.setVisibility(View.GONE);
                viewHolder.iv_pic3.setVisibility(View.GONE);
            } else if (imagList.size() == 1) {
                mLayoutParams = viewHolder.iv_pic1.getLayoutParams();
                mLayoutParams.width = mWidth / 3;
                mLayoutParams.height = mWidth / 3;
                viewHolder.iv_pic1.setLayoutParams(mLayoutParams);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(viewHolder.iv_pic1, BaseConfig.getCorrectImageUrl(imagList.get(0)), mContext, net.twoant.master.R.drawable.ic_def_large);
                viewHolder.iv_pic1.setVisibility(View.VISIBLE);
                viewHolder.iv_pic2.setVisibility(View.GONE);
                viewHolder.iv_pic3.setVisibility(View.GONE);
            } else if (imagList.size() == 2) {
                mLayoutParams = viewHolder.iv_pic1.getLayoutParams();
                mLayoutParams.width = mWidth / 3;
                mLayoutParams.height = mWidth / 3;
                viewHolder.iv_pic1.setLayoutParams(mLayoutParams);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(viewHolder.iv_pic1, BaseConfig.getCorrectImageUrl(imagList.get(0)), mContext, net.twoant.master.R.drawable.ic_def_large);
                viewHolder.iv_pic1.setVisibility(View.VISIBLE);

                mLayoutParams = viewHolder.iv_pic2.getLayoutParams();
                mLayoutParams.width = mWidth / 3;
                mLayoutParams.height = mWidth / 3;
                viewHolder.iv_pic2.setLayoutParams(mLayoutParams);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(viewHolder.iv_pic2, BaseConfig.getCorrectImageUrl(imagList.get(1)), mContext, net.twoant.master.R.drawable.ic_def_large);
                viewHolder.iv_pic2.setVisibility(View.VISIBLE);
                viewHolder.iv_pic3.setVisibility(View.GONE);
            } else {
                mLayoutParams = viewHolder.iv_pic1.getLayoutParams();
                mLayoutParams.width = mWidth / 3;
                mLayoutParams.height = mWidth / 3;
                viewHolder.iv_pic1.setLayoutParams(mLayoutParams);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(viewHolder.iv_pic1, BaseConfig.getCorrectImageUrl(imagList.get(0)), mContext, net.twoant.master.R.drawable.ic_def_large);
                viewHolder.iv_pic1.setVisibility(View.VISIBLE);

                mLayoutParams = viewHolder.iv_pic2.getLayoutParams();
                mLayoutParams.width = mWidth / 3;
                mLayoutParams.height = mWidth / 3;
                viewHolder.iv_pic2.setLayoutParams(mLayoutParams);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(viewHolder.iv_pic2, BaseConfig.getCorrectImageUrl(imagList.get(1)), mContext, net.twoant.master.R.drawable.ic_def_large);
                viewHolder.iv_pic2.setVisibility(View.VISIBLE);

                mLayoutParams = viewHolder.iv_pic3.getLayoutParams();
                mLayoutParams.width = mWidth / 3;
                mLayoutParams.height = mWidth / 3;
                viewHolder.iv_pic3.setLayoutParams(mLayoutParams);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(viewHolder.iv_pic3, BaseConfig.getCorrectImageUrl(imagList.get(2)), mContext, net.twoant.master.R.drawable.ic_def_large);
                viewHolder.iv_pic3.setVisibility(View.VISIBLE);
            }
            }else {
                viewHolder.iv_pic1.setVisibility(View.GONE);
                viewHolder.iv_pic2.setVisibility(View.GONE);
                viewHolder.iv_pic3.setVisibility(View.GONE);
            }
        } else {
            viewHolder.mLinearLayout.setVisibility(View.GONE);
            viewHolder.tv_hint.setVisibility(View.VISIBLE);
            if (0 == position) {
                viewHolder.tv_hint.setText("没有数据");
            } else {
                viewHolder.tv_hint.setText("没有更多数据");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mTitle, mContent, pub_type;
        TextView mData, mClick, mUid;
        ImageView iv_pic1, iv_pic2, iv_pic3;
        LinearLayout mLinearLayout;
        TextView tv_hint;

        public MyViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(net.twoant.master.R.id.ll_item_content);
            mTitle = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_title);
            pub_type = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_pub_type);
            mData = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_data);
            mClick = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_click);
            mUid = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_id);

            mContent = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_content);
            iv_pic1 = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_info1);
            iv_pic2 = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_info2);
            iv_pic3 = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_info3);

            tv_hint = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_hint);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != onRecyclerViewListener) {
                return onRecyclerViewListener.onItemLongClick(getPosition());
            }
            return false;
        }
    }
}
