package com.netease.nim.demo.session.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.NimUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.RedPacketAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;

public class MsgViewHolderRedPacket extends MsgViewHolderBase {

    private RelativeLayout sendView, revView;
    private TextView sendContentText, revContentText;    // 红包描述
    private TextView sendTitleText, revTitleText;    // 红包名称
    private TextView targetRevText, targetSendText;    // 领取红包，查看红包

    public MsgViewHolderRedPacket(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.red_packet_item;
    }

    @Override
    protected void inflateContentView() {
        sendContentText = findViewById(R.id.tv_bri_mess_send);
        sendTitleText = findViewById(R.id.tv_bri_name_send);
        sendView = findViewById(R.id.bri_send);
        revContentText = findViewById(R.id.tv_bri_mess_rev);
        revTitleText = findViewById(R.id.tv_bri_name_rev);
        revView = findViewById(R.id.bri_rev);
        targetRevText = findViewById(R.id.tv_bri_target_rev);
        targetSendText = findViewById(R.id.tv_bri_target_send);

    }

    @Override
    protected void bindContentView() {
        RedPacketAttachment attachment = (RedPacketAttachment) message.getAttachment();

        if (!isReceivedMessage()) {// 消息方向，自己发送的
            sendView.setVisibility(View.VISIBLE);
            revView.setVisibility(View.GONE);
            sendContentText.setText(attachment.getRpContent());
            sendTitleText.setText("聚圈红包");
            if(attachment.isDrawed()){
                targetSendText.setText("已被领取");
                sendView.setBackgroundResource(R.drawable.red_packet_send_press);
            }else{
                targetSendText.setText(" ");
                sendView.setBackgroundResource(R.drawable.red_packet_send_bg);
            }
        } else {
            sendView.setVisibility(View.GONE);
            revView.setVisibility(View.VISIBLE);
            revContentText.setText(attachment.getRpContent());
            revTitleText.setText("聚圈红包");
            if(attachment.isDrawed()){
                targetRevText.setText("已领取");
                revView.setBackgroundResource(R.drawable.red_packet_rev_press);
            }else{
                targetRevText.setText("领取红包");
                revView.setBackgroundResource(R.drawable.red_packet_rev_bg);
            }
        }
    }

    @Override
    protected int leftBackground() {
        return R.color.transparent;
    }

    @Override
    protected int rightBackground() {
        return R.color.transparent;
    }

    @Override
    protected void onItemClick() {
        if (NimUtils.isFastDoubleClick(800)) {
            return;
        }
        // 拆红包
        RedPacketAttachment attachment = (RedPacketAttachment) message.getAttachment();

        if (redPacket != null) {
            redPacket.getRP(message.getSessionId(),attachment.getRpId(), context, new EventInterface.ApiResponse<Object>(){
                @Override
                public void onSuccess(Object data) {
                    System.out.println("---------个人红包 领取红包成功------- message.getSessionId()="+ message.getSessionId());
                    attachment.setDrawed(true);
                    message.setAttachment(attachment);
                    NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
                    getMsgAdapter().notifyDataSetChanged();

                }
            });
        }
    }


    public interface RedPacket {
        void getRP(String mSessionId,String rpId, Context context, EventInterface.ApiResponse<Object> apiResponse);
    }

    private static RedPacket redPacket;

    public static void setRedPacket(RedPacket redPacket) {
        MsgViewHolderRedPacket.redPacket = redPacket;
    }
}
