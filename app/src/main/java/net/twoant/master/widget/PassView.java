package net.twoant.master.widget;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;

public class PassView extends LinearLayout implements OnClickListener {

	private TextView[] tvList;
	private TextView[] tv;
	private ImageView iv_del;
	private View view;
	private String strPassword;
	private int currentIndex = -1;
    private TextView exit;
    private TextView title;
    private TextView price;
    private TextView bigtitle;

	public PassView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

    public void setOnExit(final OnExitButton onExit){
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onExit.exit();
            }
        });
    }

	public PassView(final Context context) {
		super(context);
		view = View.inflate(context, R.layout.item_paypassword, null);
        title = (TextView) view.findViewById(R.id.tv_title_paypassword);
        exit = (TextView) view.findViewById(R.id.tv_exit_dialog_pay);
        price = (TextView) view.findViewById(R.id.tv_money_paypassword);
        bigtitle = (TextView) view.findViewById(R.id.tv_bigtitle_paypassword);
        view.findViewById(R.id.tv_forget_paypassword).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SetPayPasswordActivity.class));
            }
        });
	    tvList = new TextView[6];
        tvList[0] = (TextView)view.findViewById(R.id.pay_box1);
        tvList[1] = (TextView)view. findViewById(R.id.pay_box2);
        tvList[2] = (TextView) view.findViewById(R.id.pay_box3);
        tvList[3] = (TextView) view.findViewById(R.id.pay_box4);
        tvList[4] = (TextView) view.findViewById(R.id.pay_box5);
        tvList[5] = (TextView) view.findViewById(R.id.pay_box6);
        tv = new TextView[10];
        tv[0]= (TextView)view.findViewById(R.id.pay_keyboard_zero);
        tv[1] = (TextView)view.findViewById(R.id.pay_keyboard_one);
        tv[2]= (TextView)view.findViewById(R.id.pay_keyboard_two);
        tv[3] = (TextView)view.findViewById(R.id.pay_keyboard_three);
        tv[4] = (TextView)view.findViewById(R.id.pay_keyboard_four);
        tv[5]= (TextView)view.findViewById(R.id.pay_keyboard_five);
        tv[6] = (TextView)view.findViewById(R.id.pay_keyboard_sex);
        tv[7] = (TextView)view.findViewById(R.id.pay_keyboard_seven);
        tv[8]= (TextView)view.findViewById(R.id.pay_keyboard_eight);
        tv[9] = (TextView)view.findViewById(R.id.pay_keyboard_nine);
        iv_del= (ImageView)view.findViewById(R.id.pay_keyboard_del);
       for(int i=0;i<=9;i++){
    	   tv[i].setOnClickListener(this);
       }
       iv_del.setOnClickListener(this);
       addView(view);
	}

    public void setOnFinishInput(final OnPasswordInputFinish pass) {
        tvList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    strPassword = "";
                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }
                    pass.inputFinish(strPassword);
                }
            }
        });
    }

    public void setBigTitle(String bigTitle){
        bigtitle.setText(bigTitle);
    }

    public void setTitle(String mtitle) {
        if (title.getVisibility() != VISIBLE)
            title.setVisibility(VISIBLE);
        title.setText(mtitle);
    }

    public void setPrice(String mprice) {
        if (price.getVisibility() != VISIBLE)
            price.setVisibility(VISIBLE);
        price.setText(mprice);
    }
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pay_keyboard_one:
			getPass("1");
			break;
       case R.id.pay_keyboard_two:
    	   getPass("2");
			break;
       case R.id.pay_keyboard_three:
    	   getPass("3");
			break;
       case R.id.pay_keyboard_four:
    	   getPass("4");
			break;
       case R.id.pay_keyboard_five:
    	   getPass("5");
			break;
       case R.id.pay_keyboard_sex:
    	   getPass("6");
			break;
       case R.id.pay_keyboard_seven:
    	   getPass("7"); 
    	   break;
       case R.id.pay_keyboard_eight:
    	   getPass("8");
    	   break;
       case R.id.pay_keyboard_nine:
    	   getPass("9");
    	   break;
       case R.id.pay_keyboard_zero:
    	   getPass("0");
    	   break;
       case R.id.pay_keyboard_del:
    	   del();
    	   break;
		}
	}

    private void del() {
        if (currentIndex - 1 >= -1) {
            tvList[currentIndex].setText("");
            tvList[currentIndex].setBackground(null);
            currentIndex--;
        }
    }

    public void getPass(String str){
		if (currentIndex >= -1 && currentIndex < 5) {
            ++currentIndex;
            tvList[currentIndex].setText(str);
            tvList[currentIndex].setBackground(getResources().getDrawable(R.drawable.zy_password_cover));
		 }
	}

	public String getStrPassword() {
		return strPassword;
	}

    public void clearn(){
        for (int i = 0; i < 6; i++) {
            del();
        }
    }

    //用于给密码输入完成添加回掉事
    public interface OnPasswordInputFinish {
        void inputFinish(String strPassword);
    }
    //用于"x"的退出
    public interface OnExitButton {
        void exit();
    }
}
