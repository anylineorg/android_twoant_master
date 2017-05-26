package net.twoant.master.ui.convenient.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.convenient.activity.ConvenientInfoContentActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.control.SearchConventionControl;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by J on 2017/2/28.
 * <p>
 * 需要改数据  现在是动态列表的数据
 */

public class MyConvenientInfoController extends BaseRecyclerControlImpl<DataRow> {
    private final int mWidth;
    private Activity context;
    private String mString = "";
    private List<String> imagList = new ArrayList<>();
    private String mString1;
    private String mImgUrl;
    private ViewGroup.LayoutParams mLayoutParams;
    private String content;
    private boolean isShopPublish = false;
    private String mTitle;

    public MyConvenientInfoController(boolean isShopPub, Activity context) {
        super(1);
        this.isShopPublish = isShopPub;
        this.context = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
    }

    @Override
    protected int getItemViewType(int position, DataRow dataBean) {
        return 0;
    }

    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        if (intercept) {
            DataRow dataRow = DataRow.parseJson(response);
            if (dataRow != null) {
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    return dataRow.getSet("data") != null ? dataRow.getSet("data").getRows() : null;
                }
            }

        } else {

        }
        return null;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new MyConvenientInfoViewHolder(inflater.inflate(net.twoant.master.R.layout.xj_myself_convenient_infolist, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        final int pageCount = 3;
        final MyConvenientInfoViewHolder myConvenientInfoViewHolder = (MyConvenientInfoViewHolder) holder;
        final DataRow dataRow = mDataBean.get(position);
        if (null != dataRow) {
            try {
                content = dataRow.getString("CONTENT");
                mString = SearchConventionControl.getContent(content);
                mString = mString.replace("&lt;","<");
                mString = mString.replace("&gt;",">");
                mString = mString.replace("&#126","~");
                if (!"".equals(mString)) {
                    myConvenientInfoViewHolder.mContent.setVisibility(View.VISIBLE);
//                    EmojiUtil.handlerEmojiText(myConvenientInfoViewHolder.mContent, mString, AiSouAppInfoModel.getAppContext(), CommonUtil.getDimens(R.dimen.px_40), CommonUtil.getDimens(R.dimen.px_40));
                    String emjoiStrig = EmojiUtil.getEmjoiStrig(mString);
                    myConvenientInfoViewHolder.mContent.setText(emjoiStrig);
                } else {
                    myConvenientInfoViewHolder.mContent.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            String click = dataRow.getString("CLICK");
            if (!"null".equals(click)) {
                myConvenientInfoViewHolder.mClick.setText("阅览量:" + click);
            } else {
                myConvenientInfoViewHolder.mClick.setText("阅览量:0");
            }

            myConvenientInfoViewHolder.mDelete.setOnClickListener(onClickListener);
            mTitle = dataRow.getStringDef("TITLE", "");
            mTitle = mTitle.replace("&#126","~");
            myConvenientInfoViewHolder.mTitle.setText(EmojiUtil.getEmjoiStrig(mTitle));
            myConvenientInfoViewHolder.mData.setText(dataRow.getString("ADD_TIME"));

            myConvenientInfoViewHolder.itemView.setOnClickListener(onClickListener);
            myConvenientInfoViewHolder.itemView.setTag(dataRow.getInt("ID"));
            imagList = SearchConventionControl.getImageUrl(content);
            if (null != imagList) {
                if (imagList.size() == 0) {
                    myConvenientInfoViewHolder.iv_pic1.setVisibility(View.GONE);
                    myConvenientInfoViewHolder.iv_pic2.setVisibility(View.GONE);
                    myConvenientInfoViewHolder.iv_pic3.setVisibility(View.GONE);
                } else if (imagList.size() == 1) {
                    mLayoutParams = myConvenientInfoViewHolder.iv_pic1.getLayoutParams();
                    mLayoutParams.width = mWidth / 3;
                    mLayoutParams.height = mWidth / 3;
                    myConvenientInfoViewHolder.iv_pic1.setLayoutParams(mLayoutParams);
                    ImageLoader.getImageFromNetworkPlaceholderControlImg(myConvenientInfoViewHolder.iv_pic1, BaseConfig.getCorrectImageUrl(imagList.get(0)), mContext, net.twoant.master.R.drawable.ic_def_large);
                    myConvenientInfoViewHolder.iv_pic1.setVisibility(View.VISIBLE);
                    myConvenientInfoViewHolder.iv_pic2.setVisibility(View.GONE);
                    myConvenientInfoViewHolder.iv_pic3.setVisibility(View.GONE);
                } else if (imagList.size() == 2) {
                    mLayoutParams = myConvenientInfoViewHolder.iv_pic1.getLayoutParams();
                    mLayoutParams.width = mWidth / 3;
                    mLayoutParams.height = mWidth / 3;
                    myConvenientInfoViewHolder.iv_pic1.setLayoutParams(mLayoutParams);
                    ImageLoader.getImageFromNetworkPlaceholderControlImg(myConvenientInfoViewHolder.iv_pic1, BaseConfig.getCorrectImageUrl(imagList.get(0)), mContext, net.twoant.master.R.drawable.ic_def_large);
                    myConvenientInfoViewHolder.iv_pic1.setVisibility(View.VISIBLE);

                    mLayoutParams = myConvenientInfoViewHolder.iv_pic2.getLayoutParams();
                    mLayoutParams.width = mWidth / 3;
                    mLayoutParams.height = mWidth / 3;
                    myConvenientInfoViewHolder.iv_pic2.setLayoutParams(mLayoutParams);
                    ImageLoader.getImageFromNetworkPlaceholderControlImg(myConvenientInfoViewHolder.iv_pic2, BaseConfig.getCorrectImageUrl(imagList.get(1)), mContext, net.twoant.master.R.drawable.ic_def_large);
                    myConvenientInfoViewHolder.iv_pic2.setVisibility(View.VISIBLE);
                    myConvenientInfoViewHolder.iv_pic3.setVisibility(View.GONE);
                } else {
                    mLayoutParams = myConvenientInfoViewHolder.iv_pic1.getLayoutParams();
                    mLayoutParams.width = mWidth / 3;
                    mLayoutParams.height = mWidth / 3;
                    myConvenientInfoViewHolder.iv_pic1.setLayoutParams(mLayoutParams);
                    ImageLoader.getImageFromNetworkPlaceholderControlImg(myConvenientInfoViewHolder.iv_pic1, BaseConfig.getCorrectImageUrl(imagList.get(0)), mContext, net.twoant.master.R.drawable.ic_def_large);
                    myConvenientInfoViewHolder.iv_pic1.setVisibility(View.VISIBLE);

                    mLayoutParams = myConvenientInfoViewHolder.iv_pic2.getLayoutParams();
                    mLayoutParams.width = mWidth / 3;
                    mLayoutParams.height = mWidth / 3;
                    myConvenientInfoViewHolder.iv_pic2.setLayoutParams(mLayoutParams);
                    ImageLoader.getImageFromNetworkPlaceholderControlImg(myConvenientInfoViewHolder.iv_pic2, BaseConfig.getCorrectImageUrl(imagList.get(1)), mContext, net.twoant.master.R.drawable.ic_def_large);
                    myConvenientInfoViewHolder.iv_pic2.setVisibility(View.VISIBLE);

                    mLayoutParams = myConvenientInfoViewHolder.iv_pic3.getLayoutParams();
                    mLayoutParams.width = mWidth / 3;
                    mLayoutParams.height = mWidth / 3;
                    myConvenientInfoViewHolder.iv_pic3.setLayoutParams(mLayoutParams);
                    ImageLoader.getImageFromNetworkPlaceholderControlImg(myConvenientInfoViewHolder.iv_pic3, BaseConfig.getCorrectImageUrl(imagList.get(2)), mContext, net.twoant.master.R.drawable.ic_def_large);
                    myConvenientInfoViewHolder.iv_pic3.setVisibility(View.VISIBLE);
                }
                }else {
                myConvenientInfoViewHolder.iv_pic1.setVisibility(View.GONE);
                myConvenientInfoViewHolder.iv_pic2.setVisibility(View.GONE);
                myConvenientInfoViewHolder.iv_pic3.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.CONVENIENT_INFO_LIST_SELF;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        return mKeySet;
    }

    @Override
    public void onClickListener(final View view, final HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {
        switch (view.getId()) {
            case net.twoant.master.R.id.delete:
                CancelCenterDialog cancelCenterDialog = new CancelCenterDialog(context, Gravity.CENTER, false);
                cancelCenterDialog.setTitle("确定删除这条动态");
                cancelCenterDialog.setOnClickListener(new CancelCenterDialog.IOnClickListener() {
                    @Override
                    public void onClickListener(View v) {
                        final BaseRecyclerNetworkAdapter baseRecyclerNetworkAdapter = getBaseRecyclerNetworkAdapter();
                        if (null != baseRecyclerNetworkAdapter) {
                            ViewParent parent;
                            View rootView = view;
                            ViewGroup.LayoutParams layoutParams;
                            int pos = -1;
                            while (null != (parent = rootView.getParent())) {
                                if (parent instanceof View) {
                                    layoutParams = (rootView = (View) parent).getLayoutParams();
                                    if (layoutParams instanceof RecyclerView.LayoutParams) {
                                        pos = ((RecyclerView.LayoutParams) layoutParams).getViewAdapterPosition();
                                        break;
                                    }
                                }
                            }

                            if (-1 != pos && mDataBean.size() > 0) {
                                mKeySet.clear();
                                final int finalPos = pos;

                                LongHttp(mDataBean.get(pos).getString("ID") + "", mKeySet, new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        System.out.println(response + "");
                                        boolean result = DataRow.parseJson(response).getBoolean("result", false);
                                        if (result) {
                                            baseRecyclerNetworkAdapter.removePositionItem(finalPos);
                                        } else {
                                            ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                cancelCenterDialog.showDialog(true, true);
                break;
            case net.twoant.master.R.id.ll_my_selt_parent:
                Object tag = view.getTag();
                if (tag instanceof Integer) {
                    ConvenientInfoContentActivity.startActivity(adapter.getActivity(), tag.toString());
                }
                break;
        }
    }

    @Override
    protected List<DataRow> subRemoveMethod(boolean nonNull, List<DataRow> resultBean, boolean isRefresh) {
        final ArrayList<DataRow> arrayList = new ArrayList<>(resultBean);
        if (null != resultBean && 0 != resultBean.size()) {
            if (isShopPublish) {
//                if (ControlUtils.isNull()) {
                for (int i = resultBean.size() - 1; i >= 0; i--) {
                    if ("null".equals(resultBean.get(i).getString("FROM_SHOP_ID"))) {
                        arrayList.remove(i);
                    }
                }
            } else {
                for (int i = resultBean.size() - 1; i >= 0; i--) {
                    if ("null".equals(resultBean.get(i).getString("FROM_SHOP_ID"))) {

                    } else {
                        arrayList.remove(i);
                    }
                }
            }
        }
        return arrayList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static class MyConvenientInfoViewHolder extends BaseRecyclerViewHolder {

        private TextView mData, mClick, mTitle, mContent, mDelete;
        private ImageView iv_pic1, iv_pic2, iv_pic3;

        private MyConvenientInfoViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            mTitle = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_title);
            mDelete = (TextView) itemView.findViewById(net.twoant.master.R.id.delete);
            mClick = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_click);
            mData = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_data);

            mContent = (TextView) itemView.findViewById(net.twoant.master.R.id.convenient_info_content);
            iv_pic1 = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_info1);
            iv_pic2 = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_info2);
            iv_pic3 = (ImageView) itemView.findViewById(net.twoant.master.R.id.iv_info3);
        }
    }

    private void subContent(String mStrings) {
        mString = "";
        imagList.clear();
        StringBuffer stringBuffer = new StringBuffer();
        mStrings.trim();
        mStrings = mStrings.replace("<h1>", "");
        mStrings = mStrings.replace("<h1>", "");
        mStrings = mStrings.replace("</h1>", "");
        mStrings = mStrings.replace("<h3>", "");
        mStrings = mStrings.replace("</h3>", "");
        mStrings = mStrings.replace("<h6>", "");
        mStrings = mStrings.replace("</h6>", "");
        mStrings = mStrings.replace("<br>", "");
        mStrings = mStrings.replace("&nbsp;", "");
        String[] split = mStrings.split(">");
        for (int i = 0; i < split.length; i++) {
            Log.d("TAG", "subContent:split " + split[i]);
            if (split[i].length() == 1) {
                mString1 = split[i];
                if (i == 0) {
                    stringBuffer.append(mString1);
                } else {
                    if (stringBuffer.length() != 0) {
                        stringBuffer.append("\n");
                    }
                    stringBuffer.append(mString1);
                }
            } else if ((!"<".equals(split[i].substring(0))) && (!"/".equals(split[i].substring(split[i].length() - 2, split[i].length() - 1)))) {
                mString1 = split[i];
                if (i == 0) {
                    stringBuffer.append(mString1);
                } else {
                    if (stringBuffer.length() != 0) {
                        stringBuffer.append("\n");
                    }
                    stringBuffer.append(mString1);
                }
            } else if ((!"<".equals(split[i].substring(0))) && ("/".equals(split[i].substring(split[i].length() - 2, split[i].length() - 1)))) {
                mString1 = split[i].substring(0, split[i].indexOf("<"));
                mImgUrl = split[i].substring(split[i].indexOf("src=") + 5, split[i].indexOf("alt=") - 2).trim();
                if (i == 0) {
                    stringBuffer.append(mString1);
                } else {
                    if (stringBuffer.length() != 0) {
                        stringBuffer.append("\n");
                    }
                    stringBuffer.append(mString1);
                }
                imagList.add(mImgUrl);
            } else {
                mImgUrl = split[i].substring(split[i].indexOf("src=") + 5, split[i].indexOf("alt=") - 2).trim();
                imagList.add(mImgUrl);
            }
        }
        Log.d("TAG", "subContent: " + imagList);
        mString = stringBuffer.toString().trim();
    }

    public void LongHttp(String id, Map<String, String> map, StringCallback callback) {
        map.put("id", id);
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(ApiConstants.CONVENIENT_INFO_LIST_DELETE).params(map).build().execute(callback);
    }
}
