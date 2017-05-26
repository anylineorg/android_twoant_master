package net.twoant.master.widget.entry;

import net.twoant.master.common_utils.LogUtils;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by S_Y_H on 2016/12/19.
 * 1
 */

public class BasicUtil {
    /**
     * 是否为空或""或"null"(大写字母"NULL"不算空) 集合对象检查是否为空或集合中是否有对象
     *
     * @param recursion 是否递归查检集合对象
     */
    public static boolean isEmpty(boolean recursion, Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof Collection && recursion) {
            Collection collection = (Collection) obj;
            for (Object item : collection) {
                if (!isEmpty(true, item)) {
                    return false;
                }
            }
        } else if (obj instanceof Map && recursion) {
            Map map = (Map) obj;
            for (Object o : map.keySet()) {
                if (!isEmpty(map.get(o))) {
                    return false;
                }
            }
        } else {
            String tmp = obj.toString().trim();
            if (!tmp.equals("") && !tmp.equals("null")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Object obj) {
        return isEmpty(false, obj);
    }

    public static boolean isNotEmpty(boolean recursion, Object obj) {
        return !isEmpty(recursion, obj);
    }

    public static boolean isNotEmpty(Object obj) {
        return isNotEmpty(false, obj);
    }

    public static boolean isEqual(Object obj1, Object obj2) {
        if (null == obj1 || null == obj2) {
            return obj1 == obj2;
        } else {
            return obj1.equals(obj2);
        }
    }

    /**
     * nvl 取第一个不为null的值,没有符合条件的 则返回null
     *
     * @param recursion 对于集合变量,是否递归
     */
    public static Object nvl(boolean recursion, Object... values) {
        if (null == values) {
            return null;
        }
        for (Object item : values) {
            if ("".equals(item) || isNotEmpty(recursion, item)) {
                return item;
            }
        }
        return null;
    }

    public static Object nvl(boolean recursion, String... values) {
        if (null == values) {
            return null;
        }
        for (Object item : values) {
            if ("".equals(item) || isNotEmpty(recursion, item)) {
                return item;
            }
        }
        return null;
    }

    public static Object nvl(Object... values) {
        return nvl(false, values);
    }

    public static Object nvl(String... values) {
        return nvl(false, values);
    }

    /**
     * 反回第一个不为空(""|null|empty)的值 没有符合条件的 则返回NULL
     * 与nvl区别 : ""不符合evl条件 但符合nvl条件
     *
     * @param recursion
     * @param values
     * @return
     */
    public static Object evl(boolean recursion, Object... values) {
        if (null == values) {
            return null;
        }
        for (Object item : values) {
            if (isNotEmpty(recursion, item)) {
                return item;
            }
        }
        return null;
    }

    public static Object evl(Object... values) {
        return evl(false, values);
    }

    /**
     * 生成随机字符串
     */
    public static String getRandomString(int length, StringBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    public static String getRandomString(int length) {
        return getRandomString(length, new StringBuffer("_0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    public static String getRandomLowerString(int length) {
        return getRandomString(length, new StringBuffer("abcdefghijklmnopqrstuvwxyz"));
    }

    public static String getRandomUpperString(int length) {
        return getRandomString(length, new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    public static String getRandomNumberString(int length) {
        return getRandomString(length, new StringBuffer("1234567890"));
    }

    /**
     * 随机中文字符(GBK)
     */
    public static String getRandomCnString(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            String str = null;
            int hPos, lPos; // 定义高低位
            Random random = new Random();
            hPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
            lPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
            byte[] b = new byte[2];
            b[0] = (Integer.valueOf(hPos).byteValue());
            b[1] = (Integer.valueOf(lPos).byteValue());
            try {
                str = new String(b, "GBk"); // 转成中文
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            result += str;
        }
        return result;
    }

    /**
     * 在src的第idx位置插入key
     */
    public static String insert(String src, int idx, String key) {
        if (null == src || null == key) {
            return src;
        }
        src = src.substring(0, idx) + key + src.substring(idx);
        return src;

    }

    /**
     * 判断数字
     */
    public static boolean isNumber(Object obj) {
        boolean result = false;
        if (obj == null) {
            return false;
        }
        if (obj instanceof Number)
            return true;

        String str = obj.toString();
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static int parseInt(Object value, int def) {
        if (null == value) {
            return def;
        }
        try {
            return (int) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer parseInteger(Object value, Integer def) {
        if (null == value) {
            return def;
        }
        try {
            return (int) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static Double parseDouble(Object value, Double def) {
        if (null == value) {
            return def;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static BigDecimal parseDecimal(Object value, double def) {
        return parseDecimal(value, new BigDecimal(def));
    }

    public static BigDecimal parseDecimal(Object value, BigDecimal def) {
        if (null == value) {
            return def;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static Long parseLong(Object value, Long def) {
        if (null == value) {
            return def;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换
     */
    public static Boolean parseBoolean(Object obj, Boolean def) {
        boolean result = def;
        if (isEmpty(obj))
            return result;
        try {
            result = "1".equals(obj.toString()) || "true".equalsIgnoreCase(obj.toString()) || "on".equalsIgnoreCase(obj.toString()) || "t".equalsIgnoreCase(obj.toString()) || !("0".equals(obj.toString()) || "false".equalsIgnoreCase(obj.toString()) || "off".equalsIgnoreCase(obj.toString()) || "f".equalsIgnoreCase(obj.toString())) && Boolean.parseBoolean(obj.toString());
        } catch (Exception e) {
            LogUtils.e("parseBoolean" + e.toString());
        }
        return result;
    }

    public static boolean parseBoolean(Object obj) {
        return parseBoolean(obj, false);
    }

    /**
     * 拆分权限数 ： 将任意一个数拆分成多个（2的n次方）的和
     */
    public static List<String> parseLimit(int num) {
        List<String> list = new ArrayList<String>();
        int count = 0;
        while (num >= 1) {
            int temp = num % 2;
            num = (num - temp) / 2;
            if (temp == 1) {
                if (count == 0) {
                    list.add("1");
                } else {
                    list.add((2 << (count - 1)) + "");
                }
            }
            count++;
        }
        return list;
    }

    /**
     * 字符串替换
     */
    public static String replace(String src, String pattern, String replace) {
        if (src == null)
            return null;
        int s = 0;
        int e = 0;
        StringBuilder result = new StringBuilder();
        while ((e = src.indexOf(pattern, s)) >= 0) {
            result.append(src.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }

        result.append(src.substring(s));
        return result.toString();
    }

    /**
     * 删除空格
     */
    public static String trim(Object str) {
        String result = "";
        if (str != null) {
            if (!isNumber(str))
                result = str.toString().trim();
            else
                result = "" + str;
        } else {
            result = "";
        }
        if (result.equals("-1"))
            result = "";
        return result;
    }

    /**
     * 删除空格
     */
    public static String trim(String str) {
        String result = "";
        if (str != null) {
            if (!isNumber(str))
                result = str.trim();
            else
                result = "" + str;
        } else {
            result = "";
        }
        if (result.equals("-1"))
            result = "";
        return result;
    }

    /**
     * 压缩空白 将多个空白压缩成一个空格
     */
    public static String compressionSpace(String str) {
        if (null != str) {
            str = str.replaceAll("\\s{2,}", " ");
        }
        return str;
    }

    public static String[] compressionSpace(String[] strs) {
        if (null != strs) {
            int size = strs.length;
            for (int i = 0; i < size; i++) {
                strs[i] = compressionSpace(strs[i]);
            }
        }
        return strs;
    }

    /**
     * 填充字符(从左侧填充)
     *
     * @param src 原文
     * @param chr 填充字符
     * @param len 需要达到的长度
     */
    public static String fillLChar(String src, String chr, int len) {
        if (null != src && null != chr && chr.length() > 0) {
            while (src.length() < len) {
                src = chr + src;
            }
        }
        return src;
    }

    public static String fillRChar(String src, String chr, int len) {
        if (null != src && null != chr && chr.length() > 0) {
            while (src.length() < len) {
                src = src + chr;
            }
        }
        return src;
    }

    public static String fillChar(String src, String chr, int len) {
        return fillLChar(src, chr, len);
    }

    public static String fillChar(String src, int len) {
        return fillChar(src, "0", len);
    }

    /**
     * 提取HashMap的key
     */
    public static List<String> getMapKeys(Map<?, ?> map) {
        List<String> keys = new ArrayList<String>();
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            keys.add(it.next().toString());
        }
        return keys;
    }

    /**
     * 合成笛卡尔组合
     */
    public static List<List<String>> mergeDescartes(List<List<String>> src) {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> st = src.get(0);
        for (String t : st) {
            List<String> tmp = new ArrayList<String>();
            tmp.add(t);
            result.add(tmp);
        }
        List<List<String>> store = new ArrayList<List<String>>();
        for (int i = 1; i < src.size(); i++) {
            List<String> r2 = src.get(i);
            for (int j = 0; j < result.size(); j++) {
                List<String> rns = result.get(j);
                for (int k = 0; k < r2.size(); k++) {
                    List<String> mid = new ArrayList<String>();
                    mid.addAll(rns);
                    mid.add(r2.get(k));
                    store.add(mid);
                }
            }
            result = new ArrayList<List<String>>();
            result.addAll(store);
            store = new ArrayList<List<String>>();
        }
        return result;
    }

    /**
     * 合并数组
     */
    public static Object[] merge(Object[] obj0, Object[] obj1) {
        if (null == obj0) {
            if (null == obj1) {
                return null;
            } else {
                return obj1;
            }
        } else {
            if (null == obj1) {
                return obj0;
            } else {
                Object[] obj = new Object[obj0.length + obj1.length];
                int idx = 0;
                for (int i = 0; i < obj0.length; i++) {
                    obj[idx++] = obj0[i];
                }
                for (int i = 0; i < obj1.length; i++) {
                    obj[idx++] = obj1[i];
                }
                return obj;
            }
        }
    }

    /**
     * 数组转换成字符串
     *
     * @param list  数组
     * @param split 分隔符
     */
    public static String array2String(List<?> list, String split) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                builder.append(list.get(i));
                if (i < size - 1) {
                    builder.append(split);
                }
            }
        }
        return builder.toString();
    }

    public static String array2String(Object[] list, String split) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            int size = list.length;
            for (int i = 0; i < size; i++) {
                builder.append(list[i]);
                if (i < size - 1) {
                    builder.append(split);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 子串出现次数
     */
    public static int catSubCharCount(String src, String chr) {
        int count = 0;
        int idx = -1;
        if (null == src || null == chr || "".equals(chr.trim())) {
            return 0;
        }
        while ((idx = src.indexOf(chr, idx + chr.length())) != -1) {
            count++;
        }
        return count;
    }


    public static String cut(String src, int fr, int to) {
        if (null == src) {
            return null;
        }
        int len = src.length();
        if (to > len) {
            to = len;
        }
        return src.substring(fr, to);
    }

    /**
     * 获取本机IP
     */
    public static List<InetAddress> getLocalIps() {
        List<InetAddress> ips = new ArrayList<InetAddress>();
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        ips.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return ips;
    }

    /**
     * 获取本机IP地址
     */
    public static List<String> getLocalIpsAddress() {
        List<String> ips = new ArrayList<String>();
        List<InetAddress> list = getLocalIps();
        for (InetAddress ip : list) {
            ips.add(ip.getHostAddress());
        }
        return ips;
    }

    public static Object toUpperCaseKey(Object obj, String... keys) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Date) {
            return obj;
        }
        if (obj instanceof Map) {
            obj = toUpperCaseKey((Map<String, Object>) obj, keys);
        } else if (obj instanceof Collection) {
            obj = toUpperCaseKey((Collection) obj, keys);
        }
        return obj;
    }

    public static Collection toUpperCaseKey(Collection con, String... keys) {
        if (null == con) {
            return null;
        }
        for (Object obj : con) {
            obj = toUpperCaseKey(obj, keys);
        }
        return con;
    }

    public static Map<String, Object> toUpperCaseKey(Map<String, Object> map, String... keys) {
        if (null == map) {
            return null;
        }
        List<String> ks = getMapKeys(map);
        for (String k : ks) {
            if (null == keys || keys.length == 0 || contains(keys, k)) {
                Object v = map.get(k);
                String key = k.toUpperCase();
                map.remove(k);
                map.put(key, v);
            }
        }
        return map;
    }

    /**
     * 数组是否包含
     */
    public static boolean contains(Object[] objs, Object obj) {
        if (null == objs || null == obj) {
            return false;
        }
        for (Object o : objs) {
            if (obj.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWrapClass(Object obj) {
        try {
            return ((Class) obj.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static String escape(String src) {
        int i;
        char j;
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
}
