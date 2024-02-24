package com.jhc.ble.bean;

/**
 * @author：Chao
 * @date：2023/8/21
 */
public class QueryBean {
    /**
     * 序号
     */
    private String no;
    /**
     * RSSI
     */
    private String rssi;
    /**
     * 天线
     */
    private String antenna;
    /**
     * 信道
     */
    private String channel;
    /**
     * Data
     */
    private String epcData;
    /**
     * 数量
     */
    private String epcNum;

    private boolean isChecked;

    public QueryBean(String no, String epcData, String epcNum, String rssi, String antenna, String channel, boolean isChecked) {
        this.no = no;
        this.rssi = rssi;
        this.antenna = antenna;
        this.channel = channel;
        this.epcData = epcData;
        this.epcNum = epcNum;
        this.isChecked = isChecked;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getAntenna() {
        return antenna;
    }

    public void setAntenna(String antenna) {
        this.antenna = antenna;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEpcData() {
        return epcData;
    }

    public void setEpcData(String epcData) {
        this.epcData = epcData;
    }

    public String getEpcNum() {
        return epcNum;
    }

    public void setEpcNum(String epcNum) {
        this.epcNum = epcNum;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
