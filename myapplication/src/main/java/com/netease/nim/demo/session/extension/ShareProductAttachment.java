package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.demo.main.model.LiveBean;
import com.netease.nim.demo.main.model.ProductBean;

/**
 * Description: 商品分享推广
 */
public class ShareProductAttachment extends CustomAttachment {

//    private ProductBean data;
//    private String addressitem_rtmp;
//    private String coverStr;
//    private String headImgStr;
//    private String nameStr;
//    private String roomId;
//    private String shareType;
//    private String record_id;
    private String goods_id;
    private String goods_title;
    private String goods_thumb_url;
    private String goods_price;
    private String share_token;
    public ShareProductAttachment() {
        super(CustomAttachmentType.Product);
    }

    @Override
    protected void parseData(JSONObject data) {
       // System.out.println("-----直播数据="+data.getString("liveDic"));
     //   this.data = JSON.parseObject(data.getString("liveDic"), ProductBean.class);
        this.goods_id = data.getString("goods_id");
        this.goods_title = data.getString("goods_title");
        this.goods_thumb_url = data.getString("goods_thumb_url");
        this.goods_price = data.getString("goods_price");
        this.share_token = data.getString("share_token");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
     //   data.put("liveDic", JSON.toJSONString(this.data));
        data.put("goods_id", goods_id);
        data.put("goods_title", goods_title);
        data.put("goods_thumb_url", goods_thumb_url);
        data.put("goods_price", goods_price);
        data.put("share_token", share_token);
        return data;
    }

//    public ProductBean getData() {
//        return data;
//    }
//
//    public void setData(ProductBean data) {
//        this.data = data;
//    }


    public String getShare_token() {
        return share_token;
    }

    public void setShare_token(String share_token) {
        this.share_token = share_token;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_title() {
        return goods_title;
    }

    public void setGoods_title(String goods_title) {
        this.goods_title = goods_title;
    }

    public String getGoods_thumb_url() {
        return goods_thumb_url;
    }

    public void setGoods_thumb_url(String goods_thumb_url) {
        this.goods_thumb_url = goods_thumb_url;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }
}
