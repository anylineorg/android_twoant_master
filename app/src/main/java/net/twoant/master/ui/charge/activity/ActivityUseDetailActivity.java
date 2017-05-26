package net.twoant.master.ui.charge.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.bean.MyActivityDetailBean;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.ErrorPayPasswordDialog;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.ScrollViewListView;
import net.twoant.master.widget.SetPasswordDialog;
import net.twoant.master.widget.entry.DataRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class ActivityUseDetailActivity extends LongBaseActivity implements View.OnClickListener{

    private PassViewDialog passViewDialog;
    private TextView titleActivity;
    private TextView shopName;
    private TextView bb;// 使用本次活动/使用金额
    private TextView cc;// 次/元
    private TextView useTime;
    private TextView shopTel,shopAddress;
    private EditText rechargeMoney;
    private ScrollViewListView listView;
    private ScrollViewListView lvHistory;
    private  List<MyActivityDetailBean.DataBean.ITEMSBean> items;
    private Map<Integer, Boolean> isSelected;//判断checkbox
    private List beSelectedData = new ArrayList();
    private static String parent_activity_id;
    private static int type;
    private MyAdapter adapter;
    private RelativeLayout rlUseActivity;
    private HintDialogUtil hintDialogUtil;
    private String data;//是否有支付密码 1有; 2没有
    private HintDialogUtil stateDialog;
    private List<Integer> itemIdList;//存放item卡的id
    private List<String> itemTitleList;//存放item卡的标题名
    private int itemSlectedPosition = 0;//item默认选中第一个
    private  List<MyActivityDetailBean.DataBean.USESBean> uses;//历史记录集合
    private MyActicityHistoryAdapter historyAdapter;
    private boolean clickedUse = false;//是否点击了使用活动，用于判断是否再次获取有支付密码状态
    public int shop_id;
    private ErrorPayPasswordDialog errorPayPasswordDialog;

    public static void startActivity(Context context){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ActivityUseDetailActivity.class);
        activity.startActivity(intent);
    }
    public static void startActivity(Context context,String p_id,String typestr){
        Activity activity = (Activity) context;
        parent_activity_id = p_id;
        type = Integer.parseInt(typestr);
        Intent intent = new Intent(context, ActivityUseDetailActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.zy_activity_activityusedetail;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(R.id.btn_enter_activityusedetail).setOnClickListener(this);
        findViewById(R.id.ll_shop_activityusedetail).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        titleActivity = (TextView) findViewById(R.id.tv_title_activityusedetail);
        shopName = (TextView) findViewById(R.id.tv_shopname_activityusedetail);
        useTime = (TextView) findViewById(R.id.tv_time_activityusedetail);
        shopTel = (TextView) findViewById(R.id.tv_tel_activityusedetail);
        bb = (TextView) findViewById(R.id.bb);
        cc = (TextView) findViewById(R.id.cc);
        shopAddress = (TextView) findViewById(R.id.tv_address_activityusedetail);
        rechargeMoney = (EditText) findViewById(R.id.et_ues_activityusedetail);
        rlUseActivity = (RelativeLayout) findViewById(R.id.tl_use_activityusedetail);
        listView = (ScrollViewListView) findViewById(R.id.lv_item_activityusedetail);
        lvHistory = (ScrollViewListView) findViewById(R.id.lv_history_activityusedetail);
        passViewDialog = new PassViewDialog(ActivityUseDetailActivity.this, Gravity.BOTTOM, true);
        errorPayPasswordDialog = new ErrorPayPasswordDialog(this,Gravity.CENTER);
        passViewDialog.setBigTile("请输入支付密码");
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                hintDialogUtil.showLoading();
                PayHttpUtils.isPassword(AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(), strPassword, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hintDialogUtil.dismissDialog();
                        ToastUtil.showLong("连接失败:"+e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow dataRow = DataRow.parseJson(response);
                        boolean success = (boolean) dataRow.get("success");
                        if (success) {
                            hintDialogUtil.showLoading(R.string.use_activity);
                            postUseActivity(rechargeMoney.getText().toString().trim());
                        }else{
                            hintDialogUtil.dismissDialog();
                            passViewDialog.clearn();
                            passViewDialog.cancel();
                            errorPayPasswordDialog.showDialog(true,true);
                            errorPayPasswordDialog.setReTry(new CancelCenterDialog.IOnClickListener() {
                                @Override
                                public void onClickListener(View v) {
                                    passViewDialog.showDialog(true,true);
                                }
                            });
//                            ToastUtil.showLong("支付密码错误,请重新输入");
                        }
                    }
                });
            }
        });
        switch(type){
            case 0:
                bb.setText("使用本次活动");
                rechargeMoney.setVisibility(View.INVISIBLE);
                cc.setVisibility(View.INVISIBLE);
                break;
            case 1:
                bb.setText("使用本次活动");
                rechargeMoney.setVisibility(View.INVISIBLE);
                cc.setVisibility(View.INVISIBLE);
                break;
            case 2:
                bb.setText("使用金额");
                rechargeMoney.setVisibility(View.VISIBLE);
                cc.setVisibility(View.VISIBLE);
                cc.setText("(元)");
                break;
            case 3:
                bb.setText("使用次数");
                rechargeMoney.setVisibility(View.VISIBLE);
                cc.setVisibility(View.VISIBLE);
                cc.setText("次");
                break;
        }
        passViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                passViewDialog.clearn();
            }
        });
    }

    @Override
    protected void initData() {
        hintDialogUtil = new HintDialogUtil(this);
        itemIdList = new ArrayList<>();
        itemTitleList = new ArrayList<>();
        stateDialog = new HintDialogUtil(ActivityUseDetailActivity.this);
        if (isSelected != null)
            isSelected = null;
        isSelected = new HashMap<>();
        // 清除已经选择的项
        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
    }

    @Override
    protected void requestNetData() {
        hintDialogUtil.showLoading();
        String url = ApiConstants.ACTIVITY_DETAIL;
        url = String.format(url, AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(),parent_activity_id);
        LongHttpGet(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("连接失败"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                JSONObject jsonObject = null;
                try {
                    items = new ArrayList<>();
                    MyActivityDetailBean  bean = new MyActivityDetailBean();
                    MyActivityDetailBean.DataBean data = new MyActivityDetailBean.DataBean();
                    jsonObject = new JSONObject(response);
                    JSONObject bsseJson = jsonObject.getJSONObject("data");
                    data.setSHOP_ADDRESS(bsseJson.getString("ADDRESS"));
                    data.setCLICK(bsseJson.getInt("CLICK"));
                    bsseJson.getString("COVER_IMG");//属性里没有
                    data.setEND_TIME(bsseJson.getString("END_TIME"));
                    bsseJson.getString("CREATE_TIME");//属性里没有
                    data.setID(bsseJson.getInt("ID"));
                    bsseJson.getString("IS_ENABLE");//属性里没有
                    MyActivityDetailBean.DataBean.ITEMSBean item = null;
                    JSONArray items_array = bsseJson.getJSONArray("ITEMS");
                    int length = items_array.length();
                    if (TextUtils.isEmpty(length+"") || 0==length) {
                        rlUseActivity.setVisibility(View.GONE);
                    }
                    System.out.println(length);
                    for (int i=0;i<items_array.length();i++) {
                        JSONObject json = (JSONObject) items_array.get(i);
                        item = new MyActivityDetailBean.DataBean.ITEMSBean();
                        item.setACTIVE_VAL(json.getInt("ACTIVE_VAL"));
                        item.setACTIVITY_ITEM_ID(json.getInt("ACTIVITY_ITEM_ID"));
                        item.setACTIVITY_ID(json.getInt("ACTIVITY_ID"));
                        item.setACTIVITY_ITEM_TITLE(json.getString("ACTIVITY_ITEM_TITLE"));
                        item.setACTIVITY_SORT_NM(json.getString("ACTIVITY_SORT_NM"));
                        item.setACTIVITY_TITLE(json.getString("ACTIVITY_TITLE"));
                        item.setBALANCE(json.getInt("BALANCE"));
                        item.setBUY_TIME(json.getString("BUY_TIME"));
                        item.setEND_TIME(json.getString("END_TIME"));
                        item.setID(json.getInt("ID"));
                        item.setIS_ENABLE(json.getInt("IS_ENABLE"));
                        item.setSHOP_ADDRESS(json.getString("SHOP_ADDRESS"));
                        item.setSHOP_AVATAR(json.getString("SHOP_AVATAR"));
                        item.setSHOP_ID(json.getInt("SHOP_ID"));
                        item.setSHOP_NM(json.getString("SHOP_NM"));
                        String shop_tel = json.getString("SHOP_TEL");
                        System.out.println(shop_tel);
                        item.setSHOP_TEL(json.getString("SHOP_TEL"));
                        item.setUSER_ID(json.getInt("USER_ID"));
                        item.setUSER_NM(json.getString("USER_NM"));
                        item.setUSE_STATUS(json.getInt("USE_STATUS"));
                        item.setVAL(json.getString("VAL"));
                        item.setORDER_ITEM_PRICE(json.getString("ORDER_ITEM_PRICE"));
                        item.setORDER_ITEM_SCORE(json.getString("ORDER_ITEM_SCORE"));
                        items.add(item);
                    }
                    //LAT LON USER_ID没有
                    data.setSHOP_ADDRESS(bsseJson.getString("SHOP_ADDRESS"));
                    data.setSHOP_AVATAR(bsseJson.getString("SHOP_AVATAR"));
                    shop_id = bsseJson.getInt("SHOP_ID");
                    data.setSHOP_ID(shop_id);
                    data.setSHOP_NM(bsseJson.getString("SHOP_NM"));
                    int shop_tel = bsseJson.getInt("SHOP_TEL");
                    System.out.println(shop_tel);
                    data.setSHOP_TEL(shop_tel);
                    data.setSORT_NM(bsseJson.getString("SORT_NM"));
                    data.setSTART_TIME(bsseJson.getString("START_TIME"));
                    data.setTITLE(bsseJson.getString("TITLE"));
                    JSONArray users_array = bsseJson.getJSONArray("USES");
                    MyActivityDetailBean.DataBean.USESBean user;
//                    List<MyActivityDetailBean.DataBean.USESBean> uses = null;
                    if (null!= users_array && users_array.length()>0) {
                        //如果users结构跟ITEMS一样，那就一样的写法
                        uses = new ArrayList<MyActivityDetailBean.DataBean.USESBean>();//使用记录list
                        for (int i = 0; i < users_array.length(); i++){
                            JSONObject json = (JSONObject) users_array.get(i);
                            user = new MyActivityDetailBean.DataBean.USESBean();
                            //各种赋值
                            user.setVAL(json.getString("VAL"));
                            user.setID(json.getInt("ID"));
                            user.setACTIVITY_ID(json.getInt("ACTIVITY_ID"));
                            user.setACTIVITY_ITEM_ID(json.getInt("ACTIVITY_ITEM_ID"));
                            user.setACTIVITY_ITEM_TITLE(json.getString("ACTIVITY_ITEM_TITLE"));
                            user.setLOG_TIME(json.getString("LOG_TIME"));
                            user.setPK_ID(json.getInt("PK_ID"));
                            user.setSHOP_ID(json.getInt("SHOP_ID"));
                            user.setSHOP_NM(json.getString("SHOP_NM"));
                            user.setUSER_ID(json.getInt("USER_ID"));
                            user.setUSER_NM(json.getString("USER_NM"));
                            user.setSORT_NM(json.getString("SORT_NM"));
                            user.setORDER_ITEM_PRICE(json.getString("ORDER_ITEM_PRICE"));
                            user.setORDER_ITEM_SCORE(json.getString("ORDER_ITEM_SCORE"));
                            uses.add(user);
                        }
                    }
                    data.setUSES(uses);
                    data.setITEMS(items);
                    bean.setData(data);

                    if (null != data) {
                        titleActivity.setText(data.getTITLE());
                        shopName.setText(data.getSHOP_NM());
                        useTime.setText(data.getSTART_TIME()+"至"+data.getEND_TIME());
                        shopTel.setText(data.getSHOP_TEL()+"");
                        shopAddress.setText(data.getSHOP_ADDRESS());
                        for (MyActivityDetailBean.DataBean.ITEMSBean itemsBean:items){
                            String itemTile = itemsBean.getACTIVITY_ITEM_TITLE();
                            itemTitleList.add(itemTile);
                            int item_id = itemsBean.getID();
                            itemIdList.add(item_id);
                        }
                        if (null!=uses && uses.size()>0) {
                            historyAdapter = new MyActicityHistoryAdapter(uses);
                            lvHistory.setAdapter(historyAdapter);
                        }
                        adapter = new MyAdapter(ActivityUseDetailActivity.this,items);
                        listView.setAdapter(adapter);
                        //全部cb先存入false状态
                        for (int i = 0; i < ActivityUseDetailActivity.this.items.size(); i++) {
                            if (i==0) {
                                isSelected.put(i,true);
                            }else{
                                isSelected.put(i, false);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hintDialogUtil.dismissDialog();
            }
        });
        getPayPasswordState();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_shop_activityusedetail:
                //商家详情页
                MerchantHomePageActivity.startActivity(this,shop_id+"");
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_enter_activityusedetail:
                String use = rechargeMoney.getText().toString().trim();
                //计次和储值 判空
                if (type==2||type==3){
                    if (TextUtils.isEmpty(use)) {
                        ToastUtil.showLong("输入为空值");
                        return;
                    }
                }
                if (TextUtils.isEmpty(data)) {
                    clickedUse = true;
                    //如果支付状态为空,再次请求网络
                    getPayPasswordState();
                    stateDialog.showLoading();
                }else {
                    passViewDialog.clearn();
                    passViewDialog.showDialog(true,true);
                    passViewDialog.setTile("确认使用:"+itemTitleList.get(itemSlectedPosition));
                }

                switch(type){
                    case 0:
//                        passViewDialog.setPrice(items.get(itemSlectedPosition).getORDER_ITEM_SCORE()+"(积分)");
                        break;
                    case 1:
//                        passViewDialog.setPrice("¥"+items.get(itemSlectedPosition).getBALANCE());
                        break;
                    case 2:
                        passViewDialog.setPrice("¥"+rechargeMoney.getText().toString().trim());
                        break;
                    case 3:
//                        passViewDialog.setPrice(items.get(itemSlectedPosition).getBALANCE()+"");
                        passViewDialog.setPrice(rechargeMoney.getText().toString().trim()+"次");
                        break;
                }
                break;
        }
    }

    private void getPayPasswordState(){
        Map<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp(ApiConstants.STATE_PAY_PASSWORD, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                stateDialog.showError(e+"");
            }

            @Override
            public void onResponse(String response, int id) {
                stateDialog.dismissDialog();
                System.out.println(response);
                DataRow dataRow = DataRow.parseJson(response);
                data = dataRow.getString("data");
                if (clickedUse) {
                    if (data.equals("0")) {
                        //没有支付密码，去设置
                        SetPasswordDialog setPasswordDialog = new SetPasswordDialog(ActivityUseDetailActivity.this,Gravity.CENTER,false);
                        setPasswordDialog.showDialog(true,true);
                        return;
                    }
                    passViewDialog.showDialog(true,true);
                    passViewDialog.setTile("请确认使用:"+itemTitleList.get(itemSlectedPosition));
                }
//                DataRow data = dataRow.getRow("data");
//                data.getString("");

            }
        });
    }

    private void postUseActivity(String val) {
        Map<String,String> map = new HashMap<>();
        String s = itemIdList.get(itemSlectedPosition) + "";
        System.out.println(s);
        map.put("vch",itemIdList.get(itemSlectedPosition)+"");//活动条目id
        if (TextUtils.isEmpty(val)) {
            val="";
        }
        map.put("val",val);//使用金额或次数
        LongHttp(ApiConstants.USEACTIVITY, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                System.out.println("");
            }

            @Override
            public void onResponse(String response, int id) {
                DataRow dataRow = DataRow.parseJson(response);
                boolean success = (boolean) dataRow.get("success");
                if (success) {
                    hintDialogUtil.dismissDialog();
                    ToastUtil.showLong("使用成功");
                    rechargeMoney.setText("");
                    passViewDialog.dismiss();
                    requestNetData();
                }else {
                    ToastUtil.showLong("使用失败");
                    hintDialogUtil.dismissDialog();
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private List<MyActivityDetailBean.DataBean.ITEMSBean> resultList;

        public MyAdapter(Context context, List<MyActivityDetailBean.DataBean.ITEMSBean> resultList) {
            this.context = context;
            this.resultList = resultList;
        }

        public int getCount() {
            return resultList.size();
        }

        public Object getItem(int position) {
            return resultList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position1, View convertView, ViewGroup parent) {
            ActivityUseDetailActivity.ViewHolder holder = null;
            View view = null;
            final int position = position1;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_myactivity_detail_item, null);
                holder = new ActivityUseDetailActivity.ViewHolder();
                holder.itemName = (TextView) convertView .findViewById(R.id.tv_name_activityitem);
                holder.itemLess = (TextView) convertView .findViewById(R.id.tv_less_detailitem);
                holder.itemImportant = (TextView) convertView .findViewById(R.id.tv_important_detailitem);
                holder.checkBox = (CheckBox) convertView .findViewById(R.id.ch_item_addressmanage);
                holder.contain = (RelativeLayout) convertView.findViewById(R.id.rl_contain_detailitem);
                holder.aa = (TextView) convertView.findViewById(R.id.aa);
                convertView.setTag(holder);
            } else {
                view = convertView;
                holder = (ActivityUseDetailActivity.ViewHolder) view.getTag();
            }

            holder.contain.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //选中的再点就不取消“对勾”了
                    if (itemSlectedPosition==position) {
                        return;
                    }
                    //对点击item位置进行存储
                    itemSlectedPosition = position;
                    // 当前点击的CB
                    boolean cu = !isSelected.get(position);
                    // 先将所有的置为FALSE
                    for(Integer p : isSelected.keySet()) {
                        isSelected.put(p, false);
                    }
                    // 再将当前选择CB改为实际状态
                    isSelected.put(position, cu);
                    ActivityUseDetailActivity.MyAdapter.this.notifyDataSetChanged();
                    //beSelectedData集合存放cb选中的那个item
                    beSelectedData.clear();
                    if(cu) beSelectedData.add(resultList.get(position));
                }
            });
            holder.checkBox.setChecked(isSelected.get(position));
            holder.itemName.setText(resultList.get(position).getACTIVITY_ITEM_TITLE());
            switch(type){
                case 0:
                    //积分
                    holder.itemLess.setText("");
                    holder.itemImportant.setText(resultList.get(position).getORDER_ITEM_SCORE()+"");
                    holder.aa.setText("积分");
                    break;
                case 1:
                    //收费
                    holder.itemLess.setText("¥"+resultList.get(position).getORDER_ITEM_PRICE());
                    holder.itemImportant.setText(resultList.get(position).getORDER_ITEM_SCORE()+"");
                    holder.aa.setText("使用积分");
                    break;
                case 2:
                    //储值
                    holder.itemLess.setText("¥"+resultList.get(position).getVAL());
                    holder.itemImportant.setText(resultList.get(position).getBALANCE()+"");
                    holder.aa.setText("余额");
                    break;
                case 3:
                    //计次
                    holder.itemLess.setText(resultList.get(position).getVAL()+"次");
                    holder.itemImportant.setText(resultList.get(position).getBALANCE()+"");
                    holder.aa.setText("剩余次数:");
                    break;
            }
            return convertView;

        }

    }

    class ViewHolder {
        CheckBox checkBox;
        RelativeLayout contain;
        TextView itemName,itemLess,itemImportant,aa;
    }

    class MyActicityHistoryHolder extends BaseHolder<MyActivityDetailBean.DataBean.USESBean> {
        private TextView time,name,moeny;
        @Override
        public View initHolderView() {
            View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_item_activityhistory,null);
            time = (TextView) view.findViewById(R.id.tv_time_activityhistory);
            name = (TextView) view.findViewById(R.id.tv_name_activityhistory);
            moeny = (TextView) view.findViewById(R.id.tv_money_activityhistory);
            return view;
        }

        @Override
        public void bindData(MyActivityDetailBean.DataBean.USESBean data) {
            time.setText(data.getLOG_TIME());
            name.setText(data.getACTIVITY_ITEM_TITLE());
            switch(type){
                case 0:
                    moeny.setText(data.getORDER_ITEM_SCORE()+"积分");
                    break;
                case 1:
                    moeny.setText("¥"+data.getORDER_ITEM_PRICE()+" 使用积分"+data.getORDER_ITEM_SCORE());
                    break;
                case 2:
                    moeny.setText("¥"+data.getVAL());
                    break;
                case 3:
                    moeny.setText(data.getVAL()+"次");
                    break;
            }
        }
    }
    class MyActicityHistoryAdapter extends BasicAdapter<MyActivityDetailBean.DataBean.USESBean> {
        public MyActicityHistoryAdapter(List<MyActivityDetailBean.DataBean.USESBean> list) {
            super(list);
        }

        @Override
        protected BaseHolder<MyActivityDetailBean.DataBean.USESBean> getHolder(int position) {
            return new MyActicityHistoryHolder();
        }
    }
}
