package net.twoant.master.ui.my_center.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/18.
 */
public class AddAdministerActivity extends LongBaseActivity implements View.OnClickListener {

    private AppCompatImageView mIvBackAdminPage;
    private AppCompatTextView mTvAddAdmin;
    private ListView mLvManagerList;
    private List<DataRow> mDataRow;
    public AlertDialog alertDialog;
    private  String shop_id;
    private MyAdapter myAdapter;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_addministor;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        shop_id = getIntent().getStringExtra("shop_id");
        initView();
        myAdapter = new MyAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_back_admin_page:
                    finish();
                break;
            case net.twoant.master.R.id.tv_add_admin:
                Intent intent = new Intent(AddAdministerActivity.this, AddAdministerActivityAdd.class);
                intent.putExtra("shop_id",shop_id);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNetData();
    }

    void initAdapter(){

    }
    private void initView() {
        mIvBackAdminPage = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_back_admin_page);
        mTvAddAdmin = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_add_admin);
        mLvManagerList = (ListView) findViewById(net.twoant.master.R.id.lv_manager_list);
        mIvBackAdminPage.setOnClickListener(this);
        mTvAddAdmin.setOnClickListener(this);
    }

    void UnBinding(String id){
        HashMap<String,String> map=new HashMap<>();
        map.put("mid", id);
        LongHttp(ApiConstants.UNMANAGER, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                if (DataRow.parseJson(response).getBoolean("result",false)){
                    ToastUtil.showShort("解绑成功");
                    requestNetData();
                }else{
                    ToastUtil.showShort("错误");
                }
            }
        });
    }

    @Override
    protected void requestNetData() {

        HashMap<String,String> map=new HashMap<>();
        map.put("aid", AiSouAppInfoModel.getInstance().getUID());
        map.put("pt","0");
        LongHttp(ApiConstants.MANAGERLIST, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                mDataRow=DataRow.parseJson(response).getSet("data").getRows();
                mLvManagerList.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mDataRow.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(AddAdministerActivity.this, net.twoant.master.R.layout.zy_item_manage_admin_list,null);
            TextView textView = (TextView) view1.findViewById(net.twoant.master.R.id.tv_admin_name);
            textView.setText(mDataRow.get(i).getString("TO_USER_NM"));
            ImageView avatar=(ImageView) view1.findViewById(net.twoant.master.R.id.iv_admin_header_images);
            ImageLoader.getImageFromNetwork(avatar, BaseConfig.getCorrectImageUrl(mDataRow.get(i).getString("TO_USER_AVATAR")));
            textView.setTextColor(Color.BLACK);
            TextView time = (TextView) view1.findViewById(net.twoant.master.R.id.tv_set_admin);
            time.setText(mDataRow.get(i).getString("TIME"));
            final TextView dismiss = (TextView) view1.findViewById(net.twoant.master.R.id.tv_dismiss);
            TextView phone = (TextView) view1.findViewById(net.twoant.master.R.id.tv_phone);
            phone.setTag(mDataRow.get(i).getString("TO_USER_PHONE"));
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + view.getTag()));
                    startActivity(intent);
                }
            });
            dismiss.setTag(mDataRow.get(i).getString("ID"));
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View view1 = View.inflate(AddAdministerActivity.this, net.twoant.master.R.layout.zy_dialog_employee, null);
                    TextView tvDismiss = (TextView) view1.findViewById(net.twoant.master.R.id.tv_dismiss_dialog);
                    tvDismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    TextView tvEnter = (TextView) view1.findViewById(net.twoant.master.R.id.tv_enter_employee);
                    tvEnter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UnBinding(dismiss.getTag()+"");
                            alertDialog.dismiss();
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddAdministerActivity.this).setView(view1);
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            return view1;
        }
    }
}
