package net.twoant.master.ui.charge.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.bean.BanckCardBean;
import net.twoant.master.ui.my_center.activity.WithdrawalsActivity;
import net.twoant.master.ui.my_center.activity.out.AddBankCardActivity;
import net.twoant.master.widget.RecyclerViewDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/3.
 */

public class BankCardActivity extends LongBaseActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private TextView tvBankName,tvBankNum;
    private MyRecyclerViewAdapter adapter;
    private OnItemClick click;
    private String bank_card="0",accont_price;
    private List<BanckCardBean> bankCardList;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_bankcard;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_cardlist_bankcard);
        tvBankName = (TextView) findViewById(net.twoant.master.R.id.tv_typename_bankcard);
        tvBankNum = (TextView) findViewById(net.twoant.master.R.id.tv_banknumber_bankcard);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_Save).setOnClickListener(this);
        bank_card = getIntent().getStringExtra("bank_card");
        accont_price = getIntent().getStringExtra("accont_price");
        click = new OnItemClick() {
            @Override
            public void RecyclerViewOnItemClick(String tag) {
                if (bank_card!=null){
                    if (accont_price.equals("null")|| accont_price.equals("0") || Double.parseDouble(accont_price)<=0d){
                        ToastUtil.showShort("没有余额可提");
                        return;
                    }
                    Intent intent = new Intent(BankCardActivity.this,WithdrawalsActivity.class);
                    intent.putExtra("bank_info",bankCardList.get(Integer.parseInt(tag)));
                    intent.putExtra("accont_price",accont_price);
                    intent.putExtra("balance_getmoney","true");
                    startActivity(intent);
                    BankCardActivity.this.finish();
                }
            }

            @Override
            public void Un_BankCard(String tag) {
                HashMap<String,String> map = new HashMap<>();
                int id = bankCardList.get(Integer.parseInt(tag)).getResult().get(0).getId();
                System.out.println(id);
                map.put("id",""+bankCardList.get(Integer.parseInt(tag)).getResult().get(0).getId());
                LongHttp(ApiConstants.UNBARDCARD,"", map, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d(response);
                        requestNetData1();
                    }
                });
            }
        };
        adapter = new MyRecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bankCardList = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerViewDivider(mContext,LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNetData1();
    }

    protected void requestNetData1() {
        LongHttpGet(ApiConstants.BANK_LIST + "?user="+ AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(), new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("e:"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                bankCardList.clear();
//                bankCardList = DataRow.parseJson(response).getSet("data").getRows();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray items_array = jsonObject.getJSONArray("data");
                    for (int i=0;i<items_array.length();i++) {
                        JSONObject json = (JSONObject) items_array.get(i);
                        BanckCardBean bean = new BanckCardBean();
                        BanckCardBean.ResultBean resultBean = new BanckCardBean.ResultBean();
                        List<BanckCardBean.ResultBean> list = new ArrayList();;
                        resultBean.setBankcard(json.getString("BANKCARD"));
                        resultBean.setLogourl(json.getString("LOGOURL"));
                        resultBean.setBankname(json.getString("BANKNAME"));
                        resultBean.setId(json.getInt("ID"));
                        resultBean.setCardtype(json.getString("CARDTYPE"));
                        list.add(resultBean);
                        bean.setResult(list);
                        bankCardList.add(bean);
                    }
//                    bankCardList = (List<BanckCardBean>) GsonUtil.gsonToBean(response,BanckCardBean.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.tv_Save:
                Intent intent = new Intent(BankCardActivity.this, AddBankCardActivity.class);
//                intent.putExtra("flag","1");
                startActivity(intent);
                break;
        }
    }

    public interface OnItemClick{
      void  RecyclerViewOnItemClick(String tag);
        void Un_BankCard(String tag);
    };

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<BankCardActivity.MyRecyclerViewAdapter.MyRecyclerHolder>{

        @Override
        public BankCardActivity.MyRecyclerViewAdapter.MyRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BankCardActivity.MyRecyclerViewAdapter.MyRecyclerHolder holder = new BankCardActivity.MyRecyclerViewAdapter.MyRecyclerHolder(LayoutInflater.from(
                    BankCardActivity.this).inflate(net.twoant.master.R.layout.zy_item_bankcard, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(BankCardActivity.MyRecyclerViewAdapter.MyRecyclerHolder holder, int position) {
//            holder.itBankName.setText(bankCardList.get(position).getString("BANKNAME"));
//            holder.itBankNum.setText(bankCardList.get(position).getString("BANKCARD"));
//            holder.tvBankType.setText(bankCardList.get(position).getString("CARDTYPE"));
            holder.itBankName.setText(bankCardList.get(position).getResult().get(0).getBankname());
            holder.itBankNum.setText(bankCardList.get(position).getResult().get(0).getBankcard());
            holder.tvBankType.setText(bankCardList.get(position).getResult().get(0).getCardtype());
            holder.click_layout.setTag(position+"");
            holder.un_bankcard.setTag(position+"");
            ImageLoader.getImageFromNetwork(holder.itBankImg, BaseConfig.getCorrectImageUrl(bankCardList.get(position).getResult().get(0).getLogourl()),BankCardActivity.this);
        }

        @Override
        public int getItemCount() {
            return bankCardList.size();
        }

        class MyRecyclerHolder extends RecyclerView.ViewHolder{
            TextView itBankName;
            TextView itBankNum;
            TextView tvBankType;
            ImageView itBankImg;
            RelativeLayout click_layout;
            LinearLayout un_bankcard;
            String tag;
            public MyRecyclerHolder(View itemView) {
                super (itemView);
                itBankName = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_typename_bankcard);
                itBankNum = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_banknumber_bankcard);
                tvBankType = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_type_bankcard);
                itBankImg = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_img_bancard);
                click_layout=(RelativeLayout) itemView.findViewById(net.twoant.master.R.id.click_layout);
                un_bankcard=(LinearLayout) itemView.findViewById(net.twoant.master.R.id.ll_unbund_bankcard);
                click_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click.RecyclerViewOnItemClick(view.getTag()+"");
                    }
                });

                un_bankcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BankCardActivity.this);
                        builder.setMessage("确定解绑银行卡？");
                        builder.setTitle("提示");
                        tag = view.getTag()+"";
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                click.Un_BankCard(tag +"");
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
            }
        }
    }
}
