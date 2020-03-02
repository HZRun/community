package com.gdufs.demo.entity;

public class ResultMap {
    private int status;
    private String msg;

    public static ResultMap success() {
        ResultMap resultMap = new ResultMap();
        resultMap.setMsg("success");
        resultMap.setStatus(200);
        return resultMap;
    }

    public static ResultMap success(int status, String msg) {
        ResultMap resultMap = new ResultMap();
        resultMap.setMsg(msg);
        resultMap.setStatus(status);
        return resultMap;
    }

    public static ResultMap error(int status, String msg) {
        ResultMap resultMap = new ResultMap();
        resultMap.setMsg(msg);
        resultMap.setStatus(status);
        return resultMap;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
