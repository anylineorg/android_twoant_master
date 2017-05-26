package net.twoant.master.ui.my_center.httputils;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DZY on 2016/12/1.
 */

public class SearchGoodsHttpUtils {

    private static  Map<String,String> map = null;

    static {
        if (map == null) {
            map = new HashMap<>();
        }
    }

    private SearchGoodsHttpUtils() {
    }

    public static void LongHttp(String goods_id,String shop_id,String istrue,String aisou_id,String is_sale, StringCallback callback){
        /**
         * 根据各参数分别查找都可为空
         * */
        map.put("goods_id",goods_id);
        map.put("shop_id",shop_id);
        map.put("is_goods_img",istrue);
        map.put("is_sale",is_sale);     //  0 上架 ； 1下架
        map.put("aisou_id",aisou_id);
        map.put("page","0");
        map.put("limit","2000");
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        for (String key : map.keySet()) {
            LogUtils.d("key:"+ map.get(key));
        }
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.GUO + "search_goods.do").params(map).build().execute(callback);
    }


}
