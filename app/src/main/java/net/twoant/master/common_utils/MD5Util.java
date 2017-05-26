package net.twoant.master.common_utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 王明谦 on 2016/7/29 0029.
 * MD5加密工具类，用于密码加密
 */
public class MD5Util {

    public static String getMD5ToHex(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] bytes = digest.digest();

            StringBuilder stringBuilder = new StringBuilder((int) (bytes.length * 1.8));
            int temp;
            for (byte b : bytes) {
                temp = b & 0xFF;
                if (temp < 16 && temp >= 0) {
                    stringBuilder.append("0").append(Integer.toHexString(temp));
                } else {
                    stringBuilder.append(Integer.toHexString(temp));
                }
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getMD5ToHex(byte[] s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s);
            byte[] bytes = digest.digest();

            StringBuilder stringBuilder = new StringBuilder((int) (bytes.length * 1.8));
            int temp;
            for (byte b : bytes) {
                temp = b & 0xFF;
                if (temp < 16 && temp >= 0) {
                    stringBuilder.append("0").append(Integer.toHexString(temp));
                } else {
                    stringBuilder.append(Integer.toHexString(temp));
                }
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getMD5String(String s) {

        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "a", "b", "c", "d", "e", "f"};

    public static String sign(String src) {
        if (null == src) return "";
        String result = null;
        if (!"".equals(src)) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
                byte[] results = md.digest(src.getBytes());
                //将得到的字节数组变成字符串返回
                result = byteArrayToHexString(results);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 字符MD5加密
     */
    public static String crypto(String src) {
        return sign(src);
    }

    public static String crypto2(String str) {
        return crypto(crypto(str));
    }
/////////////////////////////////////////////////////////////////////

    /**
     * 获取单个文件的MD5值！
     */
    public static String getFileMD5(File file) {
        if (null == file || !file.isFile() || !file.exists()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param recursion ;true递归子目录中的文件
     */
    public static Map<String, String> getDirMD5(File file, boolean recursion) {
        if (null == file || !file.isDirectory() || !file.exists()) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        String md5;
        File files[] = file.listFiles();
        for (File f : files) {
            if (f.isDirectory() && recursion) {
                map.putAll(getDirMD5(f, true));
            } else {
                md5 = getFileMD5(f);
                if (md5 != null) {
                    map.put(f.getPath(), md5);
                }
            }
        }
        return map;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (byte aB : b) {
            builder.append(byteToHexString(aB));
        }
        return builder.toString();
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
