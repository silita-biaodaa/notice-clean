package com.silita.biaodaa.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dh on 2018/6/28.
 */
public class RuleUtils {

    /**
     * 大写数字、阿拉伯数字、罗马数字统一转换为阿拉伯数字
     * 若是英文，转换至大写英文
     * @param str
     * @return
     */
    public static String getNumStr (String str) {
        String regex = "[\\d一二三四五六七八九十ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ]";
        Pattern pa = Pattern.compile(regex);
        Matcher ma = pa.matcher(str);
        StringBuilder numStr = new StringBuilder();
        while (ma.find()) {
            numStr.append(ma.group());
        }
        if (MyStringUtils.isNull(numStr.toString())) {
            String regex2 = "[A-Za-z]";
            pa = Pattern.compile(regex2);
            ma = pa.matcher(str);
            while (ma.find()) {
                numStr.append(ma.group());
            }
            return MyStringUtils.isNull(numStr.toString())?"":numStr.toString().toUpperCase();
        }
        return CNNumberFormat.numberFormat(numStr.toString());
    }

    /**
     * 返回关键字下标
     * 若存在多个关键字，返回-2
     * 无关键字返回-1
     * @param title
     * @return
     */
    public static int keyWords3IndexOf (String title, String[] keyWords3) {
        int a = -1;
        int b = -1;
        for (int i = 0; i < keyWords3.length; i++) {
            if (title.contains(keyWords3[i])) {
                a = i;
                break;
            }
        }
        if (a != -1) {
            for (int i = keyWords3.length-1; i < keyWords3.length; i--) {
                if (title.contains(keyWords3[i])) {
                    b = i;
                    break;
                }
            }
        }
        return a == b? a : -2;
    }

    /**
     * 按照标题顺序，返回第一个关键字的下标
     * @param str
     * @param keyWords 关键字数组
     * @return
     */
    public static int keyWordsIndex (String str , String[] keyWords) {
        List<Integer> indexs = new ArrayList<Integer>();
        for (int i = 0; i < keyWords.length; i++) {
            int index = str.indexOf(keyWords[i]);
            if (index != -1) {
                indexs.add(index);
            }
        }

        if (indexs.isEmpty()) {
            return -1;
        }

        // 排序
        Integer[] b =  indexs.toArray(new Integer[0]);
        Arrays.sort(b);
        return b[0];
    }

    /**
     * 是否存在括号
     * @param str
     * @return
     */
    public static boolean contaninsBracket (String str) {
        return (str.contains("(") && str.contains(")")) || (str.contains("（") && str.contains("）"));
    }

    /**
     * 比较括号中的内容
     * @param var1
     * @param var2
     * @return
     */
    public static boolean compareBracketStr (String var1,String var2,String[] keyWords6) {
        var2 = var2.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]","");
        boolean b = true;
        int startIndex = -1;
        int endIndex = -1;
        char[] a = var1.toCharArray();
        for (int i = 0; i < a.length; i++) {
            if (a[i] == '(' || a[i] == '（') {
                startIndex = i;
            }
            if (a[i] == ')' || a[i] == '）') {
                endIndex = i;
            }
            if (startIndex != -1 && endIndex != -1) {
                String key = var1.substring(startIndex + 1 , endIndex);
                key = key.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]","");
                startIndex = -1;
                endIndex = -1;
                if (keyWords6IndexOf(key,keyWords6) == -1) {
                    continue;
                }
                b = var2.contains(key);
                if (!b) {
                    return b;
                }
            }
        }
        return b;
    }

    public static int keyWords6IndexOf (String title,String[] keyWords6) {
        for (int i = 0; i < keyWords6.length; i++) {
            if (title.contains(keyWords6[i])) {
                return i;
            }
        }
        return -1;
    }
}
