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
import net.twoant.master.ui.main.adapter.ActionJoinUserAdapter;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 *
 */
public class ActionJoinMemberActivity extends BaseActivity {

    private final static String EXTRA_ACTION_ID = "action_id";
    private String mActivityId;

    public static void startActivity(Activity activity, String actionId) {
        Intent intent = new Intent(activity, ActionJoinMemberActivity.class);
        intent.putExtra(EXTRA_ACTION_ID, actionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mActivityId", mActivityId);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_action_join_member;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mActivityId = intent.getStringExtra(EXTRA_ACTION_ID);
        }

        if (savedInstanceState != null)
            mActivityId = savedInstanceState.getString("mActivityId");

        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("更多报名");
        Toolbar mTbSimpleToolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(mTbSimpleToolbar);
        mTbSimpleToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionJoinMemberActivity.this.finish();
            }
        });
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView mRvRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        ActionJoinUserAdapter actionJoinUserAdapter = new ActionJoinUserAdapter(ActionJoinUserAdapter.VERTICAL, mActivityId, null);
        mRvRecyclerView.setLayoutManager(layoutManager);
        mRvRecyclerView.setAdapter(actionJoinUserAdapter);
        mRvRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2));

    }
}
