package com.qiushi.wechatshop.util;

import java.util.regex.Pattern;

/**
 * 电话号码检验
 * Created by Rylynn on 2018/1/9.
 */
public class CheckPhoneUtil {
    /**
     * 座机电话格式验证
     **/
    private static final String PHONE_CALL_PATTERN = "^(\\(\\d{3,4}\\)|\\d{3,4}-)?\\d{7,8}(-\\d{1,4})?$";

    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173,199
     **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|7[37]|8[019]|9[9])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1707,1708,1709,166
     **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|6[6]|7[6]|8[56])\\d{8}$)|(^170[7-9]\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184
     * ,187,188,147,178,1705,198
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478]|9[8])\\d{8}$)|(^1705\\d{7}$)";

    /**
     * 仅手机号格式校验
     */
    private static final String PHONE_PATTERN = new StringBuilder(300).append(CHINA_MOBILE_PATTERN)
            .append("|")
            .append(CHINA_TELECOM_PATTERN)
            .append("|")
            .append(CHINA_UNICOM_PATTERN)
            .toString();

    /**
     * 手机和座机号格式校验
     */
    private static final String PHONE_TEL_PATTERN = new StringBuilder(350).append(PHONE_PATTERN)
            .append("|")
            .append("(")
            .append(PHONE_CALL_PATTERN)
            .append(")")
            .toString();

    /**
     * 仅手机号码校验
     *
     * @param input
     * @return
     */
    public static boolean isPhone(String input) {
        return match(PHONE_PATTERN, input);
    }

    /**
     * 手机号或座机号校验
     *
     * @param input
     * @return
     */
    public static boolean isPhoneOrTel(String input) {
        return match(PHONE_TEL_PATTERN, input);
    }

    /**
     * 验证电话号码的格式
     *
     * @param str 校验电话字符串
     * @return 返回true, 否则为false
     * @author LinBilin
     */
    public static boolean isPhoneCallNum(String str) {
        return match(PHONE_CALL_PATTERN, str);
    }

    /**
     * 验证【电信】手机号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     * @author LinBilin
     */
    public static boolean isChinaTelecomPhoneNum(String str) {
        return match(CHINA_TELECOM_PATTERN, str);
    }

    /**
     * 验证【联通】手机号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     * @author LinBilin
     */
    public static boolean isChinaUnicomPhoneNum(String str) {
        return match(CHINA_UNICOM_PATTERN, str);
    }

    /**
     * 验证【移动】手机号码的格式
     *
     * @param str 校验手机字符串
     * @return 返回true, 否则为false
     * @author LinBilin
     */
    public static boolean isChinaMobilePhoneNum(String str) {
        return match(CHINA_MOBILE_PATTERN, str);
    }

    /**
     * 匹配函数
     *
     * @param regex
     * @param input
     * @return
     */
    private static boolean match(String regex, String input) {
        return Pattern.matches(regex, input);
    }
}