package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

public class RedPacketAttachment extends CustomAttachment {

    private String content;//  消息文本内容
    private String redPacketId;//  红包id
    private String title;// 红包名称
    private boolean isDrawed;// 红包被领取完了

    private static final String KEY_CONTENT = "content";
    private static final String KEY_ID = "redPacketId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ISDRAWED = "isDrawed";

    public RedPacketAttachment() {
        super(CustomAttachmentType.RedPacket);
    }

    @Override
    protected void parseData(JSONObject data) {
        content = data.getString(KEY_CONTENT);
        redPacketId = data.getString(KEY_ID);
        title = data.getString(KEY_TITLE);
        isDrawed = data.getBoolean(KEY_ISDRAWED);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_CONTENT, content);
        data.put(KEY_ID, redPacketId);
        data.put(KEY_TITLE, title);
        data.put(KEY_ISDRAWED, isDrawed);
        return data;
    }

    public String getRpContent() {
        return content;
    }

    public String getRpId() {
        return redPacketId;
    }

    public String getRpTitle() {
        return title;
    }


    public void setRpContent(String content) {
        this.content = content;
    }

    public void setRpId(String briberyID) {
        this.redPacketId = briberyID;
    }

    public void setRpTitle(String briberyName) {
        this.title = briberyName;
    }

    public boolean isDrawed() {
        return isDrawed;
    }

    public void setDrawed(boolean drawed) {
        isDrawed = drawed;
    }
}
