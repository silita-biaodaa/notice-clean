package com.silita.biaodaa.utils;


import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

public class BeanUtils {

    private static Logger logger = Logger.getLogger(BeanUtils.class);

    private BeanUtils() {
    }

    /**
     * 对象转字节数组
     *
     * @param obj
     * @return
     */
    public static byte[] ObjectToBytes(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bo = null;
        ObjectOutputStream oo = null;
        try {
            bo = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();

        } catch (IOException e) {
            logger.error(e, e);
        } finally {
            try {
                if (bo != null) {
                    bo.close();
                }
                if (oo != null) {
                    oo.close();
                }
            } catch (IOException e) {
                logger.error(e, e);
            }
        }
        return bytes;
    }

    /**
     * 字节数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object BytesToObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;
        try {
            bi = new ByteArrayInputStream(bytes);
            oi = new ObjectInputStream(bi);
            obj = oi.readObject();
        } catch (Exception e) {
            logger.error(e, e);
        } finally {
            bytes = null;
            try {
                if (oi != null) {
                    oi.close();
                }
                if (bi != null) {
                    bi.reset();
                    bi.close();
                }
            } catch (IOException e) {
                logger.error(e, e);
            }
        }

        return obj;
    }

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5 = new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:" + e.getMessage(), e);
        }
    }

    public static String fillMD5(String md5) {
        return md5.length() == 32 ? md5 : fillMD5("0" + md5);
    }

}
