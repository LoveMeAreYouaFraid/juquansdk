package com.netease.nim.uikit.business.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.session.adapter.SessionAdapter;
import com.netease.nim.uikit.business.session.module.list.MessageListPanelEx;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionActivity extends UI implements View.OnClickListener {
    public static final String RESULT_TYPE = "RESULT_TYPE"; // 返回结果
    private ListView listView;
    private SessionAdapter mAdapter;
    private List<RecentContact> recentContacts = new ArrayList<>();
    private static Context mContext;
    private Map<String, Boolean> map = new HashMap<>();
    private Map<String, String> types = new HashMap<>();
    private TextView btnSelect;

    @Override
    public void onClick(View v) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(this);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        longClickItemForwardToPerson(alertDialog);
        longClickItemForwardToTeam(alertDialog);
        alertDialog.show();
    }

    public static void startActivityForResult(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, SessionActivity.class);
        mContext = context;
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_session_select);

        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        setTitle("选择一个聊天");
        getToolBar().setTitleTextColor(getResources().getColor(R.color.white));
        findViewById(R.id.tv_add).setOnClickListener(this);
        listView = findViewById(R.id.contact_list_view);
        btnSelect = findViewById(R.id.btnSelect);
        mAdapter = new SessionAdapter(this, recentContacts);
        listView.setAdapter(mAdapter);
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {
                // recents参数即为最近联系人列表（最近会话列表）
                recentContacts.addAll(recents);
                mAdapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (map.get(recentContacts.get(position).getContactId()) != null) {
                map.put(recentContacts.get(position).getContactId(), !map.get(recentContacts.get(position).getContactId()));
            } else {
                map.put(recentContacts.get(position).getContactId(), true);
                types.put(recentContacts.get(position).getContactId(), recentContacts.get(position).getSessionType() == SessionTypeEnum.P2P ? "1" : "0");
            }

            mAdapter.re(map);
            int i = 0;
            //遍历map中的值
            for (Boolean value : map.values()) {
                System.out.println("Value = " + value);
                if (value)
                    i++;
            }
            if (i == 0)
                btnSelect.setText("确认");
            else
                btnSelect.setText("确认(" + i + ")");
//            RecentContact recentContact = recentContacts.get(position);
//            ArrayList<String> strings = new ArrayList<>();
//            if (recentContact.getSessionType() == SessionTypeEnum.P2P)
//                strings.add("1");
//            else
//                strings.add("0");
//            strings.add(recentContact.getContactId());
//            Intent intent = new Intent();
//            intent.putStringArrayListExtra(ContactSelectActivity.RESULT_DATA, strings);
//            setResult(Activity.RESULT_OK, intent);
//            this.finish();
        });
        btnSelect.setOnClickListener(v -> {
            ArrayList<String> str = new ArrayList<>();
            ArrayList<String> type = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                if (entry.getValue()) {
                    str.add(entry.getKey());
                    type.add(types.get(entry.getKey()));
                }
            }
            if (str.size() > 0) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ContactSelectActivity.RESULT_DATA, str);
                intent.putStringArrayListExtra(RESULT_TYPE, type);
                setResult(Activity.RESULT_OK, intent);
                this.finish();
            } else {
                ToastHelper.showToast(this, "请选择");
            }
        });
    }

    // 长按菜单项 -- 转发到个人
    private void longClickItemForwardToPerson(CustomAlertDialog alertDialog) {
        alertDialog.addItem(this.getString(R.string.forward_to_person), () -> {
            ContactSelectActivity.Option option = new ContactSelectActivity.Option();
            option.title = "选择转发的人";
            option.type = ContactSelectActivity.ContactSelectType.BUDDY;
            option.multi = true;
            NimUIKit.startContactSelector(mContext, option, MessageListPanelEx.REQUEST_CODE_FORWARD_PERSON);
            finish();
        });
    }

    // 长按菜单项 -- 转发到群组
    private void longClickItemForwardToTeam(CustomAlertDialog alertDialog) {
        alertDialog.addItem(this.getString(R.string.forward_to_team), () -> {
            ContactSelectActivity.Option option = new ContactSelectActivity.Option();
            option.title = "选择转发的群";
            option.type = ContactSelectActivity.ContactSelectType.TEAM;
            option.multi = true;
            NimUIKit.startContactSelector(mContext, option, MessageListPanelEx.REQUEST_CODE_FORWARD_TEAM);
            finish();
        });
    }
}
