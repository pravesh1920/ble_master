package com.jhc.ble.entity;

import com.jhc.ble.bean.LogBean;

import java.util.ArrayList;

/**
 * @author：Chao
 * @date：2023/8/24
 */
public class LogEntity {
    private String header;

    private String pc;
    private String crc;
    private String epc;
    private String pecLen;
    private String data;
    private String ant;
    private String num;

    private String footer;
    private ArrayList<LogBean> children;

    public LogEntity(String header, String footer, ArrayList<LogBean> children) {
        this.header = header;
        this.footer = footer;
        this.children = children;
    }

    public LogEntity(String header, String pc, String crc, String epc, String pecLen, String data, String ant, String num, String footer, ArrayList<LogBean> children) {
        this.header = header;
        this.pc = pc;
        this.crc = crc;
        this.epc = epc;
        this.pecLen = pecLen;
        this.data = data;
        this.ant = ant;
        this.num = num;
        this.footer = footer;
        this.children = children;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getPecLen() {
        return pecLen;
    }

    public void setPecLen(String pecLen) {
        this.pecLen = pecLen;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAnt() {
        return ant;
    }

    public void setAnt(String ant) {
        this.ant = ant;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public ArrayList<LogBean> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<LogBean> children) {
        this.children = children;
    }
}
