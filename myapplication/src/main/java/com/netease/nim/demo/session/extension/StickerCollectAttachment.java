package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * 收藏表情，添加表情
 */
public class StickerCollectAttachment extends CustomAttachment {

    public String emoji_name;//  表情名称
    public String emoji_id;//  表情id
    public String emoji_link;// 表情链接

    private static final String EMOJI_NAME = "emoji_name";
    private static final String EMOJI_ID = "emoji_id";
    private static final String EMOJI_LINK = "emoji_link";

    public StickerCollectAttachment() {
        super(CustomAttachmentType.EMOJI);
    }
    public StickerCollectAttachment(String emoji_name, String emoji_id,String emoji_link) {
        this();
        this.emoji_name = emoji_name;
        this.emoji_id = emoji_id;
        this.emoji_link = emoji_link;
    }


    @Override
    protected void parseData(JSONObject data) {

        emoji_name = data.getString(EMOJI_NAME);
        emoji_id = data.getString(EMOJI_ID);
        emoji_link = data.getString(EMOJI_LINK);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(EMOJI_NAME, emoji_name);
        data.put(EMOJI_ID, emoji_id);
        data.put(EMOJI_LINK, emoji_link);
        return data;
    }


}
