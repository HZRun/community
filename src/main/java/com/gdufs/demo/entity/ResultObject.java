package com.gdufs.demo.entity;


public class ResultObject {
    private int status;
    private String msg;
    private Object data;

    public static ResultObject error(int status, String msg) {
        ResultObject resultObject = new ResultObject();
        resultObject.setStatus(status);
        resultObject.setMsg(msg);
        return resultObject;
    }

    public static ResultObject success() {
        ResultObject resultObject = new ResultObject();
        resultObject.setStatus(200);
        resultObject.setMsg("success");
        return resultObject;
    }

    public static ResultObject success(int status, String msg) {
        ResultObject resultObject = new ResultObject();
        resultObject.setStatus(status);
        resultObject.setMsg(msg);
        return resultObject;
    }

    public static ResultObject success(Object data) {
        ResultObject resultObject = new ResultObject();
        resultObject.setStatus(200);
        resultObject.setMsg("success");
        resultObject.setData(data);
        return resultObject;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
