package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.LongLongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.other.bean.HttpResultBean;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;
import net.twoant.master.widget.timer.TimePickerView;

import org.json.JSONException;
import org.json.JSONObject;

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
public class PublishDetailActivity_ extends LongLongBaseActivity implements View.OnClickListener,HttpConnectedUtils.IOnStartNetworkCallBack {

    private RichEditor mEditor;
    private ImageView imageView,showPhoto,deleteShow,ivFontSize,ivFontColor;
    private LinearLayout ivDetailContainer,etDetailName6;
    private RelativeLayout rlAddShowPhoto,start_time_action,end_time_action;
    private TextView activity_start_time,activity_end_time,etDetailName1,etDetailName2
            ,etDetailName3,etDetailName4,etDetailName5;
    private FrameLayout fl_showphoto;
    private EditText etFirstCharge,etSecondCharge,etIntroduce,etActivityName,etAddress, etActivityCount;
    private HintDialogUtil hintDialogUtil;
    private int check_time=1;//1 是开始时间 2是结束时间
    TimePickerView pvTime;

    private String latitude;//获取纬度
    private String longitude;//获取经度

    private Spinner spinner;
    private List categoryList;
    private List items;
    private static final int PHOTO_CARMERA = 1;
    private static final int PHOTO_PICK = 2;
    private static final int PHOTO_CUT = 3;
    private int clickImageverPosition;//用于记录点击的是展示图片按钮还是详细图片按钮
    private int position;

    private List<File> filesDrtail;//活动详情图集合
    private File showFile;//活动展示图

    private PopupWindow popFont;
    private PopupWindow popColor;
    private View viewFontSize;//popwindow字体大小的页面
    private View viewFontColor;//popwindow字体颜色的页面
    private String activity_img_txt;

    // 创建一个以当前系统时间为名称的文件，防止重复,用于照相机拍照暂时存储文件
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator +
            getPhotoFileName());
    public String address;

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_publishactivity;
    }



    private void initView() {
        showPhoto = (ImageView) findViewById(net.twoant.master.R.id.iv_showphoto_publish);
        imageView = (ImageView) findViewById(net.twoant.master.R.id.iv_add_detail_publish);
        rlAddShowPhoto = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_addshow_publish);
        start_time_action = (RelativeLayout) findViewById(net.twoant.master.R.id.activity_start_time_layout);
        end_time_action = (RelativeLayout) findViewById(net.twoant.master.R.id.activity_end_time_layout);
        activity_start_time = (TextView) findViewById(net.twoant.master.R.id.activity_start_time);
        activity_end_time = (TextView) findViewById(net.twoant.master.R.id.activity_end_time);
        fl_showphoto = (FrameLayout) findViewById(net.twoant.master.R.id.fl_showphoto);
        etAddress = (EditText) findViewById(net.twoant.master.R.id.et_address_publishactivity);
        deleteShow = (ImageView) findViewById(net.twoant.master.R.id.iv_delete_showphoto);
        etDetailName1 = (TextView) findViewById(net.twoant.master.R.id.tv_detailname1_publishactivity);
        etDetailName2 = (TextView) findViewById(net.twoant.master.R.id.tv_detailname2_publishactivity);
        etDetailName3 = (TextView) findViewById(net.twoant.master.R.id.tv_detailname3_publishactivity);
        etDetailName4 = (TextView) findViewById(net.twoant.master.R.id.tv_detailname4_publishactivity);
        etDetailName5 = (TextView) findViewById(net.twoant.master.R.id.tv_detailname5_publishactivity);
        etDetailName6 = (LinearLayout) findViewById(net.twoant.master.R.id.tv_detailname6_publishactivity);
        etFirstCharge = (EditText) findViewById(net.twoant.master.R.id.et_fisrtcharge_publishactivity);
        etSecondCharge = (EditText) findViewById(net.twoant.master.R.id.et_secondcharge_publishactivity);
        etActivityName = (EditText) findViewById(net.twoant.master.R.id.et_activityname_publishactivity);
        etActivityCount = (EditText) findViewById(net.twoant.master.R.id.activity_people_count);
        findViewById(net.twoant.master.R.id.bt_post_publishactivity).setOnClickListener(this);
        deleteShow.setOnClickListener(this);
        start_time_action.setOnClickListener(this);
        end_time_action.setOnClickListener(this);
        imageView.setOnClickListener(this);
        rlAddShowPhoto.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_blod_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_underline_publishactivity).setOnClickListener(this);
        ivFontSize = (ImageView) findViewById(net.twoant.master.R.id.iv_size_publishactivity);
        ivFontSize.setOnClickListener(this);
        ivFontColor = (ImageView) findViewById(net.twoant.master.R.id.iv_fontcolor_publishactivity);
        ivFontColor.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_selectphoto_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_video_publishactivity).setOnClickListener(this);
        ivDetailContainer = (LinearLayout)findViewById(net.twoant.master.R.id.ll_detail_container_publish);//活动图片内容容器
        filesDrtail = new ArrayList<>();
        // 时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(PublishDetailActivity_.this,android.R.layout.simple_list_item_1,categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PublishDetailActivity_.this.position = position;
                judgeSelected();
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
        mEditor.setPlaceholder("请在此留下宝贝详情描述...");

        //解决scrollow和webview冲突
//        mEditor.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent ev) {
//                ((WebView)v).requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
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
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        String address =  instance.getCompletionAddress();
//        if (TextUtils.isEmpty(address)) {
//            ToastUtil.showLong("网络异常");
//            finish();
//        }
        etAddress.setText(address);
        latitude = instance.getLatitude() + "";
        longitude = instance.getLongitude() + "";
        hintDialogUtil.dismissDialog();
    }

    private void judgeSelected() {
        if (position==0) {//积分
            etDetailName1.setText("赠送积分：");
            etDetailName4.setText("积分活动详情");
        }else if (position==1) {//收费
            etDetailName1.setText("活动条名称：");
            etDetailName2.setText("¥：");
            etDetailName4.setText("收费活动详情");
            etDetailName5.setVisibility(View.GONE);
        }else if (position==2) {//储值
            etDetailName1.setText("充");
            etDetailName2.setText("送");
            etDetailName4.setText("储值活动详情");
        }else if (position==3) {//计次活动
            etDetailName1.setText("¥：");
            etDetailName2.setText("次数：");
            etDetailName3.setVisibility(View.GONE);
            etDetailName4.setText("计次活动详情");
        }else if (position==4){//红包
            etDetailName1.setText("满");
            etDetailName2.setText("可用");
            etDetailName4.setText("红包活动详情");
        }

    }

    String redprice;//红包value
    String chuzhi;//储值value
    String havemoney;//收费value
    String activity_price;//计次必填价钱value
    String activity_jici;//计次必填次数value
    String integral;//积分
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case net.twoant.master.R.id.rl_addshow_publish:
                clickImageverPosition = 1;
                selectPhoto();
                break;
            case net.twoant.master.R.id.iv_add_detail_publish:
//                clickImageverPosition = 2;
//                selectPhoto();
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
                mEditor.setBold();
                break;
            case net.twoant.master.R.id.iv_underline_publishactivity://下划线
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
                String activityCount = etActivityCount.getText().toString().trim();
                if (TextUtils.isEmpty(activityCount)) {
                    ToastUtil.showLong("亲，请填下活动人数吧》_《");
                    return;
                }
//                String introduce = etIntroduce.getText().toString().trim();
//                if (TextUtils.isEmpty(introduce)) {
//                    ToastUtil.showLong("亲，请填下活动介绍吧》_《");
//                    return;
//                }

                if (null==showFile ) {
                    ToastUtil.showLong("亲，请选择一张活动展示图片吧》_《");
                    return;
                }
//                if (filesDrtail.size()==0) {
//                    ToastUtil.showLong("亲，请选择一张活动详情图片吧》_《");
//                    return;
//                }
                if (position == 0) {//积分
                    String firstCharge = etFirstCharge.getText().toString().trim();
                    if (TextUtils.isEmpty(firstCharge)) {
                        ToastUtil.showLong("亲，您要赠送多少积分呢?");
                        return;
                    }
                    integral = firstCharge;
                }else if (position == 1) {//收费
                    String firstCharge = etFirstCharge.getText().toString().trim();
                    String secondCharge = etSecondCharge.getText().toString().trim();
                    if (!judgeFirstAndSecond(firstCharge,secondCharge)) {
                        return;
                    }
                    havemoney = firstCharge+"_"+secondCharge;


                }else if (position == 2) {//储值
                    String firstCharge = etFirstCharge.getText().toString().trim();
                    String secondCharge = etSecondCharge.getText().toString().trim();
                    if (!judgeFirstAndSecond(firstCharge,secondCharge)) {
                        return;
                    }
                    chuzhi = firstCharge+"_"+secondCharge;
                } else if (position == 3) {//计次
                    String firstCharge = etFirstCharge.getText().toString().trim();
                    String secondCharge = etSecondCharge.getText().toString().trim();
                    if (!judgeFirstAndSecond(firstCharge,secondCharge)) {
                        return;
                    }
                    activity_price = firstCharge;//计次价钱
                    activity_jici = secondCharge;//计次次数
                }else if (position == 4) {//红包
                    String firstCharge = etFirstCharge.getText().toString().trim();
                    String secondCharge = etSecondCharge.getText().toString().trim();
                    if (!judgeFirstAndSecond(firstCharge,secondCharge)) {
                        return;
                    }
                    redprice = firstCharge+"_"+secondCharge;
                }
                postPublishActivity(activityTitle,startTime,endTime,address,activityCount,
                        redprice,chuzhi,havemoney,activity_price,activity_jici,integral);
                break;
        }
    }

    private boolean judgeFirstAndSecond(String firstCharge,String secondCharge) {
        if (TextUtils.isEmpty(firstCharge)) {
            ToastUtil.showLong("亲，红包详情价格没有填哦》_《");
            return false;
        }
        if (TextUtils.isEmpty(secondCharge)) {
            ToastUtil.showLong("亲，满"+firstCharge+"可用多少呢?");
            return false;
        }
        return true;
    }

    private void postPublishActivity(String activityTitle,String startTime,String endTime,String address,
                                      String activityCount,String redprice,String chuzhi,
                                     String havemoney,String activity_price,String activity_jici,String integral) {
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

        if (TextUtils.isEmpty(activity_price))
            activity_price = "";

        HttpConnectedUtils utilsInstance = HttpConnectedUtils.getInstance(this);
        HashMap<String,String> map = new HashMap<>();
        map.put("activity_type",position+"");//活动类型
        map.put("shop_id_pk","13");//商家id
        map.put("antivity_uid","-1");//用户id，
        map.put("activity_title",activityTitle);//活动标题
        map.put("activity_price",activity_price);//活动价钱
        map.put("activity_count",activityCount);//活动人数
        map.put("activity_address ",address);//活动地点
        map.put("activity_havemoney",havemoney);//仅收费活动：啤酒_10,米饭_20
        map.put("activity_chuzhi",chuzhi);//仅储值活动：100_100
        map.put("activity_red_price",redprice);//仅红包活动：100_10
        map.put("activity_red_shop","0");//仅红包活动，0不是平台，1是
        map.put("activity_integral",integral);//仅积分活动
        map.put("activity_jici",activity_jici);//仅计次活动
        map.put("activity_start_time",startTime);//开始时间
        map.put("activity_end_time",endTime);//结束时间
        map.put("activity_longitude",longitude);//经度
        map.put("activity_latitude",latitude);//纬度
        map.put("activity_img_txt",activity_img_txt);//h5标签
        String str = null;
        for (String strings1:map.keySet()){
            String value = map.get(strings1);
            String s = strings1+":"+value+"\r\n";
            str = s +str;
        }
        LogUtils.d(str);

        Map<String,File> m = new HashMap();
        m.put("activity_cover",showFile);//活动图
        for (int i = 1; i <= filesDrtail.size(); i++) {
            m.put("activity_img"+i,filesDrtail.get(i-1));//详细图
        }
        utilsInstance.startNetworkUploading(1,"http://121.42.198.26:8080/aisou_server/send_activity.do",map,m);
    }

    // 调用系统相机
    protected void startCamera() {
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 2); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        // 指定调用相机拍照后照片的存储路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        startActivityForResult(intent, PHOTO_CARMERA);
    }

    // 调用系统相册
    protected void startPick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICK);
    }

    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(PublishDetailActivity_.this, Gravity.BOTTOM, true);
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
                            startCamera();
                        }else if (clickImageverPosition==2) {
                            startGetPhoto(true,1,tempFile);
                        }
                        break;
                    case 1:
                        // 调用相册
                        if (clickImageverPosition==1) {
                             startPick();
                        }else if (clickImageverPosition==2) {
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
                } else if (clickImageverPosition == 2) {
                    LogUtils.d(dirFile.getAbsolutePath());
                    //添加详情list集合
                    //filesDrtail.add(dirFile);
                    //生成图片地址
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
            ImageView imageView = new ImageView(PublishDetailActivity_.this);
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
        if (null!=filesDrtail && filesDrtail.size()>0) {
            for (File file : filesDrtail){
                file.delete();
            }
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
            HttpResultBean httpResultBean = GsonUtil.gsonToBean(response, HttpResultBean.class);
            HttpResultBean.ResultBean result = httpResultBean.getResult();
            String msg = result.getMsg();
            mEditor.insertImage(msg,"dachshund");
            hintDialogUtil.dismissDialog();
        } else if (clickImageverPosition == 3) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String activity_id = jsonObject.getString("activity_id");
                ToastUtil.showLong(activity_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        hintDialogUtil.showError("请求失败");
    }

    @Override
    public void takeSuccess(TResult result) {
        hintDialogUtil.showLoading(net.twoant.master.R.string.dialogloading);
        HttpConnectedUtils utilsInstance = HttpConnectedUtils.getInstance(this);
        TImage image = result.getImage();
        File file = new File(image.getCompressPath());
        HashMap<String,File> m = new HashMap<>();
        m.put("img",file);
        LogUtils.d(file.getAbsolutePath());
        utilsInstance.startNetworkUploading(1,"http://121.42.198.26:8080/aisou_server/upload.do",null,m);
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