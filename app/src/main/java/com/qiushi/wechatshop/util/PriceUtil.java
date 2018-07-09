package com.qiushi.wechatshop.util;

/**
 * Created by Rylynn on 2018-07-09.
 */
public class PriceUtil {
    public static String doubleTrans(double num) {
        if (num % 1.0 == 0) {
            return String.valueOf((long) num);
        }

        return String.valueOf(num);
    }
}