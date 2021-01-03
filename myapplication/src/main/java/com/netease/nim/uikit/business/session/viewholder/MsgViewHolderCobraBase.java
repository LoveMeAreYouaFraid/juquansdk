package com.netease.nim.uikit.business.session.viewholder;

import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class MsgViewHolderCobraBase extends MsgViewHolderBase {

    protected JSONObject data;

    MsgViewHolderCobraBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected boolean shouldDisplayReceipt() {
        return false;
    }

    private void initData() {
        try {
            data = getMessageCustomContent(message);
        } catch (Exception e) {
            //
        }
    }

    List<JSONObject> getArrayValue(String key) {
        if (data == null) initData();
        if (data != null) {
            try {
                JSONArray array = data.getJSONArray(key);
                if (array == null || array.length() == 0) return new ArrayList<>();
                List<JSONObject> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.getJSONObject(i);
                    if (o == null) continue;
                    list.add(o);
                }
                return list;
            } catch (Exception e) {
                //
            }
        }
        return new ArrayList<>();
    }

    String getStringValue(String key) {
        if (data == null) initData();
        return getStringValue(data, key);
    }

    String getStringValue(JSONObject o, String key) {
        if (o != null) {
            try {
                return o.getString(key);
            } catch (Exception e) {
                //
            }
        }
        return null;
    }

    public int getIntValue(String key) {
        if (data == null) initData();
        return getIntValue(data, key);
    }

    public int getIntValue(JSONObject o, String key) {
        if (o != null) {
            try {
                return o.getInt(key);
            } catch (Exception e) {
                //
            }
        }
        return 0;
    }

    static JSONObject getMessageCustomContent(IMMessage message) {
        try {
            String content = (String) getPrivateValue(message, "n");
            if (content != null) {
                return new JSONObject(content);
            }
        } catch (Exception e) {
            //
        }
        return null;
    }

    private static Object getPrivateValue(Object obj, String field) {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            //
        }
        return null;
    }

}
