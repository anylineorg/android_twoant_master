package net.twoant.master.ui.my_center.activity.out;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.httputils.ClassifyHttpUtils;
import net.twoant.master.ui.my_center.bean.ClassifyListBean;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/1.
 *
 * recyclerview更新notifyItemInserted(position)与notifyItemRemoved(position)
 */
public class ClassifyActivity extends LongBaseActivity implements View.OnClickListener{
    public static int CLASS_NAME = 3<<24;
    public static int CLASS_ID = 4<<24;
    private RecyclerView recyclerView;
    private List<ClassifyListBean.ResultBean> mDatasList;
    private HintDialogUtil hintDialogUtil;
    private int preCurrentPisition;
    private int currentCurrentPisition;
    private String shop_id;
    public View.OnClickListener mOnClickListener;
    private LinearLayout llContin;
    private boolean flag = true;//true可再进行编辑状态

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_classify;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        shop_id = getIntent().getStringExtra("shop_id");
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_Save).setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
        mDatasList = new ArrayList<>();
//        recyclerView = (RecyclerView) findViewById(R.id.rv_classlist_classify);
        llContin = (LinearLayout) findViewById(net.twoant.master.R.id.ll_contain_classify);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new RecyclerViewDivider(mActivity,LinearLayoutManager.VERTICAL));
//        recyclerView.setAdapter(myRecyclerViewAdapter = new MyRecyclerViewAdapter());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.tv_Save:
                Intent intent = new Intent(ClassifyActivity.this,AddClassifyDetailActivity.class);
                intent.putExtra("shopid",shop_id);
                startActivity(intent);
                break;
        }
    }


    protected void requestNet() {
        //查询商品分类
        ClassifyHttpUtils.LongHttp("0", shop_id, "", "",  new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("加载失败");
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                LogUtils.d(response);
                ClassifyListBean classifyListBean = GsonUtil.gsonToBean(response, ClassifyListBean.class);
                mDatasList = classifyListBean.getResult();
                llContin.removeAllViews();
//                myRecyclerViewAdapter.notifyItemRangeChanged(0,mDatasList.size());
                for(ClassifyListBean.ResultBean bean:mDatasList){
                    View inflate = View.inflate(ClassifyActivity.this, net.twoant.master.R.layout.zy_item_classify, null);
                    final EditText tvType = (EditText) inflate.findViewById(net.twoant.master.R.id.tv_classifyname_item);
                    final TextView tvBtn = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_btn_classifyname_item);
                    tvBtn.setTag(CLASS_NAME,bean.getNM());
                    tvBtn.setTag(CLASS_ID,bean.getCD());
                    tvBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (flag) {
                                flag = false;
                                tvBtn.setText("保存");
                                tvType.setEnabled(true);
                                tvType.setSelection(tvType.getText().toString().trim().length());
                                //弹出软键盘
                                tvType.setFocusable(true);
                                tvType.setFocusableInTouchMode(true);
                                tvType.requestFocus();
                                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            }else if ("保存".equals(tvBtn.getText().toString().trim())) {
                                hintDialogUtil.dismissDialog();
                                //2编辑商品
                                String s = tvType.getText().toString().trim();
                                if (TextUtils.isEmpty(s)) {
                                    ToastUtil.showLong("类名为空");
                                    return;
                                }
                                System.out.println(s);
                                ClassifyHttpUtils.LongHttp("2","",tvType.getText().toString().trim(),v.getTag(CLASS_ID).toString(), new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        hintDialogUtil.dismissDialog();
                                        ToastUtil.showLong("修改失败");
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        LogUtils.d(response);
                                        DataRow dataRow = DataRow.parseJson(response);
                                        String msg = dataRow.getRow("result").getString("msg");
                                        ToastUtil.showLong(msg);
                                        tvBtn.setText("编辑");
                                        tvType.setEnabled(false);
                                        flag = true;
                                        hintDialogUtil.dismissDialog();
                                        //发送广播
                                        Intent intent = new Intent();
                                        intent.putExtra("key", "数据数据");
                                        intent.setAction("gengxins");
                                        sendBroadcast(intent);
                                    }
                                });
                            }else {
                                ToastUtil.showLong("已处于编辑状态");
                                return;
                            }
                        }
                    });
                    tvType.setText(bean.getNM());
                    llContin.addView(inflate);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNet();
    }

  /*  class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerHolder>{

        @Override
        public MyRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyRecyclerHolder holder = new MyRecyclerHolder(LayoutInflater.from(ClassifyActivity.this).inflate(R.layout.zy_item_classify, parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyRecyclerHolder holder, int position) {
            holder.tvName.setText(mDatasList.get(position).getGoods_type());
            holder.tvSave.setTag(mDatasList.get(position).getShop_id());
            holder.tvSave.setOnClickListener(mOnClickListener);
            if (mOnClickListener == null) {
                mOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(v.getId()){
                            case R.id.tv_btn_classifyname_item:
                                TextView textView = (TextView) v;
                                textView.setText("保存");
                                holder.tvName.setEnabled(true);
//                                Map<String,String> map = new HashMap<>();
//                                map.put("action","2");//2编辑商品
//                                map.put("goods_typeid",v.getTag().toString());//类别Id
//                                map.put("type_name","id");   //类别Id
                                break;
                        }
                    }
                };
            }
        }

        @Override
        public int getItemCount() {
            return mDatasList.size();
        }


    }*/

   /* class MyRecyclerHolder extends RecyclerView.ViewHolder{
        EditText tvName;
        TextView tvSave;
        public MyRecyclerHolder(View itemView) {
            super (itemView);
            tvName = (EditText) itemView.findViewById(R.id.tv_classifyname_item);
            tvSave = (TextView) itemView.findViewById(R.id.tv_btn_classifyname_item);
        }
    }*/

}
