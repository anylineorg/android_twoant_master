package net.twoant.master.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.widget.emoji.Emoji;
import net.twoant.master.widget.emoji.EmojiIndicatorView;
import net.twoant.master.widget.emoji.FaceFragment;
import net.twoant.master.widget.emoji.RecentEmojiManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by DZY on 2017/2/22.
 * 佛祖保佑   永无BUG
 */

public class CommentDialog extends BaseDialog implements FaceFragment.OnEmojiClickListener {

    private TextView tvComment;
    private BackEditText etText;

    ViewPager faceViewPager;
    EmojiIndicatorView faceIndicator;
    TextView faceRecentTv;
    TextView faceFirstSetTv;
    ArrayList<View> ViewPagerItems = new ArrayList<>();

    private ArrayList<Emoji> emojiList;
    private int columns = 7;
    private int rows = 3;
    private FaceFragment.OnEmojiClickListener listener;
    private RecentEmojiManager recentManager;
    ArrayList<Emoji> recentlyEmojiList;
    Activity activityCotext;
    public final FrameLayout fl_emoji;
    public final ImageView ivEmoji;
    private Timer timer;
    public boolean isPop = false;

    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public CommentDialog(Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        activityCotext = context;
        View view = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_comment,null);
        etText = (BackEditText) view.findViewById(net.twoant.master.R.id.editText);
        fl_emoji = (FrameLayout) view.findViewById(net.twoant.master.R.id.fragment_emoji);
        tvComment = (TextView) view.findViewById(net.twoant.master.R.id.tv_comment);
        ivEmoji = (ImageView) view.findViewById(net.twoant.master.R.id.iv_emoji_publishdynamic);
        etText.setFocusable(true);
        etText.setFocusableInTouchMode(true);
        etText.requestFocus();

        etText.setHint("评论内容..");
        etText.setBackListener(new BackEditText.BackListener() {
            @Override
            public void back(TextView textView) {
                isPop = false;
                //物理返回按钮结束dialog框
                dismiss();

            }
        });

        mAlertDialog.setView(view);
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                timer.schedule(new TimerTask(){
                    public void run() {
                        //弹出软键盘
                        /*InputMethodManager imm = (InputMethodManager) etText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);*/
                        MainActivity.closeIME(true,etText);
                    }
                },100);
            }
        });

        ivEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = fl_emoji.getVisibility();
                //隐藏软键盘，显示表情
                if (visibility == GONE) {
                    MainActivity.closeIME(false,etText);
                    ivEmoji.setBackgroundResource(net.twoant.master.R.drawable.icon_keyboard);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask(){
                        public void run() {
                            CommonUtil.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    fl_emoji.setVisibility(VISIBLE);
                                }
                            });
                        }
                    },60);
                } else if (visibility == VISIBLE) {
                    MainActivity.closeIME(true,etText);
                    ivEmoji.setBackgroundResource(net.twoant.master.R.drawable.icon_face_nomal);
                    fl_emoji.setVisibility(GONE);
                }
            }
        });

        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                fl_emoji.setVisibility(GONE);
                ivEmoji.setBackgroundResource(net.twoant.master.R.drawable.icon_face_nomal);
            }
        });
        timer = new Timer();
        initEmoj(view);
    }


    /**
     * 发表按钮回调监听
     * */
    public void setOnPostListener(final OnPostCommentListener onPostListener){
        tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etText.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    if (isPop) {
                        MainActivity.closeIME(true,etText);
                    }
                    onPostListener.onPost(trim);
                    etText.setText("");
                }else {
                    ToastUtil.showLong("发表内容为空");
                    return;
                }
            }
        });
    }



    private void initEmoj(View view) {
        faceViewPager = (ViewPager) view.findViewById(net.twoant.master.R.id.face_viewPager);
        faceIndicator = (EmojiIndicatorView) view.findViewById(net.twoant.master.R.id.face_indicator);
        faceRecentTv = (TextView) view.findViewById(net.twoant.master.R.id.face_recent);
        faceFirstSetTv = (TextView) view.findViewById(net.twoant.master.R.id.face_first_set);
        initViews();
    }
    private void initViews() {
        emojiList = EmojiUtil.getEmojiList();
        initViewPager(emojiList);
        if (this instanceof FaceFragment.OnEmojiClickListener) {
            this.listener = this;
        }

        if (null == recentManager) {
            recentManager = RecentEmojiManager.make(activityCotext);
        }
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
        LayoutInflater inflater = (LayoutInflater) activityCotext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList,activityCotext);
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

    @Override
    public void onEmojiDelete() {
        String text = etText.getText().toString();
        int index = 0;
        if (text.isEmpty()) {
            return;
        }
        int index1 = etText.getSelectionStart();
//        System.out.println(index1+"");
        try {
            if ("]".equals(text.substring(index1 - 1,index1 ) ) ){
                if (index1 == -1) {
                    int action = KeyEvent.ACTION_DOWN;
                    int code = KeyEvent.KEYCODE_DEL;
                    KeyEvent event = new KeyEvent(action, code);
                    etText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                    displayTextView(index);
                    return;
                }
                etText.getText().delete(index1-4, index1);
                System.out.println(index1+"");
                displayTextView(index1-8);
                return;
            }else {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextView(index1-5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        int index = etText.getSelectionStart();
        if (emoji != null) {
            Editable editable = etText.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView(index);
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
                holder.iv.setImageBitmap(EmojiUtil.decodeSampledBitmapFromResource(activityCotext.getResources(), list.get(position).getImageUri(),
                        EmojiUtil.dip2px(activityCotext, 32), EmojiUtil.dip2px(activityCotext, 32)));
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

    private void displayTextView(int count) {
        try {
            EmojiUtil.handlerEmojiText(etText, etText.getText().toString(), activityCotext,CommonUtil.getDimens(net.twoant.master.R.dimen.px_40), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40));
            etText.setSelection(count+4);
            isPop = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


