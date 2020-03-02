package com.gdufs.demo.entity;

public class TemplateData {    //keyword1：订单类型，keyword2：下单金额，keyword3：配送地址，keyword4：取件地址，keyword5备注

    public TemplateData(String value) {
        this.value = value;
    }

    private String value;
    //,,依次排下去//    private String color;//字段颜色（微信官方已废弃，设置没有效果）}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
