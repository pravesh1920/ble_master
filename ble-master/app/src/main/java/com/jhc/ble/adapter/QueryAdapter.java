package com.jhc.ble.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jhc.ble.R;
import com.jhc.ble.bean.QueryBean;

/**
 * @author：Chao
 * @date：2023/8/21
 */
public class QueryAdapter extends BaseQuickAdapter<QueryBean, BaseViewHolder> {

    private Context mContext;

    public QueryAdapter(Context context, int layoutResId) {
        super(layoutResId);
        mContext = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, QueryBean queryBean) {
        CheckBox cbSelect = baseViewHolder.getView(R.id.cbSelect);
        TextView msgNo = baseViewHolder.getView(R.id.tvTime);
        TextView epcData = baseViewHolder.getView(R.id.tvData);
        TextView epcNum = baseViewHolder.getView(R.id.tvData1);
        TextView rssi = baseViewHolder.getView(R.id.tvData2);
        TextView antenna = baseViewHolder.getView(R.id.tvData3);
        TextView channel = baseViewHolder.getView(R.id.tvData4);

        if (queryBean.getNo().equals("序号")) {
            cbSelect.setVisibility(View.INVISIBLE);
        } else {
            cbSelect.setVisibility(View.VISIBLE);
        }
        msgNo.setText(queryBean.getNo());
        epcData.setText(queryBean.getEpcData());
        epcNum.setText(queryBean.getEpcNum());
        rssi.setText(queryBean.getRssi());
        antenna.setText(queryBean.getAntenna());
        channel.setText(queryBean.getChannel());
    }
}
