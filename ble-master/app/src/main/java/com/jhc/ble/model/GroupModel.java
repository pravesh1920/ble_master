package com.jhc.ble.model;

import android.content.Context;

import com.jhc.ble.R;
import com.jhc.ble.bean.QueryBean;
import com.jhc.ble.entity.GroupEntity;

import java.util.ArrayList;

/**
 * @author：Chao
 * @date：2023/8/24
 */
public class GroupModel {

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param queryBeanList 每个组里的子项数量
     * @return
     */
    public static ArrayList<GroupEntity> getGroups(Context context, int groupCount, ArrayList<QueryBean> queryBeanList) {
        ArrayList<GroupEntity> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            groups.add(new GroupEntity(String.valueOf(groupCount), context.getString(R.string.app_text_no),
                    "RSSI", context.getString(R.string.app_text_antenna), context.getString(R.string.app_text_channel),
                    "Data", context.getString(R.string.app_text_num), String.valueOf(groupCount), queryBeanList));
        }
        return groups;
    }

}
