package net.twoant.master.ui.my_center.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.ui.my_center.bean.AddressListBean;

/**
 * Created by DZY on 2016/12/15.
 * 佛祖保佑   永无BUG
 */

public class AddressListHolder extends BaseHolder<AddressListBean.ResultBean> {
    private TextView name,tel,address;
    private CheckBox checkBox;
    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_item_address_addressmanege,null);
        name = (TextView) view.findViewById(R.id.tv_item_name_addressmanage);
        tel = (TextView) view.findViewById(R.id.tv_item_tel_addressmanage);
        address = (TextView) view.findViewById(R.id.tv_item_address_addressmanage);
        checkBox = (CheckBox) view.findViewById(R.id.ch_item_addressmanage);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void bindData(AddressListBean.ResultBean data) {
        name.setText("收货人:"+data.getReceipt_name());
        tel.setText(data.getReceipt_tel());
        address.setText("收货地址："+data.getReceipt_address());
    }
}