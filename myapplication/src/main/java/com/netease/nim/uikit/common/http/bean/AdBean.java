package com.netease.nim.uikit.common.http.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class AdBean {
    private int id;
    private String title;
    private String content;
    private String user_name;
    private int type;//0 图文 1 视频
    private String media;
    private String account;
    private String easemob_username;
    private String group_id;
    private String user_id;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEasemob_username() {
        return easemob_username;
    }

    public void setEasemob_username(String easemob_username) {
        this.easemob_username = easemob_username;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public List<String> getMedias() {
        return JSON.parseArray(media, String.class);
    }
}
