package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongLongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.JsonUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.bean.PublishDetailResultBean;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;
import net.twoant.master.widget.timer.TimePickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Request;


/**
 * Created by DZY on 2016/11/19.
 * 发布活动界面
 */
public class PublishDetailActivity extends LongLongBaseActivity implements View.OnClickListener,HttpConnectedUtils.IOnStartNetworkCallBack {
    public static String SHOP_ADDRESS;
    private RichEditor mEditor;
    private ImageView showPhoto,deleteShow,ivFontSize,ivFontColor,single,many;
    private LinearLayout ivDetailContainer;
    private RelativeLayout rlAddShowPhoto,start_time_action,end_time_action;
    private TextView activity_start_time,activity_end_time;
    private FrameLayout fl_showphoto;
    private EditText etActivityName,etAddress;
    private HintDialogUtil hintDialogUtil;
    private int check_time=1;//1 是开始时间 2是结束时间
    TimePickerView pvTime;

//    private String latitude;//获取纬度
//    private String longitude;//获取经度

    private Spinner spinner;
    private List categoryList;
    private List items;
    private static final int PHOTO_CARMERA = 1;
    private static final int PHOTO_PICK = 2;
    private static final int PHOTO_CUT = 3;
    private static final int ACTIVITY_ITEM = 4;
    private int clickImageverPosition;//用于记录点击的是展示图片按钮还是详细图片按钮
    private int position;

//    private List<File> filesDrtail;//活动详情图集合
    private File showFile;//活动展示图

    private PopupWindow popFont;
    private PopupWindow popColor;
    private View viewFontSize;//popwindow字体大小的页面
    private View viewFontColor;//popwindow字体颜色的页面
    private String activity_img_txt;

    private int join_limit = 0;//默认单人多次参加活动

    private String shop_id;

    private boolean isBold = false;
    private boolean isItalic = false;

    // 创建一个以当前系统时间为名称的文件，防止重复,用于照相机拍照暂时存储文件
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator +
            getPhotoFileName());

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_publishactivity_;
    }

    private void initView() {
        shop_id = getIntent().getStringExtra("shop_id");
        showPhoto = (ImageView) findViewById(net.twoant.master.R.id.iv_showphoto_publish);
        rlAddShowPhoto = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_addshow_publish);
        start_time_action = (RelativeLayout) findViewById(net.twoant.master.R.id.activity_start_time_layout);
        end_time_action = (RelativeLayout) findViewById(net.twoant.master.R.id.activity_end_time_layout);
        activity_start_time = (TextView) findViewById(net.twoant.master.R.id.activity_start_time);
        activity_end_time = (TextView) findViewById(net.twoant.master.R.id.activity_end_time);
        fl_showphoto = (FrameLayout) findViewById(net.twoant.master.R.id.fl_showphoto);
        etAddress = (EditText) findViewById(net.twoant.master.R.id.et_address_publishactivity);
        deleteShow = (ImageView) findViewById(net.twoant.master.R.id.iv_delete_showphoto);
        etActivityName = (EditText) findViewById(net.twoant.master.R.id.et_activityname_publishactivity);
        findViewById(net.twoant.master.R.id.bt_post_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_adddetail_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_showdetail_publishactivity).setOnClickListener(this);
        deleteShow.setOnClickListener(this);
        start_time_action.setOnClickListener(this);
        end_time_action.setOnClickListener(this);
        rlAddShowPhoto.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_blod_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_underline_publishactivity).setOnClickListener(this);
        ivFontSize = (ImageView) findViewById(net.twoant.master.R.id.iv_size_publishactivity);
        ivFontSize.setOnClickListener(this);
        ivFontColor = (ImageView) findViewById(net.twoant.master.R.id.iv_fontcolor_publishactivity);
        ivFontColor.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_selectphoto_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_video_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_many_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_single_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        single = (ImageView) findViewById(net.twoant.master.R.id.iv_single_publishacitivty);
        many = (ImageView) findViewById(net.twoant.master.R.id.iv_many_publishacitivty);
        single.setOnClickListener(this);
        many.setOnClickListener(this);
        ivDetailContainer = (LinearLayout)findViewById(net.twoant.master.R.id.ll_detail_container_publish);//活动图片内容容器
//        filesDrtail = new ArrayList<>();
        // 时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
        pvTime.setRange(2016,2100);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener(){
            @Override
            public void onTimeSelect(Date date){
                if (check_time==1) {
                    activity_start_time.setText(getTime(date));
                }else {
                    activity_end_time.setText(getTime(date));
                }
            }
        });
        //spinner
        spinner = (Spinner) findViewById(net.twoant.master.R.id.sp_selectclass_publish);
        hintDialogUtil = new HintDialogUtil(this);
        categoryList = new ArrayList<>();
        categoryList.add("积分活动");
        categoryList.add("收费活动");
        categoryList.add("储值活动");
        categoryList.add("计次活动");
        categoryList.add("红包活动");
        items = new ArrayList();
        items.add("拍照");
        items.add("从相册选择图片");
        ArrayAdapter arrayAdapter = new ArrayAdapter(PublishDetailActivity.this,android.R.layout.simple_list_item_1,categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PublishDetailActivity.this.position = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void initData() {
        initView();
        mEditor = (RichEditor) findViewById(net.twoant.master.R.id.editor);
        mEditor.setEditorHeight(CommonUtil.getDimens(net.twoant.master.R.dimen.px_120));
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("请在此留下活动详情描述...");

        //活动详情h5标签详情
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                activity_img_txt = text;
                System.out.println(text);
            }
        });
        initFontPopupWindow();
        initColorPopupWindow();
    }

    @Override
    protected void requestNetData() {
        hintDialogUtil.showLoading(net.twoant.master.R.string.get_position);
//        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
//        String address =  instance.getCompletionAddress();
//        System.out.println(address);
        etAddress.setText(SHOP_ADDRESS);
//        latitude = instance.getLatitude() + "";
//        longitude = instance.getLongitude() + "";
        hintDialogUtil.dismissDialog();
    }

    String redprice;//红包value
    String chuzhi;//储值value
    String havemoney;//收费value
    String activity_jici;//计次必填次数value
    String integral;//积分
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.rl_single_publishactivity:
                single.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.icon_selected));
                many.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.icon_unselected));
                join_limit = 1;
                break;
            case net.twoant.master.R.id.rl_many_publishactivity:
                many.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.icon_selected));
                single.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.icon_unselected));
                join_limit = 0;
                break;
            case net.twoant.master.R.id.rl_adddetail_publishactivity:
                Intent intent = new Intent(PublishDetailActivity.this,AddRegistrationFee.class);
                intent.putExtra("position",position+"");
                startActivityForResult(intent,ACTIVITY_ITEM);
                break;
            case net.twoant.master.R.id.rl_showdetail_publishactivity:
                Intent showIntent = new Intent(PublishDetailActivity.this,ShowActionDetalActivity.class);
                showIntent.putExtra("position",position+"");
                switch(position){
                    case 0://积分
                        if (TextUtils.isEmpty(integral)) {
                            ToastUtil.showLong("还未填写积分费用");
                            return;
                        }
                        showIntent.putExtra("info",integral+"");
                        startActivity(showIntent);
                        break;
                    case 1://收费
                        if (TextUtils.isEmpty(havemoney)) {
                            ToastUtil.showLong("还未填写收费费用");
                            return;
                        }
                        showIntent.putExtra("info",havemoney+"");
                        startActivity(showIntent);
                        break;
                    case 2://储值
                        if (TextUtils.isEmpty(chuzhi)) {
                            ToastUtil.showLong("还未填写储值费用");
                            return;
                        }
                        showIntent.putExtra("info",chuzhi+"");
                        startActivity(showIntent);
                        break;
                    case 3://计次
                        if (TextUtils.isEmpty(activity_jici)) {
                            ToastUtil.showLong("还未填写计次费用");
                            return;
                        }
                        showIntent.putExtra("info",activity_jici+"");
                        startActivity(showIntent);
                        break;
                    case 4://红包
                        if (TextUtils.isEmpty(redprice)) {
                            ToastUtil.showLong("还未填写红包费用");
                            return;
                        }
                        showIntent.putExtra("info",redprice+"");
                        startActivity(showIntent);
                        break;
                }
                break;
            case net.twoant.master.R.id.rl_addshow_publish:
                clickImageverPosition = 1;
                selectPhoto();
                break;
            case net.twoant.master.R.id.activity_start_time_layout:
                check_time = 1;
                pvTime.show();
                break;
            case net.twoant.master.R.id.activity_end_time_layout:
                check_time = 2;
                pvTime.show();
                break;
            case net.twoant.master.R.id.iv_delete_showphoto:
                fl_showphoto.setVisibility(View.GONE);
                showFile = null;
                break;
            case net.twoant.master.R.id.iv_blod_publishactivity://加粗
                if (isBold) {
                    ToastUtil.showLong("取消粗体字");
                }else {
                    ToastUtil.showLong("粗体字");
                }
                isBold = !isBold;
                mEditor.setBold();
                break;
            case net.twoant.master.R.id.iv_underline_publishactivity://下划线
                if (isItalic) {
                    ToastUtil.showLong("取消斜体字");
                }else {
                    ToastUtil.showLong("斜体字");
                }
                isItalic = !isItalic;
                mEditor.setUnderline();
                break;
            case net.twoant.master.R.id.iv_size_publishactivity://字体大小
                if (popFont != null && popFont.isShowing()) {
                    popFont.dismiss();
                } else {
                    showFontPopupWindow(view);
                }
                break;
            case net.twoant.master.R.id.iv_fontcolor_publishactivity://字体颜色
                if (popColor != null && popColor.isShowing()) {
                    popColor.dismiss();
                } else {
                    showColorPopupWindow(view);
                }
                break;
            case net.twoant.master.R.id.iv_selectphoto_publishactivity://选择图片-添加图片
                clickImageverPosition = 2;
                selectPhoto();
                break;
            case net.twoant.master.R.id.iv_video_publishactivity://添加视频
                ToastUtil.showLong("此功能暂未开放");
                break;
            case net.twoant.master.R.id.bt_post_publishactivity:
                clickImageverPosition = 3;
                //非空判断
                String activityTitle = etActivityName.getText().toString().trim();
                if (TextUtils.isEmpty(activityTitle)) {
                    ToastUtil.showLong("亲，填下活动名称吧");
                    return;
                }
                String startTime = activity_start_time.getText().toString().trim();
                if (TextUtils.isEmpty(startTime)) {
                    ToastUtil.showLong("亲，请选择活动开始时间吧》_《");
                    return;
                }
                String endTime = activity_end_time.getText().toString().trim();
                if (TextUtils.isEmpty(endTime)) {
                    ToastUtil.showLong("亲，请选择活动结束时间吧》_《");
                    return;
                }
                String address = etAddress.getText().toString().trim();
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.showLong("亲，请填下活动地点吧》_《");
                    return;
                }

                if (null==showFile ) {
                    ToastUtil.showLong("亲，请选择一张活动展示图片吧》_《");
                    return;
                }

                if (TextUtils.isEmpty(activity_img_txt)) {
                    ToastUtil.showLong("亲，请描述一下详情吧》_《");
                    return;
                }

                switch(position){
                    case 0://积分
                        if (TextUtils.isEmpty(integral)) {
                            ToastUtil.showLong("请到报名费用中设置费用");
                            return;
                        }
                        break;
                    case 1://收费
                        if (TextUtils.isEmpty(havemoney)) {
                            ToastUtil.showLong("请到报名费用中设置费用");
                            return;
                        }
                        break;
                    case 2://储值
                        if (TextUtils.isEmpty(chuzhi)) {
                            ToastUtil.showLong("请到报名费用中设置费用");
                            return;
                        }
                        break;
                    case 3://计次
                        if (TextUtils.isEmpty(activity_jici)) {
                            ToastUtil.showLong("请到报名费用中设置费用");
                            return;
                        }
                        break;
                    case 4://红包
                        if (TextUtils.isEmpty(redprice)) {
                            ToastUtil.showLong("请到报名费用中设置费用");
                            return;
                        }
                        break;
                }
                postPublishActivity(activityTitle,startTime,endTime,address,
                        redprice,chuzhi,havemoney,activity_jici,integral);
                break;
        }
    }

    private void postPublishActivity(String activityTitle,String startTime,String endTime,String address,
                                     String redprice,String chuzhi,
                                     String havemoney,String activity_jici,String integral) {
        if (TextUtils.isEmpty(redprice))
            redprice = "";

        if (TextUtils.isEmpty(integral))
            integral = "";

        if (TextUtils.isEmpty(chuzhi))
            chuzhi = "";

        if (TextUtils.isEmpty(havemoney))
            havemoney = "";

        if (TextUtils.isEmpty(activity_jici))
            activity_jici = "";

        HttpConnectedUtils utilsInstance = HttpConnectedUtils.getInstance(this);
        HashMap<String,String> map = new HashMap<>();
        map.put("sort",position+"");//活动类型
        map.put("shop",shop_id);//商家id
        map.put("user","-1");//用户id，
        map.put("title",activityTitle);//活动标题
        map.put("address ",address);//活动地点
        map.put("items_1",havemoney);//仅收费活动：啤酒_10_1000_5_6
        map.put("items_2",chuzhi);//仅储值活动：啊啊_100_200_6,
        map.put("items_3",activity_jici);//仅计次活动
        map.put("items_4",redprice);//仅红包活动：100_10
        map.put("items_0",integral);//仅积分活动
        map.put("activity_red_shop","0");//仅红包活动，0不是平台，1是
        map.put("start_time",startTime);//开始时间
        map.put("end_time",endTime);//结束时间
//        map.put("lon",longitude);//经度
//        map.put("lat",latitude);//纬度
        map.put("des",activity_img_txt);//h5标签
        map.put("join_qty_limit",join_limit+"");
        String str = null;
        for (String strings1:map.keySet()){
            String value = map.get(strings1);
            String s = strings1+":"+value+"\r\n";
            str = s +str;
        }
        LogUtils.d(str);
        Map<String,File> m = new HashMap();
        m.put("cover",showFile);//活动图
//        for (int i = 1; i <= filesDrtail.size(); i++) {
//            m.put("activity_img",filesDrtail.get(i-1));//详细图
//        }
        hintDialogUtil.showLoading(net.twoant.master.R.string.get_posting);
        utilsInstance.startNetworkUploading(1, ApiConstants.ACTIVITY_PUB,map,m);
    }


    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(PublishDetailActivity.this, Gravity.BOTTOM, true);
        listViewDialog.setInitData(items,"取消");
        listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
        listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                switch (position) {
                    case 0:
                        // 调用拍照
                        // startGetPhoto(true,1,tempFile,640,280,true);
                        if (clickImageverPosition==1) {
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(true,1,tempFile,640,374,true);
                        }else if (clickImageverPosition==2) {
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(true,1,tempFile);
                        }
                        break;
                    case 1:
                        // 调用相册
                        if (clickImageverPosition==1) {
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(false,1,tempFile,640,374,true);
                        }else if (clickImageverPosition==2) {
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(false,1,tempFile);
                        }
                        break;
                }
            }
        });
        listViewDialog.showDialog(true,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_CARMERA:
                startPhotoZoom(Uri.fromFile(tempFile), 400);
                break;
            case PHOTO_PICK:
                if (null != data) {
                    startPhotoZoom(data.getData(), 400);
                }
                break;
            case PHOTO_CUT:
                if (null != data) {
                    setPicToView(data);
                }
                break;
            case ACTIVITY_ITEM:
                if (null != data) {
                    String str = data.getStringExtra("str");
                    if (position==0)//积分
                        integral = str;
                    if (position==1)//收费
                        havemoney = str;
                    if (position==2)//储值
                        chuzhi = str;
                    if (position==3)//计次
                        activity_jici = str;
                    if (position==4)//红包
                        redprice = str;
                    LogUtils.d(str);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 调用系统裁剪
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以裁剪
        intent.putExtra("crop", true);
        // aspectX,aspectY是宽高的比例
        if (clickImageverPosition == 1) {
            intent.putExtra("aspectX", 640);
            intent.putExtra("aspectY", 280);
            // outputX,outputY是裁剪图片的宽高
            intent.putExtra("outputX", size);
            intent.putExtra("outputY", size/2);
        } else if (clickImageverPosition == 2) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 2);
            // outputX,outputY是裁剪图片的宽高
            intent.putExtra("outputX", size*2);
            intent.putExtra("outputY", size);
        }
        //不启用人脸识别
        //intent.putExtra("noFaceDetection", true);
        // 设置是否返回数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CUT);
    }

    // 将裁剪后的图片显示在ImageView上
    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (null != bundle) {
            final Bitmap bmp = bundle.getParcelable("data");
            //设置圆形图片
            // Bitmap bitmap = MakeRoundCorner.makeRoundCorner(bmp);
            addPhoto(bmp);
            //保存本地file
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            int quality = 100;
            OutputStream stream = null;
            try {
                String file = Environment.getExternalStorageDirectory() + File.separator + getPhotoFileName();
                stream = new FileOutputStream(file);
                bmp.compress(format, quality, stream);
                File dirFile = new File(file);
                //点击添加位置对号入座
                if (clickImageverPosition==1) {
                    showFile = dirFile;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPhoto(Bitmap bitmap) {
        if (clickImageverPosition==1) {
            showPhoto.setImageBitmap(bitmap);
            fl_showphoto.setVisibility(View.VISIBLE);
        }else if(clickImageverPosition==2){
            int x = (int) getResources().getDimension(net.twoant.master.R.dimen.px_120);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(x, x);
            ImageView imageView = new ImageView(PublishDetailActivity.this);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.leftMargin = x/7;
            imageView.setLayoutParams(params);
            ivDetailContainer.addView(imageView);
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=showFile) {
            showFile.delete();
        }
    }

    @Override
    public void onBefore(Request request, int id) {

    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        LogUtils.d(response);
        if (clickImageverPosition==1) {

        } else if (clickImageverPosition == 2) {
            String msg = BaseConfig.getCorrectImageUrl(BaseConfig.getImageUrlForJson(response));
            if (!TextUtils.isEmpty(msg)) {
                mEditor.insertImage(msg,"dachshund");
            }
            hintDialogUtil.dismissDialog();
        } else if (clickImageverPosition == 3) {
            PublishDetailResultBean publishDetailResultBean = JsonUtil.parseJsonToBean(response, PublishDetailResultBean.class);
            hintDialogUtil.dismissDialog();
            boolean success = publishDetailResultBean.isSuccess();
            if (success) {
                ToastUtil.showLong("发布成功");
                finish();
            }
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        hintDialogUtil.showError("请求失败");
    }

    @Override
    public void takeSuccess(TResult result) {
        if (clickImageverPosition==1) {
            TImage image = result.getImage();
            showFile = new File(image.getCompressPath());
            ImageLoader.getImageFromLocation(showPhoto,showFile);
            fl_showphoto.setVisibility(View.VISIBLE);
        }else if (clickImageverPosition==2) {
            hintDialogUtil.showLoading(net.twoant.master.R.string.dialogloading);
            HttpConnectedUtils utilsInstance = HttpConnectedUtils.getInstance(this);
            TImage image = result.getImage();
            File file = new File(image.getCompressPath());
            HashMap<String,File> m = new HashMap<>();
            m.put("file",file);
            LogUtils.d(file.getAbsolutePath());
            utilsInstance.startNetworkUploading(1,ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE,null,m);
        }
    }

    private void initFontPopupWindow() {
        // 初始化poPupwindow控件
        viewFontSize = this.getLayoutInflater().inflate(
                net.twoant.master.R.layout.zy_pop_fontsize, null);
        popFont = new PopupWindow(viewFontSize, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        viewFontSize.findViewById(net.twoant.master.R.id.tv_pop_fontbig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);//设置大号字体
                closeOptionPopupWindow();
                ToastUtil.showLong("大号字体");
            }
        });
        viewFontSize.findViewById(net.twoant.master.R.id.tv_pop_fontmiddle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);//设置中号字体
                closeOptionPopupWindow();
                ToastUtil.showLong("中号字体");
            }
        });
        viewFontSize.findViewById(net.twoant.master.R.id.tv_pop_fontsmall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);//设置小号字体
                closeOptionPopupWindow();
                ToastUtil.showLong("小号字体");
            }
        });
        // 设置点击周围取消poPupwindow
        popFont.setOutsideTouchable(true);
        // 设置puPupwindow获取焦点
        popFont.setFocusable(true);
        // 设置poPupwindow可以点后退键取消poPupwindow
        popFont.setBackgroundDrawable(new BitmapDrawable());
        // 点击周围取消poPupwindow
        viewFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOptionPopupWindow();
            }
        });
    }

    private void initColorPopupWindow() {
        // 初始化poPupwindow控件
        viewFontColor = this.getLayoutInflater().inflate(
                net.twoant.master.R.layout.zy_pop_fontcolor, null);
        popColor = new PopupWindow(viewFontColor, ViewGroup.LayoutParams.MATCH_PARENT,
                CommonUtil.getDimens(net.twoant.master.R.dimen.px_180), true);
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_1));
                ToastUtil.showLong("红");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_2));
                ToastUtil.showLong("橙");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_3));
                ToastUtil.showLong("紫");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_4));
                ToastUtil.showLong("黄");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_5));
                ToastUtil.showLong("绿");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_6));
                ToastUtil.showLong("淡绿");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_7));
                ToastUtil.showLong("蓝");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_8));
                ToastUtil.showLong("深蓝");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_9));
                ToastUtil.showLong("灰");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(net.twoant.master.R.id.fontcolor10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(net.twoant.master.R.color.fontcolor_10));
                ToastUtil.showLong("黑");
                popColor.dismiss();
            }
        });
        // 设置点击周围取消poPupwindow
        popColor.setOutsideTouchable(true);
        // 设置puPupwindow获取焦点
        popColor.setFocusable(true);
        // 设置poPupwindow可以点后退键取消poPupwindow
        popColor.setBackgroundDrawable(new BitmapDrawable());
        // 点击周围取消poPupwindow
        viewFontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOptionPopupWindow();
            }
        });
    }
    /**
     * 关闭窗口
     */
    private void closeOptionPopupWindow() {
        if (popFont != null && popFont.isShowing()) {
            popFont.dismiss();
        }
        if (popColor != null && popColor.isShowing()) {
            popColor.dismiss();
        }
    }

    private void showFontPopupWindow(View v) {
        //获取控件的坐标
        int[] location = new int[2];
        //获取控件的坐标
        ivFontSize.getLocationOnScreen(location);
        int width = location[0];
        int height = location[1];
        popFont.setAnimationStyle(net.twoant.master.R.style.pop_invite_anim);
        popFont.showAtLocation(v, Gravity.NO_GRAVITY, width-CommonUtil.getDimens(net.twoant.master.R.dimen.px_60), (int) (height - CommonUtil.getViewHeight(viewFontSize, true)-getResources().getDimension(net.twoant.master.R.dimen.px_20)));
        popFont.update();
    }
    private void showColorPopupWindow(View v){
        //获取控件的坐标
        int[] location = new int[2];
        //获取控件的坐标
        ivFontColor.getLocationOnScreen(location);
        int height = location[1];
        popColor.showAtLocation(v, Gravity.NO_GRAVITY, 300  , (int) (height - CommonUtil.getViewHeight(viewFontColor, true)-getResources().getDimension(net.twoant.master.R.dimen.px_150)));
        popColor.update();
    }

    @Override
    public void onBackPressed() {
        if (popFont.isShowing()||popColor.isShowing()) {
            closeOptionPopupWindow();
        } else {
            finish();
        }
    }
}