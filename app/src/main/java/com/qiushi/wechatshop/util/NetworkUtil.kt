package com.qiushi.wechatshop.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import com.qiushi.wechatshop.WAppContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL

class NetworkUtil {

    companion object {

        private const val TIMEOUT = 3000 // TIMEOUT
        /**
         * check NetworkAvailable
         *
         * @return
         */
        @JvmStatic
        fun isNetworkAvailable(): Boolean {
            val manager = WAppContext.context.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return !(null == info || !info.isAvailable)
        }

        /**
         * 得到ip地址
         *
         * @return
         */
        @JvmStatic
        fun getLocalIpAddress(): String {
            var ret = ""
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val enumIpAddress = en.nextElement().inetAddresses
                    while (enumIpAddress.hasMoreElements()) {
                        val netAddress = enumIpAddress.nextElement()
                        if (!netAddress.isLoopbackAddress) {
                            ret = netAddress.hostAddress.toString()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return ret
        }

        /**
         * ping "http://www.baidu.com"
         *
         * @return
         */
        @JvmStatic
        private fun pingNetWork(): Boolean {
            var result = false
            var httpUrl: HttpURLConnection? = null
            try {
                httpUrl = URL("http://www.baidu.com")
                        .openConnection() as HttpURLConnection
                httpUrl.connectTimeout = TIMEOUT
                httpUrl.connect()
                result = true
            } catch (e: IOException) {
            } finally {
                if (null != httpUrl) {
                    httpUrl.disconnect()
                }
            }
            return result
        }

        /**
         * check is3G
         *
         * @return boolean
         */
        @JvmStatic
        fun is3G(): Boolean {
            val connectivityManager = WAppContext.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_MOBILE
        }

        /**
         * isWifi
         *
         * @return boolean
         */
        @JvmStatic
        fun isWifi(): Boolean {
            val connectivityManager = WAppContext.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
        }

        /**
         * is2G
         *
         * @return boolean
         */
        @JvmStatic
        fun is2G(): Boolean {
            val connectivityManager = WAppContext.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && (activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_EDGE
                    || activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
                    .subtype == TelephonyManager.NETWORK_TYPE_CDMA)
        }

        /**
         * is wifi on
         */
        @JvmStatic
        fun isWifiEnabled(): Boolean {
            val mgrConn = WAppContext.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mgrTel = WAppContext.context
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return mgrConn.activeNetworkInfo != null && mgrConn
                    .activeNetworkInfo.state == NetworkInfo.State.CONNECTED || mgrTel
                    .networkType == TelephonyManager.NETWORK_TYPE_UMTS
        }
    }
}