/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.twoant.master.base_app;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hyphenate.easeui.ui.EaseBaseActivity;

import net.twoant.master.R;
import net.twoant.master.widget.StatusBarCompat;

@SuppressLint("Registered")
public abstract class ChatBaseActivity extends EaseBaseActivity {

    public final static String EASE_USER_NAME = "username";

    /**
     * 上下文
     */
    protected Context mContext;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseConfig.onCreate(this);
        if (doBeforeSetContentView(savedInstanceState)) {
            return;
        }
        mContext = this;
        if (!setSubClassOwnContentView()) {
            setContentView(getLayoutId());
            this.subOnCreate(savedInstanceState);
        }
    }

    protected boolean setSubClassOwnContentView() {
        return false;
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

    @CallSuper
    protected boolean doBeforeSetContentView(Bundle savedInstanceState) {
        // 默认着色状态栏
        setStatusBarColor(BaseConfig.getDefStateBarColor());
        return false;
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

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else {
            this.startActivity(intent);
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    /**
     * 初始化 yh_simple_toolbar 布局的数据
     */
    public static Toolbar initSimpleToolbarData(AppCompatActivity activity, String title, View.OnClickListener onClickListener) {
        if (title != null) {
            ((AppCompatTextView) activity.findViewById(R.id.tv_title_tool_bar)).setText(title);
        }
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.tb_simple_toolbar);
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(onClickListener);
        return toolbar;
    }

}
