package net.twoant.master.ui.my_center.httputils;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DZY on 2016/12/1.
 */

public class ClassifyHttpUtils {

    private static  Map map = null;

    static {
        if (map == null) {
            map = new HashMap<>();
        }
    }

    private ClassifyHttpUtils() {
    }
    public static void LongHttp(String action,String shop_id,String type_name,String goods_typeid, StringCallback callback){
        map.put("action",action);
        map.put("shop_id",shop_id);
        map.put("type_name",type_name);
        map.put("goods_typeid",goods_typeid);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.GUO + "goods_type.do").params(map).build().execute(callback);
    }
}
