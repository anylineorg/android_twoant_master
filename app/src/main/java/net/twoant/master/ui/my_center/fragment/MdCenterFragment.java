package net.twoant.master.ui.my_center.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.api.MainActivity;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.DiscoverActivity;
import net.twoant.master.ui.my_center.activity.MyCnterBroadcastReceiver;
import net.twoant.master.ui.my_center.activity.UpdateUIListenner;
import net.twoant.master.ui.my_center.activity.out.MyOrderActivity;
import net.twoant.master.ui.my_center.activity.out.SetteingActivity;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.ui.other.activity.LoginActivity;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.umeng.socialize.utils.ContextUtil.getPackageName;

/**
 * Created by kaiguokai on 2017/4/28.
 */

public class MdCenterFragment extends ViewPagerBaseFragment implements View.OnClickListener{
    private ImageView mIvPhotoCenter;
    private AppCompatTextView mTvName;
    private AppCompatTextView mNumber;
    private AppCompatTextView mTvNumber;
    private AppCompatTextView sellered_store;
    private AppCompatTextView hydq;
    private AppCompatImageView ivEditData;
    private RelativeLayout mRlXinxi;
    private RelativeLayout mRlShoucang;
    private AppCompatImageView tip;
    private LinearLayoutCompat mLlShoukuan;
    private LinearLayoutCompat mLlFukuan;
    private RelativeLayout mRlHuodong;
    private RelativeLayout mRlAttestationCenter;
    private RelativeLayout mRlSellerCenter;
    private RelativeLayout mRlMysellerCenter;
    private RelativeLayout mRlMyorderCenter;
    private RelativeLayout mRlSettringCenter;
    private RelativeLayout mRlGetoutCenter;
    private RelativeLayout rl_dongtai;
    private final ArrayList<String> fImageUrls = new ArrayList<>();
    private ImageView iv_photo_center;
    private TextView tvSign,codeVersion;
    private MyCnterBroadcastReceiver myBroadcastReceiver;
    //TODO 布局资源id就用这个，只需修改里面的布局内容
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_center;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView(view);
        requestNetForInformation();
    }

    private void requestNetForInformation() {
        HashMap<String,String> m = new HashMap<>();
        m.put("_t", AiSouAppInfoModel.getInstance().getToken());
        longHttp("", m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("网络连接失败"+e);
            }

            @Override
            public void onResponse(String response, int id) {


                DataRow row=DataRow.parseJson(response);
                row=row.getRow("data");
                // UserDataBean userBean = JsonUtil.parseJsonToBean(response,UserDataBean.class);
                //  UserDataBean.ResultBean result = userBean.getResult();
                String nick_name = row.getString("NICK_NAME");//result.getNick_name();
                hydq.setText(row.getInt("IS_VIP")==1?"会员到期:"+row.getString("VIP_DATE"):"您还不是会员");
                AiSouAppInfoModel.IS_VIP=row.getInt("IS_VIP");
                //hydq
                sellered_store.setText("当前积分:"+row.getString("TRADE_PRICE"));
                mTvNumber.setText(row.getString("ACCOUNT"));
                if (!TextUtils.isEmpty(nick_name)) {
                    mTvName.setText(nick_name);
                }else{
                    mTvName.setText("");
                }
                String autograph = row.getString("SIGN");//result.getAutograph();
                if (!TextUtils.isEmpty(autograph)) {
                    tvSign.setText(autograph);
                }else{
                    tvSign.setText("");
                }
                //   MainActivity.checkState(getActivity(), result.getCode());
                final String photoUrl = row.getString("IMG_FILE_PATH");//ApiConstants.GUO+result.getAvatar();
                ImageLoader.getImageFromNetwork(mIvPhotoCenter,photoUrl);
                mIvPhotoCenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 打开图片查看器
                         */
                        fImageUrls.clear();
                        fImageUrls.add(photoUrl);
                        Intent intent = new Intent(getActivity(), ImageScaleActivity.class);
                        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS,fImageUrls);
                        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX,0);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void longHttp(String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getToken());
        OkHttpUtils.post().url(ApiConstants.USER_INFO).params(map).build().execute(callback);
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected boolean isShowHintInfo() {
        return true;
    }

    @Override
    protected void onUserInvisible() {

    }

    private void initView(View view) {
        tvSign = (TextView) view.findViewById(R.id.tv_sign_mycenter);
        mIvPhotoCenter = (ImageView) view.findViewById(R.id.iv_photo_center);
        tip= (AppCompatImageView) view.findViewById(R.id.store_tip);
        mTvName = (AppCompatTextView) view.findViewById(R.id.tv_name);
        mNumber = (AppCompatTextView) view.findViewById(R.id.number);
        hydq=(AppCompatTextView) view.findViewById(net.twoant.master.R.id.hydq);
        sellered_store=(AppCompatTextView) view.findViewById(net.twoant.master.R.id.sellered_store);
        mTvNumber = (AppCompatTextView) view.findViewById(R.id.tv_number);
        mTvNumber.setText(AiSouAppInfoModel.getInstance().getUID());
        ivEditData = (AppCompatImageView) view.findViewById(R.id.iv_editdata_center);
        ivEditData.setOnClickListener(this);
        mRlXinxi = (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_xinxi);
        mRlXinxi.setOnClickListener(this);
        codeVersion = (TextView) view.findViewById(net.twoant.master.R.id.tv_version_mycenter);
        mRlShoucang = (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_shoucang);//(R.id.rl_shoucang_center);
        mRlAttestationCenter = (RelativeLayout) view.findViewById(R.id.rl_attestation_center);
        mRlSellerCenter = (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_seller_center);
        mRlMysellerCenter = (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_myseller_center);
        mRlMyorderCenter = (RelativeLayout) view.findViewById(R.id.rl_myorder_center);
        mRlSettringCenter = (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_settring_center);
        mRlGetoutCenter = (RelativeLayout) view.findViewById(R.id.rl_getout_center);
        rl_dongtai= (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_dongtai);
        String roleId ="123";// AiSouAppInfoModel.getInstance().getRoleId();
        RelativeLayout rlRole = (RelativeLayout) view.findViewById(R.id.rl_supermanager_center);
//        if (!"99".equals(roleId)) {
//            rlRole.setVisibility(View.GONE);
//        }
        rlRole.setOnClickListener(this);
        mRlSellerCenter.setOnClickListener(this);
        mRlAttestationCenter.setOnClickListener(this);
        mRlSellerCenter.setOnClickListener(this);
        mRlMyorderCenter.setOnClickListener(this);
        mRlGetoutCenter.setOnClickListener(this);
        mRlSettringCenter.setOnClickListener(this);
        mRlMysellerCenter.setOnClickListener(this);
        rl_dongtai.setOnClickListener(this);
        mRlShoucang.setOnClickListener(this);
        //注册广播  ------接口回调不能使用静态注册,只能动态注册
        myBroadcastReceiver = new MyCnterBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("gengxin");
        getActivity().registerReceiver(myBroadcastReceiver, intentFilter);
        myBroadcastReceiver.SetOnUpdateUIListenner(new UpdateUIListenner() {
            @Override
            public void UpdateUI(String str) {
                requestNetForInformation();
            }
        });

        try {
            codeVersion.setText("版本号:"+getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showLong("积分可以兑换粮食哦,还能送给心仪的TA.敬请期待");
            }
        });
    }
    public void LongHttp(String url, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getToken());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
    void initSource(){
        HashMap<String,String> map=new HashMap<>();
        map.put("user",AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.USERINFO, map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                DataRow row=DataRow.parseJson(response).getRow("data");
                ImageLoader.getImageFromNetwork(mIvPhotoCenter,ApiConstants.EXTERNAL_BASE_IP+"/"+row.getString("AVATAR"));
                mTvName.setText(row.getString("NICK_NAME"));
                mTvNumber.setText(AiSouAppInfoModel.getInstance().getUID());
                String str_temp=row.getString("AUTOGRAPH");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myBroadcastReceiver != null) {
            getActivity().unregisterReceiver(myBroadcastReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_shoucang://R.id.rl_shoucang_center://配送中心
                //DiscoverActivity.startActivity(getActivity(), getString(R.string.send_center), ApiConstants.SEND_CENTER);

                  startActivity(new Intent(getActivity(), CollectActivity.class));
                break;
            case net.twoant.master.R.id.rl_dongtai://店铺管理
                //DiscoverActivity.startActivity(getActivity(), getString(R.string.shop_manager), ApiConstants.SHOP_MANGER);
                 startActivity(new Intent(getActivity(), MainActivity.class));
                break;
            case R.id.rl_xinxi:
                EditDataActivity.startActivity(getActivity());
                break;
            case net.twoant.master.R.id.iv_editdata_center:
                EditDataActivity.startActivity(getActivity());
                break;
            case R.id.rl_seller_center:
                //startActivity(new Intent(getActivity(), AccountInformationActivity.class));
                break;
            case R.id.rl_attestation_center:
//                boolean authentication = AiSouAppInfoModel.getInstance().isAuthentication();
//                if (authentication) {
//                    startActivity(new Intent(getActivity(), RealNameHasActivity.class));
//                }else {
//                    startActivity(new Intent(getActivity(), RealNameActivity.class));
//                }
                break;
            case net.twoant.master.R.id.rl_myseller_center://我的订单
                DiscoverActivity.startActivity(getActivity(), getString(R.string.my_order), ApiConstants.MY_ORDER_URL);

                //  startActivity(new Intent(getActivity(), AddressManageActivity.class));

                break;
            case net.twoant.master.R.id.rl_myorder_center:
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                break;
            case R.id.rl_settring_center:
                startActivity(new Intent(getActivity(), SetteingActivity.class));
                break;
            case net.twoant.master.R.id.rl_getout_center:
                getOut();
                break;
            case net.twoant.master.R.id.rl_supermanager_center:
                // startActivity(new Intent(getActivity(),SpuerManagerActivity.class));
                break;
        }
    }

    public void getOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认退出吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LoginActivity.logoutResetData(getActivity());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private String getVersionName() throws Exception{
        // 获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String versionCode = packInfo.versionName;
        return versionCode;
    }


}
