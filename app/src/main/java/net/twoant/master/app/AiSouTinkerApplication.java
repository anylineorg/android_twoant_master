package net.twoant.master.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by S_Y_H on 2017/2/8.
 * 热更新用， 禁止写其他任何逻辑代码。
 */

public class AiSouTinkerApplication extends TinkerApplication {


    /**
     * 参数1：tinkerFlags 表示Tinker支持的类型 dex only、library only or all suuport，default: TINKER_ENABLE_ALL
     * 参数2：delegateClassName Application代理类 这里填写你自定义的ApplicationLike
     * 参数3：loaderClassName Tinker的加载器，使用默认即可
     * 参数4：tinkerLoadVerifyFlag 加载dex或者lib是否验证md5，默认为false
     */
    public AiSouTinkerApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "net.twoant.master.app.AiSouApplication",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }

}
