package net.twoant.master.ui.main.interfaces;

/**
 * Created by S_Y_H on 2016/12/24.
 * 请求网络接口
 */

public interface IStartRequestNetwork {

    void loadingDataSuccessful(String response, int id);

    void loadingDataFail(String describe, int id);
}

