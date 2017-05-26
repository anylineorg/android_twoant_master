package net.twoant.master.ui.my_center.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

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
import net.twoant.master.ui.my_center.activity.out.GoodsDetailActivity;
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

public class MyCollectGoodsFragment extends ViewPagerBaseFragment implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack {
    private SwipeMenuListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
//    private SwipeRefreshLayout swipeRefreshLayout;
    public List<DataRow> rowList;
    private MyAdapter adapter;
    public double longitude;
    public double latitude1;
    public CancelCenterDialog closeActivityDialog;
    private final static int COLLECT_GOOD = 0x31;
    private final static int REMOVE_GOODS = 0x32;
    private HttpConnectedUtils mHttpConnectedUtils;

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity_ll;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        listView = (SwipeMenuListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        hintDialogUtil = new HintDialogUtil(getActivity());
        closeActivityDialog = new CancelCenterDialog(getActivity(), Gravity.CENTER,false);
        closeActivityDialog.setTitle("取消收藏");
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
        longitude = instance.getLongitude();//经度
        latitude1 = instance.getLatitude();
        String completionAddress = instance.getCompletionAddress();
        getSourceNet();
    }

    void getSourceNet(){
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("lon", longitude+"");
        map.put("lat", latitude1+"");
        mHttpConnectedUtils.startNetworkGetString(COLLECT_GOOD,map, ApiConstants.COLLECT_GOODS);
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
                                closeActivityDialog.dismiss();
                                System.out.println(position);
                                //取消收藏操作
                                cancelCollect(rowList.get(position).getString("GOODS_ID"));
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
                Intent intent = new Intent(getActivity(),GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_GOODS_ID,rowList.get(position).getString("GOODS_ID"));
                startActivity(intent);
            }
        });
    }

    private void cancelCollect(String goods_id) {
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("id",goods_id);
        mHttpConnectedUtils.startNetworkGetString(REMOVE_GOODS,map,ApiConstants.ALTER_COLLECT_GOODS);
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id){
            case COLLECT_GOOD:
                LogUtils.d(response);
                rowList = DataRow.parseJson(response).getSet("data").getRows();
                adapter = new MyAdapter(rowList);
                listView.setAdapter(adapter);
                createSideslipMenu();
                hintDialogUtil.dismissDialog();
                break;
            case REMOVE_GOODS:
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                Boolean success = dataRow.getBoolean("success",false);
                if (success) {
//                    getSourceNet();
                    rowList.remove(position);
                    adapter.notifyDataSetChanged();
                    //ToastUtil.showLong("已取消");
                }else {
                    ToastUtil.showLong(dataRow.getString(dataRow.getString("success")));
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        switch (id){
            case COLLECT_GOOD:
                hintDialogUtil.dismissDialog();
                break;
            case REMOVE_GOODS:
                hintDialogUtil.showError("网络故障");
                break;
        }
    }


    class  MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new CollectGoodsHolder();
        }
    }

    private class CollectGoodsHolder extends BaseHolder<DataRow> {

        TextView tvName,shopName,tvLd,tvPrice,readCount,distance;
        ImageView img;
        @Override
        public View initHolderView() {
            View view = View.inflate(getActivity(), net.twoant.master.R.layout.zy_mycollect_goods_item, null);
            tvName = (TextView) view.findViewById(net.twoant.master.R.id.tv_name_collectgoods_item);
            shopName = (TextView) view.findViewById(net.twoant.master.R.id.tv_shopname_collectgoods_item);
            tvLd = (TextView) view.findViewById(net.twoant.master.R.id.tv_ld_collectgoods_item);
            tvPrice = (TextView) view.findViewById(net.twoant.master.R.id.tv_price_collectgoods_item);
            readCount = (TextView) view.findViewById(net.twoant.master.R.id.tv_readcount_collectgoods_item);
            distance = (TextView) view.findViewById(net.twoant.master.R.id.tv_distance_mycollect_goods);
            img = (ImageView) view.findViewById(net.twoant.master.R.id.iv_img_collectgoods_item);
            return view;
        }

        @Override
        public void bindData(DataRow data) {
            tvName.setText(data.getString("GOODS_NM"));
            shopName.setText(data.getString("SHOP_NM"));
            String goods_ld = data.getString("GOODS_LD");
            if (!TextUtils.isEmpty(goods_ld) && !"null".equals(goods_ld)) {
                tvLd.setText(goods_ld);
            }
            tvPrice.setText("¥"+data.getString("GOODS_PRICE"));
            readCount.setText(data.getString("GOODS_CLICK"));
            distance.setText(data.getString("_DISTANCE_TXT"));
            ImageLoader.getImageFromNetworkPlaceholderControlImg(img, BaseConfig.getCorrectImageUrl(data.getString("GOODS_IMG")),getActivity(), net.twoant.master.R.drawable.ic_def_small);
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

