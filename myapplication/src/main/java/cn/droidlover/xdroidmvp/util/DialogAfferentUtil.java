package cn.droidlover.xdroidmvp.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import cn.droidlover.xdroidmvp.R;

public class DialogAfferentUtil {

    private static Dialog dialog;
    private static DialogAfferentUtil instance;

    public synchronized static DialogAfferentUtil instance() {
        if (null == instance) {
            instance = new DialogAfferentUtil();
        }
        return instance;
    }

    //act上下文对象
    public View showDialog(Context activity, int layoutResId, int[] resIds, OnClickListener listener, boolean canceled) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        dialog = new Dialog(activity, R.style.MyDialogStyle);
        View v = LayoutInflater.from(activity).inflate(layoutResId, null, false);
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(canceled);//设置点击屏幕Dialogs是否消失
        LayoutParams layoutParams = dialog.getWindow().getAttributes();
        WindowManager m = ((Activity) activity).getWindowManager();
        Display d = m.getDefaultDisplay();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
        if (resIds != null) {
            for (int resId : resIds) {
                View findView = v.findViewById(resId);
                if (findView != null) {
                    findView.setOnClickListener(listener);
                }
            }
        }
        dialog.show();
        return v;
    }

    //act上下文对象
    public View showDialogMatchParent(Context activity, int layoutResId, int[] resIds, OnClickListener listener) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new Dialog(activity, R.style.MyDialogStyle);
        View v = LayoutInflater.from(activity).inflate(layoutResId, null, false);
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(true);
        LayoutParams layoutParams = dialog.getWindow().getAttributes();
        WindowManager m = ((Activity) activity).getWindowManager();
        Display d = m.getDefaultDisplay();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        if (resIds != null) {
            for (int resId : resIds) {
                View findView = v.findViewById(resId);
                if (findView != null) {
                    findView.setOnClickListener(listener);
                }
            }
        }
        dialog.show();
        return v;
    }

    //隐藏Dialog
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
