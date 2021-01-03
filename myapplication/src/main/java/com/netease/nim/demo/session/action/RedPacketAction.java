package com.netease.nim.demo.session.action;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.RedPacketAttachment;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class RedPacketAction extends BaseAction {

    private static Class<? extends Activity> entrance;

    public static void setEntrance(Class<? extends Activity> entrance) {
        RedPacketAction.entrance = entrance;
    }

    public RedPacketAction() {
        super(R.drawable.nim_message_plus_rp_selector, R.string.red_packet);
    }

    private static final int CREATE_GROUP_RED_PACKET = 51;
    private static final int CREATE_SINGLE_RED_PACKET = 10;

    @Override
    public void onClick() {
        int requestCode;
        if (getContainer().sessionType == SessionTypeEnum.Team) {
            requestCode = makeRequestCode(CREATE_GROUP_RED_PACKET);
        } else if (getContainer().sessionType == SessionTypeEnum.P2P) {
            requestCode = makeRequestCode(CREATE_SINGLE_RED_PACKET);
        } else {
            return;
        }
        Intent intent = new Intent(getActivity(), RedPacketAction.entrance);
        intent.putExtra("account", getAccount());
        intent.putExtra("session_type", getContainer().sessionType == SessionTypeEnum.Team ? 1 : 0);
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        sendRpMessage(data);
    }

    private void sendRpMessage(Intent data) {
        if (data.getExtras() == null) {
            return;
        }
        String extra = data.getExtras().getString("data");
        EnvelopeBean envelopeBean = JSON.parseObject(extra, EnvelopeBean.class);
        if (envelopeBean == null) {
            return;
        }
        RedPacketAttachment attachment = new RedPacketAttachment();
        // 红包id，红包信息，红包名称
        attachment.setRpId(envelopeBean.envelopesID);
        attachment.setRpContent(envelopeBean.envelopeMessage);
        attachment.setRpTitle(envelopeBean.envelopeName);

        String content = getActivity().getString(R.string.rp_push_content);
        // 不存云消息历史记录
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableHistory = false;
        IMMessage message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), content, attachment, config);
        message.setPushContent("[红包消息]");
        sendMessage(message);
    }

    public static class EnvelopeBean {
        public String envelopesID;
        public String envelopeMessage;
        public String envelopeName;
        public int envelopeType;
    }
}
