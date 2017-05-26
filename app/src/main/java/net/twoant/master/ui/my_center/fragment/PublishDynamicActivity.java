package net.twoant.master.ui.my_center.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongLongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.emoji.Emoji;
import net.twoant.master.widget.emoji.EmojiIndicatorView;
import net.twoant.master.widget.emoji.FaceFragment;
import net.twoant.master.widget.emoji.RecentEmojiManager;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class PublishDynamicActivity extends LongLongBaseActivity implements View.OnClickListener,HttpConnectedUtils.IOnStartNetworkCallBack,FaceFragment.OnEmojiClickListener{
    private final static int UPDATE_IMAGE_DYNAMIC = 0x156;
    private final static int POST_DYNAMIC = 0x158;
    private List items;
    // 创建一个以当前系统时间为名称的文件，防止重复
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator + getPhotoFileName());
    private HttpConnectedUtils utilsInstance;
    private ArrayList<File> fileList;
    private ArrayList<String> fileListStr;
    private ArrayList<String> photoUrlList;
    private GridView gridView;
    private MyAdapter adapter;
    public EditText etContentDynamic;
    private Map<String,File> fileMap;
    private IdentityHashMap<String,String> photoUrlMap;
    private HintDialogUtil hintDialogUtil;
    private ImageView ivEmoji;
    private BottomSheetDialog dialog;

    private ArrayList<Emoji> emojiList;
    private int columns = 7;
    private int rows = 3;
    private FaceFragment.OnEmojiClickListener listener;
    private RecentEmojiManager recentManager;
    ArrayList<Emoji> recentlyEmojiList;
    public String shop_id;
    public boolean hasNet = false;
    private int imgPaddValue;
    private int imgPaddValueLeft;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_publish_dynamic;
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(net.twoant.master.R.id.tv_Title);
        findViewById(net.twoant.master.R.id.iv_emoji_publishdynamic).setOnClickListener(this);
        etContentDynamic = (EditText) findViewById(net.twoant.master.R.id.et_content_dynamic);
        tvTitle.setText("发动态");
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setSelection(fileList.size());
            }
        });
        TextView tvPost = (TextView) findViewById(net.twoant.master.R.id.tv_Save);
        tvPost.setVisibility(View.VISIBLE);
        tvPost.setText("发表");
        tvPost.setOnClickListener(this);
        gridView = (GridView) findViewById(net.twoant.master.R.id.gv_publis_dynamic);
        initData();
    }

    protected void initData() {
        hintDialogUtil = new HintDialogUtil(this);
        adapter = new MyAdapter();
        fileMap = new HashMap<>();
        photoUrlMap = new IdentityHashMap<>();
        items = new ArrayList();
        items.add("相机");
        items.add("图库");
        fileList = new ArrayList<>();
        fileListStr = new ArrayList<>();
        photoUrlList = new ArrayList<>();
        utilsInstance = HttpConnectedUtils.getInstance(this);
        gridView.setAdapter(adapter);

        emojiList = EmojiUtil.getEmojiList();
        dialog = new BottomSheetDialog(this);
//        MainActivity.closeIME(true,etContentDynamic);
        imgPaddValue = CommonUtil.getDimens(net.twoant.master.R.dimen.px_40);
        imgPaddValueLeft = CommonUtil.getDimens(net.twoant.master.R.dimen.px_30);
      //  requestNetForShop(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.tv_Save:
                map.clear();
                if (TextUtils.isEmpty(etContentDynamic.getText().toString().trim())) {
                    ToastUtil.showLong("内容为空");
                    return;
                }
//                if (hasNet) {
                    if (fileList.size() == 0) {
                        photoUrlMap.put("des",etContentDynamic.getText().toString().trim());
                        photoUrlMap.put("center", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()
                                +","+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude() );
                        utilsInstance.startNetworkGetString(POST_DYNAMIC,photoUrlMap, ApiConstants.POST_DYNAMIC);
                    }else {
                        for (File file:fileList){
                             transformUrl(file);
                        }
                    }
//                }else{
//                    requestNetForShop(true);
//                }

                break;
            case net.twoant.master.R.id.iv_emoji_publishdynamic:
                hideKeyBoard();
                showBottomSheet();
                break;
        }
    }

    private void transformUrl(File file) {
        hintDialogUtil.showLoading();
        fileMap.put("file",file);
        utilsInstance.startNetworkUploading(UPDATE_IMAGE_DYNAMIC, ApiConstants.UPLOAD_FILE, "file", file);
    }

    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(PublishDynamicActivity.this, Gravity.BOTTOM, true);
        listViewDialog.setInitData(items,"取消");
        listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
        listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                switch (position) {
                    case 0:
                        // 调用拍照
                        configCompress(200*1024,1980,1900,true);
                        startGetPhoto(true,1,tempFile);
                        break;
                    case 1:
                        // 调用相册
                        configCompress(200*1024,1980,1900,true);
                        int size = fileList.size();
                        startGetPhoto(false,9-size,tempFile);
                        break;
                }
            }
        });
        listViewDialog.showDialog(true,true);
    }

    @Override
    public void takeSuccess(TResult result) {
        ArrayList<TImage> imageArrayList1 = result.getImages();
        for (int i = 0 ; i < imageArrayList1.size(); i++){
            File file = new File(imageArrayList1.get(i).getCompressPath());
            fileList.add(file);
        }
        adapter.notifyDataSetChanged();
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
        hintDialogUtil.dismissDialog();

        System.out.println(response);
        switch(id){
            case POST_DYNAMIC:
                boolean result = DataRow.parseJson(response).getBoolean("result", false);
                if (result) {
                    setResult(999);
                    finish();
                    ToastUtil.showLong("已发布");
                }
                break;
            case UPDATE_IMAGE_DYNAMIC:
                String file_path = BaseConfig.getImageUrlForJson(response);
                if (!TextUtils.isEmpty(file_path)) {
                    photoUrlList.add(file_path);
                    photoUrlMap.put(new String("img"),file_path);
                    int size = fileList.size();
                    int size1 = photoUrlList.size();
                    System.out.println(size + size1 + "");
                    if (fileList.size() == photoUrlList.size()) {
                        photoUrlMap.put("des",etContentDynamic.getText().toString().trim());
                        photoUrlMap.put("center", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()
                                +","+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude() );
//                        if (!TextUtils.isEmpty(shop_id)) {
//                            photoUrlMap.put("shop",shop_id);
//                        }
                        utilsInstance.startNetworkGetString(POST_DYNAMIC,photoUrlMap,ApiConstants.POST_DYNAMIC);
                    }
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.showLong(e.getMessage());
    }

    @Override
    public void onEmojiDelete() {
        String text = etContentDynamic.getText().toString();
        int index = 0;
        if (text.isEmpty()) {
            return;
        }
        int index1 = etContentDynamic.getSelectionStart();
//        System.out.println(index1+"");
        try {
            if ("]".equals(text.substring(index1 - 1,index1 ) ) ){
                if (index1 == -1) {
                    int action = KeyEvent.ACTION_DOWN;
                    int code = KeyEvent.KEYCODE_DEL;
                    KeyEvent event = new KeyEvent(action, code);
                    etContentDynamic.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                    displayTextView(index);
                    return;
                }
                etContentDynamic.getText().delete(index1-4, index1);
                System.out.println(index1+"");
                displayTextView(index1-8);
                return;
            }else {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etContentDynamic.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextView(index1-5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {

        int index = etContentDynamic.getSelectionStart();
        if (emoji != null) {
            Editable editable = etContentDynamic.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView(index);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (fileList.size() == 9) {
                return fileList.size();
            }else {
                return fileList.size()+1;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View inflate = View.inflate(PublishDynamicActivity.this, net.twoant.master.R.layout.zy_item_gridview_img, null);
            ImageView imageView = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_add);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PublishDynamicActivity.this, ImageScaleActivity.class);
                    fileListStr.clear();
                    for (File file : fileList){
                        fileListStr.add(file.getAbsolutePath());
                    }
                    intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS,fileListStr);
                    intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX,position);
                    startActivity(intent);
                }
            });
            ImageView imgDelete = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_delete);
            imgDelete.setVisibility(View.VISIBLE);
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            if (position == fileList.size()) {
                imageView.setImageResource(net.twoant.master.R.drawable.em_roominfo_add_btn_normal);
                imageView.setPadding(imgPaddValueLeft,imgPaddValue,imgPaddValue,imgPaddValue);
                imgDelete.setVisibility(View.INVISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectPhoto();
                        hideKeyBoard();
                    }
                });
            }else{
                if (fileList.size() > 0) {
                    ImageLoader.getImageFromLocation(imageView,fileList.get(position));
                }
            }
            return inflate;
        }
    }

    ViewPager faceViewPager;
    EmojiIndicatorView faceIndicator;
    TextView faceRecentTv;
    TextView faceFirstSetTv;
    ArrayList<View> ViewPagerItems = new ArrayList<>();

    private void showBottomSheet() {

        if (this instanceof FaceFragment.OnEmojiClickListener) {
            this.listener = this;
        }

        if (null == recentManager) {
            recentManager = RecentEmojiManager.make(this);
        }
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.dimAmount = 0f;
//        window.setAttributes(params);
        View view = LayoutInflater.from(this).inflate(net.twoant.master.R.layout.fragment_face, null);
        faceViewPager = (ViewPager) view.findViewById(net.twoant.master.R.id.face_viewPager);
        faceIndicator = (EmojiIndicatorView) view.findViewById(net.twoant.master.R.id.face_indicator);
        faceRecentTv = (TextView) view.findViewById(net.twoant.master.R.id.face_recent);
        faceFirstSetTv = (TextView) view.findViewById(net.twoant.master.R.id.face_first_set);
        initViews();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //弹出软键盘
//                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.showSoftInput(etContentDynamic, 0);
            }
        });
        dialog.setContentView(view);
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0f;
        dialog.getWindow().setAttributes(attributes);
        dialog.show();

//        FaceFragment faceFragment = FaceFragment.Instance();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container,faceFragment).commit();
    }

    private void displayTextView(int count) {
        try {
            EmojiUtil.handlerEmojiText(etContentDynamic, etContentDynamic.getText().toString(), this,CommonUtil.getDimens(net.twoant.master.R.dimen.px_40), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40));
            etContentDynamic.setSelection(count+4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void initViews() {
        initViewPager(emojiList);
        faceFirstSetTv.setSelected(true);
        faceFirstSetTv.setOnClickListener(this);
        faceRecentTv.setOnClickListener(this);

        try {
            if (recentManager.getCollection(RecentEmojiManager.PREFERENCE_NAME) != null) {
                recentlyEmojiList = (ArrayList<Emoji>) recentManager.getCollection(RecentEmojiManager.PREFERENCE_NAME);
            } else {
                recentlyEmojiList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void initViewPager(ArrayList<Emoji> list) {
        intiIndicator(list);
        ViewPagerItems.clear();
        for (int i = 0; i < getPagerCount(list); i++) {
            ViewPagerItems.add(getViewPagerItem(i, list));
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(ViewPagerItems);
        faceViewPager.setAdapter(mVpAdapter);
        faceViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                faceIndicator.playBy(oldPosition, position);
                oldPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void intiIndicator(ArrayList<Emoji> list) {
        faceIndicator.init(getPagerCount(list));
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount(ArrayList<Emoji> list) {
        int count = list.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }
    private View getViewPagerItem(int position, ArrayList<Emoji> list) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(net.twoant.master.R.layout.layout_face_grid, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(net.twoant.master.R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        final List<Emoji> subList = new ArrayList<>();
        subList.addAll(list.subList(position * (columns * rows - 1),
                (columns * rows - 1) * (position + 1) > list
                        .size() ? list.size() : (columns
                        * rows - 1)
                        * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        if (subList.size() < (columns * rows - 1)) {
            for (int i = subList.size(); i < (columns * rows - 1); i++) {
                subList.add(null);
            }
        }
        Emoji deleteEmoji = new Emoji();
        deleteEmoji.setImageUri(net.twoant.master.R.drawable.face_delete);
        subList.add(deleteEmoji);
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList,this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == columns * rows - 1) {
                    if(listener != null){
                        listener.onEmojiDelete();
                    }
                    return;
                }
                if(listener != null){
                    listener.onEmojiClick(subList.get(position));
                }
                insertToRecentList(subList.get(position));
            }
        });
        return gridview;
    }

    class FaceVPAdapter extends PagerAdapter {
        // 界面列表
        private List<View> views;

        public FaceVPAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) (arg2));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        // 初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1));
            return views.get(arg1);
        }

        // 判断是否由对象生成界
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }
    }

    class FaceGVAdapter extends BaseAdapter {
        private List<Emoji> list;
        private Context mContext;

        public FaceGVAdapter(List<Emoji> list, Context mContext) {
            super();
            this.list = list;
            this.mContext = mContext;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            FaceGVAdapter.ViewHolder holder;
            if (convertView == null) {
                holder = new FaceGVAdapter.ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.item_face, null);
                holder.iv = (ImageView) convertView.findViewById(net.twoant.master.R.id.face_image);
                convertView.setTag(holder);
            } else {
                holder = (FaceGVAdapter.ViewHolder) convertView.getTag();
            }
            if (list.get(position) != null) {
                holder.iv.setImageBitmap(EmojiUtil.decodeSampledBitmapFromResource(getResources(), list.get(position).getImageUri(),
                        EmojiUtil.dip2px(PublishDynamicActivity.this, 32), EmojiUtil.dip2px(PublishDynamicActivity.this, 32)));
            }
            return convertView;
        }

        class ViewHolder {
            ImageView iv;
        }
    }

    private void insertToRecentList(Emoji emoji) {
        if (emoji != null) {
            if (recentlyEmojiList.contains(emoji)) {
                //如果已经有该表情，就把该表情放到第一个位置
                int index = recentlyEmojiList.indexOf(emoji);
                Emoji emoji0 = recentlyEmojiList.get(0);
                recentlyEmojiList.set(index, emoji0);
                recentlyEmojiList.set(0, emoji);
                return;
            }
            if (recentlyEmojiList.size() == (rows * columns - 1)) {
                //去掉最后一个
                recentlyEmojiList.remove(rows * columns - 2);
            }
            recentlyEmojiList.add(0, emoji);
        }
    }

    /**
     *@param commentNet 是否进行网络提交
     * */
    public void requestNetForShop(final boolean commentNet) {


        if (fileList.size() == 0) {
            photoUrlMap.put("des",etContentDynamic.getText().toString().trim());
            photoUrlMap.put("center", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()+","+ AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude() );
            utilsInstance.startNetworkGetString(POST_DYNAMIC,photoUrlMap,ApiConstants.POST_DYNAMIC);
        }else {
            for (File file:fileList){
                transformUrl(file);
            }
        }
      /*  LongHttp(ApiConstants.MYSHOPMANAGER,"", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (commentNet) {
                    ToastUtil.showLong("网络连接失败");
                }
            }

            @Override
            public void onResponse(String response, int id) {
                DataRow dataRow = DataRow.parseJson(response);
                if (null != dataRow) {
                    boolean result = dataRow.getBoolean("result", false);
                    if (result) {
                        hasNet = true;
                        List<DataRow> dataRowList = DataRow.parseJson(response).getSet("data").getRows();
                        if (null != dataRowList && dataRowList.size() > 0) {
                            DataRow item_datarow = dataRowList.get(0);
                            shop_id = item_datarow.getString("ID");
                        }
                        //获取shopid 进行网络提交..
                        if (hasNet && commentNet) {
                            if (fileList.size() == 0) {
                                photoUrlMap.put("des",etContentDynamic.getText().toString().trim());
                                photoUrlMap.put("center", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()+","+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude() );
                                utilsInstance.startNetworkGetString(POST_DYNAMIC,photoUrlMap,ApiConstants.POST_DYNAMIC);
                            }else {
                                for (File file:fileList){
                                    transformUrl(file);
                                }
                            }
                        }
                    }else{
                        ToastUtil.showLong(dataRow.getString("message"));
                    }
                }
            }

        });
    }*/
    }
}
