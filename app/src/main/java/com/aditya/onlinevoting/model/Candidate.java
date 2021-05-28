package com.aditya.onlinevoting.model;

public class Candidate {
    String name;
    String detail;

    public Candidate(){

    }

    public Candidate(String name, String detail) {
        this.name = name;
        this.detail = detail;
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
