package net.twoant.master.ui.my_center.httputils;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DZY on 2016/12/13.
 * 佛祖保佑   永无BUG
 */

public class PayHttpUtils {

    private static Map<String,String> map = null;
    private static List<GoodsItemBean> selectedList = PostOrderActivity.selectedList;

    static {
        if (map == null) {
            map = new HashMap<>();
        }
    }

    public static void PayHttpUtils(String order_no,StringCallback callback) {
        Map<String,String> map = new HashMap();
        map.put("order",order_no);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.ALI_PAY_NEW).params(map).build().execute(callback);
    }

    public static void getOder (String shopid,String voucher,String score,String purse,String sort,String product_type,StringCallback callback){
        Map<String,String> map = new HashMap();
        map.put("shop",shopid);
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("voucher",voucher);
        map.put("score",score);
        map.put("purse",purse);
        for (GoodsItemBean goodsItem : selectedList) {
            map.put("goods",goodsItem.getGoodsId());
            map.put("qty",goodsItem.getGoodsCount()+"");
        }
        map.put("sort",sort);
        map.put("ship_sort",product_type);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        StringBuilder stringBuilder = new StringBuilder();
        for (String s:map.keySet()){
            stringBuilder.append(s+":"+map.get(s)+"\r\n");
        }
        LogUtils.d(stringBuilder+"");
        OkHttpUtils.post().url(ApiConstants.GET_ORDDER).params(map).build().execute(callback);
    }
    public static void WXPay(String order,StringCallback callback){
        Map<String,String> map = new HashMap();
        map.put("order",order);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.WX_PAY_NEW).params(map).build().execute(callback);
    }

    public static void AliPay(String order,StringCallback callback){
        Map<String,String> map = new HashMap();
        map.put("order",order);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.ALI_PAY_NEW).params(map).build().execute(callback);
    }

    public static void isPassword(String user,String pwd,StringCallback callback){
        Map<String,String> map = new HashMap();
        map.put("user",user);
        map.put("pwd",pwd);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.CHECKPAYPWD).params(map).build().execute(callback);
    }

    public static void getPayPasswordState(StringCallback callback) {
        Map<String,String> map = new HashMap();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.STATE_PAY_PASSWORD).params(map).build().execute(callback);
    }
}
