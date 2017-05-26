package net.twoant.master.ui.other.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.SharedPreferencesUtils;
import net.twoant.master.widget.CheckUpdateService;

public class HintUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String ACTION_UPDATE = "HintUpdateActivity_update";
    public final static String EXTRA_TITLE = "title";
    public final static String EXTRA_DESCRIPTION = "description";
    public final static String EXTRA_IS_FORCE = "is_force";
    private final static int ID_WHAT = 1;
    private final static long DELAY_TIME = 1000;

    private String mTitle;
    /**
     * 强制更新
     */
    private boolean isForce;
    private String mDescription;
    private ServiceConnection mServiceConnection;

    private ProgressBar mProgressBar;


    private CheckUpdateService.MyDownBinder mDownBinder;
    private AppCompatButton mUpdateBtn;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mTitle", mTitle);
        outState.putBoolean("isForce", isForce);
        outState.putString("mDescription", mDescription);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isForce = savedInstanceState.getBoolean("isForce");
        mDescription = savedInstanceState.getString("mDescription");
        mTitle = savedInstanceState.getString("mTitle");
    }

    public static void startActivity(Context context, boolean isForce, String title, String description) {
        Intent intent = new Intent(context, HintUpdateActivity.class);
        intent.setAction(ACTION_UPDATE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_IS_FORCE, isForce);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.yh_activity_hint_update);

        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_UPDATE);
        isForce = intent.getBooleanExtra(EXTRA_IS_FORCE, true);
        mTitle = intent.getStringExtra(EXTRA_TITLE);
        mDescription = intent.getStringExtra(EXTRA_DESCRIPTION);
        initView();
        initService();
    }

    private void initService() {
        if (mServiceConnection == null) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (service instanceof CheckUpdateService.MyDownBinder) {
                        mDownBinder = (CheckUpdateService.MyDownBinder) service;
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
        }
        bindService(new Intent(this, CheckUpdateService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private void initView() {
        ((AppCompatTextView) findViewById(R.id.tv_title)).setText(mTitle);
        ((AppCompatTextView) findViewById(R.id.tv_content)).setText(mDescription);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progressBar);

        mUpdateBtn = (AppCompatButton) findViewById(R.id.btn_update);
        mUpdateBtn.setOnClickListener(this);
        View next = findViewById(R.id.btn_next);
        if (isForce) {
            if (next.getVisibility() != View.GONE) {
                next.setVisibility(View.GONE);
            }
        } else {
            if (next.getVisibility() != View.VISIBLE) {
                next.setVisibility(View.VISIBLE);
            }
            next.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if (mDownBinder != null) {
                    if (NetworkUtils.isNetworkConnected()) {
                        if (NetworkUtils.isWifiConnected()) {
                            mDownBinder.downApp();
                            mDownBinder.setProgressBar(mProgressBar);
                            mDownBinder.setBtn(mUpdateBtn);
                        } else if (NetworkUtils.isgMobileConnected()) {
                            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                                    .setTitle("请注意").setMessage("当前网络为" + (NetworkUtils.isgMobileConnected() ? "移动网络" : "非无线网络") +
                                    "是否继续下载")
                                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            mDownBinder.downApp();
                                            mDownBinder.setProgressBar(mProgressBar);
                                            mDownBinder.setBtn(mUpdateBtn);
                                        }
                                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (mDownBinder != null) {
                                        mDownBinder.destroy();
                                    }
                                    unbindService(mServiceConnection);
                                    stopService(new Intent(HintUpdateActivity.this, CheckUpdateService.class));
                                    if (isForce) {
                                        HintUpdateActivity.this.finish();
                                        AppManager.getAppManager().appExit(HintUpdateActivity.this, false);
                                    } else {
                                        HintUpdateActivity.this.finish();
                                    }
                                }
                            }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                        }
                    }
                }
                break;
            case R.id.btn_next:
                SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putInt(AiSouAppInfoModel.KEY_HINT_UPDATE, mDownBinder.getVersion());
                edit.apply();

                unbindService(mServiceConnection);
                stopService(new Intent(this, CheckUpdateService.class));
                mServiceConnection = null;
                HintUpdateActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownBinder != null) {
            mDownBinder.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (isForce) {

            if (mDownBinder != null) {
                mDownBinder.destroy();
            }

            unbindService(mServiceConnection);
            HintUpdateActivity.this.finish();
            AppManager.getAppManager().appExit(this, true);
        } else {

            if (mDownBinder != null) {
                mDownBinder.destroy();
            }

            unbindService(mServiceConnection);
            stopService(new Intent(this, CheckUpdateService.class));
            HintUpdateActivity.this.finish();
        }
        super.onBackPressed();
    }
}
