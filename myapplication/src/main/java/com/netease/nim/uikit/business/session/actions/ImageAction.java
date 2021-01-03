package com.netease.nim.uikit.business.session.actions;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.constant.RequestCode;
import com.netease.nim.uikit.business.session.fragment.MessageFragment;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.List;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class ImageAction extends PickImageAction {

    public ImageAction() {
        super(R.drawable.nim_message_plus_photo_selector, R.string.input_panel_photo, false);
    }

    private int index;

    @Override
    protected void onPicked(File file) {
        IMMessage message;
        if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom) {
            message = ChatRoomMessageBuilder.createChatRoomImageMessage(getAccount(), file, file.getName());
        } else {
            message = MessageBuilder.createImageMessage(getAccount(), getSessionType(), file, file.getName());
        }
        sendMessage(message);
    }

    @Override
    public void onClick() {
        Matisse.from(MessageFragment.fragment)
                .choose(MimeType.ofAll())
                .capture(false)
                .captureStrategy(
                        new CaptureStrategy(true, "com.juquan.im.fileprovider", "test"))
                .countable(false)
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(false)// Default is `true`
                .forResult(makeRequestCode(RequestCode.PICK_IMAGE));
        index = 0;
    }

    @Override
    protected boolean isTakePhoto() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (index == 0 && resultCode == Activity.RESULT_OK) {
            index++;
            if (requestCode == RequestCode.PICK_IMAGE) {// onPickImageActivityResult(requestCode, data);
//                if (data == null) {
//                    ToastHelper.showToastLong(getActivity(), R.string.picker_image_error);
//                    return;
//                }
                List<String> list = Matisse.obtainPathResult(data);
                for (String url : list) {
                    if (isVideo(url)) {
                        MediaPlayer mediaPlayer = getVideoMediaPlayer(new File(url));
                        long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
                        int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
                        int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
                        IMMessage message = MessageBuilder.createVideoMessage(getAccount(), getSessionType(), new File(url), duration, width, height, MD5.getStreamMD5(url));
                        sendMessage(message);
                    } else {
                        onPicked(new File(url));
                    }
                }
            }
        }
    }

    /**
     * 是否是视频
     *
     * @param str
     * @return
     */
    public boolean isVideo(String str) {
        String[] split = str.split("\\.");
        return split.length > 1 && (split[1].equals("mp4") || split[1].equals("mpeg"));
    }

    /**
     * 获取视频mediaPlayer
     *
     * @param file 视频文件
     * @return mediaPlayer
     */
    private MediaPlayer getVideoMediaPlayer(File file) {
        try {
            return MediaPlayer.create(getActivity(), Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

