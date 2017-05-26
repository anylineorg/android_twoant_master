package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.text.format.Formatter;
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
import android.widget.Spinner;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongLongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.activity.out.AddClassifyDetailActivity;
import net.twoant.master.ui.my_center.bean.ClassifyListBean;
import net.twoant.master.ui.my_center.bean.GoodsDetailBean;
import net.twoant.master.ui.my_center.bean.GoodsDetailImageBean;
import net.twoant.master.ui.my_center.httputils.ClassifyHttpUtils;
import net.twoant.master.ui.my_center.httputils.SearchGoodsHttpUtils;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by DZY on 2016/11/18.
 */
public class EditGoodsActivity extends LongLongBaseActivity implements View.OnClickListener,HttpConnectedUtils.IOnStartNetworkCallBack {

    private GoodsDetailImageBean.ResultBean result;
    private Spinner spinner;
    private List<String> categoryList;
    private List<String> categoryIDList;
    private LinearLayout showPhotoContain;
    private List<ClassifyListBean.ResultBean> mDatasList;
    private EditText etGoodsName,etGoodsPrice,etGoodsCount,etGoodsLd;
    private List<File> filesShow;  //店铺展示图集合
    private ArrayList<String> localImages = new ArrayList<>();
    private File fileImg;           //商品列表图
    private String fileImgStr;
    private int clickImageverPosition;//用于记录点击：1商品主图轮播,2富文本框添加图片,3商品列表展示图
    private RichEditor mEditor;
    private String goods_img_txt;//h5图文介绍
    private View viewFontSize;//popwindow字体大小的页面
    private View viewFontColor;//popwindow字体颜色的页面
    private PopupWindow popFont;
    private PopupWindow popColor;
    private ImageView showImgDelete,showImgAdd,ivFontSize,ivFontColor;
    private List items;
    private HintDialogUtil hintDialogUtil;
    private PublishGoodsBroadcastReceiver myBroadcastReceiver;

    // 创建一个以当前系统时间为名称的文件，防止重复
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator +
            getPhotoFileName());
    private ArrayAdapter arrayAdapter;
    private String goodsTypeId;
    private HttpConnectedUtils utilsInstance;
    private String shop_id;
    private String goodsid;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean flag = false;
    public int flag_x;
    public int dataListSize = 0;
    public int spinnerPosittion;
    public String goods_bander_img1;
    public String goods_bander_img2;
    public String goods_bander_img3;
    public String goods_bander_img4;
    //    private int flag = 0;
    private int oldCarouselSize;

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_publishgoods;
    }


    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        shop_id = getIntent().getStringExtra("shop_id");
        goodsid = getIntent().getStringExtra("goodsid");
        initView();
        super.subOnCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void requestNetDataForClass() {
        ClassifyHttpUtils.LongHttp("0", shop_id, "", "", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                ClassifyListBean classifyListBean = GsonUtil.gsonToBean(response, ClassifyListBean.class);
                mDatasList.clear();
                categoryList.clear();
                categoryIDList.clear();
                mDatasList = classifyListBean.getResult();

//                if (dataListSize!=mDatasList.size() ) {
                    for (ClassifyListBean.ResultBean resultBean: mDatasList){
                        categoryList.add(resultBean.getNM());
                        categoryIDList.add(resultBean.getCD());
                    }
                    if (mDatasList.size()==0) {
                        flag = true;
                        categoryList.add("暂无分类");
                        flag_x = 0;
                    }else {
                        flag = false;
                    }
                    categoryList.add("去添加分类");
                    //设置spinner适配器
                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerPosittion = position;
                            System.out.println(position);
                            int size = categoryList.size();
                            if (flag==true) {
                                if (position==0) {
                                    ++position;
                                }else{

                                }
                            }
                            if (size == ++position) {
                                if (flag == true ) {
                                    ++flag_x;
                                    if (flag_x == 2) {
                                        Intent intent = new Intent(EditGoodsActivity.this,AddClassifyDetailActivity.class);
                                        intent.putExtra("shopid",shop_id);
                                        startActivity(intent);
                                        requestNetDataForClass();
                                    }
                                    return;
                                }
                                if (flag==false) {
                                    Intent intent = new Intent(EditGoodsActivity.this,AddClassifyDetailActivity.class);
                                    intent.putExtra("shopid",shop_id);
                                    startActivity(intent);
                                    requestNetDataForClass();
                                    return;
                                }
                            }
                            if (flag == false) {
                                if (position != 0) {
                                    goodsTypeId = mDatasList.get(--position).getCD()+"";
                                }
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                dataListSize = mDatasList.size();
             }
        });
    }

    private void initView() {
        mDatasList = new ArrayList<>();
        ivFontColor = (ImageView) findViewById(net.twoant.master.R.id.iv_fontcolor_publishactivity);
        TextView tvTitle = (TextView) findViewById(net.twoant.master.R.id.tv_Title);
        tvTitle.setText("编辑商品");
        showPhotoContain = (LinearLayout) findViewById(net.twoant.master.R.id.ll_show_container_publish);
        mEditor = (RichEditor) findViewById(net.twoant.master.R.id.editor_publishgoods);
        mEditor.setEditorHeight(CommonUtil.getDimens(net.twoant.master.R.dimen.px_120));
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("请在此留下活动详情描述...");
        etGoodsName = (EditText) findViewById(net.twoant.master.R.id.edit_product_name);
        etGoodsPrice = (EditText) findViewById(net.twoant.master.R.id.edit_product_price);
        etGoodsCount = (EditText) findViewById(net.twoant.master.R.id.edit_product_count);
        showImgAdd = (ImageView) findViewById(net.twoant.master.R.id.iv_show_publishgoods);
        etGoodsLd = (EditText) findViewById(net.twoant.master.R.id.edit_product_remark);
        showImgDelete = (ImageView) findViewById(net.twoant.master.R.id.iv_delete_publishgoods);
        showImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getImageFromLocation(showImgAdd, net.twoant.master.R.drawable.add+"", net.twoant.master.R.drawable.add);
                showImgDelete.setVisibility(View.INVISIBLE);
                fileImgStr = "";
            }
        });
        ivFontSize = (ImageView) findViewById(net.twoant.master.R.id.iv_size_publishactivity);
        findViewById(net.twoant.master.R.id.iv_product_add).setOnClickListener(this);
        AppCompatButton appCompatButton = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_post_publishgoods);
        appCompatButton.setText("确 认 修 改");
        appCompatButton.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_selectphoto_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_blod_publishactivity).setOnClickListener(this);
        showImgAdd.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_underline_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_size_publishactivity).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_fontcolor_publishactivity).setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
        utilsInstance = HttpConnectedUtils.getInstance(this);
        spinner = (Spinner) findViewById(net.twoant.master.R.id.sp_selectclass_publish);
        filesShow = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryIDList = new ArrayList<>();
        //spinner 适配器
        arrayAdapter = new ArrayAdapter(EditGoodsActivity.this,android.R.layout.simple_list_item_1,categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        items = new ArrayList();
        items.add("拍照");
        items.add("从相册选择图片");

        requestNetDataForClass();

        //注册广播  ------接口回调不能使用静态注册,只能动态注册
        myBroadcastReceiver = new PublishGoodsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("gengxins");
        registerReceiver(myBroadcastReceiver, intentFilter);
        myBroadcastReceiver.SetOnUpdateUIListenner(new UpdateUIListenner() {
            @Override
            public void UpdateUI(String str) {
                requestNetDataForClass();
            }
        });
    }

    protected void initData() {
        initFontPopupWindow();
        initColorPopupWindow();
        //活动详情h5标签详情
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                goods_img_txt = text;
                System.out.println(text);
            }
        });
        initFontPopupWindow();
        initColorPopupWindow();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
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
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_post_publishgoods:
                hintDialogUtil.showLoading(net.twoant.master.R.string.get_posting);
                clickImageverPosition = 6;
                pulishGoods();
                break;
            case net.twoant.master.R.id.iv_product_add:
                clickImageverPosition = 1;
                int size = localImages.size();
                int size1 = filesShow.size();
                System.out.println(size);
                System.out.println(size1);
                if ((localImages.size()) == 4) {
                    ToastUtil.showLong("已经添加最大限制");
                    return;
                }
                selectPhoto();
                break;
            case net.twoant.master.R.id.iv_selectphoto_publishactivity:
                clickImageverPosition = 2;
                selectPhoto();
                break;
            case net.twoant.master.R.id.iv_show_publishgoods:
                clickImageverPosition = 3;
                selectPhoto();
                break;
        }
    }

    private void pulishGoods() {
        //先判断是否获取到了值
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        double longitude = instance.getLongitude();//经度
        double latitude = instance.getLatitude();  //纬度
        String completionAddress = instance.getCompletionAddress();
        if (TextUtils.isEmpty(completionAddress)) {
            ToastUtil.showLong("网络连接失败,没有获取到当前位置");
            hintDialogUtil.dismissDialog();
            return;
        }
        String goods_name = etGoodsName.getText().toString().trim();
        if (TextUtils.isEmpty(goods_name)) {
            ToastUtil.showLong("商品名不能为空");
            hintDialogUtil.dismissDialog();
            return;
        }
        if (localImages.size()==0) {
            ToastUtil.showLong("请上传商品主图吧~");
            hintDialogUtil.dismissDialog();
            return;
        }
        if (TextUtils.isEmpty(goodsTypeId)) {
            ToastUtil.showLong("请先添加商品分类后发布");
            hintDialogUtil.dismissDialog();
            return;
        }
        String goods_price = etGoodsPrice.getText().toString().trim();
        LogUtils.d(goods_price);
        if (TextUtils.isEmpty(goods_price)) {
            ToastUtil.showLong("请输入商品价钱吧>.<");
            hintDialogUtil.dismissDialog();
            return;
        }
       /* String goods_count = etGoodsCount.getText().toString().trim();
        if (TextUtils.isEmpty(goods_count)) {
            ToastUtil.showLong("请输入商品数量吧>.<");
            hintDialogUtil.dismissDialog();
            return;
        }*/
        String goods_ld = etGoodsLd.getText().toString().trim();
        if (TextUtils.isEmpty(goods_ld)) {
            ToastUtil.showLong("描述下商品亮点吧>.<");
            hintDialogUtil.dismissDialog();
            return;
        }
        if (TextUtils.isEmpty(goods_img_txt)) {
            ToastUtil.showLong("请添加商品详情吧>.<");
            hintDialogUtil.dismissDialog();
            return;
        }
        if (TextUtils.isEmpty(fileImgStr)) {
            ToastUtil.showLong("请上传一张商品列表图吧>.<");
            hintDialogUtil.dismissDialog();
            return;
        }
        if (localImages.size()<oldCarouselSize) {
            ToastUtil.showLong("商品主图至少为"+oldCarouselSize+"张");
            hintDialogUtil.dismissDialog();
            return;
        }
        HttpConnectedUtils utilsInstance = HttpConnectedUtils.getInstance(this);
        HashMap<String,String> m = new HashMap<>();
        m.put("id",goodsid);
        m.put("goods_pk_shop",shop_id);
        m.put("goods_name",goods_name);
        m.put("goods_typeid",goodsTypeId);
        m.put("goods_title","商品标题");
        m.put("goods_price",goods_price);
        m.put("goods_introduce",goods_ld);
        m.put("goods_count","0");

        m.put("longitude",longitude+"");//经度
        m.put("latitude",latitude+"");  //经度
        m.put("address",completionAddress);
        m.put("goods_img_txt",goods_img_txt);//富文本框

        for (int i = 1; i <= localImages.size(); i++) {
            String s = localImages.get(i - 1);
            String url = s;
            m.put("goods_bander_img" + i,url);//店铺展示图
        }
        System.out.println(fileImgStr);

        String replace = fileImgStr;
        System.out.println(replace);
        m.put("goods_img",replace);//列表图
        String str = null;
        for (String strings1:m.keySet()){
            String value = m.get(strings1);
            String s = strings1+":"+value+"\r\n";
            str = s +str;
        }
        LogUtils.d(str);
        utilsInstance.startNetworkGetString(1,m, ApiConstants.BASE + "shp/gd/js");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除保存的图片
        if (null!=filesShow && filesShow.size()>0) {
            for (File file : filesShow){
                file.delete();
                LogUtils.d("已删除文件："+file);
            }
        }

        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
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
        if (clickImageverPosition == 1) {
            final String msg = BaseConfig.getCorrectImageUrl(BaseConfig.getImageUrlForJson(response));
            if (!TextUtils.isEmpty(msg)) {
                localImages.add(msg);
                final View inflate = View.inflate(EditGoodsActivity.this, net.twoant.master.R.layout.zy_publishgoods_entered_select_photo, null);
                final ImageView imageView = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_add);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                if (localImages.size() == 1) {
                    params.setMargins(0,0,0,0);
                }else {
                    params.setMargins(60,0,0,0);
                }
                imageView.setLayoutParams(params);
                ImageView imgDelete = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_delete);
                imgDelete.setVisibility(View.VISIBLE);
                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPhotoContain.removeView(inflate);
                        localImages.remove(msg);
                    }
                });
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getImageFromNetwork(imageView,msg);
                showPhotoContain.addView(inflate);
                hintDialogUtil.dismissDialog();
            }


        }else if (clickImageverPosition == 2) {
            String msg = BaseConfig.getCorrectImageUrl(BaseConfig.getImageUrlForJson(response));
            if (!TextUtils.isEmpty(msg)) {
                mEditor.insertImage(msg,"dachshund");
                hintDialogUtil.dismissDialog();
            }
        }else if (clickImageverPosition == 3){
            String msg = BaseConfig.getCorrectImageUrl(BaseConfig.getImageUrlForJson(response));
            if (!TextUtils.isEmpty(msg)) {
                fileImgStr = msg;
                hintDialogUtil.dismissDialog();
            }
        }else {
            LogUtils.d(response);
            DataRow data = DataRow.parseJson(response);
            boolean success = data.getBoolean("success",false);
            String message = data.getString("message");
            if (success) {
                finish();
            }else{
                ToastUtil.showLong(message);
            }
            hintDialogUtil.dismissDialog();
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        hintDialogUtil.showError("网络连接失败，请重试");
    }

    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(EditGoodsActivity.this, Gravity.BOTTOM, true);
        listViewDialog.setInitData(items,"取消");
        listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
        listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                switch (position) {
                    case 0:
                        // 调用拍照
                        if (clickImageverPosition == 1){
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(true,1,tempFile,640,500,true);
                        }

                        if (clickImageverPosition == 2){
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(true,1,tempFile);
                        }

                        if (clickImageverPosition == 3){
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(true,1,tempFile,1,1,true);
                        }

                        break;
                    case 1:
                        // 调用相册
                        if (clickImageverPosition == 1){
                            int size = localImages.size();
                            if (size==0){
                                configCompress(200*1024,1980,1024,true);
                                startGetPhoto(false,4,tempFile,640,500,true);
                            }
                            if (size==1){
                                configCompress(200*1024,1980,1024,true);
                                startGetPhoto(false,3,tempFile,640,500,true);
                            }
                            if (size==2){
                                configCompress(200*1024,1980,1024,true);
                                startGetPhoto(false,2,tempFile,640,500,true);
                            }
                            if (size==3){
                                configCompress(200*1024,1980,1024,true);
                                startGetPhoto(false,1,tempFile,640,500,true);
                            }
                        }

                        if (clickImageverPosition == 2){
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(false,1,tempFile);
                        }

                        if (clickImageverPosition == 3){
                            configCompress(200*1024,1980,1024,true);
                            startGetPhoto(false,1,tempFile,500,500,true);
                        }
                        break;
                }
            }
        });
        listViewDialog.showDialog(true,true);
    }



    @Override
    public void takeSuccess(TResult result) {
        switch(clickImageverPosition){
            case 1:
                ArrayList<TImage> imageArrayList1 = result.getImages();
                for (int i = 0 ; i < imageArrayList1.size(); i++){
                    final File file = new File(imageArrayList1.get(i).getCompressPath());
                    filesShow.add(file);
                    LogUtils.d(file.getAbsolutePath());
                    hintDialogUtil.showLoading(net.twoant.master.R.string.dialogloading);
                    TImage image2 = result.getImage();
                    File file1 = new File(image2.getCompressPath());
                    HashMap<String,File> m = new HashMap<>();
                    m.put("file",file);
                    utilsInstance.startNetworkUploading(8, ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE,null,m);
                }
                break;
            case 2:
                hintDialogUtil.showLoading(net.twoant.master.R.string.dialogloading);
                TImage image2 = result.getImage();
                File file = new File(image2.getCompressPath());
                HashMap<String,File> m = new HashMap<>();
                m.put("file",file);
                long length = file.length();
                String s = Formatter.formatFileSize(getApplicationContext(), length);
                System.out.println(s);
                LogUtils.d(file.getAbsolutePath());
                utilsInstance.startNetworkUploading(1, ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE,null,m);
                break;
            case 3:
                TImage image3 = result.getImage();
                fileImg = new File(image3.getCompressPath());
                ImageLoader.getImageFromLocation(showImgAdd,fileImg);
                showImgDelete.setVisibility(View.VISIBLE);
                HashMap<String,File> showImgMap = new HashMap<>();
                showImgMap.put("file",fileImg);
                utilsInstance.startNetworkUploading(9, ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE,null,showImgMap);
                break;
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


    protected void requestNetData() {
        SearchGoodsHttpUtils.LongHttp(goodsid, "", "true", AiSouAppInfoModel.getInstance().getUID(),"", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
//                if (id==8) {
//                    System.out.println("ss");
//                }else{
                    result = GsonUtil.gsonToBean(response, GoodsDetailImageBean.class).getResult();
                    //对商品的展示图做添加判断
                    addShowImg();

                    //添加后显示图片
                    //showImg();

                    //设置轮播
//                  setPageIndicator();
                    String goods_img_txt1 = result.getGoods_img_txt();
                    goods_img_txt = goods_img_txt1;
                    mEditor.setHtml(goods_img_txt);
                    mEditor.requestFocus();
//                }
            }
        });

        SearchGoodsHttpUtils.LongHttp(goodsid,"", "", "","1",  new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d("goods_id:" + goodsid + response);
                //详情图
                GoodsDetailBean.ResultBean resultBean = GsonUtil.gsonToBean(response, GoodsDetailBean.class).getResult();
                etGoodsName.setText(resultBean.getGoods_name());
                etGoodsPrice.setText(resultBean.getGoods_price()+"");
                etGoodsLd.setText(resultBean.getGoods_introduce());
                // 设置默认商品类别
                String goods_type = resultBean.getGoods_type();
                for (int i = 0; i < categoryIDList.size() ; i++){
                    String typeStr = categoryIDList.get(i);
                    if (typeStr.equals(goods_type)) {
                        spinner.setSelection(i);
                        break;
                    }
                }
//                System.out.println(goodsTypeId);
                fileImgStr = resultBean.getGoods_img();
                ImageLoader.getImageFromNetwork(showImgAdd, BaseConfig.getCorrectImageUrl( fileImgStr));
                showImgDelete.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addShowImg() {
        goods_bander_img1 = result.getGoods_bander_img1();
        if (TextUtils.isEmpty(goods_bander_img1)) {
            return;
        } else {
            localImages.add(goods_bander_img1);
            final View inflate = View.inflate(EditGoodsActivity.this, net.twoant.master.R.layout.zy_publishgoods_entered_select_photo, null);
            ImageView imageView = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_add);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            params.setMargins(0,0,0,0);
            imageView.setLayoutParams(params);
            ImageView imgDelete = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_delete);
            imgDelete.setVisibility(View.VISIBLE);
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhotoContain.removeView(inflate);
                    localImages.remove(goods_bander_img1);
                }
            });
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            showPhotoContain.addView(inflate);
            ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(goods_bander_img1));
            goods_bander_img2 = result.getGoods_bander_img2();
            if (TextUtils.isEmpty(goods_bander_img2)) {
                return;
            } else {
                localImages.add(goods_bander_img2);
                final View inflate1 = View.inflate(EditGoodsActivity.this, net.twoant.master.R.layout.zy_publishgoods_entered_select_photo, null);
                ImageView imageView1 = (ImageView) inflate1.findViewById(net.twoant.master.R.id.iv_add);
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) imageView1.getLayoutParams();
                params1.setMargins(60,0,0,0);
                imageView1.setLayoutParams(params);
                ImageView imgDelete1 = (ImageView) inflate1.findViewById(net.twoant.master.R.id.iv_delete);
                imgDelete1.setVisibility(View.VISIBLE);
                imgDelete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPhotoContain.removeView(inflate1);
                        localImages.remove(goods_bander_img2);
                    }
                });
                imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                showPhotoContain.addView(inflate1);
                ImageLoader.getImageFromNetwork(imageView1,BaseConfig.getCorrectImageUrl(goods_bander_img2));
                goods_bander_img3 = result.getGoods_bander_img3();
                if (TextUtils.isEmpty(goods_bander_img3)) {
                    return;
                } else {
                    localImages.add(goods_bander_img3);
                    final View inflate2 = View.inflate(EditGoodsActivity.this, net.twoant.master.R.layout.zy_publishgoods_entered_select_photo, null);
                    ImageView imageView2 = (ImageView) inflate2.findViewById(net.twoant.master.R.id.iv_add);
                    FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) imageView2.getLayoutParams();
                    params2.setMargins(60,0,0,0);
                    imageView2.setLayoutParams(params);
                    ImageView imgDelete2 = (ImageView) inflate2.findViewById(net.twoant.master.R.id.iv_delete);
                    imgDelete2.setVisibility(View.VISIBLE);
                    imgDelete2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPhotoContain.removeView(inflate2);
                            localImages.remove(goods_bander_img3);
                        }
                    });
                    imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    showPhotoContain.addView(inflate2);
                    ImageLoader.getImageFromNetwork(imageView2,BaseConfig.getCorrectImageUrl( goods_bander_img3));
                    goods_bander_img4 = result.getGoods_bander_img4();
                    if (TextUtils.isEmpty(goods_bander_img4)) {
                        return;
                    } else {
                        localImages.add(goods_bander_img4);
                        final View inflate3 = View.inflate(EditGoodsActivity.this, net.twoant.master.R.layout.zy_publishgoods_entered_select_photo, null);
                        ImageView imageView3 = (ImageView) inflate3.findViewById(net.twoant.master.R.id.iv_add);
                        FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) imageView3.getLayoutParams();
                        params3.setMargins(60,0,0,0);
                        imageView3.setLayoutParams(params);
                        ImageView imgDelete3 = (ImageView) inflate3.findViewById(net.twoant.master.R.id.iv_delete);
                        imgDelete3.setVisibility(View.VISIBLE);
                        imgDelete3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPhotoContain.removeView(inflate3);
                                localImages.remove(goods_bander_img4);
                            }
                        });
                        imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        showPhotoContain.addView(inflate3);
                        ImageLoader.getImageFromNetwork(imageView3,BaseConfig.getCorrectImageUrl(goods_bander_img4));
                    }
                }
            }
        }
        oldCarouselSize = localImages.size();
    }

    private void showImg(){
        for (int i = 0 ; i < localImages.size(); i++){
//              final File file = new File(localImages.get(i).getPath());
//              filesShow.add(file);
            final View inflate = View.inflate(EditGoodsActivity.this, net.twoant.master.R.layout.zy_publishgoods_entered_select_photo, null);
            final ImageView imageView = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_add);
            if (i != 0) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                params.setMargins(60,0,0,0);
                imageView.setLayoutParams(params);
            }
            ImageView imgDelete = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_delete);
            imgDelete.setVisibility(View.VISIBLE);
            final int x = i;
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhotoContain.removeView(inflate);
                    localImages.remove(localImages.get(x));
                }
            });
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            showPhotoContain.addView(inflate);
            ImageLoader.getImageFromNetwork(imageView,BaseConfig.getCorrectImageUrl( localImages.get(i)),EditGoodsActivity.this);
        }
    }

}
