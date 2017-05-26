package net.twoant.master.ui.other.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.GDLocationHelper;

/**
 * Created by S_Y_H on 2017/2/13.
 * 首屏页
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        View decorView;
        if (window != null && (decorView = window.getDecorView()) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
        startActivity(new Intent(this, StartActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        AiSouAppInfoModel.getInstance(getApplication()).onCreate();
        StartActivity.initializedUserData(null);
        AiSouAppInfoModel.initComponents();
        GDLocationHelper.getInstance().getOnceLocation(null);
        this.finish();
    }
}
