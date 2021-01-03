package com.netease.nim.demo.session.action;

import android.app.Activity;
import android.content.Intent;

import com.netease.nim.uikit.business.session.actions.BaseAction;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public abstract class ADAction extends BaseAction {

    String tid;
    private static Class<? extends Activity> entrance;

    public ADAction(int iconResId, int titleId) {
        super(iconResId, titleId);
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(getActivity(), ADAction.entrance);
        intent.putExtra("group_id", tid);
        intent.putExtra("ad_type", getType());
        getActivity().startActivityForResult(intent,getRequestCode());
    }

    protected abstract int getRequestCode() ;

    protected int getType() {
        return 0;
    }

    public static void setEntrance(Class<? extends Activity> entrance) {
        ADAction.entrance = entrance;
    }
}
