package net.twoant.master.ui.my_center.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.my_center.holder.CollectShopHolder;
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

public class MyCollectSellerFragment extends ViewPagerBaseFragment implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack {
    private SwipeMenuListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter adapter;
    public List<DataRow> dataRowList;
    private double longitude;
    private double latitude1;
    private HttpConnectedUtils mHttpConnectedUtils;
    private CancelCenterDialog closeActivityDialog;
    private final static int COLLECT_SELLER = 0x21;
    private final static int REMOVE_SELLER = 0x22;

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity_ll;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        listView = (SwipeMenuListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        hintDialogUtil = new HintDialogUtil(getActivity());
        closeActivityDialog = new CancelCenterDialog(getActivity(),Gravity.CENTER,false);
        closeActivityDialog.setTitle("取消收藏");
        //先判断是否获取到了值
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        longitude = instance.getLongitude();
        latitude1 = instance.getLatitude();
        initData();
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    private void initData() {
        getSourceNet();
    }

    void getSourceNet(){
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("lon", longitude+"");
        map.put("lat", latitude1+"");
        mHttpConnectedUtils.startNetworkGetString(COLLECT_SELLER, map, ApiConstants.COLLECT_SHOP);
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
                openItem.setBackground(new ColorDrawable(getResources().getColor(net.twoant.master.R.color.listview_edit)));
                // set item width
                openItem.setWidth((int) getResources().getDimension(net.twoant.master.R.dimen.px_130));
                // set item title
                openItem.setTitle(" 取消\r\n收藏");
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
                        //取消收藏
                        closeActivityDialog.showDialog(true,false);
                        closeActivityDialog.setOnClickListener(new CancelCenterDialog.IOnClickListener() {
                            @Override
                            public void onClickListener(View v) {
                                //取消收藏操作
                                closeActivityDialog.dismiss();
                                Map<String,String> map = new HashMap<>();
                                map.put("user", AiSouAppInfoModel.getInstance().getUID());
                                map.put("shop", dataRowList.get(position).getString("SHOP_ID"));
                                mHttpConnectedUtils.startNetworkGetString(REMOVE_SELLER, map, ApiConstants.COLLECTION);
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
                MerchantHomePageActivity.startActivity(getActivity(),dataRowList.get(position).getString("SHOP_ID"));
            }
        });
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id){
            case COLLECT_SELLER:
                LogUtils.d(response);
                dataRowList = DataRow.parseJson(response).getSet("data").getRows();
                adapter = new MyAdapter(dataRowList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                createSideslipMenu();
                hintDialogUtil.dismissDialog();
                break;
            case REMOVE_SELLER:
                DataRow parse = DataRow.parseJson(response);
                if (parse == null) return;
                if (!parse.getBoolean("RESULT", false)) {
                    ToastUtil.showShort(parse.getString("MESSAGE"));
                    return;
                }else{
                    dataRowList.remove(position);
                    adapter.notifyDataSetChanged();
//                            getSourceNet();
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        hintDialogUtil.dismissDialog();
    }

    class  MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new CollectShopHolder();
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

