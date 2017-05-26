package net.twoant.master.ui.my_center.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;

/**
 * Created by DZY on 2016/12/31.
 * 佛祖保佑   永无BUG
 */
public class EditDataSignActivity extends LongBaseActivity implements View.OnClickListener{
    private EditText etName;
    private TextView tvSign;
    private Button save;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_editdata_sign;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        String name = getIntent().getStringExtra("sign");
        etName = (EditText) findViewById(net.twoant.master.R.id.et_name_editdata);
        tvSign = (TextView) findViewById(net.twoant.master.R.id.tv_editdata_sign);
        if (TextUtils.isEmpty(name)) {
            tvSign.setText(30+"");
            etName.setText("");
        }else{
            tvSign.setText((30-name.length())+"");
            etName.setText(name);
        }
        save = (Button) findViewById(net.twoant.master.R.id.tv_save);
        save.setOnClickListener(this);
        etName.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                save.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.colorPrimary));
                save.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.whiteBgColor));
                save.setEnabled(true);
                tvSign.setText((30-temp.length())+"");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.tv_save:
                Intent data = new Intent();
                data.putExtra("sign",etName.getText().toString().trim());
                setResult(RESULT_OK,data);
                finish();
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }
}
