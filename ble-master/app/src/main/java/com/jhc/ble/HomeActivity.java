package com.jhc.ble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.deadline.statebutton.StateButton;
import com.donkingliang.groupedadapter.widget.StickyHeaderLayout;
import com.example.cfblelibrary.CFBLEManager;
import com.example.cfblelibrary.callback.IGetAllParamCallback;
import com.example.cfblelibrary.callback.IGetBLENameCallback;
import com.example.cfblelibrary.callback.IGetBatteryCallback;
import com.example.cfblelibrary.callback.IGetInfoCallback;
import com.example.cfblelibrary.callback.IRebootCallback;
import com.example.cfblelibrary.callback.ISelectTagCallback;
import com.example.cfblelibrary.callback.ISetAllParamCallback;
import com.example.cfblelibrary.callback.ISetBLEModeCallback;
import com.example.cfblelibrary.callback.ISetBLENameCallback;
import com.example.cfblelibrary.callback.IStartInventoryCallback;
import com.example.cfblelibrary.callback.IStopInventoryCallback;
import com.example.cfblelibrary.param.DeviceParam;
import com.example.cfblelibrary.param.InventoryTag;
import com.hjq.bar.TitleBar;
import com.jhc.ble.adapter.QueryAdapter2;
import com.jhc.ble.bean.QueryBean;
import com.jhc.ble.model.GroupModel;
import com.jhc.ble.utils.BlueUtils;
import com.jhc.ble.utils.DataUtils;
import com.jhc.ble.utils.HexUtils;
import com.monke.mprogressbar.MHorProgressBar;
import com.monke.mprogressbar.OnProgressListener;

import java.util.ArrayList;

import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.utils.FormatUtil;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    /******************enquiry**************/
    private LinearLayout llEnquiry;
    private ImageView ivEnquiry;
    private TextView tvEnquiry;
    private EditText etPollingInterval, etAcsAddr, etAcsDataLen, etFilterTime;
    private StateButton btnStartInventory, bt_rw_tag, btHomeGet, btHomeSetting;
    private RadioButton rbEnquiryEpc, rbEnquiryTid, rbEnquiryUser;
    private RadioGroup rgEnquiry;
//    private RadioGroup rgEnquiry1, rgEnquiry2;
    private TitleBar titleBar;


    /******************settings**************/
    private LinearLayout llSettings;
    private ImageView ivSettings;
    private TextView tvSettings;
    private String[] regionArray, rfidPowerArray,bleMode;
    private String selectRegion, selectRfidPower,selectBleMode;
    private Spinner spinner_region, spinner_rfid_power,spinner_bleMode;
    private Button bt_setting_getFrequency, bt_setting_getPower;
    private Button bt_setting_frequencySetup, bt_setting_powerSetup;
    private Button btSetBleName, btDisconnection,btInitialization;
    private Button btSetBleMode;
    private LinearLayout enquiry_layout, settings_layout, enquiry_hide, llEnquiryHide;
    private ImageButton bt_enquiry_hide;
    private TextView tv_setting_battery, tv_setting_hw, tv_setting_sw;
    private TextView txtSumCount,txtTotalTag;
    private MHorProgressBar mhp_2;
    private EditText et_setting_btName;
    private RecyclerView rvQuery;
    private StickyHeaderLayout stickyLayout;
    //private QueryAdapter queryAdapter;
    private QueryAdapter2 queryAdapter2;
    private int selectCheckBox = -1;
    private String read_data = "";
    private static ArrayList<QueryBean> queryBeanList = new ArrayList<>();

    private boolean is_hide_enquiry = true;
    private boolean is_show_begin_query = true;
    private DeviceParam mDeviceParam;

    private int MSG_START = 1001;
    private int MSG_STOP = 1002;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1001:
                    doStartInventory();
                    break;
                case 1002:
                    doStopInventory();
                    break;
            }
        }
    };

    boolean isAutoInventory = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        // BarUtils.transparentStatusBar(this);
        Toast.makeText(HomeActivity.this, getString(R.string.app_text_tip_connect_success), Toast.LENGTH_SHORT).show();

        initUI();
        displayLayout(1);

        new Thread(()->
            CFBLEManager.GetInstance().AutoInventory(
                new IStartInventoryCallback() {
                    @Override
                    public void OnInventory(int State) {
                        runOnUiThread(() -> {
                            SetEnable(false);
                            queryAdapter2.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void OnData(int status, InventoryTag tag, byte[] result) {
                        runOnUiThread(() -> {
                            String epcData = FormatUtil.bytesToHexString(tag.GetEPCNum());
                            String rssiHex = FormatUtil.bytesToHexString(tag.GetRSSI());
                            rssiHex = rssiHex.replaceAll(" ", "");

                            if (queryResultDataIsExist(epcData, rssiHex)) {
                                // todo epcNum+1
                            } else {
                                Log.e("pxw=====", rssiHex + "盘点结果Data-" + epcData);
                                queryNo++;
                                int rssi = BlueUtils.convertHexToInteger(rssiHex);
                                if(rssi > 32767)rssi -= 65536;
                                QueryBean queryBean = new QueryBean(String.valueOf(queryNo),
                                        epcData, "1", String.valueOf(rssi / 10),
                                        String.valueOf(tag.GetAntenna()), String.valueOf(tag.GetChannel()), false);
                                queryBeanList.add(queryBean);
                                runOnUiThread(() -> txtTotalTag.setText(String.valueOf(queryNo)));
                            }

                            sumCount++;
                            runOnUiThread(() -> txtSumCount.setText(String.valueOf(sumCount)));
                            queryAdapter2.notifyDataSetChanged();
                        });
                    }
                    @Override
                    public void OnError(Throwable t, String SendData, String RevData) {
                        if(t.getMessage().contains("Connection is null,BT is disconnected")){
                            startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                            finish();
                        }else{
                            runOnUiThread(() -> ToastUtils.showShort("开始询查失败:" + t.getMessage() ));
                        }
                    }
                    },
                    new IStopInventoryCallback() {
                    public void OnData(int status, byte[] result) {
                        runOnUiThread(() -> {
                            SetEnable(true);
                            queryAdapter2.notifyDataSetChanged();
                        });
                    }

                    public void OnError(Throwable t, String SendData, String RevData) {
                        if(t.getMessage().contains("Connection is null,BT is disconnected")){
                            startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                            finish();
                        }else {
                            runOnUiThread(() -> {
                                SetEnable(true);
                                queryAdapter2.notifyDataSetChanged();
                            });
                        }
                    }
                })).start();
    }

    public void SetEnable(boolean status){
        // 设置状态更改
        is_show_begin_query = status;
        btHomeGet.setClickable(status);
        btHomeSetting.setClickable(status);
        btnStartInventory.setClickable(status);
        llEnquiryHide.setClickable(status);
        bt_enquiry_hide.setClickable(status);
        llSettings.setClickable(status);
        bt_rw_tag.setClickable(status);

        if(status){
            btnStartInventory.setText(getString(R.string.app_text_start));
            btnStartInventory.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
            btnStartInventory.setNormalStrokeColor(getResources().getColor(R.color.royalblue));
            btHomeGet.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
            btHomeGet.setNormalStrokeColor(getResources().getColor(R.color.royalblue));
            btHomeSetting.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
            btHomeSetting.setNormalStrokeColor(getResources().getColor(R.color.royalblue));
        }
        else{
            enquiry_hide.setVisibility(View.GONE);
            bt_enquiry_hide.setSelected(true);
            is_hide_enquiry = true;
            txtTotalTag.setText(String.valueOf(0));
            txtSumCount.setText(String.valueOf(0));

            btnStartInventory.setText(getString(R.string.app_text_end));
            btnStartInventory.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
            btnStartInventory.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));
            btHomeGet.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
            btHomeGet.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));
            btHomeSetting.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
            btHomeSetting.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));
            bt_rw_tag.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
            bt_rw_tag.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));

            //if (queryBeanList.size() > 0) {
                selectCheckBox = -1;
                queryNo = 0;
                sumCount = 0;
                queryBeanList.clear();
            //}
        }
        queryAdapter2.notifyDataSetChanged();
    }

    public void displayLayout(int a) {
        if (a == 1) {
            titleBar.setTitle(getString(R.string.app_text_inquiry));
            ivEnquiry.setImageResource(R.mipmap.ic_query_down_w);
            tvEnquiry.setTextColor(getResources().getColor(R.color.white));
            ivSettings.setImageResource(R.mipmap.ic_setting_up);
            tvSettings.setTextColor(getResources().getColor(R.color.black));
            enquiry_layout.setVisibility(View.VISIBLE);
            settings_layout.setVisibility(View.GONE);
        } else if (a == 2) {
            titleBar.setTitle(getString(R.string.app_text_set_up));
            ivEnquiry.setImageResource(R.mipmap.ic_query_up);
            tvEnquiry.setTextColor(getResources().getColor(R.color.black));
            ivSettings.setImageResource(R.mipmap.ic_setting_down_w);
            tvSettings.setTextColor(getResources().getColor(R.color.white));
            settings_layout.setVisibility(View.VISIBLE);
            enquiry_layout.setVisibility(View.GONE);

            handler.postDelayed(this::initDevicesInfo, 100);
            runOnUiThread(() -> getDeviceAllParam(5));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayLayout(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doDisconnection();
        Log.d("pxw===", "onBackPressed 断开连接");
    }

    /**
     * 根据值, 设置spinner默认选中:
     *
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void initUI() {
        titleBar = findViewById(R.id.tb_main_bar_click);
        spinner_region = findViewById(R.id.spinner_region);
        spinner_rfid_power = findViewById(R.id.spinner_rfid_power);
        spinner_bleMode = findViewById(R.id.spinnerBleMode);

        enquiry_layout = findViewById(R.id.enquiry_layout);
        settings_layout = findViewById(R.id.settings_layout);

        //查询配置
        enquiry_hide = findViewById(R.id.enquiry_hide);
        enquiry_hide.setVisibility(View.GONE);
        llEnquiryHide = findViewById(R.id.llEnquiryHide);

        bt_setting_getFrequency = findViewById(R.id.bt_setting_getFrequency);
        bt_setting_getPower = findViewById(R.id.bt_setting_getPower);
        bt_setting_frequencySetup = findViewById(R.id.bt_setting_frequencySetup);
        bt_setting_powerSetup = findViewById(R.id.bt_setting_powerSetup);
        bt_rw_tag = findViewById(R.id.bt_rw_tag);

        btnStartInventory = findViewById(R.id.btnStartInventory);
        rgEnquiry = findViewById(R.id.rgEnquiry);
//        rgEnquiry1 = findViewById(R.id.rgEnquiry1);
//        rgEnquiry2 = findViewById(R.id.rgEnquiry2);
        rbEnquiryEpc = findViewById(R.id.rbEnquiryEpc);
        rbEnquiryTid = findViewById(R.id.rbEnquiryTid);
        rbEnquiryUser = findViewById(R.id.rbEnquiryUser);

        rgEnquiry.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbEnquiryEpc:
                    isSelectedEpc = true;
                    isSelectedTid = false;
                    isSelectedUser = false;
                    break;
                case R.id.rbEnquiryTid:
                    isSelectedEpc = false;
                    isSelectedTid = true;
                    isSelectedUser = false;
                    break;
                case R.id.rbEnquiryUser:
                    isSelectedEpc = false;
                    isSelectedTid = false;
                    isSelectedUser = true;
                    break;
                default:
                    break;
            }
        });
        /**rbEnquiryEpc.setOnClickListener(v -> {
            if (isSelectedEpc) {
                rbEnquiryEpc.setChecked(false);
                isSelectedEpc = false;
            } else {
                rgEnquiry.clearCheck();
                rbEnquiryEpc.setChecked(true);
                isSelectedEpc = true;
            }
        });

        rbEnquiryTid.setOnClickListener(v -> {
            if (isSelectedTid) {
                rbEnquiryTid.setChecked(false);
                isSelectedTid = false;
            } else {
                rgEnquiry1.clearCheck();
                rbEnquiryTid.setChecked(true);
                isSelectedTid = true;
            }
        });

        rbEnquiryUser.setOnClickListener(v -> {
            if (isSelectedUser) {
                rbEnquiryUser.setChecked(false);
                isSelectedUser = false;
            } else {
                rgEnquiry2.clearCheck();
                rbEnquiryUser.setChecked(true);
                isSelectedUser = true;
            }
        });**/

        /*******************enquiry layout***********************/
        llEnquiry = findViewById(R.id.llEnquiry);
        ivEnquiry = findViewById(R.id.ivEnquiry);
        tvEnquiry = findViewById(R.id.tvEnquiry);
        etPollingInterval = findViewById(R.id.etPollingInterval);
        etAcsAddr = findViewById(R.id.etAcsAddr);
        etAcsDataLen = findViewById(R.id.etAcsDataLen);
        etFilterTime = findViewById(R.id.etFilterTime);
        btHomeGet = findViewById(R.id.btHomeGet);
        btHomeSetting = findViewById(R.id.btHomeSetting);

        etPollingInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    setNumberSize(etPollingInterval, s, start, HomeActivity.this, 25);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFilterTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    setNumberSize(etFilterTime, s, start, HomeActivity.this, 255);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*******************settings layout***********************/
        llSettings = findViewById(R.id.llSettings);
        ivSettings = findViewById(R.id.ivSettings);
        tvSettings = findViewById(R.id.tvSettings);

        txtSumCount = findViewById(R.id.txtSumCount);
        txtTotalTag = findViewById(R.id.txtTotalTag);

        tv_setting_battery = findViewById(R.id.tv_setting_battery);
        tv_setting_hw = findViewById(R.id.tv_setting_hw);
        tv_setting_sw = findViewById(R.id.tv_setting_sw);
        et_setting_btName = findViewById(R.id.et_setting_btName);
        et_setting_btName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //et_setting_btName.setFilters(new InputFilter[]{new EnglishInputFilter()});
        btSetBleName = findViewById(R.id.btSetBleName);
        btSetBleMode = findViewById(R.id.bt_setting_setBleMode);
        btDisconnection = findViewById(R.id.btDisconnection);
        btInitialization = findViewById(R.id.btInitialization);
        bt_enquiry_hide = findViewById(R.id.bt_enquiry_hide);
        bt_enquiry_hide.setSelected(true);

        btnStartInventory.setOnClickListener(this);
        bt_enquiry_hide.setOnClickListener(this);
        btHomeGet.setOnClickListener(this);
        btHomeSetting.setOnClickListener(this);
        btSetBleName.setOnClickListener(this);
        btSetBleMode.setOnClickListener(this);
        btDisconnection.setOnClickListener(this);
        btInitialization.setOnClickListener(this);
        bt_setting_getFrequency.setOnClickListener(this);
        bt_setting_getPower.setOnClickListener(this);
        bt_setting_frequencySetup.setOnClickListener(this);
        bt_setting_powerSetup.setOnClickListener(this);
        llEnquiryHide.setOnClickListener(this);

        rvQuery = findViewById(R.id.rvQuery);
        stickyLayout = findViewById(R.id.sticky_layout);
        rvQuery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        queryAdapter2 = new QueryAdapter2(this, GroupModel.getGroups(this,1, queryBeanList));
        rvQuery.setAdapter(queryAdapter2);
        stickyLayout.setSticky(true);

        queryAdapter2.setOnChildClickListener((adapter, holder, groupPosition, childPosition) -> {
            // todo
            //if ("开始询查".equals(btnStartInventory.getText().toString())) {
            if (is_show_begin_query) {
                // checkBox单选
                holder.get(R.id.cbSelect).setSelected(true);
//                selectCheckBox = childPosition;
//                // 写标签状态更改
//                bt_rw_tag.setClickable(true);
//                bt_rw_tag.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
//                bt_rw_tag.setNormalStrokeColor(getResources().getColor(R.color.royalblue));

                read_data = queryBeanList.get(childPosition).getEpcData();
                doSelectTag(read_data, childPosition);

                Log.e("pxw=====", childPosition + "选择-" + queryBeanList.get(childPosition).getNo());
                for (int i = 0; i < queryBeanList.size(); i++) {
                    if (childPosition == i) {
                        queryBeanList.get(i).setChecked(true);
                    } else {
                        queryBeanList.get(i).setChecked(false);
                    }
                }

                handler.postDelayed(() -> queryAdapter2.notifyDataSetChanged(), 200);
            } else {
                ToastUtils.showShort(getString(R.string.app_text_tip_please_end_query));
            }
        });

        /************************/
//        queryAdapter = new QueryAdapter(this, R.layout.item_msg_data2);
//
//        View headerView = getLayoutInflater().inflate(R.layout.item_msg_header, null);
//        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        queryAdapter.addHeaderView(headerView);
//        queryAdapter.setHeaderViewAsFlow(true);
//
//        rvQuery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        rvQuery.setAdapter(queryAdapter);
//        queryAdapter.setNewInstance(queryBeanList);

        //头部赋值
//        QueryBean titleBean = new QueryBean("序号", "Data", "数量", "RSSI", "天线", "信道");
//        if (queryBeanList.size() == 0) {
//            queryBeanList.add(titleBean);
//            queryAdapter.setNewInstance(queryBeanList);
//        }

//        queryAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//
//            }
//        });

        regionArray = getResources().getStringArray(R.array.frequency);
        rfidPowerArray = getResources().getStringArray(R.array.power);
        bleMode = getResources().getStringArray(R.array.blemode);
        spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectRegion = regionArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_rfid_power.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectRfidPower = rfidPowerArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_bleMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectBleMode = bleMode[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 常显示事件
        llEnquiry.setOnClickListener(view -> displayLayout(1));
        llSettings.setOnClickListener(view -> displayLayout(2));

        // 写标签
        bt_rw_tag.setOnClickListener(view -> {
            if (selectCheckBox >= 0) {
                Intent mIntent = new Intent(HomeActivity.this, WRTagActivity.class);
                mIntent.putExtra("_read_data", read_data);
                startActivity(mIntent);
            }
        });

        mhp_2 = findViewById(R.id.mhp_2);
        mhp_2.setCanTouch(false);
        mhp_2.setProgressListener(new OnProgressListener() {
            @Override
            public void moveStartProgress(float dur) {

            }

            @Override
            public void durProgressChange(float dur) {
                float tvMarginLeft = mhp_2.getRealProgressWidth()*1.0f*(dur/mhp_2.getMaxProgress()-0.02f) - tv_setting_battery.getWidth();// -0.02f目的是字体不要紧贴进度条最右端
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tv_setting_battery.getLayoutParams();
                if(tvMarginLeft>0){
                    layoutParams.leftMargin = (int) tvMarginLeft;
                }else{
                    layoutParams.leftMargin = 0;
                }
                tv_setting_battery.setLayoutParams(layoutParams);
                tv_setting_battery.setText((int)(dur*100/mhp_2.getMaxProgress())+"%");
            }

            @Override
            public void moveStopProgress(float dur) {

            }

            @Override
            public void setDurProgress(float dur) {

            }
        });

        queryBeanList.clear();
        queryAdapter2.notifyDataSetChanged();
    }

    private void showBattery(int battery) {
        mhp_2.setVisibility(View.VISIBLE);
        mhp_2.setDurProgress(battery);

        handler.postDelayed(() -> {
            float tvMarginLeft = mhp_2.getRealProgressWidth()*1.0f*(battery/mhp_2.getMaxProgress()-0.02f) - tv_setting_battery.getWidth();// -0.02f目的是字体不要紧贴进度条最右端
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tv_setting_battery.getLayoutParams();
            if(tvMarginLeft>0){
                layoutParams.leftMargin = (int) tvMarginLeft;
            }else{
                layoutParams.leftMargin = 0;
            }
            tv_setting_battery.setLayoutParams(layoutParams);
            tv_setting_battery.setText((int)(battery*100/mhp_2.getMaxProgress())+"%");
        }, 100);
    }

    private boolean isGetInfo = true;
    private boolean isGetBLEName = true;
    private boolean isGetBleMode = true;
    /**
     * 获取设备信息
     */
    public void initDevicesInfo() {
        new Thread(() -> {
            batteryValue = -1;
            getBatteryInfo();

            if (isGetInfo) {
                CFBLEManager.GetInstance().GetInfo(new IGetInfoCallback() {
                    @Override
                    public void OnData(int status, String HardVer, String FirmVer, String SNCode, byte[] result) {
                        runOnUiThread(() -> tv_setting_hw.setText(String.valueOf(HardVer)));
                        runOnUiThread(() -> tv_setting_sw.setText(String.valueOf(FirmVer)));
                        isGetInfo = false;
                    }

                    @Override
                    public void OnError(Throwable t, String SendData, String RevData) {

                        if(t.getMessage().contains("Connection is null,BT is disconnected")){
                            startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                            finish();
                        }else{
                            runOnUiThread(() -> tv_setting_hw.setText(t.getMessage()));
                            runOnUiThread(() -> tv_setting_sw.setText(t.getMessage()));
                            isGetInfo = true;
                        }
                    }
                });
            }

            if (isGetBLEName) {
                CFBLEManager.GetInstance().GetBLEName(new IGetBLENameCallback() {
                    @Override
                    public void OnData(int status, String BLEName, byte[] result) {
                        runOnUiThread(() -> et_setting_btName.setText(String.valueOf(BLEName)));
                        isGetBLEName = false;
                    }

                    @Override
                    public void OnError(Throwable t, String SendData, String RevData) {
                        if(t.getMessage().contains("Connection is null,BT is disconnected")){
                            startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                            finish();
                        }else{
                            runOnUiThread(() -> et_setting_btName.setText(t.getMessage()));
                            isGetBLEName = true;
                        }
                    }
                });
            }
        }).start();
    }

    private int batteryValue = -1;
    private void getBatteryInfo() {
        if (batteryValue == -1) {
            CFBLEManager.GetInstance().GetBattery(new IGetBatteryCallback() {
                @Override
                public void OnData(int status, int Battery, byte[] result) {
                    batteryValue = Battery;
                    if (settings_layout.getVisibility() == View.VISIBLE) {
                        runOnUiThread(() -> showBattery(Battery));
                    }
                    Log.e("pxw=====", "电池11-" + batteryValue);
                }

                @Override
                public void OnError(Throwable t, String SendData, String RevData) {
                    if(t.getMessage().contains("Connection is null,BT is disconnected")){
                        startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                        finish();
                    }else{
                        mhp_2.setVisibility(View.INVISIBLE);
                        if (settings_layout.getVisibility() == View.VISIBLE) {
                            runOnUiThread(() -> tv_setting_battery.setText(t.getMessage()));
                        }
                        handler.postDelayed(() -> getBatteryInfo(), 1000 * 5);
                        Log.e("pxw=====", "电池获取失败11-" + t.getMessage());
                    }
                }
            });
        } else {
            if (settings_layout.getVisibility() == View.VISIBLE) {
                runOnUiThread(() -> showBattery(batteryValue));
            }
        }
    }

    /**
     * 1-进入调用 2-点击按钮 3-获取工作频段 4-获取输出功率 5-Set Up
     * @param type
     */
    private void getDeviceAllParam(int type) {
        new Thread(() -> CFBLEManager.GetInstance().GetAllParam(new IGetAllParamCallback() {
            @Override
            public void OnData(int status, DeviceParam param, byte[] result) {
                runOnUiThread(() -> {
                    mDeviceParam = param;
                    if (type == 2) {
                        showDeviceParam();
                        runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_get_success)));
                    } else if (type == 3) {
                        Log.e("pxw=====", "获取频段-" + String.valueOf(param.GetRegion()));
                        if (param.GetRegion() == 0x01) {
                            selectRegion = "USA";
                            setSpinnerItemSelectedByValue(spinner_region, "USA");
                        } else if (param.GetRegion() == 0x03) {
                            selectRegion = "Europe";
                            setSpinnerItemSelectedByValue(spinner_region, "Europe");
                        }
                        runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_tip_obtained_band_success)));
                    } else if (type == 4) {
                        selectRfidPower = String.valueOf(param.GetRfidPower());
                        Log.e("pxw=====", "获取功率-" + String.valueOf(param.GetRfidPower()));
                        setSpinnerItemSelectedByValue(spinner_rfid_power, String.valueOf(param.GetRfidPower()));
                        runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_tip_obtain_power_success)));
                    } else if (type == 5) {
                        if (param.GetRegion() == 0x01) {
                            selectRegion = "USA";
                            setSpinnerItemSelectedByValue(spinner_region, "USA");
                        } else if (param.GetRegion() == 0x03) {
                            selectRegion = "Europe";
                            setSpinnerItemSelectedByValue(spinner_region, "Europe");
                        }

                        selectRfidPower = String.valueOf(param.GetRfidPower());
                        handler.postDelayed(() -> setSpinnerItemSelectedByValue(spinner_rfid_power, String.valueOf(param.GetRfidPower())), 500);
                    }
                });
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                if(t.getMessage().contains("Connection is null,BT is disconnected")){
                    startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                    finish();
                }else{
                    runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_tip_obtain_device_failed) + t.getMessage()));
                }
            }
        })).start();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llEnquiryHide:
            case R.id.bt_enquiry_hide:
                if (!is_hide_enquiry) {
                    enquiry_hide.setVisibility(View.GONE);
                    bt_enquiry_hide.setSelected(true);
                    is_hide_enquiry = true;
                } else {
                    enquiry_hide.setVisibility(View.VISIBLE);
                    bt_enquiry_hide.setSelected(false);
                    is_hide_enquiry = false;

                    getDeviceAllParam(2);
                }
                break;
            case R.id.btHomeGet:
                getDeviceAllParam(2);
                break;
            case R.id.btHomeSetting:
                setDeviceParam(1);
                break;
            case R.id.btnStartInventory:
                // todo
                //if ("开始询查".equals(btnStartInventory.getText().toString())) {
                if (is_show_begin_query) {
                    is_show_begin_query = false;
                    btnStartInventory.setText(getString(R.string.app_text_end));
                    llEnquiryHide.setClickable(false);
                    bt_enquiry_hide.setClickable(false);
                    llSettings.setClickable(false);
                    bt_rw_tag.setClickable(false);

                    enquiry_hide.setVisibility(View.GONE);
                    bt_enquiry_hide.setSelected(true);
                    is_hide_enquiry = true;

                    handler.sendEmptyMessage(MSG_START);
                } else {
                    is_show_begin_query = true;
                    btnStartInventory.setText(getString(R.string.app_text_start));
                    llEnquiryHide.setClickable(true);
                    bt_enquiry_hide.setClickable(true);
                    llSettings.setClickable(true);
                    bt_rw_tag.setClickable(true);

                    handler.sendEmptyMessage(MSG_STOP);
                }
                break;
            case R.id.btSetBleName:
                doSetBleName();
                break;
            case R.id.bt_setting_setBleMode:
                doSetBleMode();
                break;
            case R.id.btDisconnection:
                doDisconnection();
                break;
            case  R.id.btInitialization:
                doInitialization();
                break;
            case R.id.bt_setting_getFrequency:
                getDeviceAllParam(3);
                break;
            case R.id.bt_setting_getPower:
                getDeviceAllParam(4);
                break;
            case R.id.bt_setting_frequencySetup:
                setDeviceParam(2);
                break;
            case R.id.bt_setting_powerSetup:
                setDeviceParam(3);
                break;
            default:
                break;
        }
    }

    private void doDisconnection() {
            runOnUiThread(()->disConnect());
            ToastUtils.showShort(getString(R.string.app_text_bluetooth_disconnect));
            startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
            finish();
    }

    private void disConnect(){
        try {
            CFBLEManager.GetInstance().DisConnect(false);
        } catch (BLELibException e) {
            Log.e("pxw=====", "蓝牙断开异常-" + e.getMessage());
        }
    }

    private void doInitialization(){
            CFBLEManager.GetInstance().Reboot(new IRebootCallback() {
                @Override
                public void OnData(int status, byte[] result) {
                    startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                    finish();
                    // runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_init_success)));
                }

                @Override
                public void OnError(Throwable t, String SendData, String RevData) {
                    if(t.getMessage().contains("Connection is null,BT is disconnected")){
                        startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                        finish();
                    }
                    // runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_init_failed) + t.getMessage()));
                }
            });
    }

    /**
     * 设置蓝牙名
     */
    private void doSetBleName() {
        String bleName = et_setting_btName.getText().toString();
        if (TextUtils.isEmpty(bleName)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_cannot_empty));
            return;
        }
        CFBLEManager.GetInstance().SetBLEName(bleName, new ISetBLENameCallback() {
            @Override
            public void OnData(int status, byte[] result) {
                runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_set_bluetooth_name_success)));
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                if(t.getMessage().contains("Connection is null,BT is disconnected")){
                    startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                    finish();
                }else{
                    runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_set_bluetooth_name_failed) + t.getMessage()));
                }
            }
        });
    }

    private void doSetBleMode(){
        Byte bBleMode;
        if (selectBleMode.equals("HID")) {
            bBleMode = HexUtils.hexString2Byte("01");
        } else {
            bBleMode = HexUtils.hexString2Byte("00");
        }

        runOnUiThread(() -> CFBLEManager.GetInstance().SetBLEMode(bBleMode, new ISetBLEModeCallback() {
            @Override
            public void OnData(int status, byte[] result) {
                startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                finish();
                // runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_set_bluetooth_mode_success)));
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                if(t.getMessage().contains("Connection is null,BT is disconnected")){
                    startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                    finish();
                }
                // runOnUiThread(() -> ToastUtils.showShort(getString(R.string.app_text_set_bluetooth_mode_failed) + t.getMessage()+RevData));
            }
        }));
    }

    private int queryNo = 0;
    Thread th;
    /**
     * 盘点方式--0x00：按时间盘点标签；0x01: 按照循环次数盘点；0x03：自定义盘点（掩码盘点）
     *
     * 盘点方式参数--
     * 开始询查
     */
    private void doStartInventory() {
        // 设置状态更改
        btHomeGet.setClickable(false);
        btHomeGet.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
        btHomeGet.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));

        btHomeSetting.setClickable(false);
        btHomeSetting.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
        btHomeSetting.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));

        if (queryBeanList.size() > 0) {
            selectCheckBox = -1;
            queryNo = 0;

            sumCount = 0;
            txtTotalTag.setText(String.valueOf(0));
            txtSumCount.setText(String.valueOf(0));

            bt_rw_tag.setClickable(false);
            bt_rw_tag.setNormalBackgroundColor(getResources().getColor(R.color.gray_cc));
            bt_rw_tag.setNormalStrokeColor(getResources().getColor(R.color.gray_cc));

            queryBeanList.clear();
            queryAdapter2.notifyDataSetChanged();
        }

        th = new Thread(new InventoryThread());
        th.start();
    }

    int sumCount = 0;
    class InventoryThread implements Runnable{
        @Override
        public void run(){
            CFBLEManager.GetInstance().StartInventory((byte) 0x00, 0, new IStartInventoryCallback() {
                @Override
                public void OnInventory(int State) {
                    //runOnUiThread(() -> ToastUtils.showShort("开始询查State:" + State));
                }

                @Override
                public void OnData(int status, InventoryTag tag, byte[] result) {
                        runOnUiThread(() -> {
                            String epcData = FormatUtil.bytesToHexString(tag.GetEPCNum());
                            String rssiHex = FormatUtil.bytesToHexString(tag.GetRSSI());
                            rssiHex = rssiHex.replaceAll(" ", "");

                            if (queryResultDataIsExist(epcData, rssiHex)) {
                                // todo epcNum+1
                            } else {
                                Log.e("pxw=====", rssiHex + "盘点结果Data-" + epcData);
                                queryNo++;

                                int rssi = BlueUtils.convertHexToInteger(rssiHex);
                                if(rssi > 32767)rssi -= 65536;
                                QueryBean queryBean = new QueryBean(String.valueOf(queryNo),
                                        epcData, "1", String.valueOf(rssi/10),
                                        String.valueOf(tag.GetAntenna()), String.valueOf(tag.GetChannel()), false);
                                queryBeanList.add(queryBean);
                                runOnUiThread(() -> txtTotalTag.setText(String.valueOf(queryNo)));
                                // queryAdapter.setNewInstance(queryBeanList);
                            }

                            sumCount++;
                            runOnUiThread(() -> txtSumCount.setText(String.valueOf(sumCount)));
                            queryAdapter2.notifyDataSetChanged();
                        });
                }

                @Override
                public void OnError(Throwable t, String SendData, String RevData) {
                    if(t.getMessage().contains("Connection is null,BT is disconnected")){
                        startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                        finish();
                    }else{}
                    // runOnUiThread(() -> ToastUtils.showShort("开始询查失败:" + t.getMessage()));
                }
            });
        }
    }

    private boolean queryResultDataIsExist(String epcData, String rssiHex) {
        boolean isExist = false;
        for (int i = 0; i < queryBeanList.size(); i++) {
            if (epcData.equals(queryBeanList.get(i).getEpcData())) {
                isExist = true;
                int epcNum = Integer.parseInt(queryBeanList.get(i).getEpcNum());
                epcNum = epcNum + 1;
                int rssi = BlueUtils.convertHexToInteger(rssiHex);
                if(rssi > 32767)rssi -= 65536;
                queryBeanList.get(i).setEpcNum(String.valueOf(epcNum));
                queryBeanList.get(i).setRssi(String.valueOf(rssi / 10));
                break;
            }
        }

        return isExist;
    }

    /**
     * 停止询查
     */
    private void doStopInventory() {
        // 设置状态更改
        btHomeGet.setClickable(true);
        btHomeGet.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
        btHomeGet.setNormalStrokeColor(getResources().getColor(R.color.royalblue));

        btHomeSetting.setClickable(true);
        btHomeSetting.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
        btHomeSetting.setNormalStrokeColor(getResources().getColor(R.color.royalblue));

        new Thread(() -> {
                CFBLEManager.GetInstance().StopInventory(new IStopInventoryCallback() {
                        @Override
                        public void OnData(int status, byte[] result) {
                            Log.d("pxw=====", "结束盘点doStopInventory-" + status);
                            //runOnUiThread(() -> ToastUtils.showShort("停止询查成功:"));
                        }

                        @Override
                        public void OnError(Throwable t, String SendData, String RevData) {
                            Log.e("pxw=====", "结束盘点doStopInventory-" + t.getMessage());
                            if(t.getMessage().contains("Connection is null,BT is disconnected")){
                                startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                                finish();
                            }else{}
                            // runOnUiThread(() -> ToastUtils.showShort("停止询查失败:" + t.getMessage()));
                        }
                    }
                );
        }).start();
    }

    /**
     * 1-询插设置 2-工作频段 3-输出功率
     * 设置询查参数
     */
    private void setDeviceParam(int type) {
        if (type == 1) {
            String pollingInterval = etPollingInterval.getText().toString();
            String acsAddr = etAcsAddr.getText().toString();
            String acsDataLen = etAcsDataLen.getText().toString();
            String filterTime = etFilterTime.getText().toString();

            if (TextUtils.isEmpty(pollingInterval) || TextUtils.isEmpty(acsAddr) || TextUtils.isEmpty(acsDataLen) || TextUtils.isEmpty(filterTime)) {
                ToastUtils.showShort(getString(R.string.app_text_tip_cannot_empty));
                return;
            }

            if (Integer.parseInt(pollingInterval) > 25) {
                ToastUtils.showShort(getString(R.string.app_text_query_interval_time_range) + "0-25");
                return;
            }

            //设置存储区域
            int inquiryArea = 1;
            //0x00：保留区  0x01(默认)：EPC存储区  0x02：TID存储区  0x03：USER存储区
            //0x04：EPC+TID  0x05：EPC+USER  0x06：EPC+TID+USER
//            if (isSelectedEpc && isSelectedTid && isSelectedUser) {
//                inquiryArea = 6;
//            } else if (isSelectedEpc && isSelectedUser) {
//                inquiryArea = 5;
//            } else if (isSelectedEpc && isSelectedTid) {
//                inquiryArea = 4;
//            }
            if (isSelectedUser) {
                inquiryArea = 3;
            } else if (isSelectedTid) {
                inquiryArea = 2;
            }

            Log.d("pxw=====", inquiryArea + "设置参数=" + Integer.toHexString(Integer.parseInt(pollingInterval)));
            mDeviceParam.SetInquiryArea(HexUtils.hexString2Byte(Integer.toHexString(inquiryArea)));
            mDeviceParam.SetPollingInterval(HexUtils.hexString2Byte(Integer.toHexString(Integer.parseInt(pollingInterval))));
            mDeviceParam.SetAcsAddr(HexUtils.hexString2Byte(Integer.toHexString(Integer.parseInt(acsAddr))));
            mDeviceParam.SetAcsDataLen(HexUtils.hexString2Byte(Integer.toHexString(Integer.parseInt(acsDataLen))));
            mDeviceParam.SetFilterTime(HexUtils.hexString2Byte(Integer.toHexString(Integer.parseInt(filterTime))));
        } else if (type == 2) {
            if (selectRegion.equals("USA")) {
                mDeviceParam.SetRegion(HexUtils.hexString2Byte("01"));
                byte[] startFreI = new byte[2];
                startFreI[0] = (byte)0x03;
                startFreI[1] = (byte)0x86;
                mDeviceParam.SetStartFreI(startFreI);
                byte[] startFreD = new byte[2];
                startFreD[0] = (byte)0x02;
                startFreD[1] = (byte)0xEE;
                mDeviceParam.SetStartFreD(startFreD);
                byte[] stepFre = new byte[2];
                stepFre[0] = (byte)0x01;
                stepFre[1] = (byte)0xF4;
                mDeviceParam.SetStepFre(stepFre);
                mDeviceParam.SetChannel((byte)0x32);
            } else {
                mDeviceParam.SetRegion(HexUtils.hexString2Byte("03"));
                byte[] startFreI = new byte[2];
                startFreI[0] = (byte)0x03;
                startFreI[1] = (byte)0x61;
                mDeviceParam.SetStartFreI(startFreI);
                byte[] startFreD = new byte[2];
                startFreD[0] = (byte)0x00;
                startFreD[1] = (byte)0x64;
                mDeviceParam.SetStartFreD(startFreD);
                byte[] stepFre = new byte[2];
                stepFre[0] = (byte)0x00;
                stepFre[1] = (byte)0xC8;
                mDeviceParam.SetStepFre(stepFre);
                mDeviceParam.SetChannel((byte)0x0F);
            }
        } else if (type == 3) {
            // todo 设置大于17失败
            //mDeviceParam.SetRfidPower(HexUtils.hexString2Byte("10"));
            mDeviceParam.SetRfidPower(HexUtils.hexString2Byte(Integer.toHexString(Integer.parseInt(selectRfidPower))));
        }

        runOnUiThread(() -> CFBLEManager.GetInstance().SetAllParam(mDeviceParam, new ISetAllParamCallback() {
            @Override
            public void OnData(int status, byte[] result) {
                ToastUtils.showShort(getString(R.string.app_text_setting_success));
                //Log.d("pxw=====", "Sucess!-" + status + " " + FormatUtil.bytesToHexString(result));
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {

                if(t.getMessage().contains("Connection is null,BT is disconnected")){
                    startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                    finish();
                }else{
                    ToastUtils.showShort(getString(R.string.app_text_setting_failed) + t.getMessage());
                }
            }
        }));
    }

    /**
     * 选择标签
     * @param type
     * @param position
     */
    private void doSelectTag(String type, int position) {
        runOnUiThread(() -> {
            //String sendHex = "E2 " + type + " 47 15 68 30 60 23 63 2B 01 08";
            CFBLEManager.GetInstance().SelectTag(type, new ISelectTagCallback() {
                @Override
                public void OnData(int status, byte[] result) {
                    //ToastUtils.showShort("Success!-" + status + " " + FormatUtil.bytesToHexString(result));
                    Log.e("pxw=====", "成功==doRGWrite==" + type);
                    selectCheckBox = position;
                    // 写标签状态更改
                    bt_rw_tag.setClickable(true);
                    bt_rw_tag.setNormalBackgroundColor(getResources().getColor(R.color.royalblue));
                    bt_rw_tag.setNormalStrokeColor(getResources().getColor(R.color.royalblue));
                }

                @Override
                public void OnError(Throwable t, String SendData, String RevData) {
                    if(t.getMessage().contains("Connection is null,BT is disconnected")){
                        startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
                        finish();
                    }else{
                        // ToastUtils.showShort("操作失败");
                        handler.postDelayed(() -> doSelectTag(type, position), 1000 * 3);
                    }
                }
            });
        });
    }

    private boolean isSelectedEpc = false;
    private boolean isSelectedTid = false;
    private boolean isSelectedUser = false;
    /**
     * 显示询查配置信息
     */
    private void showDeviceParam() {
        runOnUiThread(() -> {
            if (mDeviceParam != null) {
                etPollingInterval.setText(String.valueOf(mDeviceParam.GetPollingInterval()));
                etAcsAddr.setText(String.valueOf(mDeviceParam.GetAcsAddr()));
                etAcsDataLen.setText(String.valueOf(mDeviceParam.GetAcsDataLen()));
                etFilterTime.setText(String.valueOf(mDeviceParam.GetFilterTime()));

                //0x00：保留区  0x01(默认)：EPC存储区  0x02：TID存储区  0x03：USER存储区
                //0x04：EPC+TID  0x05：EPC+USER  0x06：EPC+TID+USER
                String inquiryArea = String.valueOf(mDeviceParam.GetInquiryArea());
                Log.d("pxw=====", "存储区域=" + inquiryArea);
                if (inquiryArea.equals("1")) {
                    rbEnquiryEpc.setChecked(true);
                }
                if (inquiryArea.equals("2")) {
                    rbEnquiryTid.setChecked(true);
                }
                if (inquiryArea.equals("3")) {
                    rbEnquiryUser.setChecked(true);
                }
                /**
                if (inquiryArea.equals("4")) {
                    rbEnquiryEpc.setChecked(true);
                    rbEnquiryTid.setChecked(true);
                    rbEnquiryUser.setChecked(false);

                    isSelectedEpc = true;
                    isSelectedTid = true;
                    isSelectedUser = false;
                }
                if (inquiryArea.equals("5")) {
                    rbEnquiryEpc.setChecked(true);
                    rbEnquiryTid.setChecked(false);
                    rbEnquiryUser.setChecked(true);

                    isSelectedEpc = true;
                    isSelectedTid = false;
                    isSelectedUser = true;
                }
                if (inquiryArea.equals("6")) {
                    rbEnquiryEpc.setChecked(true);
                    rbEnquiryTid.setChecked(true);
                    rbEnquiryUser.setChecked(true);

                    isSelectedEpc = true;
                    isSelectedTid = true;
                    isSelectedUser = true;
                }**/
            }
        });
    }

    /**
     * 设置最大的值, 输入多个0自动删除
     *
     * @param editText
     * @param s
     * @param start
     * @param context
     * @param max      最大值
     */
    public static void setNumberSize(EditText editText, CharSequence s, int start, Context context, int max) {
        if (start >= 0) {//从一输入就开始判断
            if (max != -1) {
                try {
                    int num = Integer.parseInt(s.toString());
                    String str = String.valueOf(num);
                    //判断当前edittext中的数字(可能一开始Edittext中有数字)是否大于max
                    if (num >= 0 && num <= max && s.length() > str.length()) {
                        editText.setText(str);
                        editText.setSelection(str.length());
                    } else if (num > max) {
                        s = s.subSequence(0, s.length() - 1);//如果大于max，则内容为max
                        editText.setText(s);
                        editText.setSelection(s.length());
                        ToastUtils.showShort(context.getString(R.string.app_text_cannot_exceed) + max);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doStopInventory();
        doDisconnection();
    }
}