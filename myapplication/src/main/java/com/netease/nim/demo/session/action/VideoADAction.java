package com.netease.nim.demo.session.action;

import com.netease.nim.demo.R;

import static com.netease.nim.uikit.business.session.fragment.MessageFragment.AD_VIDEO;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class VideoADAction extends ADAction {

    public VideoADAction(String tid) {
        super(R.drawable.message_plus_guess_selector, R.string.input_panel_video_ad);
        this.tid = tid;
    }

    @Override
    protected int getRequestCode() {
        return AD_VIDEO;
    }

    @Override
    protected int getType() {
        return 1;
    }
}
