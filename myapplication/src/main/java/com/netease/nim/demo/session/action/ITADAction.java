package com.netease.nim.demo.session.action;

import com.netease.nim.demo.R;

import static com.netease.nim.uikit.business.session.fragment.MessageFragment.AD_IMAGE_TEXT;

/**
 * Created by zhoujianghua on 2015/7/31.
 */
public class ITADAction extends ADAction {

    public ITADAction(String tid) {
        super(R.drawable.message_plus_redpgg_selector, R.string.input_panel_ad);
        this.tid = tid;
    }

    @Override
    protected int getRequestCode() {
        return AD_IMAGE_TEXT;
    }

    @Override
    protected int getType() {
        return 2;
    }
}
