package com.netease.nim.uikit.business.team.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;


public class NIMVerifySelectActivity extends UI {

    ImageView iv_selected_all;//选中图标
    ImageView iv_selected_noall;//选中图标
    ImageView iv_selected_manage;//选中图标
    RelativeLayout rl_msgtip_all,rl_msgtip_noall,rl_msgtip_manage;
    private int msgTipId;
//    Free(0),
//    Apply(1),
//    Private(2);
    /**
     * *********************************lifeCycle*******************************************
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nin_group_verfiy);
        msgTipId =  getIntent().getIntExtra("type",0);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleString = "身份认证";
        setToolBar(R.id.toolbar, options);
        TextView toolbarView = findView(R.id.right_btn);
        toolbarView.setText("完成");
        toolbarView.setVisibility(View.VISIBLE);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     Intent min=new Intent();
                    min.putExtra("type",msgTipId);
                    setResult(RESULT_OK,min);
                    finish();

            }
        });
        iv_selected_all=findViewById(R.id.iv_selected_all);
        iv_selected_noall=findViewById(R.id.iv_selected_noall);
        iv_selected_manage=findViewById(R.id.iv_selected_manage);

        rl_msgtip_all=findViewById(R.id.rl_msgtip_all);
        rl_msgtip_noall=findViewById(R.id.rl_msgtip_noall);
        rl_msgtip_manage=findViewById(R.id.rl_msgtip_manage);

        if(msgTipId==0){
            iv_selected_all.setVisibility(View.VISIBLE);
            iv_selected_noall.setVisibility(View.GONE);
            iv_selected_manage.setVisibility(View.GONE);
        }else  if(msgTipId==1){
            iv_selected_all.setVisibility(View.GONE);
            iv_selected_noall.setVisibility(View.GONE);
            iv_selected_manage.setVisibility(View.VISIBLE);
        }else  if(msgTipId==2){
            iv_selected_all.setVisibility(View.GONE);
            iv_selected_noall.setVisibility(View.VISIBLE);
            iv_selected_manage.setVisibility(View.GONE);
        }
        rl_msgtip_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //允许任何人加入
                msgTipId=0;
                iv_selected_all.setVisibility(View.VISIBLE);
                iv_selected_noall.setVisibility(View.GONE);
                iv_selected_manage.setVisibility(View.GONE);
            }
        });
        rl_msgtip_noall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //需要身份认证
                msgTipId=1;
                iv_selected_all.setVisibility(View.GONE);
                iv_selected_noall.setVisibility(View.VISIBLE);
                iv_selected_manage.setVisibility(View.GONE);
            }
        });
        rl_msgtip_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不允许任何人加入
                msgTipId=2;
                iv_selected_all.setVisibility(View.GONE);
                iv_selected_noall.setVisibility(View.GONE);
                iv_selected_manage.setVisibility(View.VISIBLE);
            }
        });

    }




}
