package com.netease.nim.uikit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class CheckInDialog extends Dialog {
    public CheckInDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    private TextView dialog_title;
    private TextView dialog_content;
    private TextView submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check_in);
        //按空白处不能取消动画
        //初始化界面控件
        initView();
    }

    private void initView() {
        dialog_title = findViewById(R.id.dialog_title);
        dialog_content = findViewById(R.id.dialog_content);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("com.juquan.im.show");
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            dismiss();
        });
    }


    public void setData(String title, String content, View.OnClickListener listener) {
        dialog_title.setText(title);
        dialog_content.setText(content);
        submit.setOnClickListener(listener);
    }
    public void setDialogContent(String content){
        dialog_content.setText(content);
    }

}
