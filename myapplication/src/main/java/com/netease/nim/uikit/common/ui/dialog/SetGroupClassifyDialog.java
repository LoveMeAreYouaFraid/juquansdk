package com.netease.nim.uikit.common.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.DoubleClickUtil;


/**
 * @desc 抽中抵用金
 */
public class SetGroupClassifyDialog {
    private View view;
    private Dialog dialog;
    private OnClickListener mOnClickListener;
    public SetGroupClassifyDialog(){
    }
    public void GroupClassifyDialog(Context context) {
        //1、使用Dialog、设置style
        dialog = new Dialog(context, R.style.bottomDialogTheme);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        //2、设置布局
        view = View.inflate(context, R.layout.dialog_setgroup_classify, null);

        dialog.setContentView(view);
        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        //设置弹出动画
        window.setWindowAnimations(R.style.paidViewingAnimation);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       //
        dialog.findViewById(R.id.btn_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(mOnClickListener!=null){
                    mOnClickListener.onSetGroupClassifyCancel();
                }
            }
        });
        dialog.findViewById(R.id.btn_dialog_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //防止连击
                if (DoubleClickUtil.isDoubleClick(1000)) {
                    return;
                }
                if(mOnClickListener!=null){
                    mOnClickListener.onSetGroupClassify();
                }
            }
        });

    }


    public void showDialog(){
        if(dialog!=null) {
            dialog.show();
        }
    }
    public void dismissDialog(){
        if(dialog!=null&&dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    public SetGroupClassifyDialog setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return this;
    }
    public interface OnClickListener {
        void onSetGroupClassify();
        void onSetGroupClassifyCancel();
    }

}