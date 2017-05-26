package net.twoant.master.ui.main.widget.search_toolbar;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.ui.main.activity.DiscoverActivity;

/**
 * Created by S_Y_H on 2017/3/17.
 * 首页的搜索配置类
 */

public class HomeSearchToolbarSetter implements ISearchToolbarSetter {

    private Activity mActivity;

    public HomeSearchToolbarSetter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean getRightIcon(ImageView view) {
        view.setImageResource(net.twoant.master.R.drawable.ic_none_info);
        return true;
    }

    @Override
    public boolean getLeftIcon(ImageView view) {
        view.setImageResource(net.twoant.master.R.drawable.ic_action_back);
        return true;
    }

    @Override
    public boolean getRightText(TextView textView) {
        textView.setText(net.twoant.master.R.string.main_page_info);
        return true;
    }

    @Override
    public boolean getLeftText(TextView textView) {
        return false;
    }

    @Override
    public void onRightClickListener(View view) {
        DiscoverActivity.startActivity(mActivity, "消息", ApiConstants.MESSAGE + AiSouAppInfoModel.getInstance().getUID());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        mActivity.finish();
    }

    @Override
    public void onLeftTextClickListener(View view) {

    }
}
