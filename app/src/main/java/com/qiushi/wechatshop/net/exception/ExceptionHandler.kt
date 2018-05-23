package com.qiushi.wechatshop.net.exception

import com.google.gson.JsonParseException
import com.orhanobut.logger.Logger
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * Created by Rylynn on 2018-05-23.
 */
class ExceptionHandler {

    companion object {

        var error: Error = Error(ErrorStatus.UNKNOWN_ERROR, "")

        fun handleException(e: Throwable): Error {

            e.printStackTrace()
            if (e is SocketTimeoutException ||
                    e is UnknownHostException ||
                    e is ConnectException) {//均视为网络错误
                error.code = ErrorStatus.NETWORK_ERROR
                error.msg = "网络连接异常"
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException) {//均视为解析错误
                error.code = ErrorStatus.JSON_ERROR
                error.msg = "数据解析异常"
            } else if (e is IllegalArgumentException) {
                error.code = ErrorStatus.SERVER_ERROR
                error.msg = "参数错误"
            } else {//未知错误
                try {
                    error.msg = "未知错误: " + e.message
                } catch (e1: Exception) {
                    error.msg = "未知错误Debug调试: " + e1.message
                }
            }

            Logger.e("Error = " + error.code + " " + error.msg)
            return error
        }
    }
}