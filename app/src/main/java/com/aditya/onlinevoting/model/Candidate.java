package com.aditya.onlinevoting.model;

public class Candidate {
    String name;
    String detail;
    String userid;
    String parent;

    public Candidate(){

    }

    public Candidate(String name, String detail,String userid,String parent) {
        this.name = name;
        this.detail = detail;
        this.userid = userid;
        this.parent = parent;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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
}
