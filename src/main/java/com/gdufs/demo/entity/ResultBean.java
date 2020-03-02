package com.gdufs.demo.entity;

import java.util.Collection;

public class ResultBean<T> {
    private int status;
    private String msg;
    private Collection<T> data;

    public static ResultBean error(int status, String msg) {
        ResultBean resultBean = new ResultBean();
        resultBean.setStatus(status);
        resultBean.setMsg(msg);
        return resultBean;
    }

    public static ResultBean success() {
        ResultBean resultBean = new ResultBean();
        resultBean.setStatus(200);
        resultBean.setMsg("success");
        return resultBean;
    }

    public static <V> ResultBean<V> success(Collection<V> data) {
        ResultBean resultBean = new ResultBean();
        resultBean.setStatus(200);
        resultBean.setMsg("success");
        resultBean.setData(data);
        return resultBean;
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

    public Collection<T> getData() {
        return data;
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }
}
