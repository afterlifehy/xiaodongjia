package com.wbb.base.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by zj on 2020/12/3.
 */

public class BigDecimalUtils {
    private static final String DECIMAL_DEFAULT = "0.00";

    private static final DecimalFormat DECIMALFORMAT_DEFAULT = new DecimalFormat("0.00");

    /**
     * 将数值转换为BigDecimal
     *
     * @param o object
     * @return
     */
    public static BigDecimal objectToBigDecimal(Object o) {
        return BigDecimal.valueOf(Double.parseDouble(String.valueOf(o)));
    }

    /**
     * 将两个数值相加并返回浮点类型
     *
     * @param o1 object
     * @param o2 object
     * @return bigDecimal
     */
    public static BigDecimal plus(Object o1, Object o2) {
        return BigDecimal.valueOf(Double.parseDouble(String.valueOf(o1)))
                .add(BigDecimal.valueOf(Double.parseDouble(String.valueOf(o2))));
    }

    /**
     * 参数一减去参数二返回浮点类型
     *
     * @param o1 object
     * @param o2 object
     * @return
     */
    public static BigDecimal subtract(Object o1, Object o2) {
        return BigDecimal.valueOf(Double.parseDouble(String.valueOf(o1)))
                .subtract(BigDecimal.valueOf(Double.parseDouble(String.valueOf(o2))));
    }


    /**
     * 计算两个数值的乘积
     *
     * @param o1 object
     * @param o2 object
     * @return
     */
    public static BigDecimal multiply(Object o1, Object o2) {
        return BigDecimal.valueOf(Double.parseDouble(String.valueOf(o1)))
                .multiply(BigDecimal.valueOf(Double.parseDouble(String.valueOf(o2))));
    }

    /**
     * 向下取整数
     *
     * @param mon
     * @param newScale
     * @return
     */
    public static String toDownValue(String mon, int newScale) {
        BigDecimal bd = new BigDecimal(mon);
        return bd.setScale(newScale, BigDecimal.ROUND_DOWN).toString();
    }

    /**
     * 科学计数法转换成正常的值
     *
     * @param vlue
     * @return
     */
    public static String scienceToString(String vlue) {
        if (vlue.contains("E")) {
            return new BigDecimal(vlue).toPlainString();
        } else {
            return vlue;
        }
    }

    /**
     * 取出无效0
     *
     * @param vlue
     * @return
     */
    public static String removeInvailZero(String vlue) {
        return new BigDecimal(vlue).stripTrailingZeros().toPlainString();
    }

    /**
     * 向上取整
     *
     * @param mon
     * @param newScale
     * @return
     */
    public static String toUpValue(String mon, int newScale) {
        BigDecimal bd = new BigDecimal(mon);
        return bd.setScale(newScale, BigDecimal.ROUND_UP).toString();
    }

    /**
     * 两个数相除四舍五入
     * 根据指定保留小数，如果不指定则默认保留两位
     *
     * @param o1 object
     * @param o2 object
     * @return
     */
    public static BigDecimal divide(Object o1, Object o2, Integer i) {
        if (i == null) {
            i = 2;
        }
        return BigDecimal.valueOf(Double.parseDouble(String.valueOf(o1)))
                .divide(BigDecimal.valueOf(
                        Double.parseDouble(String.valueOf(o2))), i, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 格式化浮点类型成指定的小数四舍五入,缺省格式0.00
     * 设置格式请看DecimalFormat
     *
     * @param o      object
     * @param format string
     * @return strng
     * @see DecimalFormat
     */
    public static String objectFormatToString(Object o, String format) {
        if (format == null) {
            format = DECIMAL_DEFAULT;
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(
                BigDecimal.valueOf(Double.parseDouble(String.valueOf(o))));
    }

    /**
     * 格式化浮点类型成指定的小数四舍五入,缺省格式0.00
     * 设置格式请看DecimalFormat
     *
     * @param o      object
     * @param format string
     * @return bigDecimal
     * @see DecimalFormat
     */
    public static BigDecimal objectFormatToBD(Object o, String format) {
        if (format == null) {
            format = DECIMAL_DEFAULT;
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return BigDecimalUtils.objectToBigDecimal(
                decimalFormat.format(BigDecimal.valueOf(Double.parseDouble(String.valueOf(o)))));
    }
}
