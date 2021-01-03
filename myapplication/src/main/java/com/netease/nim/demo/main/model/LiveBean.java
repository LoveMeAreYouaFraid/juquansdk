package com.netease.nim.demo.main.model;

import java.io.Serializable;

/**
 * Created by maojunfeng on 2020-01-04.
 * Description:
 */
public class LiveBean implements Serializable {
    private static final long serialVersionUID = 3391960281619417896L;
    /**
     * id : 1
     * shop_id : 1
     * status : -1
     * room : 11012
     * title : 哈哈
     * issincerity : -1
     * cover : http://api2.hcjuquan.com/uploads/find_pic/20191111/84137faad0391aa986f180c5f0b84e17.png
     * comment : 0
     * like_number : 0
     * user_id : 191
     * addressitem : http://pili-live-hls.live.hcjuquan.com/juquanzhibo/11012.m3u8
     * addressitem_rtmp : rtmp://pili-live-rtmp.live.hcjuquan.com/juquanzhibo/11012
     * addressitem_flv : http://pili-live-hdl.live.hcjuquan.com/juquanzhibo/11012.flv
     * shop_logo : https://live.sayyin.com/static/admin/img/logo.jpg
     * goods : {"goods_name":"泡泡法代 SANDRO 19春夏 V领长袖连衣短裤连体裤休闲裤P6591E","keywords":"连衣裙连衣裙","thumb_url":"https://live.sayyin.comuploads/goods/20190418/4d047642225bd650fbc90cd01e7c29fa.jpg","market_price":"2000.00"}
     */

    private int id;
    private int shop_id;
    private int status;
    private String room;
    private String roomId;
    private String title;
    private String notice;
    private int cid;
    private int issincerity;
    private String cover;
    private int comment;
    private int like_number;
    private int peoplenum;
    private int user_id;
    private String addressitem;
    private String addressitem_rtmp;
    private String addressitem_rtmp_pull;
    private String addressitem_flv;
    private String shop_logo;
    private GoodsBean goods;
    private String chatroom;
    private String province = "";
    private String city = "";
    private String headimgurl = "";
    private String user_name = "";
    private String record_id;
    //推广
    public String order_num;
    public String total_price;
    public String credit_reduce;
    public String addtime;
    public int is_spread;
    public int position;
    public int anchor_level;//1-普通主播 2-创客讲课主播

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getIssincerity() {
        return issincerity;
    }

    public void setIssincerity(int issincerity) {
        this.issincerity = issincerity;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getLike_number() {
        return like_number;
    }

    public void setLike_number(int like_number) {
        this.like_number = like_number;
    }

    public int getPeoplenum() {
        return peoplenum;
    }

    public void setPeoplenum(int peoplenum) {
        this.peoplenum = peoplenum;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAddressitem() {
        return addressitem;
    }

    public void setAddressitem(String addressitem) {
        this.addressitem = addressitem;
    }

    public String getAddressitem_rtmp() {
        return addressitem_rtmp;
    }

    public void setAddressitem_rtmp(String addressitem_rtmp) {
        this.addressitem_rtmp = addressitem_rtmp;
    }

    public String getAddressitem_rtmp_pull() {
        return addressitem_rtmp_pull;
    }

    public void setAddressitem_rtmp_pull(String addressitem_rtmp_pull) {
        this.addressitem_rtmp_pull = addressitem_rtmp_pull;
    }

    public String getAddressitem_flv() {
        return addressitem_flv;
    }

    public void setAddressitem_flv(String addressitem_flv) {
        this.addressitem_flv = addressitem_flv;
    }

    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }

    public String getShop_logo() {
        return shop_logo;
    }

    public void setShop_logo(String shop_logo) {
        this.shop_logo = shop_logo;
    }

    public GoodsBean getGoods() {
        return goods;
    }

    public void setGoods(GoodsBean goods) {
        this.goods = goods;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public static class GoodsBean implements Serializable {
        private static final long serialVersionUID = -4868041388201035388L;
        /**
         * goods_name : 泡泡法代 SANDRO 19春夏 V领长袖连衣短裤连体裤休闲裤P6591E
         * keywords : 连衣裙连衣裙
         * thumb_url : https://live.sayyin.comuploads/goods/20190418/4d047642225bd650fbc90cd01e7c29fa.jpg
         * market_price : 2000.00
         */

        private String goods_name;
        private String keywords;
        private String thumb_url;
        private String market_price;

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getThumb_url() {
            return thumb_url;
        }

        public void setThumb_url(String thumb_url) {
            this.thumb_url = thumb_url;
        }

        public String getMarket_price() {
            return market_price;
        }

        public void setMarket_price(String market_price) {
            this.market_price = market_price;
        }
    }
}
