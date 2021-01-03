package com.netease.nim.uikit.business.team.model;

import java.io.Serializable;

/**
 * 群信息
 */
public class NimGroupBean implements Serializable {

    /**
     * id : 909
     * group_name : G1578759927738
     * group_image : http://img.hcjuquan.com/a7e7e202001120025277573.jpg
     * cateid : 2
     * information :
     * yunxin_id : 2749140488
     * owner : 18955811803
     * latlng : 39.9082878062,116.3973999023
     * people_num : 1
     *
     *     "addtime": "1576116510",
     *     "is_pay": 1,
     *     "paytime": "1576116534",
     *     "province": 17,
     *     "city": null,
     *     "area": null,
     *     "announcement": null,
     *     "classify_id": 1,
     *     "joinmode": 0,
     *     "classify_name": "其他"
     */


    public int id;
    public String group_name;
    public String group_image;
    public int cateid;
    public String information;
    public String yunxin_id;
    public String owner;
    public String latlng;
    public int people_num;

    public String addtime;
    public int is_pay;
    public String paytime;
    public String city;
    public String province;
    public String area;
    public int classify_id;
    public String announcement;
    public int joinmode;
    public String classify_name;
    public int play_alive;//【1为正在直播，0为无】
    public int room_id;//大于0为房间号，0为无
    public int alive_peoplenum;//观看人数
    public String headimgurl;//群主头像


}
