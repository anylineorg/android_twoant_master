package net.twoant.master.ui.main.interfaces;

import java.util.List;

/**
 * Created by S_Y_H on 2016/12/27.
 * 支付 回调接口
 */

public interface IOnPayBtnClickListener {

    void onPayBtnClickListener(String integral, String wallet, String shopId, List<String> actionId);

    void showPasswordDialog(String useIntegral, String useWallet);
}
