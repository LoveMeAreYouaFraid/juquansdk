package com.netease.nim.uikit.business.team.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.util.string.StringTextWatcher;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;

/**
 * 群属性
 * Created by hzxuwen on 2015/4/10.
 */
public class TeamPropertySettingActivity extends UI implements View.OnClickListener {

    private static final String EXTRA_TID = "EXTRA_TID";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_FIELD = "EXTRA_FIELD";

    // view
    private EditText editText;

    // data
    private String teamId;
    private TeamFieldEnum filed;
    private String initialValue;


    /**
     * 修改群某一个属性公用界面
     *
     * @param activity
     * @param teamId
     * @param field
     * @param initialValue
     * @param requestCode
     */
    public static void start(Activity activity, String teamId, TeamFieldEnum field, String initialValue, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, TeamPropertySettingActivity.class);
        intent.putExtra(EXTRA_TID, teamId);
        intent.putExtra(EXTRA_DATA, initialValue);
        intent.putExtra(EXTRA_FIELD, field);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 修改群某一个属性公用界面
     *
     * @param context
     * @param teamId
     * @param field
     * @param initialValue
     */
    public static void start(Context context, String teamId, TeamFieldEnum field, String initialValue) {
        Intent intent = new Intent();
        intent.setClass(context, TeamPropertySettingActivity.class);
        intent.putExtra(EXTRA_TID, teamId);
        intent.putExtra(EXTRA_DATA, initialValue);
        intent.putExtra(EXTRA_FIELD, field);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_team_name_activity);

        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);

        findViews();
        parseIntent();

        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        toolbarView.setText(R.string.save);
        toolbarView.setOnClickListener(this);
    }

    private void parseIntent() {
        teamId = getIntent().getStringExtra(EXTRA_TID);
        filed = (TeamFieldEnum) getIntent().getSerializableExtra(EXTRA_FIELD);
        initialValue = getIntent().getStringExtra(EXTRA_DATA);

        initData();
    }

    private void initData() {
        int limit = 0;
        switch (filed) {
            case Name:
                setTitle(R.string.team_settings_name);
                editText.setHint(R.string.team_settings_set_name);
                limit = 30;
                break;
            case Introduce:
                setTitle(R.string.team_introduce);
                editText.setHint(R.string.team_introduce_hint);
                limit = 512;
                break;
            case Extension:
                setTitle(R.string.team_extension);
                editText.setHint(R.string.team_extension_hint);
                limit = 65535;
                break;
        }

        if (!TextUtils.isEmpty(initialValue)) {
            editText.setText(initialValue);
            editText.setSelection(initialValue.length());
        }
        editText.addTextChangedListener(new StringTextWatcher(limit, editText));
    }

    private void findViews() {
        editText = (EditText) findViewById(R.id.discussion_name);
        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP;
            }

        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    complete();
                    return true;
                } else {
                    return false;
                }
            }
        });
        showKeyboardDelayed(editText);

        LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.background);
        backgroundLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showKeyboard(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.action_bar_right_clickable_textview) {
            showKeyboard(false);
            complete();
        } else {
        }
    }

    /**
     * 点击保存
     */
    private void complete() {
        if (filed == TeamFieldEnum.Name) {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                ToastHelper.showToast(this, R.string.not_allow_empty);
            } else {
                char[] s = editText.getText().toString().toCharArray();
                int i;
                for (i = 0; i < s.length; i++) {
                    if (String.valueOf(s[i]).equals(" ")) {
                        ToastHelper.showToast(this, R.string.now_allow_space);
                        break;
                    }
                }
                if (i == s.length) {
                    saveTeamProperty();
                }
            }
        } else {
            if (TextUtils.equals(editText.getText().toString(), initialValue)) {
                showKeyboard(false);
                finish();
            } else if (TextUtils.isEmpty(teamId)) {
                saved();
            } else {
                saveTeamProperty();
            }
        }
    }

    private void saved() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, editText.getText().toString());
        setResult(Activity.RESULT_OK, intent);
        showKeyboard(false);
        finish();
    }

    /**
     * 保存设置
     */
    private void saveTeamProperty() {
        if (teamId == null) { // 讨论组创建时，设置群名称
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATA, editText.getText().toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            EventInterface.ApiResponse<Object> apiResponse = new EventInterface.ApiResponse<Object>() {
                @Override
                public void onSuccess(Object data) {
                    ToastHelper.showToast(TeamPropertySettingActivity.this, R.string.update_success);
                    saved();
                }

                @Override
                public void onFail(int type,String msg) {
                    ToastHelper.showToast(TeamPropertySettingActivity.this, ""+msg);
                  //  ToastHelper.showToast(TeamPropertySettingActivity.this, String.format(getString(R.string.update_failed), type));
                }
            };
            switch (filed) {
                case Name:
                    updateGroupEvent.updateGroup(teamId, editText.getText().toString(), null, null, apiResponse);
                    break;
                case Introduce:
                    updateGroupEvent.updateGroup(teamId, null, editText.getText().toString(), null, apiResponse);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        showKeyboard(false);
        super.onBackPressed();
    }

    private static EventInterface.UpdateGroupEvent updateGroupEvent;

    public static void setUpdateGroupEvent(EventInterface.UpdateGroupEvent updateGroupEvent) {
        TeamPropertySettingActivity.updateGroupEvent = updateGroupEvent;
    }
}
