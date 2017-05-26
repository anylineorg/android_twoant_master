package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2017/3/13.
 * 商家入驻的 输入信息 view
 */

public class MerchantEnteredWriteInfo extends LinearLayoutCompat {

    private AppCompatEditText mEditView;

    public MerchantEnteredWriteInfo(Context context) {
        super(context);
    }

    public MerchantEnteredWriteInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MerchantEnteredWriteInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        AppCompatTextView leftTextView = new AppCompatTextView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MerchantEnteredWriteInfo);
        String text = typedArray.getString(R.styleable.MerchantEnteredWriteInfo_left_text);
        if (!TextUtils.isEmpty(text)) {
            leftTextView.setText(text);
        }
        int id = typedArray.getInt(R.styleable.MerchantEnteredWriteInfo_left_id, View.NO_ID);
        if (View.NO_ID != id) {
            leftTextView.setId(id);
        }
        leftTextView.setTextColor(typedArray.getColor(R.styleable.MerchantEnteredWriteInfo_left_textColor, Color.BLACK));
        leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimensionPixelSize(R.styleable.MerchantEnteredWriteInfo_left_textSize, 0));
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        id = context.getResources().getDimensionPixelSize(R.dimen.px_10);
        marginLayoutParams.setMargins(0, 0, id, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            marginLayoutParams.setMarginEnd(id);
        }
        leftTextView.setPadding(0, 0, 0, 0);
        leftTextView.setLayoutParams(marginLayoutParams);
        this.addView(leftTextView);

        mEditView = new AppCompatEditText(context);
        text = typedArray.getString(R.styleable.MerchantEnteredWriteInfo_right_hint_text);
        if (!TextUtils.isEmpty(text)) {
            mEditView.setHint(text);
        }
        mEditView.setBackground(null);
        mEditView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(typedArray.getInt(R.styleable.MerchantEnteredWriteInfo_right_maxLength, 1))});
        mEditView.setSingleLine();
        mEditView.setLines(1);
        switch (typedArray.getInt(R.styleable.MerchantEnteredWriteInfo_right_inputType, 0)) {
            case 1:
                mEditView.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 2:
                mEditView.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case 3:
                mEditView.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
        switch (typedArray.getInt(R.styleable.MerchantEnteredWriteInfo_right_imeOptions, 0)) {
            case 1:
                mEditView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                break;
        }
        id = typedArray.getInt(R.styleable.MerchantEnteredWriteInfo_right_id, View.NO_ID);
        if (View.NO_ID != id) {
            mEditView.setId(id);
        }
        mEditView.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimensionPixelSize(R.styleable.MerchantEnteredWriteInfo_right_textSize, 0));
        mEditView.setHintTextColor(typedArray.getColor(R.styleable.MerchantEnteredWriteInfo_right_hint_textColor, Color.GRAY));
        mEditView.setTextColor(typedArray.getColor(R.styleable.MerchantEnteredWriteInfo_right_textColor, Color.BLACK));
        mEditView.setGravity(Gravity.END);
        mEditView.setPadding(0, 0, 0, 0);
        mEditView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(mEditView);
        typedArray.recycle();
    }

    public AppCompatEditText getEditView() {
        return this.mEditView;
    }
}
