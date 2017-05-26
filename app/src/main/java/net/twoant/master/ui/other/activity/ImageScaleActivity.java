package net.twoant.master.ui.other.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.base_app.LongBaseActivity;

import android.view.GestureDetector.OnGestureListener;
import net.twoant.master.widget.HackyViewPager;

import java.util.ArrayList;

/**
 * Created by DZY on 2017/1/16.
 * 佛祖保佑   永无BUG
 */

public class ImageScaleActivity extends LongBaseActivity implements View.OnClickListener,OnGestureListener{
    private HackyViewPager viewPager;//骇客
    private TextView num;
    private int listSize;
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_image_scale;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        viewPager = (HackyViewPager) findViewById(net.twoant.master.R.id.view_pager);
        viewPager.setOffscreenPageLimit(10);
        num = (TextView) findViewById(net.twoant.master.R.id.indicator);
        //1.获取集合
        ArrayList<String> urlList = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        int currentItem = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        listSize = urlList.size();
        //2.填充数据
        ImageScaleAdapter adapter = new ImageScaleAdapter(urlList,this);

        viewPager.setAdapter(adapter);

        //设置默认选中的页
        viewPager.setCurrentItem(currentItem);
        num.setText(++currentItem+"/"+ listSize);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                num.setText(++position+"/"+ listSize);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
