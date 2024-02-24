package com.jhc.ble.bean;

/**
 * @author：Chao
 * @date：2023/9/4
 */
public class LogBean {
    private String pc;
    private String crc;
    private String epc;
    private String pecLen;
    private String data;
    private String ant;
    private String num;

    public LogBean(String pc, String crc, String epc, String pecLen, String data, String ant, String num) {
        this.pc = pc;
        this.crc = crc;
        this.epc = epc;
        this.pecLen = pecLen;
        this.data = data;
        this.ant = ant;
        this.num = num;
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
}
