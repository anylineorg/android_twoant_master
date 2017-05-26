package net.twoant.master.ui.chat.fragment;

import android.content.Intent;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.ui.chat.activity.NearbyUserActivity;
import net.twoant.master.ui.my_center.fragment.LDynamicActivity;

/**
 * Created by S_Y_H on 2017/2/16.
 * 动态Fragment
 */

public class DynamicFragment extends ViewPagerBaseFragment implements View.OnClickListener {

    public static DynamicFragment newInstance() {
        return new DynamicFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.yh_dynamic_fragment;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView(view);
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //动态
            case R.id.cfl_dynamic:
                startActivity(new Intent(getActivity(),LDynamicActivity.class));
                break;

            //附近
            case R.id.cfl_nearby:
                startActivity(new Intent(getActivity(),NearbyUserActivity.class));
                break;

            //活动
            case R.id.cfl_activity:

                break;
        }
    }

    private void initView(View view) {
        view.findViewById(R.id.cfl_dynamic).setOnClickListener(this);
        view.findViewById(R.id.cfl_nearby).setOnClickListener(this);
        view.findViewById(R.id.cfl_activity).setOnClickListener(this);
    }

}
