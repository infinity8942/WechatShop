package com.qiushi.wechatshop;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public class App extends TinkerApplication {
    public App() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.qiushi.wechatshop.AppLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}