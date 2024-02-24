package com.jhc.ble.entity;

import com.jhc.ble.bean.QueryBean;

import java.util.ArrayList;

/**
 * @author：Chao
 * @date：2023/8/24
 */
public class GroupEntity {

    private String header;

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
    private String footer;
    private ArrayList<QueryBean> children;

    public GroupEntity(String header, String footer, ArrayList<QueryBean> children) {
        this.header = header;
        this.footer = footer;
        this.children = children;
    }

    public GroupEntity(String header, String no, String rssi, String antenna, String channel, String epcData, String epcNum, String footer, ArrayList<QueryBean> children) {
        this.header = header;
        this.no = no;
        this.rssi = rssi;
        this.antenna = antenna;
        this.channel = channel;
        this.epcData = epcData;
        this.epcNum = epcNum;
        this.footer = footer;
        this.children = children;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public ArrayList<QueryBean> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<QueryBean> children) {
        this.children = children;
    }
}
