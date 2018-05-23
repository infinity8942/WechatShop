package com.qiushi.wechatshop

import com.tencent.tinker.loader.app.TinkerApplication
import com.tencent.tinker.loader.shareutil.ShareConstants

class WApp : TinkerApplication(ShareConstants.TINKER_ENABLE_ALL,
        "com.qiushi.wechatshop.WAppLike",
        "com.tencent.tinker.loader.TinkerLoader",
        false)