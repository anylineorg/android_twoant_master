package net.twoant.master.ui.my_center.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.DialogUtil;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.bean.ClassifyListBean;
import net.twoant.master.ui.my_center.httputils.ClassifyHttpUtils;
import net.twoant.master.ui.other.bean.HttpResultBean;

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

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by DZY on 2016/11/18.
 */
public class PublishGoodsActivity_ extends LongBaseActivity implements View.OnClickListener,HttpConnectedUtils.IOnStartNetworkCallBack{
    private Spinner spinner;
    private ImageView ivGoodsImg;
    private List<String> categoryList;
    private LinearLayout showPhotoContain,detailPhotoContain;
    private List<ClassifyListBean.ResultBean> mDatasList;
    private EditText etGoodsName,etGoodsPrice,etGoodsCount,etGoodsIntroduce;

    private String[] items = {"拍照上传", "本地上传"};
    private String title = "选择照片";
    private static final int PHOTO_CARMERA = 1;
    private static final int PHOTO_PICK = 2;
    private static final int PHOTO_CUT = 3;
    private List<File> filesShow;  //店铺展示图集合
    private List<File> filesDetail;//商品详情图集合
    private File fileImg;           //商品列表图
    private int clickImageverPosition;//用于记录点击的是展示图片按钮还是详细图片按钮

    private HintDialogUtil hintDialogUtil;

    // 创建一个以当前系统时间为名称的文件，防止重复
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator +
            getPhotoFileName());
    private ArrayAdapter arrayAdapter;
    private String goodsTypeId;

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_publishgoods;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected void requestNetData() {
        ClassifyHttpUtils.LongHttp("0", "13", "", "", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                ClassifyListBean classifyListBean = GsonUtil.gsonToBean(response, ClassifyListBean.class);
                mDatasList = classifyListBean.getResult();
                for (ClassifyListBean.ResultBean resultBean: mDatasList){
                    categoryList.add(resultBean.getNM());
                }
                //设置spinner适配器
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        goodsTypeId = mDatasList.get(position).getCD()+"";
                        LogUtils.d(goodsTypeId);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void initView() {
        showPhotoContain = (LinearLayout) findViewById(R.id.ll_show_container_publish);
        detailPhotoContain = (LinearLayout) findViewById(R.id.ll_detail_container_publish);
        etGoodsName = (EditText) findViewById(R.id.edit_product_name);
        etGoodsPrice = (EditText) findViewById(R.id.edit_product_price);
        etGoodsCount = (EditText) findViewById(R.id.edit_product_count);
        findViewById(R.id.iv_product_add).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_Save).setOnClickListener(this);
        ivGoodsImg.setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);

        spinner = (Spinner) findViewById(R.id.sp_selectclass_publish);
        filesShow = new ArrayList<>();
        filesDetail = new ArrayList<>();
        categoryList = new ArrayList<>();
        //spinner 适配器
        arrayAdapter = new ArrayAdapter(PublishGoodsActivity_.this,android.R.layout.simple_list_item_1,categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_Save:
                hintDialogUtil.showLoading(R.string.get_posting);
                pulishGoods();
                break;
            case R.id.iv_product_add:
                if (filesShow.size() == 4) {
                    ToastUtil.showLong("已经添加最大限制");
                    return;
                }
                clickImageverPosition = 1;
                selectPhoto();
                break;
        }
    }

    private void pulishGoods() {
        HttpConnectedUtils utilsInstance = HttpConnectedUtils.getInstance(this);
        HashMap<String,String> m = new HashMap<>();
        m.put("goods_pk_shop","13");
        String goods_name = etGoodsName.getText().toString().trim();
        if (TextUtils.isEmpty(goods_name)) {
            ToastUtil.showLong("商品名不能为空");
            return;
        }
        m.put("goods_name",goods_name);
        m.put("goods_typeid",goodsTypeId);
        if (TextUtils.isEmpty(goodsTypeId)) {
            ToastUtil.showLong("请选择商品类型");
            return;
        }
        m.put("goods_title","商品标题");
        String goods_price = etGoodsPrice.getText().toString().trim();
        if (TextUtils.isEmpty(goods_price)) {
            ToastUtil.showLong("请输入商品价钱吧>.<");
            return;
        }
        m.put("goods_price",goods_price);
        String goods_count = etGoodsCount.getText().toString().trim();
        if (TextUtils.isEmpty(goods_count)) {
            ToastUtil.showLong("请输入商品数量吧>.<");
            return;
        }
        m.put("goods_count",goods_count);
        String goods_introduce = etGoodsIntroduce.getText().toString().trim();
        if (TextUtils.isEmpty(goods_count)) {
            ToastUtil.showLong("请把商品描述一下吧>.<");
            return;
        }
        m.put("goods_introduce",goods_introduce);
        Map<String, File> map = new HashMap<>();
       if (TextUtils.isEmpty(fileImg.getAbsolutePath())) {
           ToastUtil.showLong("请上传一张商品列表图吧>.<");
           return;
        }
        map.put("goods_img",fileImg);//列表图
        if (filesShow.size()==0) {
            ToastUtil.showLong("请上传一张店铺展示图吧>.<");
            return;
        }
        for (int i = 1; i <= filesShow.size(); i++) {
            map.put("goods_bander_img" + i,filesShow.get(i-1));//店铺展示图
        }
        if (filesDetail.size()==0) {
            ToastUtil.showLong("请上传一张店铺详细图吧>.<");
            return;
        }
        for (int i = 1; i <= filesDetail.size(); i++) {
            map.put("goods_bander_dt" + i,filesDetail.get(i-1));//店铺详细图
        }
        utilsInstance.startNetworkUploading(1, ApiConstants.GUO+"send_goods.do",m,map);
    }

    // 调用系统相机
    protected void startCamera(DialogInterface dialog) {
        dialog.dismiss();
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
    protected void startPick(DialogInterface dialog) {
        dialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICK);
    }


    private void selectPhoto() {
        AlertDialog.Builder dialog = DialogUtil.getListDialogBuilder(this, items, title, dialogListener);
        dialog.show();
    }
    private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    // 调用拍照
                    startCamera(dialog);
                    break;
                case 1:
                    // 调用相册
                    startPick(dialog);
                    break;
            }
        }
    };

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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY是裁剪图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        // 设置是否返回数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CUT);
    }

    // 将裁剪后的图片显示在ImageView上
    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (null != bundle) {
            final Bitmap bmp = bundle.getParcelable("data");
            //保存本地file
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            int quality = 100;
            OutputStream stream = null;
            try {
                String file = Environment.getExternalStorageDirectory() + File.separator + getPhotoFileName();
                stream = new FileOutputStream(file);
                bmp.compress(format, quality, stream);
                File dirFile = new File(file);
                if (clickImageverPosition == 1) {
                    //添到主图list集合
                    filesShow.add(dirFile);
                    for (File file1 :filesShow) {
                        LogUtils.d(file1.getAbsolutePath());
                    }
                }else if (clickImageverPosition == 2){
                    //添到详细list集合
                    filesDetail.add(dirFile);
                    for (File file1 :filesDetail) {
                        LogUtils.d(file1.getAbsolutePath());
                    }
                }else if (clickImageverPosition == 3) {
                    LogUtils.d(dirFile.getAbsolutePath());
                    fileImg = dirFile;
                    LogUtils.d(fileImg.getAbsolutePath());
                    ivGoodsImg.setImageBitmap(bmp);
                    return;//就不再往集合中添加了
                }
                //设置圆形图片
                // Bitmap bitmap = MakeRoundCorner.makeRoundCorner(bmp);
                addPhoto(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPhoto(Bitmap bitmap) {
        int x = (int) getResources().getDimension(R.dimen.px_120);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(x, x);
        final ImageView imageView = new ImageView(PublishGoodsActivity_.this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickImageverPosition==1) {
                    showPhotoContain.removeView(imageView);
                } else if (clickImageverPosition == 2) {
                    detailPhotoContain.removeView(imageView);
                }
            }
        });
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (clickImageverPosition == 1) {
            if (null != filesShow) {//第一个没有margin值
                if (filesShow.size() == 1) {
                    params.leftMargin=0;
                }else{
                    params.leftMargin = x/7;
                }
            }
        }else if (clickImageverPosition ==2) {
            if (null != filesDetail) {//第一个没有margin值
                if (filesShow.size() == 1) {
                    params.leftMargin=0;
                }else{
                    params.leftMargin = x/7;
                }
            }
        }
        imageView.setLayoutParams(params);
        if (clickImageverPosition==1) {
            showPhotoContain.addView(imageView);
        }else if(clickImageverPosition==2){
            detailPhotoContain.addView(imageView);
        }
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
        if (null!=filesDetail && filesDetail.size()>0) {
            for (File file : filesDetail){
                file.delete();
                LogUtils.d("已删除文件："+file);
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
        HttpResultBean httpResultBean = GsonUtil.gsonToBean(response, HttpResultBean.class);
        String msg = httpResultBean.getResult().getMsg();
        ToastUtil.showLong(msg);
        //清空页面数据
        etGoodsName.setText("");
        etGoodsPrice.setText("");
        etGoodsCount.setText("");
        hintDialogUtil.dismissDialog();
        etGoodsIntroduce.setText("");
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
        hintDialogUtil.showError("请求错误");
    }
}
