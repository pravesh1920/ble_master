package com.jhc.ble.model;

import android.content.Context;

import com.jhc.ble.R;
import com.jhc.ble.bean.LogBean;
import com.jhc.ble.entity.LogEntity;

import java.util.ArrayList;

/**
 * @author：Chao
 * @date：2023/8/24
 */
public class LogGroupModel {

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param queryBeanList 每个组里的子项数量
     * @return
     */
    public static ArrayList<LogEntity> getGroups(Context context, int groupCount, ArrayList<LogBean> queryBeanList) {
        ArrayList<LogEntity> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            groups.add(new LogEntity(String.valueOf(groupCount), "PC", "CRC", "EPC",
                    context.getString(R.string.app_text_data_len), context.getString(R.string.app_text_data),
                    context.getString(R.string.app_text_antenna), context.getString(R.string.app_text_num),
                    String.valueOf(groupCount), queryBeanList));
        }
        return groups;
    }

}
