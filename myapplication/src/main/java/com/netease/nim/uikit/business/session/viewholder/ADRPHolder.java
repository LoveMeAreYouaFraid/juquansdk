package com.netease.nim.uikit.business.session.viewholder;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

import org.json.JSONObject;

public class ADRPHolder extends MsgViewHolderBase {

    private View rp;
    private static EventInterface.RedPacket redPacket;


    public static void setRedPacket(EventInterface.RedPacket redPacket) {
        ADRPHolder.redPacket = redPacket;
    }

    public ADRPHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.layout_ad_rp_viewholder;
    }

    @Override
    protected void inflateContentView() {
        rp = findViewById(R.id.send_red_racket_to_master);
    }

    @Override
    protected void bindContentView() {
        JSONObject messageCustomContent = MsgViewHolderCobraBase.getMessageCustomContent(message);
        if (messageCustomContent != null) {
            ADViewHolder.ADRequestEntity adRequestEntity = JSON.parseObject(messageCustomContent.toString(), ADViewHolder.ADRequestEntity.class);
            rp.setOnClickListener(v -> {
                        if (redPacket != null) {
                            redPacket.getRP(adRequestEntity.groupId, adRequestEntity.advertisement_id, context);
                        }
                    }
            );
        } else {
            ToastHelper.showToast(context, "参数错误");
        }
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }
}
