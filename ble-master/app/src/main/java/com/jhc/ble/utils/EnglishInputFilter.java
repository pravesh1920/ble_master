package com.jhc.ble.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 过滤中文
 * @author：Chao
 * @date：2023/9/13
 */
public class EnglishInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//        String pattern = "[a-zA-Z]+";
//        if (!source.toString().matches(pattern)) {
//            return "";
//        }
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (c >= 0x4E00 && c <= 0x9FA5) {  // 判断是否为中文字符
                return "";
            }
        }
        return null;
    }
}
