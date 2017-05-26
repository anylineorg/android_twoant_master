package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.FixPhoneActivityManager;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class FixPhoneActivity extends LongBaseActivity implements View.OnClickListener{

    private EditText etPhone;
    private AppCompatButton btnNext;
    private String phone;
    private HintDialogUtil progressDialog;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_fix_phone;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_nex_fixphone).setOnClickListener(this);
        FixPhoneActivityManager.getStackManager().pushActivity(this);
        etPhone = (EditText) findViewById(net.twoant.master.R.id.et_phone_fixphone);
        btnNext = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_nex_fixphone);
        etPhone.addTextChangedListener(new TextWatcher() {
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
                if (etPhone.getText().length()==13) {
                    //值满13位时
                    btnNext.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_frame_btn));
                    btnNext.setTextColor(Color.WHITE);
                    btnNext.setEnabled(true);
                }else {
                    //不满时
                    btnNext.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_fix_phone));
                    btnNext.setTextColor(Color.GRAY);
                    btnNext.setEnabled(false);
                };
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
                    location = etPhone.getSelectionEnd();
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
                    etPhone.setText(str);
                    Editable etable = etPhone.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });

        progressDialog = new HintDialogUtil(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_nex_fixphone:
                String trim = etPhone.getText().toString().trim();
                phone = trim.replace("-","");
                if (TextUtils.isEmpty(phone) && phone.length()<11 ) {
                    return;
                }
                map.put("p",phone);
                LongHttp(ApiConstants.CHECKPHONE, "", map, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow row = DataRow.parseJson(response);
                        if (row.getBoolean("result",false)){
                            Intent intent = new Intent(FixPhoneActivity.this,FixPhoneGetCodeActivity.class);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                        }else{
                            ToastUtil.showLong(row.getString("message"));
                            progressDialog.dismissDialog();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixPhoneActivityManager.getStackManager().popAllActivitys();
    }
}
