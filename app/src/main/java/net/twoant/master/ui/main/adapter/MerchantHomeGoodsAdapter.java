package net.twoant.master.ui.main.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.control.ControlUtils;
import net.twoant.master.ui.main.adapter.holder.MerchantHomeGoodsViewHolder;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.interfaces.IOnDataChangeListener;
import net.twoant.master.ui.main.interfaces.IOnItemClickListener;
import net.twoant.master.ui.main.interfaces.ISuspensionInterface;
import net.twoant.master.ui.main.widget.shopping_cart.GoodsAnimation;
import net.twoant.master.ui.my_center.bean.ClassifyListBean;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by S_Y_H on 2016/12/15.
 * 商家首页 商品列表的适配器
 */

public class MerchantHomeGoodsAdapter extends BaseRecyclerAdapter<DataRow, MerchantHomeGoodsViewHolder> implements ISuspensionInterface<DataRow>, View.OnClickListener {

    private final String TAG = getClass().getName();
    private final static int TYPE_RIGHT = 1;
    private final static int ID_ADD_GOODS = 4 << 24;
    private final static int ID_SUBTRACT_GOODS = 5 << 24;
    private final static int ID_GOODS_ID = 10 << 24;

    public interface IOnBtnClickListener {
        void onBtnClickListener(View view, String goodsId, boolean isAdd);
    }

    private IOnBtnClickListener iOnBtnClickListener;
    /**
     * 动画
     */
    private GoodsAnimation mGoodsAnimation;

    private IOnDataChangeListener iOnDataChangeListener;
    /**
     * 标题 数量
     */
    private int mTitleSize;

    private SimpleArrayMap<String, String> mTitleMap;

    /**
     * 存储 adapter 位置, key为GoodsId，Value为mGoodsItems
     */
    private ArrayMap<String, GoodsItemBean> mGoodsItems;

    private SimpleArrayMap<String, Integer> mPositionMap;

    /**
     * 当前在窗口中的ViewHolder
     */
    private SparseArray<MerchantHomeGoodsViewHolder> mViewHolders;

    /**
     * 屏幕宽度
     */
    private int mScreenWidth;

    @Override
    public boolean isEmpty() {
        return mTitleSize == 0;
    }

    @Override
    public int size() {
        return mTitleSize;
    }

    @Override
    public boolean isShowSuspension(int position) {
        return mDataList.size() > position && position >= 0 && mDataList.get(position) != null;
    }

    public void setOnBtnClickListener(IOnBtnClickListener iOnBtnClickListener) {
        this.iOnBtnClickListener = iOnBtnClickListener;
    }

    @Override
    public String getSuspensionTag(int position) {
        if (mTitleMap != null && mDataList.size() > position) {
            DataRow dataRow = mDataList.get(position);
            if (dataRow != null) {
                return mTitleMap.get(dataRow.getString("ID"));
            }
        }
        return null;
    }

    @Override
    public DataRow getPositionData(int position) {
        return position > mDataList.size() - 1 ? null : mDataList.get(position);
    }

    @Override
    public List<DataRow> setDataBean(boolean isShowLoadingHint, List<DataRow> data) {
        final List<DataRow> list = super.setDataBean(isShowLoadingHint, data);
        initPositionMapKey(mDataList);
        return list;
    }

    @Override
    public List<DataRow> setDataBean(List<DataRow> data) {
        final List<DataRow> list = super.setDataBean(data);
        initPositionMapKey(mDataList);
        return list;
    }

    @Override
    public List<DataRow> setDataBean(List<DataRow> data, boolean isRefresh) {
        final List<DataRow> list = super.setDataBean(data, isRefresh);
        initPositionMapKey(mDataList);
        return list;
    }

    public void setTitleList(List<ClassifyListBean.ResultBean> titleList) {
        if (mTitleMap != null) {
            mTitleMap.clear();
        } else {
            mTitleMap = new SimpleArrayMap<>(titleList.size());
        }

        if (titleList != null) {
            for (ClassifyListBean.ResultBean bean : titleList) {
                mTitleMap.put(bean.getCD(), bean.getNM());
            }
            this.mTitleSize = mDataList.size();
        }
    }

    @Override
    public void changeTitle(int positionD, DataRow data) {
    }

    private IOnItemClickListener<String> iOnItemClickListener;

    public void setOnItemClickListener(IOnItemClickListener<String> iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    public MerchantHomeGoodsAdapter(Activity activity, ILoadingData iLoadingData) {
        super(activity, iLoadingData);
        this.mScreenWidth = DisplayDimensionUtils.getScreenWidth();
        this.mScreenWidth = (int) (this.mScreenWidth / 6.0F * 4.5F + 0.5F);
        mViewHolders = new SparseArray<>();
        mGoodsItems = new ArrayMap<>();
    }

    @Override
    protected List<DataRow> subRemoveMethod(boolean nonNull, List<DataRow> resultBean, boolean isRefresh) {
        return ControlUtils.removeDuplicate(resultBean, isRefresh ? null : mDataList, "ID");
    }

    @Override
    public int getItemCount() {
        return mTitleSize = mDataList.size();
    }

    @Override
    protected void onBindViewHolder(MerchantHomeGoodsViewHolder holder, int viewType, int position) {
        DataRow dataBean = mDataList.get(position);

        if (dataBean != null) {

            String goods_id = dataBean.getStringDef("ID", "");
            AppCompatImageButton subtractGoods = holder.getIvSubtractGoods();
            AppCompatTextView tvGoodsCount = holder.getTvGoodsCount();
            GoodsItem goodsItem = mGoodsItems.get(goods_id);
            if (goodsItem != null) {
                int goodsCount = goodsItem.getGoodsCount();
                tvGoodsCount.setText(String.valueOf(goodsCount));
                subtractGoods.setVisibility(goodsCount > 0 ? View.VISIBLE : View.GONE);
            } else if (subtractGoods.getVisibility() != View.GONE) {
                subtractGoods.setVisibility(View.GONE);
                tvGoodsCount.setText("");
            }

            AppCompatImageView ivGoodsImg = holder.getIvGoodsImg();
            ViewGroup.LayoutParams layoutParams = ivGoodsImg.getLayoutParams();

            int width = (int) (mScreenWidth * 200F / 640F + 0.5F);
            layoutParams.width = width;
            layoutParams.height = (int) (width * 168F / 200F + 0.5F);
            ivGoodsImg.setLayoutParams(layoutParams);

            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(goods_id);
            ImageLoader.getImageFromNetwork(ivGoodsImg, dataBean.getString("IMG_FILE_PATH"), mContext, net.twoant.master.R.drawable.ic_def_small);
            holder.getTvGoodsTitle().setText(dataBean.getStringDef("TITLE", ""));
            String 会员价=dataBean.getStringDef("MEMBER_PRICE",dataBean.getStringDef("PUB_PRICE", "?"));
            会员价=" 会员价:"+会员价;
            holder.getTvGoodsPrice().setText("原价:"+ StringUtils.subZeroAndDot(String.valueOf(dataBean.getStringDef("PUB_PRICE", "?")))+会员价);
            //  holder.getTvGoodsPrice().setText(String.valueOf("￥" + StringUtils.subZeroAndDot(String.valueOf(dataBean.getString("PRICE")))));
            holder.getTvIntroduce().setText(dataBean.getString("SUB_TITLE"));

            holder.getTvData().setText(String.valueOf("库存: " + dataBean.getStringDef("QTY", "0")));

            AppCompatImageButton ivAddGoods = holder.getIvAddGoods();
            ivAddGoods.setTag(position);
            ivAddGoods.setTag(ID_ADD_GOODS, goods_id);
            ivAddGoods.setOnClickListener(this);

            subtractGoods.setTag(position);
            subtractGoods.setTag(ID_SUBTRACT_GOODS, goods_id);
            subtractGoods.setOnClickListener(this);
        }
    }

    @Override
    protected MerchantHomeGoodsViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new MerchantHomeGoodsViewHolder(layoutInflater.inflate(net.twoant.master.R.layout.yh_item_merchant_goods, parent, false), viewType);
    }

    @Override
    protected int getItemViewType(int position, DataRow data) {
        return TYPE_RIGHT;
    }

    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (mViewHolders != null && holder instanceof MerchantHomeGoodsViewHolder) {
            mViewHolders.put(holder.getAdapterPosition(), (MerchantHomeGoodsViewHolder) holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mViewHolders != null && holder instanceof MerchantHomeGoodsViewHolder)
            mViewHolders.delete(holder.getAdapterPosition());
    }

    public void updateList(ArrayMap<String, GoodsItemBean> newGoodsItemArrayMap) {
        if (newGoodsItemArrayMap.isEmpty()) {
            mGoodsItems = newGoodsItemArrayMap;
            this.notifyDataSetChanged();
            return;
        }

        final ArrayMap<String, GoodsItemBean> olderGoodsItems = mGoodsItems;
        mGoodsItems = newGoodsItemArrayMap;
        final HashSet<String> goodsIds = new HashSet<>(newGoodsItemArrayMap.size() + olderGoodsItems.size());
        Set<String> tempKey;
        if (!olderGoodsItems.isEmpty()) {
            tempKey = olderGoodsItems.keySet();
            for (String old : tempKey) {
                goodsIds.add(old);
            }
        }
        tempKey = newGoodsItemArrayMap.keySet();
        for (String old : tempKey) {
            goodsIds.add(old);
        }

        if (goodsIds.isEmpty()) {
            return;
        }

        final SimpleArrayMap<String, Integer> positions = new SimpleArrayMap<>(mPositionMap);
        MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder;
        GoodsItemBean goodsItemTemp;
        for (String goodsId : goodsIds) {
            Integer integer = positions.get(goodsId);
            if (null == integer) {
                continue;
            }
            merchantHomeGoodsViewHolder = mViewHolders.get(integer);
            if (null != merchantHomeGoodsViewHolder) {
                int count = null == (goodsItemTemp = newGoodsItemArrayMap.get(goodsId)) ? 0 : goodsItemTemp.getGoodsCount();
                if (0 == count && View.VISIBLE == merchantHomeGoodsViewHolder.getIvSubtractGoods().getVisibility()) {
                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText("");
                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(), merchantHomeGoodsViewHolder.getIvAddGoods());
                } else {
                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(count));
                }
            }
        }
        goodsIds.clear();
        positions.clear();
    }

    private List<DataRow> initPositionMapKey(List<DataRow> list) {
        if (null == mPositionMap) {
            mPositionMap = new SimpleArrayMap<>(list.size());
        } else {
            mPositionMap.clear();
        }
        DataRow dataRow;
        for (int i = list.size() - 1; i >= 0; --i) {
            dataRow = list.get(i);
            if (null != dataRow) {
                mPositionMap.put(dataRow.getString("ID"), i);
            }
        }
        return list;
    }


    private ArrayList<GoodsItemBean> mGoodsArray;

    @NonNull
    public ArrayList<GoodsItemBean> getGoodsItems() {
        Set<String> goods = mGoodsItems.keySet();
        if (mGoodsArray == null)
            mGoodsArray = new ArrayList<>();
        else
            mGoodsArray.clear();

        for (String integer : goods) {
            mGoodsArray.add(mGoodsItems.get(integer));
        }
        return mGoodsArray;
    }

    public void setOnDataChangeListener(IOnDataChangeListener iOnDataChangeListener) {
        this.iOnDataChangeListener = iOnDataChangeListener;
    }
    public void setGoodsItems(ArrayMap<String, GoodsItemBean> goodsItems, boolean isReplay) {
        if (isReplay) {
            this.mGoodsItems = goodsItems;
        } else {
            mGoodsItems.clear();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case net.twoant.master.R.id.ll_item_loading:
                if (v instanceof ViewGroup)
                    BaseRecyclerAdapter.resetState((ViewGroup) v);
                if (iLoadingData != null)
                    iLoadingData.getData();
                break;
            //添加商品
            case net.twoant.master.R.id.iv_add_goods:
                Object position = v.getTag();
                Object goodsId = v.getTag(ID_ADD_GOODS);

                if (position != null && goodsId != null && position instanceof Integer && goodsId instanceof String)
                    setGoodsBean(v, (Integer) position, (String) goodsId, true);
                break;
            //减少商品
            case net.twoant.master.R.id.iv_subtract_goods:
                Object pos = v.getTag();
                Object goods = v.getTag(ID_SUBTRACT_GOODS);

                if (pos != null && goods != null && pos instanceof Integer && goods instanceof String)
                    setGoodsBean(v, (Integer) pos, (String) goods, false);
                break;

            case net.twoant.master.R.id.iv_goods_img:
                v.setClickable(false);
                Object id = v.getTag(ID_GOODS_ID);
                if (id instanceof String) {
                    if (iOnItemClickListener != null) {
                        iOnItemClickListener.onItemClickListener((String) id);
                    }
                }
                v.setClickable(true);
                break;

            case net.twoant.master.R.id.ll_goods_parent:
                Object goods_id = v.getTag();
                if (goods_id instanceof String) {
                    if (iOnItemClickListener != null) {
                        iOnItemClickListener.onItemClickListener((String) goods_id);
                    }
                }
                break;
        }

    }

    /**
     * @param pressView 当前事件View
     * @param position  适配器位置
     * @param goodsId   商品id
     * @param isAdd     是否是增加
     */
    private void setGoodsBean(View pressView, int position, String goodsId, boolean isAdd) {
        GoodsItemBean goodsItem = mGoodsItems.get(goodsId);
        if (goodsItem != null) {
            int goodsCount = goodsItem.getGoodsCount();

            if (isAdd) {
                goodsItem.setGoodsCount(++goodsCount);
                goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice()).add(new BigDecimal(goodsItem.getGoodsUnitPrice())).setScale(2, RoundingMode.HALF_UP).floatValue());
            } else {
                goodsItem.setGoodsCount(--goodsCount);

                if (goodsCount >= 0) {
                    goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice()).subtract(new BigDecimal(goodsItem.getGoodsUnitPrice())).setScale(2, RoundingMode.HALF_UP).floatValue());
                }
            }


            //隐藏减号图标 清空显示
            MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
            if (merchantHomeGoodsViewHolder != null) {
                if (goodsCount <= 0) {
                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText("");
                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
                            merchantHomeGoodsViewHolder.getIvAddGoods());
                    mGoodsItems.remove(goodsId);
                } else {
                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(goodsCount));
                    if (isAdd && goodsCount == 1) {
                        GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
                                merchantHomeGoodsViewHolder.getIvAddGoods());
                    }
                }
            }
        } else {
            DataRow resultBean = mDataList.get(position);
            goodsItem = new GoodsItemBean();
            goodsItem.setGoodsCount(1);
//            goodsItem.setGoodsName(resultBean.getStringDef("NM", ""));
//            goodsItem.setGoodsId(String.valueOf(resultBean.getString("ID")));
//            goodsItem.setGoodsAvatar(BaseConfig.getCorrectImageUrl(resultBean.getStringDef("IMG", "")));


            goodsItem.setGoodsId(String.valueOf(resultBean.getString("ID")));
            goodsItem.setGoodsAvatar(BaseConfig.getCorrectImageUrl(resultBean.getStringDef("IMG_FILE_PATH", "")));

            goodsItem.setGoods_img(resultBean.getString("IMG_FILE_PATH"));
            goodsItem.setStock_qty(resultBean.getInt("QTY"));
            try {
                Float price = AiSouAppInfoModel.IS_VIP==1?resultBean.getFloat("MEMBER_PRICE"): resultBean.getFloat("PUB_PRICE");
                goodsItem.setGoodsPrice(price);
                goodsItem.setGoodsUnitPrice(price);
                goodsItem.setMember_price(resultBean.getFloat("MEMBER_PRICE"));
                //goodsItem.setgoo
            } catch (Exception e) {
                LogUtils.e(TAG + e.toString());
            }
            goodsItem.setGoodsName(resultBean.getStringDef("TITLE", ""));
            mGoodsItems.put(goodsItem.getGoodsId(), goodsItem);
            if (isAdd) {
                MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
                if (merchantHomeGoodsViewHolder != null) {

                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(1));
                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
                            merchantHomeGoodsViewHolder.getIvAddGoods());
                }
            }
        }

        if (null != iOnBtnClickListener) {
            iOnBtnClickListener.onBtnClickListener(pressView, goodsId, isAdd);
        }
    }


//    /**
//     * @param pressView 当前事件View
//     * @param position  适配器位置
//     * @param goodsId   商品id
//     * @param isAdd     是否是增加
//     */
//    private void setGoodsBean(View pressView, int position, String goodsId, boolean isAdd) {
//
//        GoodsItemBean goodsItem = mGoodsItems.get(goodsId);
//
//        if (goodsItem != null) {
//
//            int goodsCount = goodsItem.getGoodsCount();
//
//            if (isAdd) {
//                if (goodsItem.getStock_qty()==goodsCount){
//                    return;
//                }
//                goodsItem.setGoodsCount(++goodsCount);
//                float press = goodsItem.getGoodsPrice() + goodsItem.getGoodsUnitPrice();
//                goodsItem.setGoodsPrice(new BigDecimal(press).setScale(2, RoundingMode.HALF_UP).floatValue());
//            } else {
//
//                goodsItem.setGoodsCount(--goodsCount);
//
//                if (goodsCount >= 0) {
//                    goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice() - goodsItem.getGoodsUnitPrice()).setScale(2, RoundingMode.HALF_UP).floatValue());
//                }
//            }
//
//
//            //隐藏减号图标 清空显示
//            MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
//            if (merchantHomeGoodsViewHolder != null) {
//                if (goodsCount <= 0) {
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText("");
//                    hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(), merchantHomeGoodsViewHolder.getIvAddGoods(), false);
//                    mGoodsItems.remove(goodsId);
//                } else {
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(goodsCount));
//                    if (isAdd && goodsCount == 1) {
//                        hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(), merchantHomeGoodsViewHolder.getIvAddGoods(), true);
//                    }
//                }
//
//                if (iOnDataChangeListener != null) {
//                    if (isAdd)
//                        mGoodsAnimation.initAnim(pressView, iOnDataChangeListener.getCartView());
//                    else
//                        iOnDataChangeListener.onDataChangeListener(getGoodsItems());
//                }
//            }
//        } else {
//            goodsItem = new GoodsItemBean();
//            int goodsCount = goodsItem.getGoodsCount();
//            goodsItem.setGoodsCount(++goodsCount);
//            DataRow resultBean = mDataList.get(position);
//            goodsItem.setGoodsId(String.valueOf(resultBean.getString("ID")));
//            goodsItem.setGoods_img(resultBean.getString("IMG_FILE_PATH"));
//            goodsItem.setStock_qty(resultBean.getInt("QTY"));
//            try {
//                Float price = AiSouAppInfoModel.IS_VIP==1?resultBean.getFloat("MEMBER_PRICE"): resultBean.getFloat("PUB_PRICE");
//                goodsItem.setGoodsPrice(price);
//                goodsItem.setGoodsUnitPrice(price);
//                goodsItem.setMember_price(resultBean.getFloat("MEMBER_PRICE"));
//                //goodsItem.setgoo
//            } catch (Exception e) {
//                LogUtils.e(TAG + e.toString());
//            }
//            goodsItem.setGoodsName(resultBean.getStringDef("TITLE", ""));
//            mGoodsItems.put(goodsItem.getGoodsId(),  goodsItem);
//            if (isAdd) {
//                MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
//                if (merchantHomeGoodsViewHolder != null) {
//
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(goodsCount));
//                    hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(), merchantHomeGoodsViewHolder.getIvAddGoods(), true);
//                    if (iOnDataChangeListener != null) {
//                        mGoodsAnimation.initAnim(pressView, iOnDataChangeListener.getCartView());
//                    }
//                }
//            }
//        }
//    }
//    /**
//     * @param pressView 当前事件View
//     * @param position  适配器位置
//     * @param goodsId   商品id
//     * @param isAdd     是否是增加
//     */
//    private void setGoodsBean(View pressView, int position, String goodsId, boolean isAdd) {
////        GoodsItemBean goodsItem = mGoodsItems.get(goodsId);
////        if (goodsItem != null) {
////            int goodsCount = goodsItem.getGoodsCount();
////
////            if (isAdd) {
////                goodsItem.setGoodsCount(++goodsCount);
////                goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice()).add(new BigDecimal(goodsItem.getGoodsUnitPrice())).setScale(2, RoundingMode.HALF_UP).floatValue());
////            } else {
////                goodsItem.setGoodsCount(--goodsCount);
////
////                if (goodsCount >= 0) {
////                    goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice()).subtract(new BigDecimal(goodsItem.getGoodsUnitPrice())).setScale(2, RoundingMode.HALF_UP).floatValue());
////                }
////            }
////
////
////            //隐藏减号图标 清空显示
////            MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
////            if (merchantHomeGoodsViewHolder != null) {
////                if (goodsCount <= 0) {
////                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText("");
////                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
////                            merchantHomeGoodsViewHolder.getIvAddGoods());
////                    mGoodsItems.remove(goodsId);
////                } else {
////                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(goodsCount));
////                    if (isAdd && goodsCount == 1) {
////                        GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
////                                merchantHomeGoodsViewHolder.getIvAddGoods());
////                    }
////                }
////            }
//
//
//
//        GoodsItemBean goodsItem = mGoodsItems.get(goodsId);
//
//        if (goodsItem != null) {
//
//            int goodsCount = goodsItem.getGoodsCount();
//
//            if (isAdd) {
//                if (goodsItem.getStock_qty()==goodsCount){
//                    return;
//                }
//                goodsItem.setGoodsCount(++goodsCount);
//                float press = goodsItem.getGoodsPrice() + goodsItem.getGoodsUnitPrice();
//                goodsItem.setGoodsPrice(new BigDecimal(press).setScale(2, RoundingMode.HALF_UP).floatValue());
//            } else {
//
//                goodsItem.setGoodsCount(--goodsCount);
//
//                if (goodsCount >= 0) {
//                    goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice() - goodsItem.getGoodsUnitPrice()).setScale(2, RoundingMode.HALF_UP).floatValue());
//                }
//            }
//
//
//            //隐藏减号图标 清空显示
//            MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
//            if (merchantHomeGoodsViewHolder != null) {
//                if (goodsCount <= 0) {
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText("");
//                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
//                           merchantHomeGoodsViewHolder.getIvAddGoods());
//                    mGoodsItems.remove(goodsId);
//                } else {
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(goodsCount));
//                    if (isAdd && goodsCount == 1) {
//                        GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
//                                merchantHomeGoodsViewHolder.getIvAddGoods()); }
//                }
//
//                if (iOnDataChangeListener != null) {
//                    if (isAdd)
//                        mGoodsAnimation.initAnim(pressView, iOnDataChangeListener.getCartView());
//                    else
//                        iOnDataChangeListener.onDataChangeListener(getGoodsItems());
//                }
//            }
//
//
//
//
//        } else {
//            DataRow resultBean = mDataList.get(position);
//
//            goodsItem = new GoodsItemBean();
//            goodsItem.setGoodsCount(1);
//            goodsItem.setGoodsName(resultBean.getStringDef("NM", ""));
//            goodsItem.setGoodsId(String.valueOf(resultBean.getString("ID")));
//        //    goodsItem.setGoodsAvatar(BaseConfig.getCorrectImageUrl(resultBean.getStringDef("IMG", "")));
//
//            try {
//                Float price = Float.valueOf(resultBean.getStringDef("PRICE", "0"));
//                goodsItem.setGoodsPrice(price);
//                goodsItem.setGoodsUnitPrice(price);
//            } catch (Exception e) {
//                LogUtils.e(TAG + e.toString());
//            }
//            mGoodsItems.put(goodsId, goodsItem);
//            if (isAdd) {
//                MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
//                if (merchantHomeGoodsViewHolder != null) {
//
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(1));
//                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
//                            merchantHomeGoodsViewHolder.getIvAddGoods());
//                }
//            }
//            goodsItem = new GoodsItemBean();
//            int goodsCount = goodsItem.getGoodsCount();
//            goodsItem.setGoodsCount(++goodsCount);
//            DataRow resultBean = mDataList.get(position);
//            goodsItem.setGoodsId(String.valueOf(resultBean.getString("PRODUCT_ID")));
//            goodsItem.setGoods_img(resultBean.getString("IMG_FILE_PATH"));
//            goodsItem.setStock_qty(resultBean.getInt("QTY"));
//            try {
//                Float price = AiSouAppInfoModel.IS_VIP==1?resultBean.getFloat("MEMBER_PRICE"): resultBean.getFloat("PUB_PRICE");
//                goodsItem.setGoodsPrice(price);
//                goodsItem.setGoodsUnitPrice(price);
//                goodsItem.setMember_price(resultBean.getFloat("MEMBER_PRICE"));
//                //goodsItem.setgoo
//            } catch (Exception e) {
//                LogUtils.e(TAG + e.toString());
//            }
//            goodsItem.setGoodsName(resultBean.getStringDef("TITLE", ""));
//            mGoodsItems.put(goodsId, goodsItem);
//            if (isAdd) {
//                MerchantHomeGoodsViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
//                if (merchantHomeGoodsViewHolder != null) {
//
//                    merchantHomeGoodsViewHolder.getTvGoodsCount().setText(String.valueOf(goodsCount));
//                    GoodsAnimation.hideOrShowSubtract(merchantHomeGoodsViewHolder.getIvSubtractGoods(),
//                            merchantHomeGoodsViewHolder.getIvAddGoods());
//                }
//            }
//        }
//
//        if (null != iOnBtnClickListener) {
//            iOnBtnClickListener.onBtnClickListener(pressView, goodsId, isAdd);
//        }
//    }


    private void hideOrShowSubtract(final View view, View location, boolean isShow) {

        if (isShow) {
            view.setTranslationX(location.getX());
            view.animate().translationX(0).rotation(360).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (view.getVisibility() != View.VISIBLE)
                        view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
        } else
            view.animate().translationX(location.getX()).rotation(360).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (view.getVisibility() != View.VISIBLE)
                        view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
    }
}

