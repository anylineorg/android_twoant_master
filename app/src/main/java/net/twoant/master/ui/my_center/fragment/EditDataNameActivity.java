package net.twoant.master.ui.my_center.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.twoant.master.R;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;

/**
 * Created by DZY on 2016/12/31.
 * 佛祖保佑   永无BUG
 */
public class EditDataNameActivity extends LongBaseActivity implements View.OnClickListener{
    private EditText etName;
    private Button save;
    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_editdata_name;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(R.id.iv_back).setOnClickListener(this);
        String name = getIntent().getStringExtra("name");
        etName = (EditText) findViewById(R.id.et_name_editdata);
        if (TextUtils.isEmpty(name)) {
            etName.setText("");
        }else{
            etName.setText(name);
        }
        save = (Button) findViewById(R.id.tv_save);
        save.setOnClickListener(this);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save.setBackgroundColor(CommonUtil.getColor(R.color.colorPrimary));
                save.setTextColor(CommonUtil.getColor(R.color.whiteBgColor));
                save.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_save:
                Intent data = new Intent();
                data.putExtra("name",etName.getText().toString().trim());
                setResult(0,data);
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
