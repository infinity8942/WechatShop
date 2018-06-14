package com.qiushi.wechatshop.util.oss

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.reflect.KProperty

class NetConfig private constructor(){

    private var gson: Gson? = null

    private object Holder {
        val instance = NetConfig()
    }

    companion object {
        val instance: NetConfig by lazy { Holder.instance }
    }

    fun getGson(): Gson? {
        if (gson == null)
            gson = GsonBuilder()
                    .registerTypeAdapter(String::class.java, StringConverter())
                    .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                    .create()
        return gson
    }
}


