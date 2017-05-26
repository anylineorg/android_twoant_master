package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.twoant.master.R;

import java.lang.reflect.Field;

/**
 * Created by S_Y_H on 2017/4/15.
 * 列表的
 */

public class ListBottomSheetDialog extends BottomSheetDialog {

    private RecyclerView mRecyclerView;
    private AppCompatTextView mTvTitle;
    private BottomSheetBehavior<FrameLayout> mParentBehavior;
    private AppCompatTextView mTvRightOperation;

    public ListBottomSheetDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ListBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        initView(context);
    }

    protected ListBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (null != mTvTitle) {
            mTvTitle.setText(title);
        }
    }

    public void setRightOperationClickListener(View.OnClickListener onClickListener) {
        if (null != mTvRightOperation) {
            if (View.VISIBLE != mTvRightOperation.getVisibility()) {
                mTvRightOperation.setVisibility(View.VISIBLE);
            }
            mTvRightOperation.setOnClickListener(onClickListener);
        }
    }

    public void setTvRightOperation(String content) {
        if (null != mTvRightOperation) {
            if (View.VISIBLE != mTvRightOperation.getVisibility()) {
                mTvRightOperation.setVisibility(View.VISIBLE);
            }
            mTvRightOperation.setText(content);
        }
    }

    public void setTvRightOperation(@StringRes int stringRes) {
        if (null != mTvRightOperation) {
            mTvRightOperation.setText(stringRes);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (null != mTvTitle) {
            mTvTitle.setText(titleId);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (null != mRecyclerView) {
            Context context = getContext();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(context, R.color.dividerLineColor, 0, R.dimen.px_2));
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void show() {
        super.show();
        if (null != mParentBehavior) {
            if (BottomSheetBehavior.STATE_EXPANDED != mParentBehavior.getState()) {
                mParentBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
        if (null != mRecyclerView) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
        }
    }


    @SuppressWarnings("inflater")
    private void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.yh_dialog_list_bottom_sheet, null);
        mRecyclerView = (RecyclerView) inflate.findViewById(R.id.rv_recycler_view);
        inflate.findViewById(R.id.btn_close_password_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTvTitle = (AppCompatTextView) inflate.findViewById(R.id.tv_title);
        mTvRightOperation = (AppCompatTextView) inflate.findViewById(R.id.tv_right_operation);
        setContentView(inflate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Class<?> superclass = this.getClass().getSuperclass();
        try {
            Field behavior = superclass.getDeclaredField("mBehavior");
            behavior.setAccessible(true);
            Object o = behavior.get(this);
            if (o instanceof BottomSheetBehavior) {
                mParentBehavior = (BottomSheetBehavior<FrameLayout>) o;
                mParentBehavior.setSkipCollapsed(true);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
