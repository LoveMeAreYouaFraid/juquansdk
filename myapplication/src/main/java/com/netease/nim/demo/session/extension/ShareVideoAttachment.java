package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.demo.main.model.LiveBean;
import com.netease.nim.demo.main.model.VideoBean;

/**
 * Description: 短视频分享
 */
public class ShareVideoAttachment extends CustomAttachment {

    private VideoBean data;
    private String videoUrl;
    private String headImgStr;
    private String nameStr;
    public ShareVideoAttachment() {
        super(CustomAttachmentType.Videos);
    }

    @Override
    protected void parseData(JSONObject data) {
        this.data = JSON.parseObject(data.getString("videoDic"), VideoBean.class);
        this.videoUrl = data.getString("videoUrl");
        this.headImgStr = data.getString("headImgStr");
        this.nameStr = data.getString("nameStr");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("videoDic", JSON.toJSONString(this.data));
        data.put("videoUrl", videoUrl);
        data.put("headImgStr", headImgStr);
        data.put("nameStr", nameStr);
        return data;
    }

    public VideoBean getData() {
        return data;
    }

    public void setData(VideoBean data) {
        this.data = data;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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
}
