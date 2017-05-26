package net.twoant.master.ui.my_center.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.ActionDetailActivity;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.sidesliplistview.SwipeMenu;
import net.twoant.master.widget.sidesliplistview.SwipeMenuCreator;
import net.twoant.master.widget.sidesliplistview.SwipeMenuItem;
import net.twoant.master.widget.sidesliplistview.SwipeMenuListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class MyCollectActivityFragment extends ViewPagerBaseFragment implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack {
    private SwipeMenuListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
    private LinearLayout swipeRefreshLayout;
    public List<DataRow> rowList;
    public MyAdapter adapter;
    private double longitude;
    private double latitude1;
    private Map<String,String> map;
    private HttpConnectedUtils mHttpConnectedUtils;
    private CancelCenterDialog cancelCenterDialog;
    private final static int COLLECT_ACTIVITY = 0x11;
    private final static int REMOVE_ACTIVITY = 0x12;

    @Override
    protected int getLayoutRes() {
        return R.layout.zy_fragment_myactivity_ll;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        listView = (SwipeMenuListView) view.findViewById(R.id.lv_myactivity1);
        swipeRefreshLayout = (LinearLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        hintDialogUtil = new HintDialogUtil(getActivity());
        cancelCenterDialog = new CancelCenterDialog(getActivity(),Gravity.CENTER,false);
        cancelCenterDialog.setTitle("取消收藏");
        initData();
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    private void initData() {
        //先判断是否获取到了值
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        longitude = instance.getLongitude();
        latitude1 = instance.getLatitude();
        getSourceNet();
    }

    void getSourceNet(){
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("lon", longitude+"");
        map.put("lat", latitude1+"");
        mHttpConnectedUtils.startNetworkGetString(COLLECT_ACTIVITY, map, ApiConstants.COLLECT_ACTION);
    }
    int position;
    private void createSideslipMenu() {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.listview_edit)));
                // set item width
                openItem.setWidth((int) getResources().getDimension(net.twoant.master.R.dimen.px_130));
                // set item title
                openItem.setTitle(" 取消\r\n\r\n收藏");
                // set item title fontsize
                openItem.setTitleSize((int) getResources().getDimension(net.twoant.master.R.dimen.px_text_12));
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position1, SwipeMenu menu, final int index) {
                // ApplicationInfo item = mAppList.get(position);
                LogUtils.d(index+"");
                position = position1;
                System.out.println(position+"");
                switch (index) {
                    case 0:
                        //取消活动收藏
                        cancelCenterDialog.showDialog(true,false);
                        cancelCenterDialog.setOnClickListener(new CancelCenterDialog.IOnClickListener() {
                            @Override
                            public void onClickListener(View v) {
                                cancelCenterDialog.dismiss();
                                //取消活动收藏操作
                                Map<String,String> map = new HashMap<>();
                                map.put("user", AiSouAppInfoModel.getInstance().getUID());
                                map.put("activity", rowList.get(position).getString("ACTIVITY_ID"));
                                mHttpConnectedUtils.startNetworkGetString(REMOVE_ACTIVITY,map,ApiConstants.ACTIVITY_COLLECTION);
                            }
                        });
                        break;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActionDetailActivity.startActivity(ActionDetailActivity.TYPE_PURCHASER,getActivity(),rowList.get(position).getString("ACTIVITY_ID"),rowList.get(position).getInt("ACTIVITY_SORT_ID"));
            }
        });
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id){
            case REMOVE_ACTIVITY:
                DataRow parse = DataRow.parseJson(response);
                if (parse == null) return;
                boolean result = parse.getBoolean("RESULT", false);
                if (!result) {
                    ToastUtil.showShort(parse.getString("MESSAGE"));
                    return;
                }else{
//                    getSourceNet();
                    rowList.remove(position);
                    adapter.notifyDataSetChanged();
                }
                break;
            case COLLECT_ACTIVITY:
                LogUtils.d(response);
                rowList = DataRow.parseJson(response).getSet("data").getRows();
                adapter = new MyAdapter(rowList);
                listView.setAdapter(adapter);
                createSideslipMenu();
                hintDialogUtil.dismissDialog();
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (null!=hintDialogUtil){
            hintDialogUtil.dismissDialog();
        }
    }

    class  MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new CollectActivityHolder();
        }
    }

    class CollectActivityHolder extends BaseHolder<DataRow> {

        TextView shopName,distance,readCount,time,address,price;
        ImageView shopImg,actionImg;

        @Override
        public View initHolderView() {
            View view = View.inflate(getActivity(), R.layout.zy_mycollect_action_item, null);
            shopName = (TextView) view.findViewById(R.id.tv_shopname_collectaction_item);
            distance = (TextView) view.findViewById(R.id.tv_distance_mycollect_goods);
            readCount = (TextView) view.findViewById(R.id.tv_readcount_collectaction_item);
            address = (TextView) view.findViewById(net.twoant.master.R.id.tv_address_collectaction_item);
            time = (TextView) view.findViewById(R.id.tv_time_collectaction_item);
            price = (TextView) view.findViewById(net.twoant.master.R.id.tv_price_collectaction_item);
            shopImg = (ImageView) view.findViewById(R.id.iv_shopimg_collectaction_item);
            actionImg = (ImageView) view.findViewById(R.id.iv_actionimg_collectaction_item);
            return view;
        }

        @Override
        public void bindData(DataRow data) {
            shopName.setText(data.getString("SHOP_NM"));//商家名
            distance.setText(data.getString("DISTANCE")+"M");
            address.setText(data.getString("ACTIVITY_ADDRESS"));
            double price1 = 0;
            double score1 = 0;
            try {
                price1 = data.getSet("ITEMS").getRows().get(0).getDouble("PRICE");
                score1 = data.getSet("ITEMS").getRows().get(0).getDouble("SCORE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String scoreStr = "";
            if (score1 > 0 ) {
                scoreStr = score1+"积分";
            }
            if (price1 > 0 ) {
                price.setText("¥" + price1 +scoreStr);
            }else{
                price.setText("" + scoreStr);
            }
            readCount.setText(data.getString("ACTIVITY_CLICK"));//浏览数量
            time.setText("活动时间:"+data.getString("ACTIVITY_START_TIME")+"—"+data.getString("ACTIVITY_END_TIME"));
            ImageLoader.getImageFromNetworkControlImg(shopImg, BaseConfig.getCorrectImageUrl(data.getString("SHOP_AVATAR")),getActivity(), R.drawable.ic_def_circle);
            ImageLoader.getImageFromNetworkPlaceholderControlImg(actionImg,BaseConfig.getCorrectImageUrl(data.getString("COVER_IMG")),getActivity(), R.drawable.ic_def_action);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null!=mHttpConnectedUtils){
            mHttpConnectedUtils.onDestroy();
            mHttpConnectedUtils = null;
        }
    }
}

