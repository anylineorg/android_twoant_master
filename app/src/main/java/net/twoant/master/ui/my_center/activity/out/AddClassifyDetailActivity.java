package net.twoant.master.ui.my_center.activity.out;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/1.
 */
public class AddClassifyDetailActivity extends LongBaseActivity implements View.OnClickListener{
    private EditText editText;
    private HintDialogUtil hintDialogUtil;
    public String shopid;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_addclassdetail;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        shopid = getIntent().getStringExtra("shopid");
        editText = (EditText) findViewById(net.twoant.master.R.id.et_addclass_classify);
        findViewById(net.twoant.master.R.id.tv_post_classifydetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.tv_post_classifydetail:
                String typeNme = editText.getText().toString().trim();
                if (TextUtils.isEmpty(typeNme)) {
                    ToastUtil.showLong("类名为空");
                }else {
                    hintDialogUtil.showLoading(net.twoant.master.R.string.get_posting);
                    requestNet(typeNme);
                }
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }

    private void requestNet(String typeNme) {
        Map<String,String> map = new HashMap<>();
        map.put("shop_id",shopid);
        map.put("type_name",typeNme);
        LongHttp(ApiConstants.ADD_CLASS, "",map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("提交失败");
            }

            @Override
            public void onResponse(String response, int id) {
                editText.setText("");
                hintDialogUtil.dismissDialog();
                LogUtils.d(response);
                DataRow dataRow = DataRow.parseJson(response);
                boolean success = dataRow.getBoolean("success", false);
                String msg = dataRow.getString("message");
                if (success) {
                    finish();
                    //发送广播
                    Intent intent = new Intent();
                    intent.putExtra("key", "数据数据");
                    intent.setAction("gengxins");
                    sendBroadcast(intent);
                }else{
                    ToastUtil.showShort(msg);
                }
            }
        });
      //添加商品类型
       /* ClassifyHttpUtils.LongHttp("1", shopid, typeNme, "", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("提交失败");
                LogUtils.d(e+"");
            }
            @Override
            public void onResponse(String response, int id) {
                editText.setText("");
                hintDialogUtil.dismissDialog();
                LogUtils.d(response);
                HttpResultBean httpResultBean = GsonUtil.gsonToBean(response, HttpResultBean.class);
                HttpResultBean.ResultBean result = httpResultBean.getResult();
                String msg = result.getMsg();
                ToastUtil.showShort(msg);
                if (msg.contains("成功")) {
                    finish();
                    //发送广播
                    Intent intent = new Intent();
                    intent.putExtra("key", "数据数据");
                    intent.setAction("gengxins");
                    sendBroadcast(intent);
                }
            }
        });*/
    }

}
