package com.netease.nim.demo.main.model;


import java.io.Serializable;
import java.util.List;

/**
 * 作者: dingxn
 * 创建时间：2019-10-31 22:36
 * 修改时间：(from 2019-10-31)
 * 邮箱： 757125222@qq.com
 * 说明:
 */
public class VideoBean implements Serializable {


    /**
     * id : 9
     * title : 测试短视频1
     * desc : 短视频1描述短视频1描述短视频1描述短视频1描述短视频1描述短视频1描述
     * media : https://live.sayyin.com/uploads/find_pic/20190927/9607fe342a0a141b3444649e3e1b6a1c.jpg
     * user_id : 187
     * good_num : 0
     * discuss_num : 0
     * latlng : 39.165165161,106.323213535
     * addtime : 2019-10-18 15:42:38
     * checked : 1
     * checktime : 1571384234
     * member_info : {"user_name":"名字","phone":"18983181366","headimgurl":"https://live.sayyin.com/uploads/find_pic/20190925/536bdb9bf6877dea11b3540d137a1b73.png","shop_id":0}
     * lat : 39.165165161
     * lng : 106.323213535
     * attention : 3
     */

    public int id;
    public String title;
    public String desc;
    public String media;
    public int user_id;
    public int good_num;
    public int share_num;//分享数量
    public int discuss_num;
    public String latlng;
    public String addtime;
    public int checked;
    public String checktime;
    public MemberInfoBean member_info;
    public String lat;
    public String lng;
    public int attention;
    public int goodlike;
    public int is_charge;
    public String price;

    public int isclose;
    public String remarks;
    public int is_pay;
    public int is_bring;
    public int goods_id;
    public List<GoodsBean> goods;
    public VipGroupBean vip_group;
    public int play_alive;//【1为正在直播，0为无】
    public int room_id;//大于0为房间号，0为无
    public int alive_peoplenum;//观看人数

    public static class GoodsBean implements Serializable {
        public int id;
        public String goods_name;
        public String thumb_url;
        public String market_price;
        public String shop_price;
        public int onsale;
        public int sale_num;
        public int is_recycle;
        public String goods_number;
    }

    public int getIs_charge() {
        return is_charge;
    }

    public void setIs_charge(int is_charge) {
        this.is_charge = is_charge;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getGood_num() {
        return good_num;
    }

    public void setGood_num(int good_num) {
        this.good_num = good_num;
    }

    public int getShare_num() {
        return share_num;
    }

    public void setShare_num(int share_num) {
        this.share_num = share_num;
    }

    public int getDiscuss_num() {
        return discuss_num;
    }

    public void setDiscuss_num(int discuss_num) {
        this.discuss_num = discuss_num;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public String getChecktime() {
        return checktime;
    }

    public void setChecktime(String checktime) {
        this.checktime = checktime;
    }

    public MemberInfoBean getMember_info() {
        return member_info;
    }

    public void setMember_info(MemberInfoBean member_info) {
        this.member_info = member_info;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public int getGoodlike() {
        return goodlike;
    }

    public void setGoodlike(int goodlike) {
        this.goodlike = goodlike;
    }

    public static class MemberInfoBean implements Serializable {
        /**
         * user_name : 名字
         * phone : 18983181366
         * headimgurl : https://live.sayyin.com/uploads/find_pic/20190925/536bdb9bf6877dea11b3540d137a1b73.png
         * shop_id : 0
         */

        private String user_name;
        private String phone;
        private String headimgurl;
        private int shop_id;
        public String easemob_username;


        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }
    }
}
