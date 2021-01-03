package com.netease.nim.demo.session.action;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.activity.AVChatActivity;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class AudioAndVideoAction extends BaseAction {

    public AudioAndVideoAction() {
        super(R.drawable.message_plus_video_chat_selector, R.string.input_panel_audio_call);
    }

    /**
     * **********************语音视频************************
     */
    private void chooseAudioAndVideo() {
        showBottomDialog();
    }
    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(getActivity(), R.style.bottomDialogTheme);
        //2、设置布局
        View view = View.inflate(getActivity(), R.layout.nim_pop_aduio_video, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.nim_aduiovideos_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.ll_call_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (NetworkUtil.isNetAvailable(getActivity())) {
                    startAudioVideoCall(AVChatType.VIDEO);
                } else {
                    ToastHelper.showToast(getActivity(), R.string.network_is_not_available);
                }
            }
        });

        dialog.findViewById(R.id.ll_call_aduio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (NetworkUtil.isNetAvailable(getActivity())) {
                    startAudioVideoCall(AVChatType.AUDIO);
                } else {
                    ToastHelper.showToast(getActivity(), R.string.network_is_not_available);
                }
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClick() {
        chooseAudioAndVideo();
    }
    /************************ 音视频通话 ***********************/

    public void startAudioVideoCall(AVChatType avChatType) {
        AVChatKit.outgoingCall(getActivity(), getAccount(), UserInfoHelper.getUserDisplayName(getAccount()), avChatType.getValue(), AVChatActivity.FROM_INTERNAL);
    }
}
