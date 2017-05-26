package net.twoant.master.ui.my_center.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongLongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;
import net.twoant.master.widget.timer.TimePickerViewBase;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

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
import java.util.Set;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by DZY on 2016/11/21.
 */
public class EditDataActivity extends LongLongBaseActivity implements View.OnClickListener,HttpConnectedUtils.IOnStartNetworkCallBack {

    private ImageView iv_photo_center, iv_back;
    private Button button;
    private TextView tv_sex;
    private TextView et_nickName, et_age, et_sign;
    private RelativeLayout rlPhoto, rlSex,rlAge;

    //    private String[] items = {"拍照上传", "本地上传"};
    private List items;
    private static final int PHOTO_CARMERA = 1;
    private static final int PHOTO_PICK = 2;
    private static final int PHOTO_CUT = 3;
    private static final int EDIT_NAME = 4;
    private static final int EDIT_SIGN = 5;

    private String brithday="";
    private Map<String, File> map = new HashMap<>();;
    private final ArrayList<String> fImageUrls = new ArrayList<>();

    // 创建一个以当前系统时间为名称的文件，防止重复
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator +
            getPhotoFileName());
    private File dirFile;
    private AlertDialog alertDialog;
    private HintDialogUtil hintDialogUtil;
    TimePickerViewBase pvTime;
    private boolean noNet = false;
    private HttpConnectedUtils utilsInstance;
    private String firstNickName,secondNickName;
    private int firstSex,secondSex;
    private int firstAge,secondAge;
    private String firstAutograph,secondAutograph;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, EditDataActivity.class);
        context.startActivity(intent);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_editdata;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        requestNetData();
    }

    protected void requestNetData() {
        hintDialogUtil.showLoading("获取个人信息中",true,false);
        HashMap<String,String> m = new HashMap<>();
        m.put("_t", AiSouAppInfoModel.getInstance().getToken());

        LongHttp(ApiConstants.USER_INFO,"", m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("网络连接失败"+e);
                noNet = true;
                hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (noNet) {
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                hintDialogUtil.dismissDialog();

                DataRow row=DataRow.parseJson(response);
                row=row.getRow("data");
//                Gson gson = new Gson();
//                UserDataBean userBean = gson.fromJson(response,UserDataBean.class);
//                UserDataBean.ResultBean result = userBean.getResult();
                firstNickName = row.getString("NICK_NAME");//result.getNick_name();
                secondNickName = firstNickName ;
                firstSex = row.getInt("SEX");//result.getSex();
                secondSex = firstSex;
                firstAge = row.getInt("AGE");//result.getAge();
                secondAge = firstAge;
                firstAutograph = row.getString("SIGN");//result.getAutograph();
                secondAutograph = firstAutograph;
                et_nickName.setText(firstNickName);
                tv_sex.setText(firstSex ==1?"男":"女");
                et_age.setText(firstAge +"");
                et_sign.setText(firstAutograph);
                final String photoUrl = row.getString("IMG_FILE_PATH");//ApiConstants.GUO+result.getAvatar();
                ImageLoader.getImageFromNetwork(iv_photo_center,photoUrl);
             /*   iv_photo_center.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        *//**
                 * 打开图片查看器
                 *//*
                        fImageUrls.clear();
                        fImageUrls.add(photoUrl);
                        Intent intent = new Intent(EditDataActivity.this, ImagePagerActivity.class);
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,fImageUrls);
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX,0);
                        startActivity(intent);
                    }
                });*/
            }
        });
    }

    private void initView() {
        et_nickName = (TextView) findViewById(net.twoant.master.R.id.et_nickname_edit);
        tv_sex = (TextView) findViewById(net.twoant.master.R.id.tv_sex_edit);
        et_age = (TextView) findViewById(net.twoant.master.R.id.et_age_edit);
        et_sign = (TextView) findViewById(net.twoant.master.R.id.et_sign_edit);
        iv_photo_center = (ImageView) findViewById(net.twoant.master.R.id.iv_photo_center);
        button = (Button) findViewById(net.twoant.master.R.id.btn_register);
        iv_back = (ImageView) findViewById(net.twoant.master.R.id.iv_back);
        rlPhoto = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_photo_edit);
        rlSex = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_sex_edit);
        rlAge = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_age_edit);
        rlAge.setOnClickListener(this);
//        iv_photo_center.setOnClickListener(this);
        button.setOnClickListener(this);
        rlPhoto.setOnClickListener(this);
        rlSex.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
        hintDialogUtil.getDialog();
        utilsInstance = HttpConnectedUtils.getInstance(this);
        findViewById(net.twoant.master.R.id.rl_nickname_edit).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_sign_editdetail).setOnClickListener(this);
        // 时间选择器
        pvTime = new TimePickerViewBase(this, TimePickerViewBase.Type.ALL);
        pvTime.setRange(1900,2017);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        items = new ArrayList();
        items.add("拍照");
        items.add("从相册选择图片");
        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerViewBase.OnTimeSelectListener(){
            @Override
            public void onTimeSelect(Date date){
                brithday=getTime(date);
                String[] split = getTime(date).split("-");
                int year = Integer.parseInt(split[0]);
                String month = split[1];//获取月份
                String day = split[2];  //获取天
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String[] currentS = getTime(curDate).split("-");
                int currentYear = Integer.parseInt(currentS[0]);
                if (currentYear < year) {
                    ToastUtil.showLong("伙计你穿越了嘛");
                    return;
                }
                secondAge = currentYear-year;
                et_age.setText(secondAge+"");
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case net.twoant.master.R.id.rl_nickname_edit:
                Intent intentName = new Intent(EditDataActivity.this,EditDataNameActivity.class);
                intentName.putExtra("name",et_nickName.getText().toString().trim());
                startActivityForResult(intentName,EDIT_NAME);
                break;
//            case R.id.iv_photo_center:
//                selectPhoto();
//                break;
            case net.twoant.master.R.id.btn_register:
                System.out.println();
                if (TextUtils.isEmpty(firstAutograph) || TextUtils.isEmpty(firstNickName)) {
                    postData();
                    return;
                }
                if (null==dirFile && firstNickName.equals(secondNickName) && firstSex==secondSex && firstAge==secondAge && firstAutograph.equals(secondAutograph)) {
                    finish();
                }else{
                    postData();
                }
                break;

            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.rl_photo_edit:
                selectPhoto();
                break;
            case net.twoant.master.R.id.rl_sex_edit:
                selectSex();
                break;
            case net.twoant.master.R.id.rl_age_edit:
                pvTime.show();
                break;
            case net.twoant.master.R.id.rl_sign_editdetail:
                Intent intentSign = new Intent(EditDataActivity.this,EditDataSignActivity.class);
                String trim = et_sign.getText().toString().trim();
                System.out.println(trim);
                intentSign.putExtra("sign",secondAutograph);
                startActivityForResult(intentSign,EDIT_SIGN);
                break;
        }
    }

    private void selectSex() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            View view = View.inflate(this, net.twoant.master.R.layout.zy_dialog_sex,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(net.twoant.master.R.id.rg_group);
            final RadioButton rbFemal = (RadioButton) view.findViewById(net.twoant.master.R.id.rb_female);
            final RadioButton rbMale = (RadioButton) view.findViewById(net.twoant.master.R.id.rb_male);
            if (tv_sex.getText().toString().trim().equals("男")) {
                rbMale.setChecked(true);
            }else{
                rbFemal.setChecked(true);
            }
            rbMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rbMale.isChecked()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SystemClock.sleep(100);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_sex.setText("男");
                                        secondSex = 0;
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        }).start();
                    }
                }
            });
            rbFemal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(100);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_sex.setText("女");
                                    secondSex = 1;
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    }).start();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }else{
            String[] strings = new String[] { "男", "女" };
            AlertDialog.Builder builder = new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle);
            builder.setTitle("性别");
            String sexStr = tv_sex.getText().toString().trim();
            builder.setSingleChoiceItems(strings,"男".endsWith(sexStr)?0:1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        tv_sex.setText("男");
                    } else if (i == 1) {
                        tv_sex.setText("女");
                    }
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }
    Map<String,String> m = new HashMap<>();
    private void postData() {
        hintDialogUtil.showLoading(net.twoant.master.R.string.get_posting);

        m.put("_t",AiSouAppInfoModel.getInstance().getToken());
        if (TextUtils.isEmpty(secondNickName)) {
            secondNickName ="";
        }
        m.put("nick_name",secondNickName);
        if (TextUtils.isEmpty(secondAutograph)) {
            secondAutograph ="";
        }//SCHOOL
        m.put("sign",secondAutograph);
        if (TextUtils.isEmpty(secondAutograph)) {
            secondAutograph = "";
        }
        m.put("sex",tv_sex.getText().toString().endsWith("男")?0+"":1+"");
        if (!brithday.equals("") && brithday!="") {
            m.put("birthday", brithday);
        }
        utilsInstance.startNetworkUploading(1, ApiConstants.UPDATE_USER,m,map);//ApiConstants.GUO+"update_user.do",m,map);

    }
    public void startNetworkUploading(int id, String url, Map<String, String> parameter, Map<String, File> files) {

        PostFormBuilder builder = OkHttpUtils.post().url(url);
        if (files != null) {
            Set<String> strings = files.keySet();
            for (String string : strings) {
                File file = files.get(string);
                builder.addFile(string, getName(file.getName()), file);
            }
        }
        if (parameter==null){
            parameter=new HashMap<>();
        }
        if (parameter != null) {
            parameter.put("_t", AiSouAppInfoModel.getInstance().getToken());
            parameter.put("_tk", "zxedr68tf7gy8ouijnkl");
            parameter.put("width", "100");
            builder.params(parameter);
        }
        builder.tag(String.valueOf(id)).id(id).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                //  ToastUtil.showLong(response);
                DataRow data_row=DataRow.parseJson(response);
                if (data_row.getBoolean("RESULT",false)){
                    data_row=data_row.getRow("DATA");
                    m.put("img_file_path",data_row.getString("URL"));

                }else{

                    ToastUtil.showLong("上传失败");
                }
            }
        });
    }

    private String getName(String name) {
        if (name != null && !name.contains(".")) {
            name += ".jpg";
        }
        return name;
    }
    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(EditDataActivity.this, Gravity.BOTTOM, true);
        listViewDialog.setInitData(items,"取消");
        listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
        listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                switch (position) {
                    case 0:
                        // 调用拍照
                        // startGetPhoto(true,1,tempFile,640,280,true);tempFile,640,374,true);
                        configCompress(50*1024,500);
                        startGetPhoto(true,1,tempFile,1024,1024,true);
                        break;
                    case 1:
                        // 调用相册
                        configCompress(50*1024,500);
                        startGetPhoto(false,1,tempFile,1024,1024,true);
                        break;
                }
            }
        });
        listViewDialog.showDialog(true,true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_CARMERA:
                startPhotoZoom(Uri.fromFile(tempFile), 500);
                break;
            case PHOTO_PICK:
                if (null != data) {
                    startPhotoZoom(data.getData(), 500);
                }
                break;
            case PHOTO_CUT:
                if (null != data) {
                    setPicToView(data);
                }
                break;
            case EDIT_NAME:
                if (null != data) {
                    secondNickName = data.getStringExtra("name");
                    if (!TextUtils.isEmpty(secondAutograph)) {
                        et_nickName.setText(secondNickName);
                    }
                }
                break;
            case EDIT_SIGN:
                if (null != data) {
                    secondAutograph = data.getStringExtra("sign");
                    if (!TextUtils.isEmpty(secondAutograph)) {
                        et_sign.setText(secondAutograph);
                    }else{
                        et_sign.setText("");
                    }
                }
                break;
        }
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
            //切圆
            //Bitmap bitmap = MakeRoundCorner.makeRoundCorner(bmp);
            iv_photo_center.setImageBitmap(bmp);
            //btn_head.setClickable(true);
            //btn_head.setBackground(getResources().getDrawable(R.drawable.round_checkbox));
            //saveCropPic(bmp);
            //保存本地file
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            int quality = 100;
            OutputStream stream = null;
            try {
                String file = Environment.getExternalStorageDirectory() + File.separator + getPhotoFileName();
                stream = new FileOutputStream(file);
                bmp.compress(format, quality, stream);
                dirFile = new File(file);
                //开始上传文件
                map.put("img_file_path", dirFile);
                startNetworkUploading(1, ApiConstants.UPLOAD_FILE,null,map);//ApiConstants.GUO+"update_user.do",m,map);



                //添加list集合
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBefore(Request request, int id) {

    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return new HintDialogUtil(EditDataActivity.this);
    }

    @Override
    public void onResponse(String response, int id) {
        hintDialogUtil.dismissDialog();
        if (response.contains("success")) {
            ToastUtil.showLong("修改完成");
            finish();
            //发送广播
            Intent intent = new Intent();
            intent.putExtra("key", "数据数据");
            intent.setAction("gengxin");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        hintDialogUtil.dismissDialog();
        LogUtils.d(e.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=dirFile) {
            if (dirFile.exists()) { // 判断文件是否存在n
                dirFile.delete();
            }
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    public void takeSuccess(TResult result) {
        TImage image = result.getImage();
        dirFile = new File(image.getCompressPath());
        if (null!=map) {
            map.put("file", dirFile);
            ImageLoader.getImageFromLocation(iv_photo_center,dirFile);
            startNetworkUploading(1, ApiConstants.UPLOAD_FILE,null,map);//ApiConstants.GUO+"update_user.do",m,map);

        }
    }
}
