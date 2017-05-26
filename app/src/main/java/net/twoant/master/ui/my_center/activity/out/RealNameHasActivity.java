package net.twoant.master.ui.my_center.activity.out;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/22.
 */

public class RealNameHasActivity extends LongBaseActivity implements View.OnClickListener {
    private ArrayList<String> fImageUrls = new ArrayList<>();
    private HintDialogUtil hintDialogUtil;
    private boolean noNet = false;
    private String photoUrl;
    private ImageView mIvPhotoCenter;
    private TextView etName,etIdentification;
    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_realname_has;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mIvPhotoCenter = (ImageView) findViewById(R.id.iv_img_realname_has);
        etName = (TextView) findViewById(R.id.et_name_realname);
        etIdentification = (TextView) findViewById(R.id.et_identity_realname);
        mIvPhotoCenter.setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
        hintDialogUtil.showLoading();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_img_realname_has:
                /**
                 * 打开图片查看器
                 */
                fImageUrls.clear();
                fImageUrls.add(photoUrl);
                Intent intent = new Intent(RealNameHasActivity.this, ImageScaleActivity.class);
                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS,fImageUrls);
                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX,0);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void requestNetData() {
        HashMap<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.REALNAME_INFOR, "",m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                noNet = true;
                hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (noNet) {
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                LogUtils.d(response);
                DataRow dataRow = DataRow.parseJson(response);
                DataRow data = dataRow.getRow("data");
                photoUrl = BaseConfig.getCorrectImageUrl(data.getString("AVATAR"));
                String nameStr = data.getString("REALNAME");
                String name = nameStr.substring(1,nameStr.length());
                etName.setText("*"+name);
                String identificationStr = data.getString("IDCARD");
                if (!TextUtils.isEmpty(identificationStr) && 18 <= identificationStr.length()) {
                    String a1 = identificationStr.substring(0, 1);
                    String a2 = identificationStr.substring(17, 18);
                    etIdentification.setText(a1 + "*****************" + a2);
                }
                ImageLoader.getImageFromNetwork(mIvPhotoCenter,photoUrl,RealNameHasActivity.this);
            }
        });
    }


}
