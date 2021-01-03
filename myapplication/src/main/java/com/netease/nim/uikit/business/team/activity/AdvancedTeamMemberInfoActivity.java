package com.netease.nim.uikit.business.team.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.team.model.NimUserIInfoBean;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyEditDialog;
import com.netease.nim.uikit.common.ui.dialog.MenuDialog;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.widget.SwitchButton;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.PhoneUtils;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群成员详细信息界面
 * Created by hzxuwen on 2015/3/19.
 */
public class AdvancedTeamMemberInfoActivity extends UI implements View.OnClickListener {
    private final boolean FLAG_ADD_FRIEND_DIRECTLY = false; // 是否直接加为好友开关，false为需要好友申请
    private static final String TAG = AdvancedTeamMemberInfoActivity.class.getSimpleName();

    // constant
    public static final int REQ_CODE_REMOVE_MEMBER = 11;

    private static final String EXTRA_ID = "EXTRA_ID";

    private static final String EXTRA_TID = "EXTRA_TID";

    public static final String EXTRA_ISADMIN = "EXTRA_ISADMIN";

    public static final String EXTRA_ISREMOVE = "EXTRA_ISREMOVE";

    private final String KEY_MUTE_MSG = "mute_msg";

    // data
    private String account;

    private String teamId;

    private TeamMember viewMember;

    private boolean isSetAdmin;

    private Map<String, Boolean> toggleStateMap;

    // view
   // private HeadImageView headImageView;

   // private TextView memberName;

    private TextView nickName;

    private TextView identity;

    private View nickContainer;

    private Button removeBtn;

    private View identityContainer;
    private View report_container;
    private View deletefriend_container;
    private View addriend_container;



    private MenuDialog setAdminDialog;

    private MenuDialog cancelAdminDialog;

    private ViewGroup toggleLayout;

    private SwitchButton muteSwitch;

    private TextView invitorInfo;

    // state
    private boolean isSelfCreator = false;

    private boolean isSelfManager = false;
    private String userId;
    private int isFriend;

    private HeadImageView headImageView;
    private TextView nameText;
    private ImageView genderImage;
    private TextView accountText;

    public static void startActivityForResult(Activity activity, String account, String tid) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, account);
        intent.putExtra(EXTRA_TID, tid);
        intent.setClass(activity, AdvancedTeamMemberInfoActivity.class);
        activity.startActivityForResult(intent, REQ_CODE_REMOVE_MEMBER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_advanced_team_member_info_layout);
        ToolBarOptions options = new NimToolBarOptions();
       // options.titleId = R.string.team_member_info;
        options.titleString="设置";
        setToolBar(R.id.toolbar, options);
        parseIntentData();
        findViews();
        loadMemberInfo();
        initMemberInfo();
        //加个loading
        DialogMaker.showProgressDialog(this, null, false);
        getUserInfoEvent.getUserInfo(account, new EventInterface.ApiResponse<NimUserIInfoBean>() {
            @Override
            public void onSuccess(NimUserIInfoBean data) {
                DialogMaker.dismissProgressDialog();
                if(data!=null){
                    userId=data.id+"";
                    isFriend=data.is_friend;
                    nameText.setText(data.user_name);
                    if(isFriend==1){
                        deletefriend_container.setVisibility(View.VISIBLE);
                        accountText.setText("帐号：" + account);
                    }else{
                        accountText.setText("帐号：" + PhoneUtils.blurPhone(account));
                        if(!account.equals(NimUIKit.getAccount())){
                            addriend_container.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFail(int type) {
                DialogMaker.dismissProgressDialog();
            }
        });
        //accountText.setText("帐号：" + account);
        headImageView.loadBuddyAvatar(account);
        if(isFriend==1){
            deletefriend_container.setVisibility(View.VISIBLE);
            accountText.setText("帐号：" + account);
        }else{
            accountText.setText("帐号：" + PhoneUtils.blurPhone(account));
            if(!account.equals(NimUIKit.getAccount())){
                addriend_container.setVisibility(View.VISIBLE);
            }
        }

//        if (TextUtils.equals(account, DemoCache.getAccount())) {
//            nameText.setText(UserInfoHelper.getUserName(account));
//        }

        final NimUserInfo userInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(account);
        if (userInfo == null) {
            LogUtil.e(TAG, "userInfo is null when updateUserInfoView");
            return;
        }

        if (userInfo.getGenderEnum() == GenderEnum.MALE) {
            genderImage.setVisibility(View.VISIBLE);
            genderImage.setBackgroundResource(R.mipmap.uikit_mine_setup_male);
        } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
            genderImage.setVisibility(View.VISIBLE);
            genderImage.setBackgroundResource(R.mipmap.uikit_mine_setup_wommale);
        } else {
            genderImage.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToggleView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (setAdminDialog != null) {
            setAdminDialog.dismiss();
        }
        if (cancelAdminDialog != null) {
            cancelAdminDialog.dismiss();
        }
    }

    private void parseIntentData() {
        account = getIntent().getStringExtra(EXTRA_ID);
        teamId = getIntent().getStringExtra(EXTRA_TID);
    }
    private boolean iscancel;
    private void findViews() {
        nickContainer = findViewById(R.id.nickname_container);
        identityContainer = findViewById(R.id.identity_container);
        report_container= findViewById(R.id.report_container);
        deletefriend_container= findViewById(R.id.deletefriend_container);
        addriend_container= findViewById(R.id.addriend_container);
      //  headImageView = findViewById(R.id.team_member_head_view);
     //   memberName = findViewById(R.id.team_member_name);
        nickName = findViewById(R.id.team_nickname_detail);
        identity = findViewById(R.id.team_member_identity_detail);
        removeBtn = findViewById(R.id.team_remove_member);
        toggleLayout = findView(R.id.toggle_layout);
        invitorInfo = findView(R.id.invitor_info);

        headImageView = findView(R.id.user_head_image);
        nameText = findView(R.id.user_name);
        genderImage = findView(R.id.gender_img);
        accountText = findView(R.id.user_account);
        RelativeLayout team_member_black = findView(R.id.team_member_black);
        ((TextView) team_member_black.findViewById(R.id.user_profile_title)).setText("拉黑");
        ((View) team_member_black.findViewById(R.id.line)).setVisibility(View.GONE);
        SwitchButton user_profile_toggle =team_member_black.findViewById(R.id.user_profile_toggle);
        user_profile_toggle.setOnChangedListener(new SwitchButton.OnChangedListener() {
            @Override
            public void OnChanged(View v, boolean checkState) {
                if (checkState) {
                    EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(AdvancedTeamMemberInfoActivity.this,"拉黑" ,
                            "拉黑，您将不在收到对方的消息，并 且你们互相看不到对方动态", true,
                            new EasyAlertDialogHelper.OnDialogActionListener() {

                                @Override
                                public void doCancelAction() {
                                    user_profile_toggle.setCheck(!checkState);
                                    iscancel=true;
                                }

                                @Override
                                public void doOkAction() {
                                    NIMClient.getService(FriendService.class).addToBlackList(account).setCallback(new RequestCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void param) {
                                            ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "加入黑名单成功");
                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            if (code == 408) {
                                                ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, R.string.network_is_not_available);
                                            } else {
                                                ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "on failed：" + code);
                                            }
                                            user_profile_toggle.setCheck(!checkState);
                                        }

                                        @Override
                                        public void onException(Throwable exception) {

                                        }
                                    });

                                }
                            });
                    if (!isFinishing() && !isDestroyedCompatible()) {
                        dialog.show();
                    }

                } else {
                    if(iscancel){
                        iscancel=false;
                        return;
                    }
                    NIMClient.getService(FriendService.class).removeFromBlackList(account).setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "移除黑名单成功");
                        }

                        @Override
                        public void onFailed(int code) {
                            if (code == 408) {
                                ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, R.string.network_is_not_available);
                            } else {
                                ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "on failed:" + code);
                            }
                            user_profile_toggle.setCheck(!checkState);
                        }

                        @Override
                        public void onException(Throwable exception) {

                        }
                    });
                }
            }
        });
        setClickListener();
    }

    private void setClickListener() {
        nickContainer.setOnClickListener(this);
        identityContainer.setOnClickListener(this);
        removeBtn.setOnClickListener(this);
        report_container.setOnClickListener(this);
        deletefriend_container.setOnClickListener(this);
        addriend_container.setOnClickListener(this);
    }

    private void updateToggleView() {
        if (getMyPermission()) {
            boolean isMute = NimUIKit.getTeamProvider().getTeamMember(teamId, account).isMute();
            if (muteSwitch == null) {
                addToggleBtn(isMute);
            } else {
                setToggleBtn(muteSwitch, isMute);
            }
            Log.i(TAG, "mute=" + isMute);
        }

    }

    // 判断是否有权限
    private boolean getMyPermission() {
        if (isSelfCreator && !isSelf(account)) {
            return true;
        }
        if (isSelfManager && identity.getText().toString().equals(getString(R.string.team_member))) {
            return true;
        }
        return false;
    }

    private void addToggleBtn(boolean isMute) {
        muteSwitch = addToggleItemView(KEY_MUTE_MSG, R.string.mute_msg, isMute);
    }

    private void setToggleBtn(SwitchButton btn, boolean isChecked) {
        btn.setCheck(isChecked);
    }

    private SwitchButton addToggleItemView(String key, int titleResId, boolean initState) {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(R.layout.nim_user_profile_toggle_item, null);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources()
                        .getDimension(R.dimen.isetting_item_height));
        vp.setLayoutParams(vlp);
        TextView titleText = vp.findViewById(R.id.user_profile_title);
        titleText.setText(titleResId);
        SwitchButton switchButton = vp.findViewById(R.id.user_profile_toggle);
        switchButton.setCheck(initState);
        switchButton.setOnChangedListener(onChangedListener);
        switchButton.setTag(key);
        toggleLayout.addView(vp);
        if (toggleStateMap == null) {
            toggleStateMap = new HashMap<>();
        }
        toggleStateMap.put(key, initState);
        return switchButton;
    }

    private SwitchButton.OnChangedListener onChangedListener = new SwitchButton.OnChangedListener() {

        @Override
        public void OnChanged(View v, final boolean checkState) {
            final String key = (String) v.getTag();
            if (!NetworkUtil.isNetAvailable(AdvancedTeamMemberInfoActivity.this)) {
                ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, R.string.network_is_not_available);
                if (key.equals(KEY_MUTE_MSG)) {
                    muteSwitch.setCheck(!checkState);
                }
                return;
            }
            updateStateMap(checkState, key);
            if (key.equals(KEY_MUTE_MSG)) {
                NIMClient.getService(TeamService.class).muteTeamMember(teamId, account, checkState).setCallback(
                        new RequestCallback<Void>() {

                            @Override
                            public void onSuccess(Void param) {
                                if (checkState) {
                                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "群禁言成功");
                                } else {
                                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "取消群禁言成功");
                                }
                            }

                            @Override
                            public void onFailed(int code) {
                                if (code == 408) {
                                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this,
                                            R.string.network_is_not_available);
                                } else {
                                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "on failed:" + code);
                                }
                                updateStateMap(!checkState, key);
                                muteSwitch.setCheck(!checkState);
                            }

                            @Override
                            public void onException(Throwable exception) {
                            }
                        });
            }
        }
    };

    private void updateStateMap(boolean checkState, String key) {
        if (toggleStateMap.containsKey(key)) {
            toggleStateMap.put(key, checkState);  // update state
            Log.i(TAG, "toggle " + key + "to " + checkState);
        }
    }

    private void loadMemberInfo() {
        viewMember = NimUIKit.getTeamProvider().getTeamMember(teamId, account);
        if (viewMember != null) {
            updateMemberInfo();
        } else {
            requestMemberInfo();
        }
    }

    /**
     * 查询群成员的信息
     */
    private void requestMemberInfo() {
        NimUIKit.getTeamProvider().fetchTeamMember(teamId, account, new SimpleCallback<TeamMember>() {

            @Override
            public void onResult(boolean success, TeamMember member, int code) {
                if (success && member != null) {
                    viewMember = member;
                    updateMemberInfo();
                }
            }
        });
    }

    private void initMemberInfo() {
       // memberName.setText(UserInfoHelper.getUserDisplayName(account));
        headImageView.loadBuddyAvatar(account);
    }

    private void updateMemberInfo() {
        updateMemberIdentity();
        updateMemberNickname();
        updateSelfIndentity();
        updateRemoveBtn();
        updateMemberInvitor();
    }

    private void updateMemberInvitor() {
        if (viewMember.getInvitorAccid() == null) {
            List<String> accids = new ArrayList<>();
            accids.add(viewMember.getAccount());
            NIMClient.getService(TeamService.class).getMemberInvitor(viewMember.getTid(), accids).setCallback(
                    new RequestCallbackWrapper<Map<String, String>>() {

                        @Override
                        public void onResult(int code, Map<String, String> result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS && result != null) {
                                String invitor = result.get(viewMember.getAccount());
                                if (invitor != null) {
                                    invitorInfo.setText(invitor);
                                }
                            }
                        }
                    });
        } else {
            invitorInfo.setText(viewMember.getInvitorAccid());
        }
    }

    /**
     * 更新群成员的身份
     */
    private void updateMemberIdentity() {
        if (viewMember.getType() == TeamMemberType.Manager) {
            identity.setText(R.string.team_admin);
            isSetAdmin = true;
        } else {
            isSetAdmin = false;
            if (viewMember.getType() == TeamMemberType.Owner) {
                identity.setText(R.string.team_creator);
            } else {
                identity.setText(R.string.team_member);
            }
        }
    }

    /**
     * 更新成员群昵称
     */
    private void updateMemberNickname() {
        nickName.setText(
                viewMember.getTeamNick() != null ? viewMember.getTeamNick() : getString(R.string.team_nickname_none));
    }

    /**
     * 获得用户自己的身份
     */
    private void updateSelfIndentity() {
        TeamMember selfTeamMember = NimUIKit.getTeamProvider().getTeamMember(teamId, NimUIKit.getAccount());
        if (selfTeamMember == null) {
            return;
        }
        if (selfTeamMember.getType() == TeamMemberType.Manager) {
            isSelfManager = true;
        } else if (selfTeamMember.getType() == TeamMemberType.Owner) {
            isSelfCreator = true;
        }
    }

    /**
     * 更新是否显移除本群按钮
     */
    private void updateRemoveBtn() {
        if (viewMember.getAccount().equals(NimUIKit.getAccount())) {
            removeBtn.setVisibility(View.GONE);
        } else {
            if (isSelfCreator) {
                removeBtn.setVisibility(View.VISIBLE);
            } else if (isSelfManager) {
                if (viewMember.getType() == TeamMemberType.Owner) {
                    removeBtn.setVisibility(View.GONE);
                } else if (viewMember.getType() == TeamMemberType.Normal) {
                    removeBtn.setVisibility(View.VISIBLE);
                } else {
                    removeBtn.setVisibility(View.GONE);
                }
            } else {
                removeBtn.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 更新群昵称
     *
     * @param name
     */
    private void setNickname(final String name) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        NIMClient.getService(TeamService.class).updateMemberNick(teamId, account, name).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        nickName.setText(name != null ? name : getString(R.string.team_nickname_none));
                        ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, R.string.update_success);
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.nickname_container) {
            editNickname();

        } else if (i == R.id.identity_container) {
            showManagerButton();

        } else if (i == R.id.team_remove_member) {
            showConfirmButton();
        } else if (i == R.id.report_container) {
            //举报
            complaintUserEvent.action(AdvancedTeamMemberInfoActivity.this, userId);
        } else if (i == R.id.deletefriend_container) {
            //删除好友
            onRemoveFriend();
        } else if (i == R.id.addriend_container) {
            //添加好友
            if (FLAG_ADD_FRIEND_DIRECTLY) {
                doAddFriend("", true);  // 直接加为好友
            } else {
                onAddFriendByVerify(); // 发起好友验证请求
            }
        } else {
        }
    }
    /**
     * 通过验证方式添加好友
     */
    private void onAddFriendByVerify() {
        final EasyEditDialog requestDialog = new EasyEditDialog(this);
        requestDialog.setEditTextMaxLength(32);
        requestDialog.setTitle("好友验证请求");
        requestDialog.addNegativeButtonListener(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
            }
        });
        requestDialog.addPositiveButtonListener(R.string.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
                String msg = requestDialog.getEditMessage();
                doAddFriend(msg, false);
            }
        });
        requestDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        requestDialog.show();
    }

    private void doAddFriend(String msg, boolean addDirectly) {
        if (!NetworkUtil.isNetAvailable(this)) {
            ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, R.string.network_is_not_available);
            return;
        }
        if (!TextUtils.isEmpty(account) && account.equals(NimUIKit.getAccount())) {
            ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "不能加自己为好友");
            return;
        }
        final VerifyType verifyType = addDirectly ? VerifyType.DIRECT_ADD : VerifyType.VERIFY_REQUEST;
        DialogMaker.showProgressDialog(this, "", true);
        //发起好友请求
        if (userId != null) {
            addFriendEvent.addFriend(userId,msg, new EventInterface.ApiResponse<Object>() {
                @Override
                public void onSuccess(Object data) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "添加好友请求发送成功");
                }

                @Override
                public void onFail(int type) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "发起失败！");
                }
            });
        }
    }
    private void onRemoveFriend() {
        Log.i(TAG, "onRemoveFriend");
        if (!NetworkUtil.isNetAvailable(this)) {
            ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, R.string.network_is_not_available);
            return;
        }
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(AdvancedTeamMemberInfoActivity.this, "删除好友",
                "删除好友后，将同时解除双方的好友关系", true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {

                        if (userId != null) {
                            DialogMaker.showProgressDialog(AdvancedTeamMemberInfoActivity.this, "", true);
                            deleteFriendEvent.deleteFriend(userId,new EventInterface.ApiResponse<Object>() {
                                @Override
                                public void onSuccess(Object data) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "删除成功");
                                    finish();
                                }

                                @Override
                                public void onFail(int type) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(AdvancedTeamMemberInfoActivity.this, "删除失败！");
                                }
                            });
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AdvancedTeamNicknameActivity.REQ_CODE_TEAM_NAME && resultCode == Activity.RESULT_OK) {
            final String teamName = data.getStringExtra(AdvancedTeamNicknameActivity.EXTRA_NAME);
            setNickname(teamName);
        }
    }

    /**
     * 设置群昵称
     */
    private void editNickname() {
        if (isSelfCreator || isSelf(account)) {
            AdvancedTeamNicknameActivity.start(AdvancedTeamMemberInfoActivity.this, nickName.getText().toString());
        } else if (isSelfManager && identity.getText().toString().equals(getString(R.string.team_member))) {
            AdvancedTeamNicknameActivity.start(AdvancedTeamMemberInfoActivity.this, nickName.getText().toString());
        } else {
            ToastHelper.showToast(this, R.string.no_permission);
        }
    }


    /**
     * 显示设置管理员按钮
     */
    private void showManagerButton() {
        if (identity.getText().toString().equals(getString(R.string.team_creator))) {
            return;
        }
        if (!isSelfCreator) {
            return;
        }
        // TODO: 2020-01-10 改为接口请求
//        if (identity.getText().toString().equals(getString(R.string.team_member))) {
//            switchManagerButton(true);
//        } else {
//            switchManagerButton(false);
//        }
    }

    /**
     * 转换设置或取消管理员按钮
     *
     * @param isSet 是否设置
     */
    private void switchManagerButton(boolean isSet) {
        if (isSet) {
            if (setAdminDialog == null) {
                List<String> btnNames = new ArrayList<>();
                btnNames.add(getString(R.string.set_team_admin));
                setAdminDialog = new MenuDialog(this, btnNames, new MenuDialog.MenuDialogOnButtonClickListener() {

                    @Override
                    public void onButtonClick(String name) {
                        addManagers();
                        setAdminDialog.dismiss();
                    }
                });
            }
            setAdminDialog.show();
        } else {
            if (cancelAdminDialog == null) {
                List<String> btnNames = new ArrayList<>();
                btnNames.add(getString(R.string.cancel_team_admin));
                cancelAdminDialog = new MenuDialog(this, btnNames, new MenuDialog.MenuDialogOnButtonClickListener() {

                    @Override
                    public void onButtonClick(String name) {
                        removeManagers();
                        cancelAdminDialog.dismiss();
                    }
                });
            }
            cancelAdminDialog.show();
        }
    }

    /**
     * 添加管理员权限
     */
    private void addManagers() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty));
        ArrayList<String> accountList = new ArrayList<>();
        accountList.add(account);
        NIMClient.getService(TeamService.class).addManagers(teamId, accountList).setCallback(
                new RequestCallback<List<TeamMember>>() {

                    @Override
                    public void onSuccess(List<TeamMember> managers) {
                        DialogMaker.dismissProgressDialog();
                        identity.setText(R.string.team_admin);
                        ToastHelper.showToastLong(AdvancedTeamMemberInfoActivity.this, R.string.update_success);
                        viewMember = managers.get(0);
                        updateMemberInfo();
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToastLong(AdvancedTeamMemberInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 撤销管理员权限
     */
    private void removeManagers() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty));
        ArrayList<String> accountList = new ArrayList<>();
        accountList.add(account);
        NIMClient.getService(TeamService.class).removeManagers(teamId, accountList).setCallback(
                new RequestCallback<List<TeamMember>>() {

                    @Override
                    public void onSuccess(List<TeamMember> members) {
                        DialogMaker.dismissProgressDialog();
                        identity.setText(R.string.team_member);
                        ToastHelper.showToastLong(AdvancedTeamMemberInfoActivity.this, R.string.update_success);
                        viewMember = members.get(0);
                        updateMemberInfo();
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToastLong(AdvancedTeamMemberInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 移除群成员确认
     */
    private void showConfirmButton() {
        EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

            @Override
            public void doCancelAction() {
            }

            @Override
            public void doOkAction() {
                removeMember();
            }
        };
        final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, getString(
                R.string.team_member_remove_confirm), getString(R.string.remove), getString(R.string.cancel), true,
                listener);
        dialog.show();
    }

    /**
     * 移除群成员
     */
    private void removeMember() {
        kickGroupEvent.kickGroup(teamId, account, new EventInterface.ApiResponse<Object>() {
            @Override
            public void onSuccess(Object data) {
                makeIntent(account, isSetAdmin, true);
                finish();
                ToastHelper.showToastLong(AdvancedTeamMemberInfoActivity.this, "移除成功");
            }

            @Override
            public void onFail(int type) {
                ToastHelper.showToastLong(AdvancedTeamMemberInfoActivity.this,
                        String.format("移除失败", type));
            }
        });
    }

    @Override
    public void onBackPressed() {
        makeIntent(account, isSetAdmin, false);
        super.onBackPressed();
    }

    /**
     * 设置返回的Intent
     *
     * @param account    帐号
     * @param isSetAdmin 是否设置为管理员
     * @param value      是否移除群成员
     */
    private void makeIntent(String account, boolean isSetAdmin, boolean value) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, account);
        intent.putExtra(EXTRA_ISADMIN, isSetAdmin);
        intent.putExtra(EXTRA_ISREMOVE, value);
        setResult(RESULT_OK, intent);
    }

    private boolean isSelf(String account) {
        return NimUIKit.getAccount().equals(account);
    }

    private static EventInterface.KickGroupEvent kickGroupEvent;

    public static void setKickGroupEvent(EventInterface.KickGroupEvent kickGroupEvent) {
        AdvancedTeamMemberInfoActivity.kickGroupEvent = kickGroupEvent;
    }
    private static EventInterface.GetUserInfoEvent getUserInfoEvent;

    public static void getUserInfoEvent(EventInterface.GetUserInfoEvent getUserInfoEvent) {
        AdvancedTeamMemberInfoActivity.getUserInfoEvent = getUserInfoEvent;
    }

    private static EventInterface.ComplaintUserEvent complaintUserEvent;
    public static void setComplaintUserEvent(EventInterface.ComplaintUserEvent complaintUserEvent) {
        AdvancedTeamMemberInfoActivity.complaintUserEvent = complaintUserEvent;
    }
    private static EventInterface.DeleteFriendEvent deleteFriendEvent;
    public static void setDeleteFriendEvent(EventInterface.DeleteFriendEvent deleteFriendEvent) {
        AdvancedTeamMemberInfoActivity.deleteFriendEvent = deleteFriendEvent;
    }
    private static EventInterface.AddFriendEvent addFriendEvent;
    public static void setAddFriendEvent(EventInterface.AddFriendEvent addFriendEvent) {
        AdvancedTeamMemberInfoActivity.addFriendEvent = addFriendEvent;
    }
}
