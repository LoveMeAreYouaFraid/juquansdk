package com.netease.nim.uikit.business.session.emoji;

import java.io.Serializable;

public class StickerItem implements Serializable {
    public String category;//类别名
    public String name;
    public String stickid;
    public StickerItem(String category, String name) {
        this.category = category;
        this.name = name;
    }
    public StickerItem(String category, String name,String stickid) {
        this.category = category;
        this.name = name;
        this.stickid = stickid;
    }

    public String getIdentifier() {
        return category + "/" + name;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof StickerItem) {
            StickerItem item = (StickerItem) o;
            return item.getCategory().equals(category) && item.getName().equals(name);
        }

        return false;
    }
}
