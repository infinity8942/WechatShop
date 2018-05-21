package com.qiushi.wechatshop;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public class WApp extends TinkerApplication {
    public WApp() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.qiushi.wechatshop.WAppLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}