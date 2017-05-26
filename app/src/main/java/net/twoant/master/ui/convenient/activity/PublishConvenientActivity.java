package net.twoant.master.ui.convenient.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongLongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.convenient.db.ConvenientClassHelper;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Request;

public class PublishConvenientActivity extends LongLongBaseActivity implements View.OnClickListener, HttpConnectedUtils.IOnStartNetworkCallBack {

    private RichEditor mEditor;
    private SQLiteDatabase db;
    private HttpConnectedUtils utilsInstance;
    private static final int DB_CLASS = 0X56;
    private static final int PHOTO = 0X57;
    private static final int POST_CONVENIENT = 0X58;
    private Spinner sp1;
    private Spinner sp2;
    private Spinner sp3;
    private ConvenientClassHelper helper;
    private List<String> categoryList;
    private List<String> sp1titileList; //用于存放sp1中的数据
    private List<String> sp2titileList; //用于存放sp2中的数据
    private List<String> sp3titileList;
    private List<String> sp1IdList;//用于存放sp1中手机类型的id集合
    private List<String> sp2IdList;//用于存放sp2中手机类型的id集合
    private HintDialogUtil hintDialogUtil;

    private String goods_img_txt;//h5图文介绍
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean flag = false; //是否有网络的flag
    private PopupWindow popFont;
    private PopupWindow popColor;
    private ImageView ivFontSize, ivFontColor;
    private View viewFontSize;//popwindow字体大小的页面
    private View viewFontColor;//popwindow字体颜色的页面
    private List items;
    // 创建一个以当前系统时间为名称的文件，防止重复
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator + getPhotoFileName());
    public EditText etTitle;
    public int classId;
    public String base_Id;
    public String shopid;
    private LinearLayout shcouse_user;//个人或商家发布
    private boolean isShopPub = false;
    private String mTitle;

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_publish_convenient;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        shcouse_user = (LinearLayout) findViewById(R.id.edit_shcouse_user);
        getShopid();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AppCompatTextView titile = (AppCompatTextView) findViewById(R.id.tv_title_tool_bar);
        titile.setText("信息发布");
        mEditor = (RichEditor) findViewById(R.id.editor_publishgoods);
        sp1 = (Spinner) findViewById(R.id.sp_class1_convenient);
        sp2 = (Spinner) findViewById(R.id.sp_class2_convenient);
        sp3 = (Spinner) findViewById(R.id.sp_user_convenient);
        mEditor.setEditorHeight(CommonUtil.getDimens(R.dimen.px_120));
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("分享身边的新鲜事...");
        utilsInstance = HttpConnectedUtils.getInstance(this);
        findViewById(R.id.iv_blod_publishactivity).setOnClickListener(this);
        findViewById(R.id.iv_selectphoto_publishactivity).setOnClickListener(this);
        findViewById(R.id.btn_post_publishgoods).setOnClickListener(this);
        findViewById(R.id.iv_underline_publishactivity).setOnClickListener(this);
        ivFontColor = (ImageView) findViewById(R.id.iv_fontcolor_publishactivity);
        ivFontSize = (ImageView) findViewById(R.id.iv_size_publishactivity);
        etTitle = (EditText) findViewById(R.id.et_title_convenient);
        ivFontColor.setOnClickListener(this);
        ivFontSize.setOnClickListener(this);
        items = new ArrayList();
        items.add("拍照");
        items.add("从相册选择图片");
        hintDialogUtil = new HintDialogUtil(this);
        hintDialogUtil.showLoading(true, false);
        hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (flag) {
                    finish();
                }
            }
        });
        initData();
        initDatabase();
    }

    private void initDatabase() {
        ConvenientClassHelper helper = new ConvenientClassHelper(this, 1);
        db = helper.getWritableDatabase();
        utilsInstance.startNetworkGetString(DB_CLASS, map, ApiConstants.ALL_CONVENIENT_CLASS);
    }

    @Override
    protected void initData() {
        initFontPopupWindow();
        initColorPopupWindow();
        //活动详情h5标签详情
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                goods_img_txt = text;
                System.out.println(text);
            }
        });
    }

    protected void initDataBase() {
        categoryList = new ArrayList<>();
        sp1titileList = new ArrayList<>();
        sp2titileList = new ArrayList<>();
        sp3titileList = new ArrayList<>();
        sp3titileList.add("个人发布");
        sp3titileList.add("商家发布");
        sp1IdList = new ArrayList<>();
        sp2IdList = new ArrayList<>();
        helper = new ConvenientClassHelper(this, 1);
        selectSp1("0");
        selectSp3();
    }

    private void selectSp1(String categoryid) {
        db = helper.getReadableDatabase();
        sp1titileList.clear();
        sp1IdList.clear();
        Cursor cursor = db.rawQuery("select * from clazz where parent_id=" + categoryid, null);//查询一级列表
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            categoryList.add(title);
            String categoryId = cursor.getString(cursor.getColumnIndex("class_layer"));//把所有手机类型的id存入一个集合
            sp1IdList.add(categoryId);
        }
        cursor.close();
        db.close();
        //默认选中第一个
        selectSp2(sp1IdList.get(0));
        base_Id = sp1IdList.get(0);


        ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp1.setAdapter(arrayAdapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSp2(sp1IdList.get(position));
                base_Id = sp1IdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectSp2(String categoryid) {
        db = helper.getReadableDatabase();
        sp2IdList.clear();
        sp2titileList.clear();
        Cursor cursor = db.rawQuery("select * from clazz where parent_id=" + categoryid, null);//查询二级列表
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            sp2titileList.add(title);
            String sp2Id = cursor.getString(cursor.getColumnIndex("class_layer"));//把sp2中所有手机类型的id存入一个集合
            sp2IdList.add(sp2Id);
        }
        cursor.close();
        db.close();

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(PublishConvenientActivity.this, android.R.layout.simple_list_item_1, sp2titileList);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp2.setAdapter(arrayAdapter2);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(sp2IdList.get(position));
                classId = Integer.parseInt(sp2IdList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectSp3() {
        ArrayAdapter arrayAdapter3 = new ArrayAdapter(PublishConvenientActivity.this, android.R.layout.simple_list_item_1, sp3titileList);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp3.setAdapter(arrayAdapter3);
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) {
                    isShopPub = false;
                } else {
                    isShopPub = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_blod_publishactivity://加粗
                if (isBold) {
                    ToastUtil.showLong("取消粗体字");
                } else {
                    ToastUtil.showLong("粗体字");
                }
                isBold = !isBold;
                mEditor.setBold();
                break;
            case R.id.iv_underline_publishactivity://下划线
                if (isItalic) {
                    ToastUtil.showLong("取消下划线");
                } else {
                    ToastUtil.showLong("下划线");
                }
                isItalic = !isItalic;
                mEditor.setUnderline();
                break;
            case R.id.iv_size_publishactivity://字体大小
                if (popFont != null && popFont.isShowing()) {
                    popFont.dismiss();
                } else {
                    showFontPopupWindow(view);
                }
                break;
            case R.id.iv_fontcolor_publishactivity://字体颜色
                if (popColor != null && popColor.isShowing()) {
                    popColor.dismiss();
                } else {
                    showColorPopupWindow(view);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_selectphoto_publishactivity:
                selectPhoto();
                break;
            case R.id.btn_post_publishgoods:
                postInfo();
                break;
        }
    }

    private void getShopid() {
        map.clear();
        //判断是否是商家号，是则取出shopid
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.MYSHOPMANAGER, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("连接失败" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d("输出:" + response);
                try {
                    List<DataRow> mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                    if (mDataRow != null && mDataRow.size() > 0) {
                        shopid = mDataRow.get(0).getString("ID");
                        shcouse_user.setVisibility(View.VISIBLE);
                    } else {
                        shcouse_user.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void postInfo() {
        mTitle = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(mTitle)) {
            ToastUtil.showLong("标题为空");
            return;
        }
        if (TextUtils.isEmpty(goods_img_txt)) {
            ToastUtil.showLong("详情内容为空");
            return;
        }
        String stringuncler = EmojiUtil.getEmojiStrings(goods_img_txt);
        String emojiStrings = EmojiUtil.getEmojiStrings(mTitle);
        map.clear();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("des", stringuncler);
        map.put("title",emojiStrings);
        if (0 == classId) {
            map.put("sort", base_Id);
        } else {
            map.put("sort", classId + "");
        }
        Log.d("TAG", "onResponse:classId " + classId);
        if (!TextUtils.isEmpty(shopid) && isShopPub) {
            map.put("shop", shopid);
        }
        utilsInstance.startNetworkGetString(POST_CONVENIENT, map, ApiConstants.POST_CONVENIENT);

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
        System.out.println(response);
        hintDialogUtil.dismissDialog();
        switch (id) {
            case DB_CLASS:
                hintDialogUtil.dismissDialog();
                db.beginTransaction();//启动SQLite事务处理
                try {
                    //ClearDB();//先清除数据库数据再存
                    db.execSQL("DELETE FROM clazz");

                    List<DataRow> dataRowList = DataRow.parseJson(response).getSet("data").getRows();
                    for (int i = 0; i < dataRowList.size(); i++) {//拿到所有Json数据并存入数据库
                        String id_ = dataRowList.get(i).getString("ID");
                        String class_layer = dataRowList.get(i).getString("ID");
                        String parent_id = dataRowList.get(i).getString("BASE_ID");
                        if (TextUtils.isEmpty(parent_id) || "null".equals(parent_id)) {
                            parent_id = "0";
                        }
                        String title = dataRowList.get(i).getString("NM");
                        int sort_id = dataRowList.get(i).getInt("IDX");
                        Log.d("popop", "requestSuccess: " + class_layer + id_ + parent_id + title + sort_id);
                        db.execSQL("insert into clazz(class_layer,parent_id,sort_id,title) values(?,?,?,?)",
                                new Object[]{class_layer, parent_id, sort_id, title});
                    }
                    db.setTransactionSuccessful();//事务处理成功
                    db.endTransaction();//关闭事务处理
                    initDataBase();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (db != null) {
                        db.close();
                    }
                }
                break;
            case PHOTO:
                String msg = BaseConfig.getCorrectImageUrl(BaseConfig.getImageUrlForJson(response));

                if (!TextUtils.isEmpty(msg)) {
                    mEditor.insertImage(msg, "dachshund");
                }

                break;
            case POST_CONVENIENT:
                boolean result1 = DataRow.parseJson(response).getBoolean("result", false);
                if (result1) {
                    try {
                        View toastView = View.inflate(PublishConvenientActivity.this, R.layout.zy_mytoast, null);
                        TextView tv = (TextView) toastView.findViewById(R.id.tv_toast_text);
                        Toast toast = Toast.makeText(PublishConvenientActivity.this, "top", Toast.LENGTH_SHORT);
                        //此处只使用一个TextView，当然也可以使用更复杂的View
                        tv.setText("已发布");
                        tv.setPadding(30, 30, 30, 30);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        tv.setGravity(Gravity.CENTER);
                        toast.setView(toastView);
                        toast.show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        hintDialogUtil.showError("连接失败");
        flag = true;
    }

    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(PublishConvenientActivity.this, Gravity.BOTTOM, true);
        listViewDialog.setInitData(items, "取消");
        listViewDialog.setTextColor(R.color.principalTitleTextColor);
        listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                switch (position) {
                    case 0:
                        configCompress(200 * 1024, 1980, 1024, true);
                        startGetPhoto(true, 1, tempFile);
                        break;
                    case 1:
                        configCompress(200 * 1024, 1980, 1024, true);
                        startGetPhoto(false, 1, tempFile);
                        break;
                }
            }
        });
        listViewDialog.showDialog(true, true);
    }

    private void showFontPopupWindow(View v) {
        //获取控件的坐标
        int[] location = new int[2];
        //获取控件的坐标
        ivFontSize.getLocationOnScreen(location);
        int width = location[0];
        int height = location[1];
        popFont.setAnimationStyle(R.style.pop_invite_anim);
        popFont.showAtLocation(v, Gravity.NO_GRAVITY, width - CommonUtil.getDimens(R.dimen.px_60), (int) (height - CommonUtil.getViewHeight(viewFontSize, true) - getResources().getDimension(R.dimen.px_20)));
        popFont.update();
    }

    private void showColorPopupWindow(View v) {
        //获取控件的坐标
        int[] location = new int[2];
        //获取控件的坐标
        ivFontColor.getLocationOnScreen(location);
        int height = location[1];
        popColor.showAtLocation(v, Gravity.NO_GRAVITY, 300, (int) (height - CommonUtil.getViewHeight(viewFontColor, true) - getResources().getDimension(R.dimen.px_150)));
        popColor.update();
    }

    private void initFontPopupWindow() {
        // 初始化poPupwindow控件
        viewFontSize = this.getLayoutInflater().inflate(
                R.layout.zy_pop_fontsize, null);
        popFont = new PopupWindow(viewFontSize, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        viewFontSize.findViewById(R.id.tv_pop_fontbig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);//设置大号字体
                closeOptionPopupWindow();
                ToastUtil.showLong("大号字体");
            }
        });
        viewFontSize.findViewById(R.id.tv_pop_fontmiddle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);//设置中号字体
                closeOptionPopupWindow();
                ToastUtil.showLong("中号字体");
            }
        });
        viewFontSize.findViewById(R.id.tv_pop_fontsmall).setOnClickListener(new View.OnClickListener() {
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
                R.layout.zy_pop_fontcolor, null);
        popColor = new PopupWindow(viewFontColor, ViewGroup.LayoutParams.MATCH_PARENT,
                CommonUtil.getDimens(R.dimen.px_180), true);
        viewFontColor.findViewById(R.id.fontcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_1));
                ToastUtil.showLong("红");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_2));
                ToastUtil.showLong("橙");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_3));
                ToastUtil.showLong("紫");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_4));
                ToastUtil.showLong("黄");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_5));
                ToastUtil.showLong("绿");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_6));
                ToastUtil.showLong("淡绿");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_7));
                ToastUtil.showLong("蓝");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_8));
                ToastUtil.showLong("深蓝");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_9));
                ToastUtil.showLong("灰");
                popColor.dismiss();
            }
        });
        viewFontColor.findViewById(R.id.fontcolor10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setTextColor(CommonUtil.getColor(R.color.fontcolor_10));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void takeSuccess(TResult result) {
        hintDialogUtil.showLoading(R.string.dialogloading);
        TImage image2 = result.getImage();
        File file = new File(image2.getCompressPath());
        HashMap<String, File> m = new HashMap<>();
        m.put("file", file);
        long length = file.length();
        String s = Formatter.formatFileSize(getApplicationContext(), length);
        System.out.println(s);
        LogUtils.d(file.getAbsolutePath());
        utilsInstance.startNetworkUploading(PHOTO, ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE, null, m);
    }

    //获得的表情字符转换为十六进制
    public static int getIngeger(String s) {
        int a = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int i1 = c - 48;
            a += i1;
        }
        return a;
    }
    //是否是表情
    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0)
                || (codePoint == 0x9)
                || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    //把字符串格式化的代码
    public static String  stringuncler(String str) {
        int a = 0;
        int b = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isEmojiCharacter(c)) {
                a += c - 48;
                b += 1;
                if (b < 2) {
                    continue;
                }
            }
            if (a != 0) {
                String s2 = Integer.toHexString(a);
                BigInteger bigInteger = new BigInteger(s2, 16);
                stringBuffer.append("&#" + Integer.toHexString(bigInteger.intValue() + 16419));
                b = 0;
                a = 0;
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }
}
