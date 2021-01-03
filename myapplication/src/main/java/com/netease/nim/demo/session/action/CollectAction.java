package com.netease.nim.demo.session.action;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.activity.AVChatActivity;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.constant.RequestCode;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class CollectAction extends BaseAction {
    private static Class<? extends Activity> entrance;

    public static void setEntrance(Class<? extends Activity> entrance) {
        CollectAction.entrance = entrance;
    }

    public CollectAction() {
        super(R.drawable.message_plus_collect_selector, R.string.input_panel_collect);
    }

    /**
     * ******************跳转到收藏************************
     */
    private void chooseCollect() {
        Intent intent = new Intent(getActivity(), CollectAction.entrance);
        intent.putExtra("msgType", getSessionType().getValue());
        intent.putExtra("session", getAccount());
        intent.putExtra("fragment_type", "fragment_type_collection");
        getActivity().startActivityForResult(intent, makeRequestCode(9));
    }

    @Override
    public void onClick() {
        chooseCollect();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9) {
            IMMessage message = (IMMessage) data.getSerializableExtra("IMMessage");
            sendMessage(message);
        }
    }

}
