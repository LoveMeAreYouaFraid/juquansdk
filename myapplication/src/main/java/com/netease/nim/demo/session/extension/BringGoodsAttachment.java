package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.demo.main.model.LiveBean;

public class BringGoodsAttachment extends CustomAttachment {

    private String shopUrl;//  商品链接
    private String shopName;//  商品名称
    private String userName;// 用户名
    private String operationType;// 类型：1：购买状态，2商品展示
    private String stateStr;// 商品购买状态1：加入购物车，2：正在购买 3；购买成功
    private String moneyStr;// 商品价格
    private String shopID;// 商品id
    public BringGoodsAttachment() {
        super(CustomAttachmentType.BringGoods);
    }

    @Override
    protected void parseData(JSONObject data) {
        this.shopUrl = data.getString("shopUrl");
        this.shopName = data.getString("shopName");
        this.userName = data.getString("userName");
        this.operationType = data.getString("operationType");
        this.stateStr = data.getString("stateStr");
        this.moneyStr = data.getString("moneyStr");
        this.shopID = data.getString("shopID");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("shopUrl", shopUrl);
        data.put("shopName", shopName);
        data.put("userName", userName);
        data.put("operationType", operationType);
        data.put("stateStr", stateStr);
        data.put("moneyStr", moneyStr);
        data.put("shopID", shopID);
        return data;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getStateStr() {
        return stateStr;
    }

    public void setStateStr(String stateStr) {
        this.stateStr = stateStr;
    }

    public String getMoneyStr() {
        return moneyStr;
    }

    public void setMoneyStr(String moneyStr) {
        this.moneyStr = moneyStr;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }
}
