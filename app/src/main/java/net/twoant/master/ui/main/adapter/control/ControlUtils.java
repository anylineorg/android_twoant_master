package net.twoant.master.ui.main.adapter.control;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by S_Y_H on 2017/3/17.
 * 适用于control 列表， 工具类
 */

public final class ControlUtils {

    /**
     * 领取红包的tag key
     */
    final static int DRAW_KEY = 11 << 24;

    /**
     * 再次领取
     */
    private final static int PICK_SECOND = 0;
    /**
     * 已经领取
     */
    private final static int ALREADY_GET = 1;
    /**
     * 立即领取
     */
    public final static int GET_NEW = 2;
    /**
     * 已经结束
     */
    private final static int ALREADY_END = 3;
    /**
     * 全部抢完
     */
    private final static int GET_EMPTY = 4;
    /**
     * 活动关闭
     */
    private final static int STATE_CLOSE = 5;


    private ControlUtils() {
    }

    /**
     * 移除指定的 key ， 所重复的项
     *
     * @param result   当前请求的数据
     * @param location 所有数据
     * @param key      排除项
     * @return 过滤后
     */
    @Nullable
    public static List<DataRow> removeDuplicate(@Nullable List<DataRow> result, @Nullable List<DataRow> location,
                                         @NonNull final String key) {

        if (null == result || result.isEmpty()) {
            return result;
        }

        final ArrayList<DataRow> dataRows = new ArrayList<>(result);
        final ArrayList<String> ids = new ArrayList<>(result.size());

        String activityId;
        DataRow dataRow, localDataRow;

        for (int i = 0, j, size = result.size(), dataSize = null == location ? 0 : location.size(); i < size; ++i) {
            dataRow = result.get(i);

            if (null == dataRow) {
                continue;
            }

            activityId = dataRow.getString(key);

            if (ids.contains(activityId)) {
                dataRows.remove(dataRow);
                continue;
            } else {
                ids.add(activityId);
            }

            for (j = 0; j < dataSize; ++j) {
                localDataRow = location.get(j);
                if (null != localDataRow && null != activityId && activityId.equals(localDataRow.getString(key))) {
                    dataRows.remove(dataRow);
                    break;
                }
            }
        }
        ids.clear();
        return dataRows;
    }

    public static String getRedStateDescription(int code) {
        switch (code) {
            case PICK_SECOND:
                return "再次领取";

            case ALREADY_GET:
                return "已经领取";

            case GET_NEW:
                return "立即领取";

            case ALREADY_END:
                return "已经结束";

            case GET_EMPTY:
                return "全部抢完";

            case STATE_CLOSE:
                return "活动关闭";
        }
        return "";
    }


    /**
     * @param resultBean      外层 实体
     * @param position        适配器位置（只用于活动列表）
     * @param onClickListener 点击监听
     * @param item            外层实体 中的 item （resultBean.getRow("ITEM")）
     * @param isOnceAction    是否是单人多次活动
     * @param button          领取按钮
     * @param date            活动的结束日期（resultBean.getString("END_TIME")）
     * @return 状态码
     */
    public static int initRedState(DataRow resultBean, final int position, View.OnClickListener onClickListener, DataRow item, boolean isOnceAction, AppCompatButton button, Date date) {
        if (isOnceAction) {
            int state = getRedState(resultBean, position, onClickListener, item, button, date);
            if (PICK_SECOND == state) {
                button.setClickable(true);
                button.setEnabled(true);
                button.setTag(resultBean.getString("ID"));
                button.setTag(DRAW_KEY, position);
                button.setOnClickListener(onClickListener);
                button.setText("再次\n领取");
                return GET_NEW;
            } else {
                return state;
            }
        } else {
            if (resultBean.getBoolean("IS_JOIN", false)) {
                button.setClickable(false);
                button.setEnabled(false);
                button.setText("已经\n领取");
                return ALREADY_GET;
            } else {
                return getRedState(resultBean, position, onClickListener, item, button, date);
            }
        }
    }

    private static int getRedState(DataRow resultBean, int position, View.OnClickListener onClickListener, DataRow item, AppCompatButton button, Date date) {
        if (!resultBean.getBoolean("IS_ENABLE", false)) {
            button.setClickable(false);
            button.setEnabled(false);
            button.setText("活动\n关闭");
            return STATE_CLOSE;
        } else if (new BigDecimal(System.currentTimeMillis()).compareTo(new BigDecimal(date.getTime())) == 1) {
            button.setClickable(false);
            button.setEnabled(false);
            button.setText("已经\n结束");
            return ALREADY_END;
        } else if (isFullJoin(item)) {
            button.setClickable(false);
            button.setEnabled(false);
            button.setText("全部\n抢完");
            return GET_EMPTY;
        } else if (!resultBean.getBoolean("IS_JOIN", true)) {
            button.setClickable(true);
            button.setEnabled(true);
            button.setTag(resultBean.getString("ID"));
            button.setTag(DRAW_KEY, position);
            button.setOnClickListener(onClickListener);
            button.setText("立即\n领取");
            return GET_NEW;
        }
        return PICK_SECOND;
    }

    private static boolean isFullJoin(DataRow item) {
        return item != null && item.getInt("JOIN_QTY_LIMIT") != 0 && item.getInt("JOIN_QTY_LIMIT") - item.getInt("JOIN_QTY") <= 0;
    }


    public static boolean isNull(CharSequence charSequence) {
        return charSequence == null || "null".equals(charSequence) || charSequence.length() == 0;
    }

    static void getDistance(TextView textView, String... distances) {
        if (distances == null) {
            textView.setText("");
            return;
        }

        String distance;
        if (distances.length > 1 && !isNull((distance = distances[distances.length - 1]))) {
            textView.setText(distance);
            return;
        }

        distance = distances[0];
        if ("-1".equals(distance)) {
            SpannableString spannableString = new SpannableString("50km+");
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableString.setSpan(superscriptSpan, 4, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        } else {
            if (distance == null) {
                textView.setText("");
                return;
            }

            int length = distance.length();
            switch (length) {
                case 0:
                case 1:
                case 2:
                    textView.setText(String.valueOf(distance + "m"));
                    break;
                case 3:
                    textView.setText(String.valueOf("0." + distance.substring(0, length - 1) + "km"));
                    break;
                case 4:
                    textView.setText(String.valueOf(distance.charAt(0) + "." + distance.substring(1, length - 1) + "km"));
                    break;
                case 5:
                    textView.setText(String.valueOf(distance.substring(0, 2) + "." + distance.substring(2, length - 1) + "km"));
                    break;
            }
        }
    }


}
