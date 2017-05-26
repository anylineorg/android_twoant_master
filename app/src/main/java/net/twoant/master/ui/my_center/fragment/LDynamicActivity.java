package net.twoant.master.ui.my_center.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by DZY on 2017/2/9.
 * 佛祖保佑   永无BUG
 */
public class LDynamicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.twoant.master.R.layout.zy_activity_dynamic);

        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        DynamicFragment dynamicFragment = new DynamicFragment();
        transaction.replace(net.twoant.master.R.id.contain,dynamicFragment);
        transaction.commit();
    }

}
