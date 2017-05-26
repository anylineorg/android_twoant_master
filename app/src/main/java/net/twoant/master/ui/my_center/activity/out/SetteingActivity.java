package net.twoant.master.ui.my_center.activity.out;

import android.os.Bundle;
import android.view.View;

import net.twoant.master.base_app.LongBaseActivity;

/**
 * Created by DZY on 2016/11/22.
 */

public class SetteingActivity extends LongBaseActivity implements View.OnClickListener{
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_setting;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back_admin_page).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case net.twoant.master.R.id.iv_back_admin_page:
                 finish();
                break;
        }
    }
}
