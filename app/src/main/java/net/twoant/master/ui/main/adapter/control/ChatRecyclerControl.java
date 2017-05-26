package net.twoant.master.ui.main.adapter.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/3/21.
 * 消息列表的搜索
 */

public class ChatRecyclerControl extends BaseRecyclerControlImpl<Object> {

    private Map<String, EaseUser> mContactList;

    public ChatRecyclerControl() {
        super(1);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {

    }

    @Override
    public String getUrl(int category) {
        return null;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mContactList = ChatHelper.getContactList();
        if (mContactList.containsKey(mKeyword)) {
            mDataBean.add(mContactList.get(mKeyword));
        }
        return null;
    }

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {

    }

    @Override
    protected List<Object> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        return null;
    }

    private static class ChatSearchViewHolder extends BaseRecyclerViewHolder {

        public ChatSearchViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {

        }
    }
}