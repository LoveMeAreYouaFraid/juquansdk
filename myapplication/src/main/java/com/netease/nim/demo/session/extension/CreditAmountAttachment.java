package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Description: 抵用金转账
 */
public class CreditAmountAttachment extends CustomAttachment {


    private String transfer_amount;
    private String transfer_username;
    private String transfer_userId;
    private String transfer_easemob_username;
    public CreditAmountAttachment() {
        super(CustomAttachmentType.CreditTrans);
    }

    @Override
    protected void parseData(JSONObject data) {
       // System.out.println("-----直播数据="+data.getString("liveDic"));
     //   this.data = JSON.parseObject(data.getString("liveDic"), ProductBean.class);
        this.transfer_amount = data.getString("transfer_amount");
        this.transfer_username = data.getString("transfer_username");
        this.transfer_userId = data.getString("transfer_userId");
        this.transfer_easemob_username = data.getString("transfer_easemob_username");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
     //   data.put("liveDic", JSON.toJSONString(this.data));
        data.put("transfer_amount", transfer_amount);
        data.put("transfer_username", transfer_username);
        data.put("transfer_userId", transfer_userId);
        data.put("transfer_easemob_username", transfer_easemob_username);
        return data;
    }

    public String getTransfer_amount() {
        return transfer_amount;
    }

    public void setTransfer_amount(String transfer_amount) {
        this.transfer_amount = transfer_amount;
    }

    public String getTransfer_username() {
        return transfer_username;
    }

    public void setTransfer_username(String transfer_username) {
        this.transfer_username = transfer_username;
    }

    public String getTransfer_userId() {
        return transfer_userId;
    }

    public void setTransfer_userId(String transfer_userId) {
        this.transfer_userId = transfer_userId;
    }

    public String getTransfer_easemob_username() {
        return transfer_easemob_username;
    }

    public void setTransfer_easemob_username(String transfer_easemob_username) {
        this.transfer_easemob_username = transfer_easemob_username;
    }
}
