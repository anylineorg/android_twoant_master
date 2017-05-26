package net.twoant.master.base_app;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import net.twoant.master.widget.StatusBarCompat;


/**
 * Created by S_Y_H on 2016/11/14.14:55
 * activity的基类，适用于不需要mvp模式的简单类
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 上下文
     */
    protected Context mContext;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseConfig.onCreate(this);
        doBeforeSetContentView();
        setContentView(getLayoutId());
        mContext = this;
        this.subOnCreate(savedInstanceState);
    }

    /**
     * 获取布局文件的资源layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 子类实现方法，实现方法后，子类无需再重写 OnCreate（Bundle savedInstanceState）方法;
     */
    protected abstract void subOnCreate(Bundle savedInstanceState);

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void setStatusBarColor(@ColorRes int colorRes) {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, colorRes));
    }


    /**
     * 该方法在setContentView 之前调用
     * 设置layout前的配置
     */
    protected void doBeforeSetContentView() {
        // 默认着色状态栏
        setStatusBarColor(BaseConfig.getDefStateBarColor());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BaseConfig.onRestart(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseConfig.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseConfig.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseConfig.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseConfig.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseConfig.onDestroy(this);
    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else {
            this.startActivity(intent);
            this.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }

    protected void startActivityForResult(int requestCode, Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else {
            this.startActivityForResult(intent, requestCode);
            this.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }
}
