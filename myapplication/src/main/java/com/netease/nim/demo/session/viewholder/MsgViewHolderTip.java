package com.netease.nim.demo.session.viewholder;

import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.Map;

/**
 * Created by huangjun on 2015/11/25.
 * Tip类型消息ViewHolder
 */
public class MsgViewHolderTip extends MsgViewHolderBase {

    protected TextView notificationTextView;

    public MsgViewHolderTip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return com.netease.nim.uikit.R.layout.nim_message_item_notification;
    }

    @Override
    protected void inflateContentView() {
        notificationTextView = (TextView) view.findViewById(com.netease.nim.uikit.R.id.message_item_notification_label);
    }

    @Override
    protected void bindContentView() {
        String text = "未知通知提醒";
        if (TextUtils.isEmpty(message.getContent())) {
            handleTextNotification(text);
//            Map<String, Object> content = message.getRemoteExtension();
//            if (content != null && !content.isEmpty()) {
//                text = (String) content.get("content");
//            }
//
//            if(text.equals("领取红包")){
//              //  text= UserInfoHelper.getUserTitleName(String.valueOf(content.get("get_id")), SessionTypeEnum.P2P)+"领取了"+UserInfoHelper.getUserTitleName(String.valueOf(content.get("send_id")), SessionTypeEnum.P2P)+"的红包";
//                text= UserInfoHelper.getUserName(String.valueOf(content.get("get_id")))+"领取了"+UserInfoHelper.getUserName(String.valueOf(content.get("send_id")))+"的红包";
//
//            }
        } else {
            text = message.getContent();
            if(text.contains("RED_PACKET")){
                Map<String, Object> content = message.getRemoteExtension();
//                if (content != null && !content.isEmpty()) {
//                    text = (String) content.get("content");
//                }
                if(content!=null) {
                    String getUserName = UserInfoHelper.getUserDisplayNameEx((String) content.get("getUserId"), "你");
                    String sendUserName = UserInfoHelper.getUserDisplayNameEx((String) content.get("sendUserId"), "你");
                    System.out.println("--------- 领取红包通知提示-----2--getUserName=" + getUserName);
                    System.out.println("--------- 领取红包通知提示-----2--sendUserName=" + sendUserName);
                    System.out.println("--------- 领取红包通知提示-------NimUIKit.getAccount()=" + NimUIKit.getAccount());

                    text = getUserName + "领取了" + sendUserName + "的";

                    System.out.println("--------- 领取红包通知提示-------text=" + text);
                    System.out.println("--------- 领取红包通知提示-------getUserId=" + content.get("getUserId"));
                    System.out.println("--------- 领取红包通知提示-------sendUserId=" + content.get("sendUserId"));
                    String htmlTxt=text+"<font color=#E64A4E>红包</font>";
                    notificationTextView.setText(Html.fromHtml(htmlTxt));
                }else{
                    text=" ";
                    handleTextNotification(text);
                }
            }else{
                handleTextNotification(text);
            }

        }



    }

    private void handleTextNotification(String text) {
        System.out.println("--------handleTextNotification- 领取红包通知提示-------text=" + text);
        MoonUtil.identifyFaceExpressionAndATags(context, notificationTextView, text, ImageSpan.ALIGN_BOTTOM);
        notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }
}
