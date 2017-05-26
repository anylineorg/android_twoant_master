package net.twoant.master.ui.main.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.interfaces.IOnItemClickListener;
import net.twoant.master.ui.main.interfaces.IOnLoadingDataListener;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.MarqueeTextView;
import net.twoant.master.ui.my_center.bean.ClassifyListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2016/12/18.
 * 商家详情页的 商品展示中 商品分类 适配器
 */

public class MerchantHomeGoodsCategoryAdapter extends RecyclerView.Adapter<MerchantHomeGoodsCategoryAdapter.CategoryViewHolder> implements View.OnClickListener {

    private List<ClassifyListBean.ResultBean> mDataBean;
    private final static int ID_CATEGORY = 6 << 24;
    private final static int TYPE_LEFT = 1;
    private IOnItemClickListener<String> iOnItemClickListener;
    private IOnLoadingDataListener iOnLoadingDataListener;
    /**
     * 当前点击的分类 item适配器位置
     */
    private int mPosition = 0;

    private int mTempPosition = 0;

    public MerchantHomeGoodsCategoryAdapter() {
        this.mDataBean = new ArrayList<>();
        this.mDataBean.add(null);
    }

    public void setOnItemClickListener(IOnItemClickListener<String> iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    public void setOnLoadingDataListener(IOnLoadingDataListener iOnLoadingDataListener) {
        this.iOnLoadingDataListener = iOnLoadingDataListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == IRecyclerViewConstant.TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_loading, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_merchant_category, parent, false);
        }
        return new CategoryViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        if (holder.mViewType == IRecyclerViewConstant.TYPE_LOADING) {
            holder.itemView.setOnClickListener(this);
            ProgressBar progress = holder.mProgressBar;
            if (progress.getVisibility() != View.VISIBLE) {
                progress.setVisibility(View.VISIBLE);
                holder.mTvHintInfo.setText("加载中...");
            }
        } else {
            ClassifyListBean.ResultBean resultBean = mDataBean.get(position);
            if (resultBean != null) {
                MarqueeTextView tvCategory = holder.mTvCategory;
                if (mPosition == position) {
                    tvCategory.setSelected(true);
                    tvCategory.setFocused(true);
                } else {
                    tvCategory.setSelected(false);
                    tvCategory.setFocused(false);
                }
                tvCategory.setText(resultBean.getNM());
                tvCategory.setOnClickListener(this);
                tvCategory.setTag(String.valueOf(resultBean.getCD()));
                tvCategory.setTag(ID_CATEGORY, position);
            }
        }
    }

    public int getCurrentItem() {
        return this.mPosition;
    }

    public int getTempCurrentPosition() {
        return this.mTempPosition;
    }

    public List<ClassifyListBean.ResultBean> getDataBean() {
        return this.mDataBean;
    }

    public void setDataBean(List<ClassifyListBean.ResultBean> dataBean) {
        if (dataBean != null) {
            if (dataBean.size() != 0)
                this.mDataBean = dataBean;
            else
                this.mDataBean.clear();
            this.notifyDataSetChanged();
        }
    }

    /**
     * 显示加载错误信息
     *
     * @param sequence 错误信息
     */
    public void showErrorLoadingHint(RecyclerView recyclerView, CharSequence sequence) {
        if (recyclerView != null) {
            int childCount = recyclerView.getChildCount();

            View view;
            if (childCount == 1) {
                view = recyclerView.getChildAt(0);
            } else {
                view = recyclerView.getChildAt(childCount - 1);
            }
            if (view != null && view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; ++i) {
                    View subView = viewGroup.getChildAt(i);
                    if (subView instanceof AppCompatTextView) {
                        ((AppCompatTextView) subView).setText(sequence);
                    } else if (subView instanceof ProgressBar) {
                        subView.setVisibility(View.GONE);
                    }
                }
            }

        } else {
            ToastUtil.showShort(net.twoant.master.R.string.loading_fail);
        }
    }

    private void resetState(ViewGroup view) {
        try {
            View progressBar = view.getChildAt(0);
            if (progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
                ((AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_hint_info)).setText(net.twoant.master.R.string.dialog_loading);
            }
        } catch (Exception e) {
            LogUtils.e("MerchantHomeGoodsAdapter resetState" + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mDataBean.size();
    }

    @Override
    public void onViewAttachedToWindow(CategoryViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (iOnLoadingDataListener != null && holder.mViewType == IRecyclerViewConstant.TYPE_LOADING) {
            iOnLoadingDataListener.onLoadingDataListener();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataBean.get(position) == null) return IRecyclerViewConstant.TYPE_LOADING;

        return TYPE_LEFT;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case net.twoant.master.R.id.ll_item_loading:
                if (v instanceof ViewGroup)
                    resetState((ViewGroup) v);
                if (iOnLoadingDataListener != null)
                    iOnLoadingDataListener.onLoadingDataListener();
                break;

            case net.twoant.master.R.id.tv_category:
                Object tag = v.getTag();
                Object index = v.getTag(ID_CATEGORY);
                if (tag instanceof String && index instanceof Integer) {
                    if (iOnItemClickListener != null) {
                        iOnItemClickListener.onItemClickListener((String) tag);
                    }
                    final int temp = mPosition;
                    mPosition = mTempPosition = (int) index;
                    this.notifyItemChanged(temp);
                    v.setSelected(true);
                    if (v instanceof MarqueeTextView) {
                        ((MarqueeTextView)v).setFocused(true);
                    }

                }
                break;
        }
    }

    /**
     * @param autoLoading 加载数据
     * @param change      是否更改显示
     * @param v           当前 焦点View
     * @param tag         type-id
     * @param index       下标
     */
    public void setCurrentItem(boolean autoLoading, boolean change, View v, String tag, Integer index) {
        if (autoLoading && iOnItemClickListener != null) {
            iOnItemClickListener.onAutoClickListener(tag);
        }
        if (change) {
            final int temp = mPosition;
            mPosition = mTempPosition = index;
            this.notifyItemChanged(temp);
            v.setSelected(true);
            if (v instanceof MarqueeTextView) {
                ((MarqueeTextView)v).setFocused(true);
            }

        } else {
            mTempPosition = index;
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        int mViewType;
        private MarqueeTextView mTvCategory;

        private ProgressBar mProgressBar;
        private AppCompatTextView mTvHintInfo;

        CategoryViewHolder(View itemView, int viewType) {
            super(itemView);
            this.mViewType = viewType;
            if (viewType == IRecyclerViewConstant.TYPE_LOADING) {
                this.mProgressBar = (ProgressBar) itemView.findViewById(net.twoant.master.R.id.progress_bar);
                this.mTvHintInfo = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_hint_info);
            } else {
                this.mTvCategory = (MarqueeTextView) itemView.findViewById(net.twoant.master.R.id.tv_category);
            }
        }
    }
}

