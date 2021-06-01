package com.aditya.onlinevoting.model;

public class Poll {
    String name;
    String detail;
    String close;
    String userid;
    String result;

    public Poll(){

    }

    public Poll(String name, String detail, String close, String userid,String result) {
        this.name = name;
        this.detail = detail;
        this.close = close;
        this.userid = userid;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }
}
