package net.twoant.master.ui.main.adapter.control;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.adapter.ReportStatementItemAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.holder.ReportStatementViewHolder;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/2/7.
 * 商家的报表
 */

public class ReportStatementControl extends BaseRecyclerControlImpl<DataRow> {

    private final static int TYPE = 0x1;
    private int mActivityKind;
    private String mActionId;

    public ReportStatementControl(String actionId, int activityKind) {
        super(1);
        this.mActionId = actionId;
        this.mActivityKind = activityKind;
    }

    @Override
    protected int getItemViewType(int position, DataRow dataBean) {
        return TYPE;
    }

    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        if (intercept) {
            DataRow dateRow = DataRow.parseJson(response);
            DataSet data;
            if (dateRow != null && dateRow.getBoolean("result", false) && (data = dateRow.getSet("data")) != null && data.size() > 0) {
                return data.getRows();
            }
        }
        return null;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new ReportStatementViewHolder(inflater.inflate(R.layout.yh_item_report_statement, parent, false), viewType);

    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        DataRow dataRow = mDataBean.get(position);

        if (TYPE != viewType || dataRow == null) {
            return;
        }

        ReportStatementViewHolder reportStatementViewHolder = (ReportStatementViewHolder) holder;
        RecyclerView mListView = reportStatementViewHolder.getListView();
        RecyclerView.Adapter adapter = mListView.getAdapter();
        if (adapter == null) {
            mListView.setLayoutManager(new LinearLayoutManager(mContext));
            mListView.setEnabled(false);
            mListView.setNestedScrollingEnabled(false);
            mListView.setFocusable(false);
            mListView.addItemDecoration(new RecyclerViewItemDecoration(mContext, R.color.lightGreyColor, 0, R.dimen.px_2));
            mListView.setAdapter(new ReportStatementItemAdapter(dataRow.getSet("ITEMS"), mActivityKind));
        } else {
            ((ReportStatementItemAdapter) adapter).refreshData(dataRow.getSet("ITEMS"), mActivityKind);
        }
        reportStatementViewHolder.getTvNickname().setText(dataRow.getString("RECEIVE_NAME"));
        reportStatementViewHolder.getTvPhoneNumber().setText(String.valueOf("电话: " + dataRow.getString("RECEIVE_TEL")));
        ImageLoader.getImageFromNetwork(reportStatementViewHolder.getIvHeadImg(), BaseConfig.getCorrectImageUrl(dataRow.getString("BUYER_AVATAR")), mEnvironment, R.drawable.ic_def_circle);
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.ACTIVITY_MERCHANT_REPORT_STATEMENT;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("activity", mActionId);
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        return mKeySet;
    }

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {

    }

}
