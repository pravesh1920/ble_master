package com.jhc.ble.adapter;

import android.content.Context;
import android.widget.CheckBox;

import androidx.cardview.widget.CardView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.jhc.ble.R;
import com.jhc.ble.bean.QueryBean;
import com.jhc.ble.entity.GroupEntity;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;

import java.util.ArrayList;

/**
 * @author：Chao
 * @date：2023/8/24
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter {

    protected ArrayList<GroupEntity> mGroups;
    private Context mContent;

    public GroupedListAdapter(Context context, ArrayList<GroupEntity> groups) {
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
        ArrayList<QueryBean> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    public void clear(){
        mGroups.clear();
        notifyDataChanged();
    }

    public void setGroups(ArrayList<GroupEntity> groups){
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
        return R.layout.item_msg_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.item_msg_header;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.item_msg_data2;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity entity = mGroups.get(groupPosition);
        holder.setText(R.id.tvTime, entity.getNo());
        holder.setText(R.id.tvData, entity.getEpcData());
        holder.setText(R.id.tvData1, entity.getEpcNum());
        holder.setText(R.id.tvData2, entity.getRssi());
        holder.setText(R.id.tvData3, entity.getAntenna());
        holder.setText(R.id.tvData4, entity.getChannel());
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity entity = mGroups.get(groupPosition);
        //holder.setText(R.id.tv_footer, entity.getFooter());
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        QueryBean entity = mGroups.get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tvTime, entity.getNo());
        holder.setText(R.id.tvData, entity.getEpcData());
        holder.setText(R.id.tvData1, entity.getEpcNum());
        holder.setText(R.id.tvData2, entity.getRssi());
        holder.setText(R.id.tvData3, entity.getAntenna());
        holder.setText(R.id.tvData4, entity.getChannel());

        CardView cdMsgData = holder.get(R.id.cdMsgData);
        CheckBox mCheckBox = holder.get(R.id.cbSelect);
        if (entity.isChecked()) {
            mCheckBox.setChecked(true);
            cdMsgData.setBackgroundColor(mContent.getResources().getColor(R.color.ivory));
        } else {
            mCheckBox.setChecked(false);
            cdMsgData.setBackgroundColor(mContent.getResources().getColor(R.color.white));
        }
    }
}