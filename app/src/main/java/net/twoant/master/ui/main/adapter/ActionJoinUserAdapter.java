package net.twoant.master.ui.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.activity.ActionJoinMemberActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.interfaces.IStartRequestNetwork;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by S_Y_H on 2016/12/24.
 * 活动参加用户列表适配器
 */

public class ActionJoinUserAdapter extends RecyclerView.Adapter<ActionJoinUserAdapter.ActionJoinUserViewHolder> implements View.OnClickListener, IStartRequestNetwork {

    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;
    private final String fActivityId;
    /**
     * 当前请求页数
     */
    private int mIndex = 1;
    private final HomePagerHttpControl fHomePagerHttpControl;

    private int mOrientation;
    private ArrayMap<String, String> mParameter;
    private List<DataRow> mDataSet;
    private Context mContext;
    private SimpleDateFormat mSimpleDateFormat;
    private SimpleDateFormat mMonth;
    private Activity mActivity;

    public ActionJoinUserAdapter(int orientation, String activityId, @Nullable Activity activity) {
        mActivity = activity;
        mDataSet = new ArrayList<>(11);
        mContext = AiSouAppInfoModel.getAppContext();
        mOrientation = orientation;
        fActivityId = activityId;
        fHomePagerHttpControl = HomePagerHttpControl.getInstance();
        if (mOrientation == VERTICAL)
            mDataSet.add(null);
    }

    @Override
    public ActionJoinUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case HORIZONTAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yh_item_user_horizontal_list, parent, false);
                break;
            case VERTICAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yh_item_user_vertical_list, parent, false);
                break;
            case IRecyclerViewConstant.TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yh_item_loading, parent, false);
                break;
        }
        return new ActionJoinUserViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ActionJoinUserViewHolder holder, int position) {
        DataRow row = mDataSet.get(position);
        switch (holder.getItemViewType()) {
            case HORIZONTAL:
                if (row == null) return;
                holder.itemView.setOnClickListener(this);
                holder.mTvNickname.setText(row.getString("USER_NM"));
                ImageLoader.getImageFromNetwork(holder.mIvHeadImg, BaseConfig.getCorrectImageUrl(row.getString("USER_AVATAR")), mContext, R.drawable.ic_def_circle);
                break;
            case VERTICAL:
                if (row == null) return;
                holder.mTvNickname.setText(row.getString("USER_NM"));
                ImageLoader.getImageFromNetwork(holder.mIvHeadImg, BaseConfig.getCorrectImageUrl(row.getString("USER_AVATAR")), mContext, R.drawable.ic_def_circle);

                if (mSimpleDateFormat == null) {
                    mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
                }
                if (mMonth == null) {
                    mMonth = new SimpleDateFormat("MM月dd日", Locale.CHINA);
                }

                try {
                    holder.mTvDate.setText(mMonth.format(mSimpleDateFormat.parse(row.getString("BUY_TIME"))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case IRecyclerViewConstant.TYPE_LOADING:
                AppCompatTextView hintInfo = holder.getHintInfo();
                View progressBar = holder.getProgressBar();
                if (mDataSet.size() == 1) {
                    hintInfo.setText("加载中...");
                    if (progressBar.getVisibility() != View.VISIBLE)
                        progressBar.setVisibility(View.VISIBLE);
                } else {
                    if (progressBar.getVisibility() != View.GONE)
                        progressBar.setVisibility(View.GONE);
                    hintInfo.setText("点击查看更多");
                }
                holder.itemView.setOnClickListener(this);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mOrientation == VERTICAL && mDataSet.size() - 1 == position) {
            return IRecyclerViewConstant.TYPE_LOADING;
        }
        return mOrientation;
    }

    @Override
    public void loadingDataSuccessful(String response, int id) {
        DataRow dataRow = DataRow.parseJson(response);
        if (dataRow != null) {
            DataSet set = dataRow.getSet("DATA");
            List<DataRow> ros;
            if (set != null && (ros = set.getRows()) != null && ros.size() > 0) {
                if (mOrientation == HORIZONTAL) {
                    mDataSet = ros;
                    this.notifyDataSetChanged();
                } else {
                    int size = mDataSet.size() - 1;
                    mDataSet.addAll(size, ros);
                    this.notifyItemRangeInserted(size, ros.size());
                    showLoadingHint(false, "点击加载更多");
                }
            } else {
                if (mOrientation == VERTICAL) {
                    showLoadingHint(false, "没有更多");
                    mIndex = --mIndex <= 1 ? 1 : mIndex;
                }
            }
        }
    }

    @Override
    public void loadingDataFail(String describe, int id) {
        if (mOrientation == VERTICAL)
            showLoadingHint(false, "加载失败");
        mIndex = --mIndex <= 1 ? 1 : mIndex;

    }

    private void startNetwork() {
        if (mParameter == null)
            mParameter = new ArrayMap<>();
        mParameter.clear();
        mParameter.put("activity", fActivityId);
        mParameter.put("_anyline_page", String.valueOf(mIndex++));
        fHomePagerHttpControl.startNetwork(mOrientation, this, mParameter, ApiConstants.ACTIVITY_USER_LIST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.ll_item_loading:
                showLoadingHint(true, "加载中...");
                startNetwork();
                break;
            case R.id.ll_linear_parent:
                if (mActivity != null)
                    ActionJoinMemberActivity.startActivity(mActivity, fActivityId);
                break;
            case R.id.ll_vertical_parent:

                break;
        }
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        startNetwork();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
        mActivity = null;
        if (fHomePagerHttpControl != null) {
            fHomePagerHttpControl.cancelRequest(this);
        }
    }

    /**
     * 设置显示的信息
     *
     * @param showProgress true显示progressBar
     * @param sequence     提示信息
     * @return false 未处理成功， true 更改成功
     */
    private boolean showLoadingHint(boolean showProgress, CharSequence sequence) {
        if (mRecyclerView != null) {
            RecyclerView.ViewHolder viewHolderForAdapterPosition;
            if ((viewHolderForAdapterPosition = mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0)))
                    .getItemViewType() == IRecyclerViewConstant.TYPE_LOADING ||
                    (viewHolderForAdapterPosition = mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1)))
                            .getItemViewType() == IRecyclerViewConstant.TYPE_LOADING) {

                if (viewHolderForAdapterPosition instanceof ActionJoinUserViewHolder) {
                    ActionJoinUserViewHolder viewHolder = (ActionJoinUserViewHolder) viewHolderForAdapterPosition;
                    int visibility = showProgress ? View.VISIBLE : View.GONE;
                    if (visibility != viewHolder.getProgressBar().getVisibility()) {
                        viewHolder.getProgressBar().setVisibility(visibility);
                    }
                    viewHolder.getHintInfo().setText(sequence);
                    return true;
                }
            }
        }
        return false;
    }


    static class ActionJoinUserViewHolder extends BaseRecyclerViewHolder {

        private AppCompatTextView mTvDate;
        private CircleImageView mIvHeadImg;
        private AppCompatTextView mTvNickname;

        ActionJoinUserViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        protected void initView(View itemView, int viewType) {
            switch (viewType) {
                case ActionJoinUserAdapter.HORIZONTAL:
                    this.mIvHeadImg = (CircleImageView) itemView.findViewById(R.id.iv_head_img);
                    this.mTvNickname = (AppCompatTextView) itemView.findViewById(R.id.tv_nickname);
                    break;
                case ActionJoinUserAdapter.VERTICAL:
                    this.mIvHeadImg = (CircleImageView) itemView.findViewById(R.id.iv_head_img);
                    this.mTvNickname = (AppCompatTextView) itemView.findViewById(R.id.tv_nickname);
                    this.mTvDate = (AppCompatTextView) itemView.findViewById(R.id.tv_date);
                    break;
            }
        }
    }
}
