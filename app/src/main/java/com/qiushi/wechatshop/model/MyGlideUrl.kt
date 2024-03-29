package com.qiushi.wechatshop.model

import com.bumptech.glide.load.model.GlideUrl

/**
 * Created by Rylynn on 2018-04-11.
 * 去除阿里云oss图片地址的签名，获取图片缓存key
 */
class MyGlideUrl(private val mUrl: String) : GlideUrl(mUrl) {

    private val NONCE = "Signature"

    override fun getCacheKey(): String {
        return mUrl.replace(deleteSignature(), "")
    }

    private fun deleteSignature(): String {
        var tokenParam = ""
        val tokenKeyIndex = if (mUrl.contains(String.format("?%s=", NONCE)))
            mUrl.indexOf(String.format("?%s=", NONCE)) else mUrl.indexOf(String.format("&%s=", NONCE))
        if (tokenKeyIndex != -1) {
            val nextAndIndex = mUrl.indexOf("&", tokenKeyIndex + 1)
            if (nextAndIndex != -1) {
                tokenParam = mUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1)
            } else {
                tokenParam = mUrl.substring(tokenKeyIndex)
            }
        }
        return tokenParam
    }
}