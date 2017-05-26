package net.twoant.master.ui.my_center.activity.out;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DZY on 2017/1/8.
 * 佛祖保佑   永无BUG
 */

public class AddBankCardActivity extends LongBaseActivity implements View.OnClickListener{
    private EditText etName,etBankNum,etIdentityNum,etTelePhone;
    private HintDialogUtil hintDialogUtil;
    private boolean noNet = false;
    public String nameStr;
    public String identificationStr;
    private boolean flag = false;

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_realname;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        etName = (EditText) findViewById(R.id.et_name_realname);
        etBankNum = (EditText) findViewById(R.id.et_banknum_realname);
        etBankNum.addTextChangedListener(new TextWatcher() {
            int beforeTextLength=0;
            int onTextLength=0;
            boolean isChanged = false;

            int location=0;//记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if(buffer.length()>0){
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if(s.charAt(i) == '-'){
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if(onTextLength == beforeTextLength || onTextLength <= 3 || isChanged){
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isChanged){
                    location = etBankNum.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if(buffer.charAt(index) == ' '){
                            buffer.deleteCharAt(index);
                        }else{
                            index++;
                        }
                    }
                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        //银行卡号的话需要改这里
                        if((index == 4 || index == 9 || index == 14 || index == 19)){
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if(konggeNumberC>konggeNumberB){
                        location+=(konggeNumberC-konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if(location>str.length()){
                        location = str.length();
                    }else if(location < 0){
                        location = 0;
                    }
                    etBankNum.setText(str);
                    Editable etable = etBankNum.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
        etIdentityNum = (EditText) findViewById(R.id.et_identity_realname);
        etTelePhone = (EditText) findViewById(R.id.et_telephone_realname);
        etTelePhone.addTextChangedListener(new TextWatcher() {
            int beforeTextLength=0;
            int onTextLength=0;
            boolean isChanged = false;

            int location=0;//记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if(buffer.length()>0){
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if(s.charAt(i) == '-'){
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if(onTextLength == beforeTextLength || onTextLength <= 3 || isChanged){
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isChanged){
                    location = etTelePhone.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if(buffer.charAt(index) == '-'){
                            buffer.deleteCharAt(index);
                        }else{
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        //银行卡号的话需要改这里
                        if((index == 3 || index == 8)){
                            buffer.insert(index, '-');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if(konggeNumberC>konggeNumberB){
                        location+=(konggeNumberC-konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if(location>str.length()){
                        location = str.length();
                    }else if(location < 0){
                        location = 0;
                    }
                    etTelePhone.setText(str);
                    Editable etable = etTelePhone.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
        findViewById(R.id.bt_post_realname).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
        boolean authentication = AiSouAppInfoModel.getInstance().getAiSouUserBean().isAuthentication();
        if (authentication) {
            requestNetForSelInfo();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bt_post_realname:
                postRealName();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void postRealName() {
        String realName = etName.getText().toString().trim();
        if (TextUtils.isEmpty(realName)) {
            ToastUtil.showLong("姓名不能不空");
            return;
        }

        String realBankNum = etBankNum.getText().toString().trim();
        if (TextUtils.isEmpty(realBankNum)) {
            ToastUtil.showLong("银行卡号不能不空");
            return;
        }

        String realIdentityNum = etIdentityNum.getText().toString().trim();
        if (TextUtils.isEmpty(realIdentityNum)) {
            ToastUtil.showLong("身份证号不能不空");
            return;
        }

        String realTelePhone = etTelePhone.getText().toString().trim();
        if (TextUtils.isEmpty(realTelePhone)) {
            ToastUtil.showLong("手机号不能不空");
            return;
        }
        if (flag) {
            realName = nameStr;
            realIdentityNum = identificationStr;
        }
        HashMap<String,String> m = new HashMap<>();
        m.put("aisou_id", AiSouAppInfoModel.getInstance().getUID());//
        m.put("bankcard",realBankNum.replace(" ",""));//
        m.put("idcard",realIdentityNum);//
        m.put("mobile",realTelePhone.replace("-",""));//
        m.put("realname",realName);//
        hintDialogUtil.showLoading();
        LongHttp(ApiConstants.BING_BANK,"",m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                hintDialogUtil.dismissDialog();
                boolean result = DataRow.parseJson(response).getBoolean("result", false);
                String message = DataRow.parseJson(response).getString("message");
                if (!result) {
                    finish();
                    ToastUtil.showLong("完成绑定");
                }else{
                    ToastUtil.showLong(message);
                }
            }
        });
    }

    private void  requestNetForSelInfo(){
        hintDialogUtil.showLoading();
        HashMap<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.REALNAME_INFOR, "",m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("连接失败");
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
                nameStr = data.getString("REALNAME");
                String name = nameStr.substring(1, nameStr.length());
                etName.setText("*"+name);
                etName.setEnabled(false);
                identificationStr = data.getString("IDCARD");
                String a1 = identificationStr.substring(0,1);
                String a2 = identificationStr.substring(17,18);
                etIdentityNum.setText(a1 + "*****************" + a2);
                etIdentityNum.setEnabled(false);
                flag = true;
            }
        });
    }

}
