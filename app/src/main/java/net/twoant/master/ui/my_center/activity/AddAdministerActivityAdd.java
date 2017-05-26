package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.bean.UserDataBean;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
/**
 * Created by DZY on 2016/11/18.
 */
public class AddAdministerActivityAdd extends LongBaseActivity implements View.OnClickListener {

    private AppCompatImageView mIvBackAdminaddPage;
    private AppCompatEditText mTvTextkeyAdministrator;
    private AppCompatTextView mTvSearchAdministrator;
    private ListView mLvAdminstorlistAdministrator;
    private final ArrayList<String> fImageUrls = new ArrayList<>();
    public TextView tvAdd;
    private String shop_id;
    private HintDialogUtil hintDialogUtil;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_addministor_add;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        shop_id=getIntent().getStringExtra("shop_id");
        initView();
    }

    @Override
    protected void initData() {
        hintDialogUtil = new HintDialogUtil(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case net.twoant.master.R.id.tv_search_administrator:
                submit();
                break;
            case R.id.iv_back_adminadd_page:
                finish();
                break;
        }
    }

    private void initView() {
        mIvBackAdminaddPage = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_back_adminadd_page);
        mIvBackAdminaddPage.setOnClickListener(this);
        mTvTextkeyAdministrator = (AppCompatEditText) findViewById(R.id.tv_textkey_administrator);
        mTvTextkeyAdministrator.setOnClickListener(this);
        mTvSearchAdministrator = (AppCompatTextView) findViewById(R.id.tv_search_administrator);
        mTvSearchAdministrator.setOnClickListener(this);
        mLvAdminstorlistAdministrator = (ListView) findViewById(net.twoant.master.R.id.lv_adminstorlist_administrator);
    }

    String aisou_id;
    private void submit() {
         aisou_id = mTvTextkeyAdministrator.getText().toString().trim();
        if (TextUtils.isEmpty(aisou_id)) {
            Toast.makeText(this, "蚂蚁号为空", Toast.LENGTH_SHORT).show();
            return;
        }
        requestNet(aisou_id);
    }

    private void requestNet(String aisou_id) {
        HashMap<String,String> m = new HashMap<>();
        m.put("aisou_id",aisou_id);
        LongHttp("search_user.do", m, new StringCallback(){
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                UserDataBean userBean = gson.fromJson(response,UserDataBean.class);
                UserDataBean.ResultBean result = userBean.getResult();
                final String nick_name = result.getNick_name();
                final String photoUrl = BaseConfig.getCorrectImageUrl(result.getAvatar());
                fImageUrls.add(photoUrl);
                mLvAdminstorlistAdministrator.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 1;
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
                    public View getView(int i, View cover, ViewGroup viewGroup) {
                        View view = View.inflate(AddAdministerActivityAdd.this, net.twoant.master.R.layout.zy_item_addadminister_administer,null);
                        tvAdd = (TextView) view.findViewById(R.id.tv_item_add);
                        TextView tvName = (TextView) view.findViewById(R.id.tv_admin_name);
                        ImageView ivPhoto = (ImageView) view.findViewById(R.id.iv_admin_header_image);
                        tvName.setText(nick_name);
                        ImageLoader.getImageFromNetwork(ivPhoto,photoUrl);
                        tvAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addAdminister();
                            }
                        });
                        ivPhoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 * 打开图片查看器
                                 */
                                Intent intent = new Intent(AddAdministerActivityAdd.this, ImageScaleActivity.class);
                                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS,fImageUrls);
                                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX,0);
                                startActivity(intent);
                                AddAdministerActivityAdd.this.overridePendingTransition(R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
                            }
                        });
                        return view;
                    }
                });
                LogUtils.d(response);
            }
        });
    }

    private void addAdminister() {
        Map<String,String> m = new HashMap<>();
        hintDialogUtil.showLoading();
        m.put("owner_id", AiSouAppInfoModel.getInstance().getUID());
        m.put("manager_id",aisou_id);//被指派人的id，意：搜索出来的人
        m.put("shop_id",shop_id);
        LongHttp(ApiConstants.POST_ADD_ADMINITOR,"" ,m, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                hintDialogUtil.dismissDialog();
                //{"result":true,"code":"200","data":[],"success":true,"type":"map","message":null}
                boolean result = DataRow.parseJson(response).getBoolean("result",false);
                if (result) {
                    ToastUtil.showLong("添加成功");
                    tvAdd.setEnabled(false);
                    tvAdd.setBackgroundColor(Color.GRAY);
                }else{
                    ToastUtil.showLong("添加失败");
                }

            }
        });
    }
}
