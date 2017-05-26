package net.twoant.master.app;

import android.support.annotation.Nullable;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.api.AppConfig;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.SharedPreferencesUtils;
import net.twoant.master.widget.entry.DataRow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by DZY on 2017/3/11.
 * 佛祖保佑   永无BUG
 */

public class GdNearbyUserHelp  implements HttpConnectedUtils.IOnStartNetworkCallBack {
    private HttpConnectedUtils utilsInstance;
    private Map map;
    private final static int ADDUSER = 0x1;
    private final static int UPDATEUSER = 0x2;
    private final static int SEARCH = 0x3;
    private final static int DELETE = 0x4;
    private final static int GETINFO = 0x5;
    private final static int USERINFO = 0X6;
    private final static int UPDATEUSER_FINISH = 0X7;
    public String uid;
    public OnNearbyUserListener onNearbyUserListener;
    public int id_search;

    public GdNearbyUserHelp() {
        if (null == utilsInstance) {
            utilsInstance = HttpConnectedUtils.getInstance(this);
            map = new HashMap();
        }
    }


    public void addUser(String nickname,String age,String autographo,String uid,String sex,String photourl){
        map.clear();
        map.put("key", AppConfig.GaoDe_Key);
        map.put("tableid", AppConfig.NEARBY_USER_TABLEID);
        map.put("loctype","1");
        String latitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude()+"";
        String longitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()+"";
        String str = "{" +
                "  \"_name\": \""+uid+"\"," +
                "  \"_location\": \""+longitude+""+","+latitude+""+"\"," +
                "  \"age\": \""+age+"\"," +
                "  \"autographo\": \""+autographo+"\", "+
                "  \"sex\": \""+sex+"\", "+
                "  \"nickname\": \""+nickname+"\"," +
                "  \"uphoto\": \""+photourl+"\"" +
                " }";
        map.put("data",str);

        utilsInstance.startNetworkGetString(ADDUSER,map, ApiConstants.ADD_USER);


    }

    @Override
    public void onBefore(Request request, int id) {

    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id){
            case SEARCH:
                System.out.println(response+"");
                DataRow dataRow2 = DataRow.parseJson(response);
                int count = dataRow2.getInt("count");

                if (count == 0) {
                    // 0为没有该用户，添加到云图  (先查)
                    map.put("aisou_id",uid);
                    utilsInstance.startNetworkGetString(USERINFO,map,ApiConstants.GET_USER_INFO);
                } else if (count == 1) {
                    // 1为已存在该用户，更新云图信息 (先查)
                    List<DataRow> datas = dataRow2.getSet("datas").getRows();
                    id_search = datas.get(0).getInt("_id");
                    map.put("aisou_id",uid);
                    utilsInstance.startNetworkGetString(UPDATEUSER,map,ApiConstants.GET_USER_INFO);
                }else {
                    // count>1 为多条用户信息，删除多余的信息，仅留一条
                    List<DataRow> datas = dataRow2.getSet("datas").getRows();
                    String ids = "";
                    for (int i = 1 ; i < count;i++) {
                        int id_delete = datas.get(1).getInt("_id");
                        ids = ids + ","+ id_delete;
                    }
                    deleteData(ids);
                }
                break;

            case ADDUSER:
                int status = DataRow.parseJson(response).getInt("status");
                if (1 == status) {
                    searchNearbtUser();
                }
                break;

            case UPDATEUSER:
                Date now = new Date();
                long currentTime = now.getTime();
                long nearby_time = SharedPreferencesUtils.getSharedLongData("nearby_time");
                System.out.println(currentTime+":::"+nearby_time);
                if (currentTime>(nearby_time+600000)) {
                    DataRow dataRow1 = DataRow.parseJson(response).getRow("result");
                    updateUserData(""+id_search,dataRow1.getString("nick_name"),dataRow1.getString("age"),dataRow1.getString("autograph"),dataRow1.getString("uid"),dataRow1.getString("sex"),dataRow1.getString("avatar"));
                    SharedPreferencesUtils.setSharedLongData("nearby_time",currentTime);
                }
                break;

            case DELETE:

                break;

            case GETINFO:
                onNearbyUserListener.setResponse(response);
                break;

            case USERINFO:
                DataRow dataRow = DataRow.parseJson(response).getRow("result");
                addUser(dataRow.getString("nick_name"),dataRow.getString("age"),dataRow.getString("autograph"),dataRow.getString("uid"),dataRow.getString("sex"),dataRow.getString("avatar"));
                break;

            case UPDATEUSER_FINISH:
                //更新用户信息结束
//                System.out.println(response);
                int status1 = DataRow.parseJson(response).getInt("status");
                if (1 == status1) {
//                    searchNearbtUser();
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }


    public void searchNearbtUser(){
        String latitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude()+"";
        String longitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()+"";
        map.clear();
        map.put("key",AppConfig.GaoDe_Key);
        map.put("tableid", AppConfig.NEARBY_USER_TABLEID);
        map.put("center",longitude+","+latitude);
        map.put("radius","50000");
//        map.put("limit","");  //每页记录数
//        map.put("page","");   //当前页数
        utilsInstance.startNetworkGetString(GETINFO,map,ApiConstants.SEARCHINFO);
    }

    public void search(String uid,OnNearbyUserListener onNearbyUserListener){
        this.uid = uid;
        this.onNearbyUserListener = onNearbyUserListener;
        map.clear();
        map.put("key",AppConfig.GaoDe_Key);
        map.put("tableid", AppConfig.NEARBY_USER_TABLEID);
        map.put("filter","_name:"+ uid);
        utilsInstance.startNetworkGetString(SEARCH,map,ApiConstants.SEARCH);
    }

    public void updateUserData(String id,String nickname,String age,String autographo,String uid,String sex,String photourl){
        map.clear();
        map.put("key",AppConfig.GaoDe_Key);
        map.put("tableid", AppConfig.NEARBY_USER_TABLEID);
        String latitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude()+"";
        String longitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()+"";
        String data = "{" +
                "  \"_id\": \""+id+"\"," +
                "  \"_name\": \""+uid+"\"," +
                "  \"_location\": \""+longitude+""+","+latitude+""+"\"," +
                "  \"age\": \""+age+"\"," +
                "  \"autographo\": \""+autographo+"\", "+
                "  \"nickname\": \""+nickname+"\"," +
                "  \"sex\": \""+sex+"\"," +
                "  \"uphoto\": \""+photourl+"\"" +
                " }";
        map.put("data",data);
        utilsInstance.startNetworkGetString(UPDATEUSER_FINISH,map,ApiConstants.UPDATE_TABLE);
    }

    public void deleteData(String ids){
        map.clear();
        map.put("key",AppConfig.GaoDe_Key);
        map.put("tableid", AppConfig.NEARBY_USER_TABLEID);
        map.put("ids",ids);//ids 删除的数据_id 一次请求限制1-50条数据, 多个_id用逗号隔开
        utilsInstance.startNetworkGetString(DELETE,map,ApiConstants.DELETE);
    }


    public interface OnNearbyUserListener {
        void setResponse(String response);
    }
}
