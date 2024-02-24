package com.jhc.ble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.donkingliang.groupedadapter.widget.StickyHeaderLayout;
import com.example.cfblelibrary.CFBLEManager;
import com.example.cfblelibrary.callback.IOperateTagCallback;
import com.example.cfblelibrary.callback.ISelectTagCallback;
import com.example.cfblelibrary.param.OperateTag;
import com.example.cfblelibrary.param.TagParam;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.jhc.ble.adapter.WriteLogAdapter;
import com.jhc.ble.bean.LogBean;
import com.jhc.ble.model.LogGroupModel;
import com.jhc.ble.utils.HexUtils;
import com.troido.hexinput.formatter.HexFormatters;
import com.troido.hexinput.ui.editor.HexEditText;
import com.troido.hexinput.ui.keyboard.HexKeyboardView;

import java.util.ArrayList;

import cn.wch.blelib.utils.FormatUtil;

public class WRTagActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar tbBar;
    private Button bt_write_tag, bt_lock_tag, bt_kill_tag, bt_read_data, bt_write_data;
    private LinearLayout write_tag_layout, lock_tag_layout, kill_tag_layout;
    private RadioGroup rGWrite, rgLockArea, rgLockAction;
    private int selectLockArea = 2;
    private int selectLockAction = 2;
    private String selectMemBank = "01";
    private RecyclerView log_display_tv, logLockTv, logKillTv;
    private StickyHeaderLayout stickyLayoutW, stickyLayoutL, stickyLayoutK;
    private WriteLogAdapter writeLogAdapter, lockLogAdapter, killLogAdapter;
    private TextView tvWriteResult, tvLockResult, tvKillResult;
    private ArrayList<LogBean> logBeanArrayList = new ArrayList<>();

    private Button btLockTagLock, btKillTagKill;
    private EditText edWriteTagPtr, edWriteTagCount;//写标签
    private HexEditText edWriteTagPwd, edKillTagPwd, edLockTagPwd, etUserInput;
    private HexKeyboardView myHexKeyboard;
    private String read_data = "";
    private int read_data_len = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrtag);
        //BarUtils.transparentStatusBar(this);
        Intent intent = getIntent();
//        read_data = intent.getStringExtra("_read_data");
//        String readData = read_data.replaceAll(" ", "");
//        int readDataLen = readData.length()/2;
//        if (readDataLen > read_data_len) {
//            read_data_len = readDataLen;
//        }
        initUI();

        displayLayout(1);
    }
    public void initUI(){
        tbBar = findViewById(R.id.tbBar);
        bt_write_tag = findViewById(R.id.bt_write_tag);
        bt_lock_tag = findViewById(R.id.bt_lock_tag);
        bt_kill_tag = findViewById(R.id.bt_kill_tag);
        bt_read_data = findViewById(R.id.bt_read_data);
        bt_write_data = findViewById(R.id.bt_write_data);
        btLockTagLock = findViewById(R.id.btLockTagLock);
        btKillTagKill = findViewById(R.id.btKillTagKill);
        edKillTagPwd = findViewById(R.id.edKillTagPwd);
        edLockTagPwd = findViewById(R.id.edLockTagPwd);
        edWriteTagPwd = findViewById(R.id.edWriteTagPwd);
        edWriteTagPtr = findViewById(R.id.edWriteTagPtr);
        edWriteTagCount = findViewById(R.id.edWriteTagCount);
//        edWriteTagCount.setText(String.valueOf(read_data_len));
        etUserInput = findViewById(R.id.etUserInput);
//        etUserInput.setText(read_data);
//        etUserInput.setHexValuesLimit(read_data_len * 2);
        edWriteTagPwd.setFormatter(HexFormatters.INSTANCE.getFormatter(HexFormatters.FormatterType.SINGLE_BYTE_HEX_FORMATTER));
        edWriteTagPwd.setHexValuesLimit(8);

        edWriteTagCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    setNumberSize(edWriteTagCount, s, start, WRTagActivity.this, 255);
                } else {
                    etUserInput.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tagInput = edWriteTagCount.getText().toString();
                if (TextUtils.isEmpty(tagInput)) {
                    edWriteTagCount.requestFocus();
                    ToastUtils.showShort(getString(R.string.app_text_tip_enter_data_length));
                } else {
                    int inputLen = Integer.parseInt(tagInput);
                    int maxLimit = inputLen * 2 + (inputLen - 1);
                    Log.e("pxw=====", tagInput + "==限制长度==" + maxLimit);
                    etUserInput.setHexValuesLimit(inputLen * 4);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edKillTagPwd.setFormatter(HexFormatters.INSTANCE.getFormatter(HexFormatters.FormatterType.SINGLE_BYTE_HEX_FORMATTER));
        edKillTagPwd.setHexValuesLimit(8);

        edLockTagPwd.setFormatter(HexFormatters.INSTANCE.getFormatter(HexFormatters.FormatterType.SINGLE_BYTE_HEX_FORMATTER));
        edLockTagPwd.setHexValuesLimit(8);

        myHexKeyboard = findViewById(R.id.myHexKeyboard);
        //KeyboardManager.INSTANCE.showHexKeyboard(myHexKeyboard);

        write_tag_layout = findViewById(R.id.write_tag_layout);
        lock_tag_layout = findViewById(R.id.lock_tag_layout);
        kill_tag_layout = findViewById(R.id.kill_tag_layout);

        log_display_tv = findViewById(R.id.log_display_tv);
        stickyLayoutW = findViewById(R.id.stickyLayoutW);
        stickyLayoutL = findViewById(R.id.stickyLayoutL);
        stickyLayoutK = findViewById(R.id.stickyLayoutK);
        logLockTv = findViewById(R.id.logLockTv);
        logKillTv = findViewById(R.id.logKillTv);

        tvWriteResult = findViewById(R.id.tvWriteResult);
        tvLockResult = findViewById(R.id.tvLockResult);
        tvKillResult = findViewById(R.id.tvKillResult);

        bt_read_data.setOnClickListener(this);
        bt_write_data.setOnClickListener(this);
        btLockTagLock.setOnClickListener(this);
        btKillTagKill.setOnClickListener(this);

        log_display_tv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        logLockTv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        logKillTv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        writeLogAdapter = new WriteLogAdapter(this, LogGroupModel.getGroups(this,1, logBeanArrayList));
        lockLogAdapter = new WriteLogAdapter(this, LogGroupModel.getGroups(this,1, logBeanArrayList));
        killLogAdapter = new WriteLogAdapter(this, LogGroupModel.getGroups(this,1, logBeanArrayList));
        log_display_tv.setAdapter(writeLogAdapter);
        logLockTv.setAdapter(lockLogAdapter);
        logKillTv.setAdapter(killLogAdapter);
        stickyLayoutW.setSticky(true);
        stickyLayoutL.setSticky(true);
        stickyLayoutK.setSticky(true);

        tbBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                finish();
            }

            @Override
            public void onTitleClick(TitleBar titleBar) {
                OnTitleBarListener.super.onTitleClick(titleBar);
            }

            @Override
            public void onRightClick(TitleBar titleBar) {
                OnTitleBarListener.super.onRightClick(titleBar);
            }
        });

        bt_write_tag.setOnClickListener(view -> displayLayout(1));
        bt_lock_tag.setOnClickListener(view -> displayLayout(2));
        bt_kill_tag.setOnClickListener(view -> displayLayout(3));

        rGWrite = findViewById(R.id.rGWrite);
        rGWrite.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbWriteReserve:
                    // doRGWrite("00");
                    selectMemBank = "00";
                    break;
                case R.id.rbWriteEpc:
                    // doRGWrite("01");
                    selectMemBank = "01";
                    break;
                case R.id.rbWriteTid:
                    // doRGWrite("02");
                    selectMemBank = "02";
                    break;
                case R.id.rbWriteUser:
                    // doRGWrite("03");
                    selectMemBank = "03";
                    break;
            }
        });

        //需要锁定的区域
        rgLockArea = findViewById(R.id.rgLockArea);
        rgLockArea.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbLockArea00:
                    selectLockArea = 0;
                    break;
                case R.id.rbLockArea01:
                    selectLockArea = 1;
                    break;
                case R.id.rbLockArea02:
                    selectLockArea = 2;
                    break;
                case R.id.rbLockArea03:
                    selectLockArea = 3;
                    break;
                case R.id.rbLockArea04:
                    selectLockArea = 4;
                    break;
            }
        });

        //锁定操作类型
        rgLockAction = findViewById(R.id.rgLockAction);
        rgLockAction.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbLockAction00:
                    selectLockAction = 0;
                    break;
                case R.id.rbLockAction01:
                    selectLockAction = 1;
                    break;
                case R.id.rbLockAction02:
                    selectLockAction = 2;
                    break;
                case R.id.rbLockAction03:
                    selectLockAction = 3;
                    break;
            }
        });
    }
    public void displayLayout(int a) {
        if (a == 1) {
            cleanLogData(1);
            bt_write_tag.setTextColor(getResources().getColor(R.color.white));
            bt_lock_tag.setTextColor(getResources().getColor(R.color.white));
            bt_kill_tag.setTextColor(getResources().getColor(R.color.white));

            bt_write_tag.setBackgroundColor(getResources().getColor(R.color.royalblue));
            bt_lock_tag.setBackgroundColor(getResources().getColor(R.color.gray_cc));
            bt_kill_tag.setBackgroundColor(getResources().getColor(R.color.gray_cc));

            write_tag_layout.setVisibility(View.VISIBLE);
            lock_tag_layout.setVisibility(View.GONE);
            kill_tag_layout.setVisibility(View.GONE);
        } else if(a == 2) {
            cleanLogData(2);
            bt_lock_tag.setTextColor(getResources().getColor(R.color.white));
            bt_write_tag.setTextColor(getResources().getColor(R.color.white));
            bt_kill_tag.setTextColor(getResources().getColor(R.color.white));

            bt_write_tag.setBackgroundColor(getResources().getColor(R.color.gray_cc));
            bt_lock_tag.setBackgroundColor(getResources().getColor(R.color.royalblue));
            bt_kill_tag.setBackgroundColor(getResources().getColor(R.color.gray_cc));

            write_tag_layout.setVisibility(View.GONE);
            lock_tag_layout.setVisibility(View.VISIBLE);
            kill_tag_layout.setVisibility(View.GONE);
        } else if(a == 3) {
            cleanLogData(3);
            bt_kill_tag.setTextColor(getResources().getColor(R.color.white));
            bt_lock_tag.setTextColor(getResources().getColor(R.color.white));
            bt_write_tag.setTextColor(getResources().getColor(R.color.white));

            bt_write_tag.setBackgroundColor(getResources().getColor(R.color.gray_cc));
            bt_lock_tag.setBackgroundColor(getResources().getColor(R.color.gray_cc));
            bt_kill_tag.setBackgroundColor(getResources().getColor(R.color.royalblue));

            write_tag_layout.setVisibility(View.GONE);
            lock_tag_layout.setVisibility(View.GONE);
            kill_tag_layout.setVisibility(View.VISIBLE);
        }
    }

    private void cleanLogData(int show) {
        if (logBeanArrayList != null) {
            logBeanArrayList.clear();
            if (show == 1) {
                writeLogAdapter.notifyDataSetChanged();
            } else if (show == 2) {
                lockLogAdapter.notifyDataSetChanged();
            } else if (show == 3) {
                killLogAdapter.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_read_data:
                tvWriteResult.setText("");
                cleanLogData(1);
                doReadData();
                break;
            case R.id.bt_write_data:
                tvWriteResult.setText("");
                cleanLogData(1);
                doWriteData();
                break;
            case R.id.btLockTagLock:
                tvLockResult.setText("");
                cleanLogData(2);
                doLockTagLock();
                break;
            case R.id.btKillTagKill:
                tvKillResult.setText("");
                cleanLogData(3);
                doKillTagKill();
                break;
            default:
                break;
        }
    }

    /**
     * 写标签-读
     */
    private void doReadData() {
        String tagPwd = edWriteTagPwd.getText().toString();
        String tagPtr = edWriteTagPtr.getText().toString();
        String tagCount =Integer.toHexString(Integer.parseInt(edWriteTagCount.getText().toString())) ;
        if (TextUtils.isEmpty(tagPwd)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_password));
            return;
        }

        if (TextUtils.isEmpty(tagPtr)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_start_address));
            return;
        }

        if (TextUtils.isEmpty(tagCount)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_data_length));
            return;
        }

        long time = System.currentTimeMillis();
        //addLog(log_display_tv, DateUtils.transformNormal(time).trim(), 1);

        TagParam param = new TagParam();
        tagPwd = tagPwd.replaceAll(" ", "");
        param.SetAccPwd(HexUtils.hexString2ByteArray(tagPwd));//密码（HEX）
        param.SetMemBank(HexUtils.hexString2Byte(selectMemBank));//读写区域 0x00：Reserved  --0x01：EPC--  0x02：TID--  0x03：User

        String hexPtr = HexUtils.leftZeroShift(Integer.toHexString(Integer.parseInt(tagPtr)), 4);
        param.SetWordPtr(HexUtils.hexString2ByteArray(hexPtr));//起始地址
        param.SetWordCount(HexUtils.hexString2Byte(tagCount));//数据长度

        runOnUiThread(() -> CFBLEManager.GetInstance().ReadTag(param, new IOperateTagCallback() {
            @Override
            public void OnData(int status, OperateTag tag, byte[] result) {
                runOnUiThread(() -> {
                    tvWriteResult.setText(FormatUtil.bytesToHexString(tag.GetData()));
                    Log.i("pxw=====", "成功==ReadTag==");
                    //ToastUtils.showShort("Sucess!-" + status + " " + FormatUtil.bytesToHexString(result));
                    read_data = FormatUtil.bytesToHexString(tag.GetData());
                    read_data_len = tag.GetWordCount();
                    // String readData = read_data.replaceAll(" ", "");
                    // int readDataLen = readData.length()/4;
                    // if (readDataLen > read_data_len) {
                    //     read_data_len = readDataLen;
                    // }
                    edWriteTagCount.setText(String.valueOf(read_data_len));
                    etUserInput.setText(read_data);
                    etUserInput.setHexValuesLimit(read_data_len * 4);

                    LogBean logBean = new LogBean(FormatUtil.bytesToHexString(tag.GetPC()),
                            FormatUtil.bytesToHexString(tag.GetCRC()),
                            FormatUtil.bytesToHexString(tag.GetEPCCode()),
                            String.valueOf(tag.GetEPCLen()),
                            FormatUtil.bytesToHexString(tag.GetData()),
                            String.valueOf(tag.GetAntenna()),
                            String.valueOf(tag.GetWordCount()));

                    logBeanArrayList.add(logBean);
                    writeLogAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                runOnUiThread(() -> {
                    ToastUtils.showShort(t.getMessage()+" - " + SendData+" - " + RevData);
                    tvWriteResult.setText(t.getMessage());
                });
                Log.e("pxw=====", "失败==ReadTag==" + t.getMessage());
            }
        }));
    }

    /**
     * 写标签-写
     */
    private void doWriteData() {
        String tagPwd = edWriteTagPwd.getText().toString();
        String tagPtr = edWriteTagPtr.getText().toString();
        String tagCount =Integer.toHexString(Integer.parseInt(edWriteTagCount.getText().toString())) ;
        String tagInput = etUserInput.getText().toString();
        if (TextUtils.isEmpty(tagPwd)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_password));
            return;
        }

        if (TextUtils.isEmpty(tagPtr)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_start_address));
            return;
        }

        if (TextUtils.isEmpty(tagCount)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_data_length));
            return;
        }

        if (TextUtils.isEmpty(tagInput)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_write_label));
            return;
        }

        long time = System.currentTimeMillis();
        //addLog(log_display_tv, DateUtils.transformNormal(time).trim(), 2);

        TagParam param = new TagParam();
        tagPwd = tagPwd.replaceAll(" ", "");
        param.SetAccPwd(HexUtils.hexString2ByteArray(tagPwd));//密码（HEX）
        param.SetMemBank(HexUtils.hexString2Byte(selectMemBank));//读写区域 0x00：Reserved  --0x01：EPC--  0x02：TID--  0x03：User

        String hexPtr = HexUtils.leftZeroShift(Integer.toHexString(Integer.parseInt(tagPtr)), 4);
        param.SetWordPtr(HexUtils.hexString2ByteArray(hexPtr));//起始地址
        param.SetWordCount(HexUtils.hexString2Byte(tagCount));//数据长度

        int inputLen = Integer.valueOf(tagCount,16);
        tagInput = tagInput.replaceAll(" ", "");
        tagInput = HexUtils.rightZeroShift(tagInput, inputLen*4);
        param.SetData(HexUtils.hexString2ByteArray(tagInput));//写入标签的数据
        //Log.e("pxw=====", tagInput + "发送数据==WriteTag==" + FormatUtil.bytesToHexString(HexUtils.hexString2ByteArray(tagInput)));

        runOnUiThread(() -> CFBLEManager.GetInstance().WriteTag(param, new IOperateTagCallback() {
            @Override
            public void OnData(int status, OperateTag tag, byte[] result) {
                runOnUiThread(() -> {
                    Log.e("pxw=====", "成功==WriteTag==" + tag.GetWordCount());
                    //ToastUtils.showShort("Sucess!-" + status + " " + FormatUtil.bytesToHexString(result));
                    tvWriteResult.setText(FormatUtil.bytesToHexString(tag.GetData()));
                    LogBean logBean = new LogBean(FormatUtil.bytesToHexString(tag.GetPC()),
                            FormatUtil.bytesToHexString(tag.GetCRC()),
                            FormatUtil.bytesToHexString(tag.GetEPCCode()),
                            String.valueOf(tag.GetEPCLen()),
                            FormatUtil.bytesToHexString(tag.GetData()),
                            String.valueOf(tag.GetAntenna()),
                            String.valueOf(tag.GetWordCount()));

                    logBeanArrayList.add(logBean);
                    writeLogAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                runOnUiThread(() -> {
                    ToastUtils.showShort(t.getMessage());
                    tvWriteResult.setText(t.getMessage());
                });
                Log.e("pxw=====", "失败==WriteTag==" + t.getMessage());
            }
        }));
    }

    /**
     * 锁标签
     */
    private void doLockTagLock() {
        if (selectLockArea == -1) {
            ToastUtils.showShort(getString(R.string.app_text_tip_select_locked_area));
            return;
        }
        if (selectLockAction == -1) {
            ToastUtils.showShort(getString(R.string.app_text_tip_select_lock_operation));
            return;
        }

        String tagPwd = edLockTagPwd.getText().toString();
        if (TextUtils.isEmpty(tagPwd)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_password));
            return;
        }

        long time = System.currentTimeMillis();
        int logType = 4;
        //3-锁标签开放 4-锁标签锁定 5-锁标签永久开放  6-锁标签永久锁定
        if (selectLockAction == 0) {
            logType = 3;
        } else if (selectLockAction == 1) {
            logType = 5;
        } else if (selectLockAction == 3) {
            logType = 6;
        }
        //addLog(logLockTv, DateUtils.transformNormal(time).trim(), logType);

        TagParam param = new TagParam();
        tagPwd = tagPwd.replaceAll(" ", "");
        param.SetAccPwd(HexUtils.hexString2ByteArray(tagPwd));//密码（HEX）
        param.SetArea((byte) selectLockArea);
        param.SetAction((byte) selectLockAction);

        runOnUiThread(() -> CFBLEManager.GetInstance().LockTag(param, new IOperateTagCallback() {
            @Override
            public void OnData(int status, OperateTag tag, byte[] result) {
                runOnUiThread(() -> {
                    Log.e("pxw=====", "成功==doLockTagLock==" + tag.GetWordCount());
                    //ToastUtils.showShort("Sucess!-" + status + " " + FormatUtil.bytesToHexString(result));
                    tvLockResult.setText(FormatUtil.bytesToHexString(tag.GetData()));
                    LogBean logBean = new LogBean(FormatUtil.bytesToHexString(tag.GetPC()),
                            FormatUtil.bytesToHexString(tag.GetCRC()),
                            FormatUtil.bytesToHexString(tag.GetEPCCode()),
                            String.valueOf(tag.GetEPCLen()),
                            FormatUtil.bytesToHexString(tag.GetData()),
                            String.valueOf(tag.GetAntenna()),
                            String.valueOf(tag.GetWordCount()));

                    logBeanArrayList.add(logBean);
                    lockLogAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                runOnUiThread(() -> {
                    ToastUtils.showShort(t.getMessage());
                    tvLockResult.setText(t.getMessage());
                });
            }
        }));
    }

    /**
     * 灭活
     */
    private void doKillTagKill() {
        String killHex = edKillTagPwd.getText().toString();
        if (TextUtils.isEmpty(killHex)) {
            ToastUtils.showShort(getString(R.string.app_text_tip_enter_the_kill_password));
            return;
        }

        long time = System.currentTimeMillis();
        //addLog(logKillTv, DateUtils.transformNormal(time).trim(), 7);

        TagParam param = new TagParam();
        killHex = killHex.replaceAll(" ", "");
        param.SetKillPwd(HexUtils.hexString2ByteArray(killHex));

        runOnUiThread(() -> CFBLEManager.GetInstance().KillTag(param, new IOperateTagCallback() {
            @Override
            public void OnData(int status, OperateTag tag, byte[] result) {
                runOnUiThread(() -> {
                    Log.e("pxw=====", "成功==doKillTagKill==" + tag.GetWordCount());
                    //ToastUtils.showShort("Sucess!-" + status + " " + FormatUtil.bytesToHexString(result));
                    tvKillResult.setText(FormatUtil.bytesToHexString(tag.GetData()));
                    LogBean logBean = new LogBean(FormatUtil.bytesToHexString(tag.GetPC()),
                            FormatUtil.bytesToHexString(tag.GetCRC()),
                            FormatUtil.bytesToHexString(tag.GetEPCCode()),
                            String.valueOf(tag.GetEPCLen()),
                            FormatUtil.bytesToHexString(tag.GetData()),
                            String.valueOf(tag.GetAntenna()),
                            String.valueOf(tag.GetWordCount()));

                    logBeanArrayList.add(logBean);
                    killLogAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                runOnUiThread(() -> {
                    ToastUtils.showShort(t.getMessage());
                    tvKillResult.setText(t.getMessage());
                });
            }
        }));
    }

    /**
     * 操作日志
     * @param textView
     * @param content
     * @param type 1-写标签读取 2-写标签写入 3-锁标签开放 4-锁标签锁定 5-锁标签永久开放  6-锁标签永久锁定 7-灭活标签灭活
     */
    public void addLog(TextView textView, String content, int type){
        textView.append("\t" + content);
        if (type == 1) {
            textView.append("\t读取成功......");
        } else if (type == 2) {
            textView.append("\t写入成功......");
        } else if (type == 3) {
            textView.append("\t开放成功......");
        } else if (type == 4) {
            textView.append("\t锁定成功......");
        } else if (type == 5) {
            textView.append("\t永久开放成功......");
        } else if (type == 6) {
            textView.append("\t永久锁定成功......");
        } else if (type == 7) {
            textView.append("\t灭活成功......");
        }
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    /**
     * 选择标签
     * @param type
     */
    private void doRGWrite(String type) {
        //E2 00 47 15 68 30 60 23 63 2B 01 08
        String sendHex = "E2 " + type + " 47 15 68 30 60 23 63 2B 01 08";
        CFBLEManager.GetInstance().SelectTag(sendHex, new ISelectTagCallback() {
            @Override
            public void OnData(int status, byte[] result) {
                ToastUtils.showShort("Success!-" + status + " " + FormatUtil.bytesToHexString(result));
                Log.e("pxw=====", "成功==doRGWrite==" + type);
            }

            @Override
            public void OnError(Throwable t, String SendData, String RevData) {
                ToastUtils.showShort(getString(R.string.app_text_tip_operation_failed));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
}