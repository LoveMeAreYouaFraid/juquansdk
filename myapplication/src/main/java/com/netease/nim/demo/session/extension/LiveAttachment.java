package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.demo.main.model.LiveBean;

/**
 * Created by maojunfeng on 2020-01-04.
 * Description:
 */
public class LiveAttachment extends CustomAttachment {

    private LiveBean data;
    private String addressitem_rtmp;
    private String coverStr;
    private String headImgStr;
    private String nameStr;
    private String roomId;
    private String shareType;
    private String record_id;


    public LiveAttachment() {
        super(CustomAttachmentType.Live);
    }

    @Override
    protected void parseData(JSONObject data) {
       // System.out.println("-----直播数据="+data.getString("liveDic"));
        this.data = JSON.parseObject(data.getString("liveDic"), LiveBean.class);
        this.addressitem_rtmp = data.getString("addressitem_rtmp");
        this.coverStr = data.getString("coverStr");
        this.headImgStr = data.getString("headImgStr");
        this.nameStr = data.getString("nameStr");
        this.roomId = data.getString("roomId");
        this.shareType = data.getString("shareType");
        this.record_id = data.getString("record_id");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("liveDic", JSON.toJSONString(this.data));
        data.put("addressitem_rtmp", addressitem_rtmp);
        data.put("coverStr", coverStr);
        data.put("headImgStr", headImgStr);
        data.put("nameStr", nameStr);
        data.put("roomId", roomId);
        data.put("shareType", shareType);
        data.put("record_id", record_id);
        return data;
    }

    public LiveBean getData() {
        return data;
    }

    public void setData(LiveBean data) {
        this.data = data;
    }

    public String getAddressitem_rtmp() {
        return addressitem_rtmp;
    }

    public void setAddressitem_rtmp(String addressitem_rtmp) {
        this.addressitem_rtmp = addressitem_rtmp;
    }

    public String getCoverStr() {
        return coverStr;
    }

    public void setCoverStr(String coverStr) {
        this.coverStr = coverStr;
    }

    public String getHeadImgStr() {
        return headImgStr;
    }

    public void setHeadImgStr(String headImgStr) {
        this.headImgStr = headImgStr;
    }

    public String getNameStr() {
        return nameStr;
    }

    public void setNameStr(String nameStr) {
        this.nameStr = nameStr;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }
}
