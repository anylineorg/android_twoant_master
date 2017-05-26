package net.twoant.master.ui.main.presenter;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.widget.entry.DataRow;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2017/3/31.
 * 买家看到商品首页
 */

public final class BuyerGoodsDetailControl implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack {

    private final static int ID_GET_DETAIL = 1;
    /**
     * 查询收藏状态
     */
    private final static int ID_GET_COLLECTION = 2;
    /**
     * 修改收藏状态
     */
    private final static int ID_CHANGE_COLLECTION = 3;
    private HttpConnectedUtils mHttpUtils;
    private IOnBuyerGoodsDetailListener iOnBuyerGoodsDetailListener;

    public BuyerGoodsDetailControl(IOnBuyerGoodsDetailListener iOnBuyerGoodsDetailListener) {
        this();
        this.iOnBuyerGoodsDetailListener = iOnBuyerGoodsDetailListener;
    }

    public void onDestroy() {
        if (null != mHttpUtils) {
            mHttpUtils.onDestroy();
            mHttpUtils = null;
        }
        iOnBuyerGoodsDetailListener = null;
    }

    public interface IOnBuyerGoodsDetailListener {
        /**
         * 查询收藏状态失败
         */
        int QUERY_COLLECTION_FAIL = 0xA1;
        /**
         * 查询收藏 状态 收藏
         */
        int IS_COLLECTION = 0x2;
        /**
         * 查询收藏 状态 为收藏
         */
        int NOT_COLLECTION = 0xA3;
        /**
         * 取消收藏成功
         */
        int CANCEL_COLLECTION_SUCCESSFUL = 0xA4;
        /**
         * 进行收藏成功
         */
        int DO_COLLECTION_SUCCESSFUL = 0xA5;

        int DIALOG_SHOW_LOADING = 0xA6;
        int DIALOG_SHOW_ERROR = 0xA7;
        int DIALOG_CLOSE = 0xA8;

        void onRequestDetailSuccessful(@NonNull DataRow result);

        void onRequestCollection(int state);

        void showDialog(int state, String hint);
    }

    public BuyerGoodsDetailControl() {
        mHttpUtils = HttpConnectedUtils.getInstance(this);
    }

    public void start(String goodsId) {
        if (null != iOnBuyerGoodsDetailListener) {
            iOnBuyerGoodsDetailListener.showDialog(IOnBuyerGoodsDetailListener.DIALOG_SHOW_LOADING, "加载中…");
        }
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        keyValue.put("id", TextUtils.isEmpty(goodsId) ? "" : goodsId);
        keyValue.put("_t", AiSouAppInfoModel.getInstance().getToken());
        mHttpUtils.startNetworkGetString(ID_GET_DETAIL, keyValue, ApiConstants.BUYER_GOODS_DETAIL);

        keyValue.put("id", TextUtils.isEmpty(goodsId) ? "" : goodsId);
        mHttpUtils.startNetworkGetString(ID_GET_COLLECTION, keyValue, ApiConstants.GET_COLLECT_STATE);
    }

    /**
     * 收藏商品
     *
     * @param goodId 商品id
     */
    public void chageCollectionGoodState(String goodId) {
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        keyValue.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        keyValue.put("id", TextUtils.isEmpty(goodId) ? "" : goodId);
        mHttpUtils.startNetworkGetString(ID_CHANGE_COLLECTION, keyValue, ApiConstants.ALTER_COLLECT_GOODS);
    }


    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_GET_DETAIL:
                initGoodsDetail(response);
                break;

            case ID_GET_COLLECTION:
                initCollectionState(response, false);
                break;

            case ID_CHANGE_COLLECTION:
                initCollectionState(response, true);
                break;
        }
    }

    /**
     * 初始化收藏状态
     */
    private void initCollectionState(String response, boolean isChange) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != iOnBuyerGoodsDetailListener) {
            if (null != dataRow && dataRow.getBoolean("result", false)) {
                iOnBuyerGoodsDetailListener.onRequestCollection(dataRow.getBoolean("data", false) ?
                        (isChange ? IOnBuyerGoodsDetailListener.DO_COLLECTION_SUCCESSFUL : IOnBuyerGoodsDetailListener.IS_COLLECTION) :
                        (isChange ? IOnBuyerGoodsDetailListener.CANCEL_COLLECTION_SUCCESSFUL : IOnBuyerGoodsDetailListener.NOT_COLLECTION));
            } else {
                iOnBuyerGoodsDetailListener.onRequestCollection(IOnBuyerGoodsDetailListener.QUERY_COLLECTION_FAIL);
            }
        }
    }

    /**
     * {"result":true,"code":"200",
     * <p>
     * "data":{"REG_TIME":"2017-03-30 14:15:06","SHOP_TOWN_CODE":"370211",
     * "SORT_ID":584,"SHOP_ID":335,"GOODS_TYPE":584,"SHOP_LON":"119.98423",
     * "LONGITUDE":null,"SHOP_CITY_CODE":"0532",
     * "GOODS_IMG":"activity_img/149085432937176db1396.jpg",
     * "GOODS_LD":null,"SHOP_LAT":"35.8403","SHOP_LONGITUDE":"119.98423",
     * "GOODS_SEND_PRICE":0,"GOODS_INTRODUCE":"大鹅一只",
     * "GOODS_NAME":"正宗东北大鹅，东北家常菜","ID":1000,
     * "UPT_TIME":"2017-03-31 22:06:09","SORT_NM":"炒菜，凉菜",
     * "MAP_ID":3725,
     * "SHOP_ADDRESS":"山东省青岛市黄岛区滨海街道珠山南路22-5号",
     * <p>
     * "IMG":{"GOODS_BANDER_IMG3":"activity_img/1490854149774f8885a3e.jpg",
     * "GOODS_BANDER_IMG4":"activity_img/1490854149789cf03f6d4.jpg",
     * "GOODS_BANDER_DT4":null,"GOODS_IMG_TXT":"大鹅特价一只168，半只88",
     * "GOODS_BANDER_IMG1":"activity_img/1490854149789d4304415.jpg",
     * "GOODS_ID":1000,"GOODS_BANDER_IMG2":"activity_img/1490854149789d1987ed5.jpg",
     * "ID":966,"GOODS_BANDER_DT1":null,"GOODS_BANDER_DT3":null,"GOODS_BANDER_DT2":null},
     * <p>
     * "PRICE":168,"GOODS_TITLE":"商品标题","GOODS_STATE":0,"SHOP_NM":"东北骨头城",
     * "TIME":"2017-03-30 14:15:06","UNIT_NM":null,"DATA_STATUS":1,"SELL_QTY":0,"SHOP_IS_REVIEWED":2,
     * "GOODS_PRICE":168,"GOODS_ID":1000,"ADDRESS":"山东省青岛市黄岛区珠山南路靠近禾田园蛋糕","GOODS_COUNT":0,
     * "CLICK":37,"SHOP_LATITUDE":"35.8403","GOODS_PK_SHOP":335,"LATITUDE":null,"NM":"正宗东北大鹅，东北家常菜"},
     * <p>
     * "success":true,"type":"map","message":null}
     *
     */
    private void initGoodsDetail(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != iOnBuyerGoodsDetailListener) {
            if (null != dataRow) {
                if (dataRow.getBoolean("result", false) && null != dataRow.getRow("data")) {
                    iOnBuyerGoodsDetailListener.onRequestDetailSuccessful(dataRow.getRow("data"));
                    iOnBuyerGoodsDetailListener.showDialog(IOnBuyerGoodsDetailListener.DIALOG_CLOSE, "");
                } else {
                    iOnBuyerGoodsDetailListener.showDialog(IOnBuyerGoodsDetailListener.DIALOG_SHOW_ERROR, dataRow.getStringDef("message", "未能获取到商品详情"));
                }
            } else {
                iOnBuyerGoodsDetailListener.showDialog(IOnBuyerGoodsDetailListener.DIALOG_SHOW_ERROR, "加载详情失败");
            }
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        switch (id) {
            case ID_GET_DETAIL:
                if (null != iOnBuyerGoodsDetailListener) {
                    iOnBuyerGoodsDetailListener.showDialog(IOnBuyerGoodsDetailListener.DIALOG_SHOW_ERROR, NetworkUtils.getNetworkStateDescription(call, e, "获取详情失败"));
                }
                break;
            case ID_GET_COLLECTION:
            case ID_CHANGE_COLLECTION:
                if (null != iOnBuyerGoodsDetailListener) {
                    iOnBuyerGoodsDetailListener.onRequestCollection(IOnBuyerGoodsDetailListener.QUERY_COLLECTION_FAIL);
                }
                break;
        }
    }
}
