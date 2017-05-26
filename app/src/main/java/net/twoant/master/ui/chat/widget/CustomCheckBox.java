package net.twoant.master.ui.chat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

/**
 * Created by S_Y_H on 2017/3/14.
 * 开关切换按钮
 */

public class CustomCheckBox extends LinearLayoutCompat {

    private AppCompatCheckBox mCheckBox;

    public CustomCheckBox(Context context) {
        super(context);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setWeightSum(1);
        setClickable(true);
        setBackgroundResource(net.twoant.master.R.drawable.yh_btn_press_transparent_grey);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, net.twoant.master.R.styleable.CustomCheckBox);

        AppCompatTextView appCompatTextView = new AppCompatTextView(context);
        appCompatTextView.setText(typedArray.getString(net.twoant.master.R.styleable.CustomCheckBox_text));
        appCompatTextView.setTextColor(typedArray.getColor(net.twoant.master.R.styleable.CustomCheckBox_textColor, Color.BLACK));
        appCompatTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimensionPixelSize(net.twoant.master.R.styleable.CustomCheckBox_textSize, 0));
        appCompatTextView.setLayoutParams(new LinearLayoutCompat.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        addView(appCompatTextView);

        mCheckBox = new AppCompatCheckBox(context);
        mCheckBox.setButtonDrawable(net.twoant.master.R.drawable.yh_check_box_switch_green);
        mCheckBox.setBackground(null);
        mCheckBox.setClickable(false);
        mCheckBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mCheckBox);
        typedArray.recycle();
    }

    public AppCompatCheckBox getCheckBox() {
        return this.mCheckBox;
    }
}
