package com.jhc.ble.adapter;

import android.content.Context;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.jhc.ble.R;
import com.jhc.ble.bean.LogBean;
import com.jhc.ble.entity.LogEntity;

import java.util.ArrayList;

/**
 * @author：Chao
 * @date：2023/8/24
 */
public class LogListAdapter extends GroupedRecyclerViewAdapter {

    protected ArrayList<LogEntity> mGroups;
    private Context mContent;

    public LogListAdapter(Context context, ArrayList<LogEntity> groups) {
        super(context);
        mGroups = groups;
        mContent = context;
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<LogBean> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    public void clear(){
        mGroups.clear();
        notifyDataChanged();
    }

    public void setGroups(ArrayList<LogEntity> groups){
        mGroups = groups;
        notifyDataChanged();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return true;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.item_log_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.item_log_header;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.item_log_data2;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        LogEntity entity = mGroups.get(groupPosition);
        holder.setText(R.id.tvPc, entity.getPc());
        holder.setText(R.id.tvCrc, entity.getCrc());
        holder.setText(R.id.tvEpc, entity.getEpc());
        holder.setText(R.id.tvLen, entity.getPecLen());
        holder.setText(R.id.tvData, entity.getData());
        holder.setText(R.id.tvAnt, entity.getAnt());
        holder.setText(R.id.tvNum, entity.getNum());
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        LogEntity entity = mGroups.get(groupPosition);
        //holder.setText(R.id.tv_footer, entity.getFooter());
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        LogBean entity = mGroups.get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tvPc, entity.getPc());
        holder.setText(R.id.tvCrc, entity.getCrc());
        holder.setText(R.id.tvEpc, entity.getEpc());
        holder.setText(R.id.tvLen, entity.getPecLen());
        holder.setText(R.id.tvData, entity.getData());
        holder.setText(R.id.tvAnt, entity.getAnt());
        holder.setText(R.id.tvNum, entity.getNum());
    }
}