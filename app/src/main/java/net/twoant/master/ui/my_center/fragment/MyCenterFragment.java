package net.twoant.master.ui.my_center.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.api.AppConfig;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.HomeBaseFragment;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.JsonUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.my_center.activity.BuyerMemberListActivity;
import net.twoant.master.ui.my_center.activity.DynamicListActivity;
import net.twoant.master.ui.my_center.activity.MyCnterBroadcastReceiver;
import net.twoant.master.ui.my_center.activity.SettingActivity;
import net.twoant.master.ui.my_center.activity.SpuerManagerActivity;
import net.twoant.master.ui.my_center.activity.UpdateUIListenner;
import net.twoant.master.ui.my_center.activity.out.RealNameActivity;
import net.twoant.master.ui.my_center.activity.out.RealNameHasActivity;
import net.twoant.master.ui.my_center.activity.out.ShopQRCodeActivity;
import net.twoant.master.ui.my_center.bean.UserDataBean;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.ui.other.activity.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.umeng.socialize.utils.ContextUtil.getPackageName;

/**
 * Created by DZY on 2016/11/16.
 * 首页的 我的中心 界面
 */

public class MyCenterFragment extends HomeBaseFragment implements View.OnClickListener {
    private ImageView mIvPhotoCenter;
    private AppCompatTextView mTvName;
    private AppCompatTextView mNumber;
    private AppCompatImageView ivEditData;
    private LinearLayoutCompat mRlXinxi;
    private LinearLayoutCompat mRlShoucang;
    private LinearLayoutCompat mLlShoukuan;
    private LinearLayoutCompat mLlFukuan;
    private LinearLayoutCompat mRlHuodong;
    private LinearLayoutCompat mRlAttestationCenter;
    private LinearLayoutCompat mRlSellerCenter;
    private LinearLayoutCompat mRlMysellerCenter;
    private LinearLayoutCompat mRlMyorderCenter;
    private LinearLayoutCompat mRlSettringCenter;
    private LinearLayoutCompat mRlGetoutCenter;
    private final ArrayList<String> fImageUrls = new ArrayList<>();
    private AppCompatImageView iv_photo_center;
    private AppCompatTextView tvSign, codeVersion;
    private MyCnterBroadcastReceiver myBroadcastReceiver;
    private LinearLayoutCompat mRlConvenientInfo;
    public AppCompatImageView imageView;

    //TODO 布局资源id就用这个，只需修改里面的布局内容
    @Override
    protected int getLayoutRes() {
        return R.layout.zy_fragment_my_center;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView(view);
        requestNetForInformation();
    }

    private void requestNetForInformation() {
        HashMap<String, String> m = new HashMap<>();
        m.put("aisou_id", AiSouAppInfoModel.getInstance().getUID());
        longHttp("search_user.do", m, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("网络连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                UserDataBean userBean = JsonUtil.parseJsonToBean(response, UserDataBean.class);
                UserDataBean.ResultBean result = userBean.getResult();
                String nick_name = result.getNick_name();
                if (!TextUtils.isEmpty(nick_name)) {
                    mTvName.setText(nick_name);
                } else {
                    mTvName.setText("");
                }
                String autograph = result.getAutograph();
                if (!TextUtils.isEmpty(autograph)) {
                    tvSign.setText("个性签名:"+autograph);
                } else {
                    tvSign.setText("个性签名:");
                }
                MainActivity.checkState(getActivity(), result.getCode());
                final String photoUrl = BaseConfig.getCorrectImageUrl(result.getAvatar());
                ImageLoader.getImageFromNetwork(mIvPhotoCenter, photoUrl);
                mIvPhotoCenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 打开图片查看器
                         */
                        fImageUrls.clear();
                        fImageUrls.add(photoUrl);
                        Intent intent = new Intent(getActivity(), ImageScaleActivity.class);
                        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, fImageUrls);
                        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, 0);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void longHttp(String name, Map<String, String> map, StringCallback callback) {
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.GUO + name).params(map).build().execute(callback);
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onResume() {
        super.onResume();
        requestNetForInformation();
    }

    @Override
    protected void onUserInvisible() {

    }

    private void initView(View view) {
        imageView = (AppCompatImageView) view.findViewById(net.twoant.master.R.id.iv_dynamic);
        view.findViewById(R.id.rl_dynamic_center).setOnClickListener(this);
        tvSign = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_sign_mycenter);
        mIvPhotoCenter = (ImageView) view.findViewById(R.id.iv_photo_center);
        mTvName = (AppCompatTextView) view.findViewById(R.id.tv_name);
        mNumber = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.number);
        mNumber.setText("蚂蚁号:"+AiSouAppInfoModel.getInstance().getUID());
        ivEditData = (AppCompatImageView) view.findViewById(R.id.iv_editdata_center);
        ivEditData.setOnClickListener(this);
        mRlXinxi = (LinearLayoutCompat) view.findViewById(R.id.rl_xinxi);
        mRlXinxi.setOnClickListener(this);
        codeVersion = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_version_mycenter);
        mRlShoucang = (LinearLayoutCompat) view.findViewById(R.id.rl_shoucang_center);
        mRlConvenientInfo = (LinearLayoutCompat) view.findViewById(R.id.rl_my_convenient_info);
        mRlAttestationCenter = (LinearLayoutCompat) view.findViewById(net.twoant.master.R.id.rl_attestation_center);
        mRlSellerCenter = (LinearLayoutCompat) view.findViewById(R.id.rl_seller_center);
        mRlMysellerCenter = (LinearLayoutCompat) view.findViewById(R.id.rl_myseller_center);
        mRlMyorderCenter = (LinearLayoutCompat) view.findViewById(net.twoant.master.R.id.rl_dynamic_center);
        view.findViewById(net.twoant.master.R.id.rl_clean_cache).setOnClickListener(this);
        mRlSettringCenter = (LinearLayoutCompat) view.findViewById(net.twoant.master.R.id.rl_settring_center);
        mRlGetoutCenter = (LinearLayoutCompat) view.findViewById(R.id.rl_getout_center);
        String roleId = AiSouAppInfoModel.getInstance().getAiSouUserBean().getRoleId();
        LinearLayoutCompat rlRole = (LinearLayoutCompat) view.findViewById(net.twoant.master.R.id.rl_supermanager_center);
        if (!"99".equals(roleId)) {
            rlRole.setVisibility(View.GONE);
        }
        rlRole.setOnClickListener(this);
        mRlSellerCenter.setOnClickListener(this);
        mRlAttestationCenter.setOnClickListener(this);
        mRlGetoutCenter.setOnClickListener(this);
        mRlSettringCenter.setOnClickListener(this);
        mRlMysellerCenter.setOnClickListener(this);
        mRlConvenientInfo.setOnClickListener(this);
        view.findViewById(R.id.rl_my_member_merchant).setOnClickListener(this);
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
            codeVersion.setText("版本号:" + getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        setImage();
    }

    private void setImage() {
        String contentString = AppConfig.CODE_URL + AiSouAppInfoModel.getInstance().getUID();//AiSouAppInfoModel.uid.toString();
        if (!contentString.equals("")) {
            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（400*400）
            try {
                Bitmap qrCodeBitmap = ShopQRCodeActivity.EncodingHandler.createQRCode(contentString, 400);
                imageView.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "请登录账号", Toast.LENGTH_SHORT).show();
        }
    }

    public void LongHttp(String url, Map<String, String> map, StringCallback callback) {
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
   /* void initSource(){
        HashMap<String,String> map=new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.USERINFO, map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                DataRow row=DataRow.parseJson(response).getRow("data");
                ImageLoader.getImageFromNetwork(mIvPhotoCenter, BaseConfig.getCorrectImageUrl(row.getString("AVATAR")),this, R.drawable.ic_def_circle);
                mTvName.setText(row.getString("NICK_NAME"));
                mTvNumber.setText(AiSouAppInfoModel.getInstance().getUID());
                String str_temp=row.getString("AUTOGRAPH");
            }
        });
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (myBroadcastReceiver != null) {
                getActivity().unregisterReceiver(myBroadcastReceiver);
            }
        } catch (Exception e) {
            //empty
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.rl_shoucang_center://收藏
                startActivity(new Intent(getActivity(), CollectActivity.class));
                break;
            case R.id.rl_my_convenient_info:
                MyConvenientInfoActivity.startActivity(getActivity(), false);
                break;
            case net.twoant.master.R.id.rl_xinxi:
                EditDataActivity.startActivity(getActivity());
                break;
            case net.twoant.master.R.id.iv_editdata_center:
                EditDataActivity.startActivity(getActivity());
                break;
            case net.twoant.master.R.id.rl_seller_center:
                startActivity(new Intent(getActivity(), AccountInformationActivity.class));
                break;
            case R.id.rl_attestation_center:
                boolean authentication = AiSouAppInfoModel.getInstance().getAiSouUserBean().isAuthentication();
                if (authentication) {
                    startActivity(new Intent(getActivity(), RealNameHasActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), RealNameActivity.class));
                }
                break;
            case R.id.rl_myseller_center:
                Intent telIntent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + "4006165858");
                telIntent.setData(data);
                startActivity(telIntent);
                break;

            case net.twoant.master.R.id.rl_settring_center:
                SettingActivity.startActivity(mActivity);
//                startActivity(new Intent(getActivity(), SetteingActivity.class));
                break;
            case R.id.rl_getout_center:
                getOut();
                break;
            case R.id.rl_supermanager_center:
                startActivity(new Intent(getActivity(), SpuerManagerActivity.class));
                break;
            case R.id.rl_dynamic_center:
                startActivity(new Intent(getActivity(), DynamicListActivity.class));
                break;
            case net.twoant.master.R.id.rl_clean_cache:
                SettingActivity.startActivity(mActivity);
                break;
            case net.twoant.master.R.id.rl_my_member_merchant:
                BuyerMemberListActivity.startActivity(mActivity);
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

    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String versionCode = packInfo.versionName;
        return versionCode;
    }


}
