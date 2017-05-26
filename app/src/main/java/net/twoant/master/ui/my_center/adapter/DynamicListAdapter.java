package net.twoant.master.ui.my_center.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J on 2017/2/19.
 */

public class DynamicListAdapter extends BaseAdapter{
    private final LayoutInflater mFrom;
    private final int mWidth;
    private List<DataRow> mInfoListBeen = new ArrayList<>();
    private ViewGroup.LayoutParams mLayoutParams;
    private DataRow mDataRow;
    private Context mContext;

    public DynamicListAdapter(Context context, List<DataRow> infoListBeen) {
        mInfoListBeen = infoListBeen;
        mContext = context;
        mFrom = LayoutInflater.from(mContext);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        mWidth = wm.getDefaultDisplay().getWidth();

    }
    public void setDatas(List<DataRow> infoListBeen){
        mInfoListBeen = infoListBeen;
        notifyDataSetChanged();
    }
    public void addDatas(List<DataRow> infoListBeen){
        mInfoListBeen.addAll(infoListBeen);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mInfoListBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfoListBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mFrom.inflate(R.layout.xj_item_convenient_fragment, null, false);
            viewHolder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.line_info);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.convenient_info_title);
            viewHolder.mData = (TextView) convertView.findViewById(R.id.convenient_info_data);
//            viewHolder.mLook = (TextView) convertView.findViewById(R.id.convenient_info_look);
//            viewHolder.mUid = (TextView) convertView.findViewById(R.id.convenient_info_id);
            viewHolder.mContent = (TextView) convertView.findViewById(R.id.convenient_info_content);
            viewHolder.iv_pic1 = (ImageView) convertView.findViewById(R.id.iv_info1);
            viewHolder.iv_pic2 = (ImageView) convertView.findViewById(R.id.iv_info2);
            viewHolder.iv_pic3 = (ImageView) convertView.findViewById(R.id.iv_info3);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        mDataRow = mInfoListBeen.get(position);
        viewHolder.mTitle.setText(mDataRow.getString("TITLE"));
        viewHolder.mData.setText(mDataRow.getString("REG_TIME"));
        viewHolder.mLook.setText("阅览:8888次");
        viewHolder.mUid.setText(mDataRow.getInt("FROM_USER_ID")+"");
        viewHolder.mContent.setText(mDataRow.getString("CONTENT"));
        mLayoutParams = viewHolder.iv_pic1.getLayoutParams();
        mLayoutParams.width = mWidth/3;
        mLayoutParams.height = mWidth/3;
        viewHolder.iv_pic1.setLayoutParams(mLayoutParams);
        viewHolder.iv_pic1.setVisibility(View.GONE);
        mLayoutParams = viewHolder.iv_pic2.getLayoutParams();
        mLayoutParams.width = mWidth/3;
        mLayoutParams.height = mWidth/3;
        viewHolder.iv_pic2.setLayoutParams(mLayoutParams);
        viewHolder.iv_pic2.setVisibility(View.GONE);
        mLayoutParams = viewHolder.iv_pic3.getLayoutParams();
        mLayoutParams.width = mWidth/3;
        mLayoutParams.height = mWidth/3;
        viewHolder.iv_pic3.setLayoutParams(mLayoutParams);
        viewHolder.iv_pic3.setVisibility(View.GONE);
        return convertView;
    }

    class ViewHolder {
        TextView mTitle, mData, mLook, mUid, mContent;
        ImageView iv_pic1,iv_pic2,iv_pic3;
        LinearLayout mLinearLayout;
    }



}
