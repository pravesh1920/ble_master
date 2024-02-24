package com.jhc.ble.utils;

public class HexUtils {

    public static boolean isOdd(int num) {
        return (num & 0x1) == 1;
    }

    /**
     * 16 -> int
     *
     * @param hexString string
     * @return int
     */
    public static int hexString2Int(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    /**
     * 16 -> byte
     *
     * @param hexString String
     * @return byte
     */
    public static byte hexString2Byte(String hexString) {
        return (byte) Integer.parseInt(hexString, 16);
    }

    /**
     *
     *
     * @param hexByte byte
     * @return string
     */
    public static String byte2HexString(Byte hexByte) {
        return String.format("%02x", hexByte).toUpperCase();
    }

    /**
     *
     *
     * @param hexbytearray byte
     * @return string
     */
    public static String byteArray2HexString(byte[] hexbytearray) {

        return byteArray2HexString(hexbytearray, 0, hexbytearray.length);
    }

    /**
     * 字符串转十六进制字符串
     * @param str
     * @return
     */
    public static String str2HexString(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = null;
        try {
            bs = str.getBytes("utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     *
     *
     * @param hexbytearray byte
     * @return  string
     */
    public static String byteArray2HexString(byte[] hexbytearray, int beginIndex, int endIndex)//字节数组转转hex字符串，可选长度
    {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = beginIndex; i < endIndex; i++) {
            strBuilder.append(byte2HexString(hexbytearray[i]));
        }
        return strBuilder.toString();
    }

    /**
     *  hex string -> byte[]
     *
     * @param hexString array
     * @return byte[]
     */
    public static byte[] hexString2ByteArray(String hexString) {
        int len = hexString.length();
        byte[] result;
        if (isOdd(len)) {
            //奇数
            len++;
            result = new byte[(len / 2)];
            hexString = "0" + hexString;
        } else {
            //偶数
            result = new byte[(len / 2)];
        }
        int j = 0;
        for (int i = 0; i < len; i += 2) {
            result[j] = hexString2Byte(hexString.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 左侧补零
     *
     * @param s      s的长度超过length,返回s;小于length，左侧不足补零
     * @param length 返回字符串长度
     * @return
     */
    public static String leftZeroShift(String s, int length) {
        if (s == null || s.length() > length) {
            return s;
        }
        String str = getZero(length) + s;
        str = str.substring(str.length() - length);
        return str;
    }

    /**
     * 右侧补零
     *
     * @param s      s的长度超过length,返回s;小于length，右侧不足补零
     * @param length 返回字符串长度
     * @return
     */
    public static String rightZeroShift(String s, int length) {
        if (s == null || s.length() > length) {
            return s;
        }
        String str = s + getZero(length);
        str = str.substring(0, length);
        return str;
    }

    /**
     * 获取0的字符串
     *
     * @param length
     * @return
     */
    public static String getZero(int length) {
        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            str.append("0");
        }
        return str.toString();
    }
}
