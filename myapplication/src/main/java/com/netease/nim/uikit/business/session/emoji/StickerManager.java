package com.netease.nim.uikit.business.session.emoji;

import android.content.res.AssetManager;
import android.util.Log;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.util.file.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 贴图管理类
 */
public class StickerManager {
    private static final String TAG = "StickerManager";

    private static StickerManager instance;
    private static final String CATEGORY_AJMD = "ajmd";
    private static final String CATEGORY_XXY = "xxy";
    private static final String CATEGORY_LT = "lt";
    private static final String CATEGORY_QBHD = "qbhd";
    private static final String CATEGORY_GGXM = "ggxm";
    private static final String CATEGORY_SDQ = "sdq";
    private static final String CATEGORY_COLLECTION = "collection";
    /**
     * 数据源
     */
    private List<StickerCategory> stickerCategories = new ArrayList<>();
    private Map<String, StickerCategory> stickerCategoryMap = new HashMap<>();
    private Map<String, Integer> stickerOrder = new HashMap<>(3);

    public static StickerManager getInstance() {
        if (instance == null) {
            instance = new StickerManager();
        }
        return instance;
    }

    public StickerManager() {
        initStickerOrder();
        loadStickerCategory();
    }

    public void init() {
        Log.i(TAG, "Sticker Manager init...");
        stickerCategories.clear();
        stickerCategoryMap.clear();
        loadStickerCategory();//重新初始化数据
    }

    private void initStickerOrder() {
        // 默认贴图顺序
        stickerOrder.put(CATEGORY_COLLECTION, 1);
        stickerOrder.put(CATEGORY_AJMD, 2);
        stickerOrder.put(CATEGORY_XXY, 3);
        stickerOrder.put(CATEGORY_LT, 4);
        stickerOrder.put(CATEGORY_QBHD, 5);
        stickerOrder.put(CATEGORY_GGXM, 6);
        stickerOrder.put(CATEGORY_SDQ, 7);
    }

    private boolean isSystemSticker(String category) {
        return CATEGORY_XXY.equals(category) ||
                CATEGORY_AJMD.equals(category) ||
                CATEGORY_LT.equals(category) ||
                CATEGORY_QBHD.equals(category) ||
                CATEGORY_GGXM.equals(category) ||
                CATEGORY_SDQ.equals(category);
    }
    private boolean isSystemStickerGif(String category) {
        return CATEGORY_QBHD.equals(category) ||
                CATEGORY_GGXM.equals(category) ;
    }
    private boolean isSystemStickerJpg(String category) {
        return CATEGORY_SDQ.equals(category) ;
    }

    private boolean isCollectionSticker(String category) {
        return CATEGORY_COLLECTION.equals(category);
    }

    private int getStickerOrder(String categoryName) {
        if (stickerOrder.containsKey(categoryName)) {
            return stickerOrder.get(categoryName);
        } else {
            return 100;
        }
    }

    private void loadStickerCategory() {
        AssetManager assetManager = NimUIKit.getContext().getResources().getAssets();
        try {
            String[] files = assetManager.list("sticker");
            StickerCategory category;

            for (String name : files) {
                if (!FileUtil.hasExtentsion(name)) {
                    category = new StickerCategory(name, name, true, getStickerOrder(name));
                    stickerCategories.add(category);
                    stickerCategoryMap.put(name, category);
                }
            }
            // 排序
            Collections.sort(stickerCategories, new Comparator<StickerCategory>() {
                @Override
                public int compare(StickerCategory l, StickerCategory r) {
                    return l.getOrder() - r.getOrder();
                }
            });
            if(this.mMyEventListener!=null){
                this.mMyEventListener.handleEvent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<StickerCategory> getCategories() {
        return stickerCategories;
    }

    public synchronized StickerCategory getCategory(String name) {
        return stickerCategoryMap.get(name);
    }

    public String getStickerUri(String categoryName, String stickerName,int position) {
        StickerManager manager = StickerManager.getInstance();
        StickerCategory category = manager.getCategory(categoryName);
        if (category == null) {
            return null;
        }

        if (isSystemSticker(categoryName)) {
            if(isSystemStickerGif(categoryName)){
                if (!stickerName.contains("gif")) {
                    stickerName += ".gif";
                }
            }else if(isSystemStickerJpg(categoryName)){
                if (!stickerName.contains("jpg")) {
                    stickerName += ".jpg";
                }
            }else {
                if (!stickerName.contains(".png")) {
                    stickerName += ".png";
                }
            }
            String path = "sticker/" + category.getName() + "/" + stickerName;
            return "file:///android_asset/" + path;
        }else  if (isCollectionSticker(categoryName)) {
            if (!stickerName.contains(".gif")) {
                stickerName += ".gif";
            }
            return stickerName;
        }

        return null;
    }
    public String getStickerUri(String categoryName, String stickerName) {
        StickerManager manager = StickerManager.getInstance();
        StickerCategory category = manager.getCategory(categoryName);
        if (category == null) {
            return null;
        }

        if (isSystemSticker(categoryName)) {
            if(isSystemStickerGif(categoryName)){
                if (!stickerName.contains("gif")) {
                    stickerName += ".gif";
                }
            }else if(isSystemStickerJpg(categoryName)){
                if (!stickerName.contains("jpg")) {
                    stickerName += ".jpg";
                }
            }else {
                if (!stickerName.contains(".png")) {
                    stickerName += ".png";
                }
            }
            String path = "sticker/" + category.getName() + "/" + stickerName;
            return "file:///android_asset/" + path;
        }

        return null;
    }
    private  MyEventListener mMyEventListener;
    public interface MyEventListener extends EventListener {
        public void handleEvent ();
    }
    public void setMyEventListener(MyEventListener mMyEventListener){
        this.mMyEventListener =mMyEventListener;
    }
}
