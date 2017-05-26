package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 * Created by S_Y_H on 2017/1/9.
 * 消息列表 （暂时无作用）
 */
public class MessageDetailActivity extends BaseActivity {

    private RecyclerView mRvContentFragment;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MessageDetailActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_message;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        Toolbar mTbSimpleToolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(mTbSimpleToolbar);
        mTbSimpleToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDetailActivity.this.finish();
            }
        });
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("消息");

        this.mRvContentFragment = (RecyclerView) findViewById(net.twoant.master.R.id.rv_content_fragment);
        mRvContentFragment.setLayoutManager(new LinearLayoutManager(this));
        mRvContentFragment.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor,
                0, net.twoant.master.R.dimen.px_2));
    }
}
