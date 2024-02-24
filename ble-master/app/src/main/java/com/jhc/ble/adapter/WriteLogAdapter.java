package com.jhc.ble.adapter;

import android.content.Context;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.jhc.ble.entity.LogEntity;
import java.util.ArrayList;

/**
 * 操作日志
 * @author：Chao
 * @date：2023/8/24
 */
public class WriteLogAdapter extends LogListAdapter {

    public WriteLogAdapter(Context context, ArrayList<LogEntity> groups) {
        super(context, groups);
    }

    /**
     * 返回false表示没有组尾
     *
     * @param groupPosition
     * @return
     */
    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    /**
     * 当hasFooter返回false时，这个方法不会被调用。
     *
     * @return
     */
    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    /**
     * 当hasFooter返回false时，这个方法不会被调用。
     *
     * @param holder
     * @param groupPosition
     */
    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }
}
