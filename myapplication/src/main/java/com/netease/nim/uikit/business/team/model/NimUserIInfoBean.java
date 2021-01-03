package com.netease.nim.uikit.business.team.model;

import java.io.Serializable;

/*
 *
 * @author： tanhuohui
 * @date:   2020/3/15
 *
 */
public class NimUserIInfoBean implements Serializable {
    public int id;
    public String user_name;
    public String phone;
    public String headimgurl;
    public int integral;
    public String signature;
    public int sex;
    public String birth;
    public String email;
    public String mobile;
    public int oauth;
    public String user_level;
    public int shop_id;
    public String easemob_username;
    public String easemob_password;
    public String easemob_nickname;
    public int is_buy_community;
    public int is_sign;//是否完善信息
    public int be_attentions;//被关注数量
    public int has_attentions;//关注他人的数据
    public int has_praises;//获赞数
    public int is_attention;//用户是否关注了该成员
    public int is_friend;//用户是否加了好友
}
