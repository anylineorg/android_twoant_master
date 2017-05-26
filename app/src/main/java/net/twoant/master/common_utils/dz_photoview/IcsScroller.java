package net.twoant.master.common_utils.dz_photoview;

import android.content.Context;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class IcsScroller extends GingerScroller{

    public IcsScroller(Context context) {
        super(context);
    }

    @Override
    public boolean computeScrollOffset() {
        return mScroller.computeScrollOffset();
    }
}
