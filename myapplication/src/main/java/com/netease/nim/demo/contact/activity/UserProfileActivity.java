package com.netease.nim.demo.contact.activity;

import android.app.Activity;
import android.content.Context;
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

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.constant.UserConstant;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.business.team.activity.AdvancedTeamInfoActivity;
import com.netease.nim.uikit.business.team.activity.NIMMsgTipSelectActivity;
import com.netease.nim.uikit.business.team.model.NimUserIInfoBean;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyEditDialog;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.widget.SwitchButton;
import com.netease.nim.uikit.common.util.DoubleClickUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.PhoneUtils;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.MuteListChangedNotify;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

/**
 * 用户资料页面
 * Created by huangjun on 2015/8/11.
 */
public class UserProfileActivity extends UI {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private static final int REQUEST_CODE_MSGTIP= 110;
    private final boolean FLAG_ADD_FRIEND_DIRECTLY = false; // 是否直接加为好友开关，false为需要好友申请
    private final String KEY_BLACK_LIST = "black_list";
    private final String KEY_MSG_NOTICE = "msg_notice";
    private final String KEY_RECENT_STICKY = "recent_contacts_sticky";

    private String account;
    private String userId;
    private int isFriend;
    // 基本信息
    private HeadImageView headImageView;
    private TextView nameText;
    private ImageView genderImage;
    private TextView accountText;
    private TextView birthdayText;
    private TextView mobileText;
    private TextView emailText;
    private TextView signatureText;
    private RelativeLayout birthdayLayout;
    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private RelativeLayout signatureLayout;
    private RelativeLayout aliasLayout;
    private TextView nickText;
    private RelativeLayout qrcodeLayout;//二维码
    private RelativeLayout reportLayout;//举报
    private RelativeLayout clearmsgLayout;//清空聊天消息
    private RelativeLayout notification_config_layout;//消息提醒
    private TextView notificationConfigText;

    // 开关
    private ViewGroup toggleLayout;
    private Button addFriendBtn;
    private Button removeFriendBtn;
    private Button chatBtn;
    private SwitchButton blackSwitch;
    private SwitchButton noticeSwitch;
    private SwitchButton stickySwitch;

    public static void start(Context context, String account) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String account, String title) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String account, String userId,int isFriend, String title) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_TITLE, title);
        intent.putExtra(Extras.EXTRA_USER_ID, userId);
        intent.putExtra(Extras.EXTRA_IS_FRIEND, isFriend);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        userId = getIntent().getStringExtra(Extras.EXTRA_USER_ID);
        isFriend = getIntent().getIntExtra(Extras.EXTRA_IS_FRIEND,-1);

        String title = getIntent().getStringExtra(Extras.EXTRA_TITLE);
        if (TextUtils.isEmpty(account)) {
            ToastHelper.showToast(UserProfileActivity.this, "传入的帐号为空");
            finish();
            return;
        }

        ToolBarOptions options = new NimToolBarOptions();
        if (TextUtils.isEmpty(title)) {
            options.titleId = R.string.user_profile;
        } else {
            options.titleString = title;
        }
        setToolBar(R.id.toolbar, options);
        initActionbar();
        findViews();
        updateUserInfo();
        updateToggleView();
        //加个loading
        DialogMaker.showProgressDialog(this, null, false);
        getUserInfoEvent.getUserInfo(account, new EventInterface.ApiResponse<NimUserIInfoBean>() {
            @Override
            public void onSuccess(NimUserIInfoBean data) {
                DialogMaker.dismissProgressDialog();
                if(data!=null){
                    userId=data.id+"";
                    isFriend=data.is_friend;
                    updateUserOperatorView();
                }
            }

            @Override
            public void onFail(int type) {
                DialogMaker.dismissProgressDialog();
            }
        });
        registerObserver(true);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObserver(false);
    }

    private void registerObserver(boolean register) {
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        NIMClient.getService(FriendServiceObserve.class).observeMuteListChangedNotify(muteListChangedNotifyObserver, register);
    }

    Observer<MuteListChangedNotify> muteListChangedNotifyObserver = new Observer<MuteListChangedNotify>() {
        @Override
        public void onEvent(MuteListChangedNotify notify) {
            //setToggleBtn(noticeSwitch, !notify.isMute());
        }
    };

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onDeletedFriends(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            updateUserOperatorView();
        }
    };

    private void findViews() {
        headImageView = findView(R.id.user_head_image);
        nameText = findView(R.id.user_name);
        genderImage = findView(R.id.gender_img);
        accountText = findView(R.id.user_account);
        toggleLayout = findView(R.id.toggle_layout);
        addFriendBtn = findView(R.id.add_buddy);
        chatBtn = findView(R.id.begin_chat);
        removeFriendBtn = findView(R.id.remove_buddy);
        birthdayLayout = findView(R.id.birthday);
        nickText = findView(R.id.user_nick);
        birthdayText = birthdayLayout.findViewById(R.id.value);
        phoneLayout = findView(R.id.phone);
        mobileText = phoneLayout.findViewById(R.id.value);
        emailLayout = findView(R.id.email);
        emailText = emailLayout.findViewById(R.id.value);
        signatureLayout = findView(R.id.signature);
        qrcodeLayout = findView(R.id.qrcode);
        signatureText = signatureLayout.findViewById(R.id.value);
        aliasLayout = findView(R.id.alias);
        reportLayout= findView(R.id.report);
        clearmsgLayout= findView(R.id.clearmsg);
        notification_config_layout= findView(R.id.notification_config_layout);

        reportLayout.setVisibility(View.VISIBLE);
        reportLayout.findViewById(R.id.arrow_right).setVisibility(View.VISIBLE);
        ((TextView) birthdayLayout.findViewById(R.id.attribute)).setText(R.string.birthday);
        ((TextView) phoneLayout.findViewById(R.id.attribute)).setText(R.string.phone);
        ((TextView) emailLayout.findViewById(R.id.attribute)).setText(R.string.email);
        ((TextView) signatureLayout.findViewById(R.id.attribute)).setText(R.string.signature);
        ((TextView) aliasLayout.findViewById(R.id.attribute)).setText(R.string.alias);
        ((TextView) qrcodeLayout.findViewById(R.id.attribute)).setText("二维码");
        ((TextView) reportLayout.findViewById(R.id.attribute)).setText("举报");
        ((TextView) clearmsgLayout.findViewById(R.id.attribute)).setText("清空聊天记录");
        ((TextView) notification_config_layout.findViewById(R.id.attribute)).setText("消息提醒");
        notificationConfigText = (TextView) notification_config_layout.findViewById(R.id.value);
        notification_config_layout.findViewById(R.id.arrow_right).setVisibility(View.VISIBLE);
        addFriendBtn.setOnClickListener(onClickListener);
        chatBtn.setOnClickListener(onClickListener);
        removeFriendBtn.setOnClickListener(onClickListener);
        notification_config_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                Intent min=new Intent();
                int type = 0;
                if(mMessageNotify){
                    type = 0;
                }else{
                    type = 2;
                }
                min.putExtra("type",type);
                min.putExtra("user",1);
                min.setClass(UserProfileActivity.this, NIMMsgTipSelectActivity.class);
                startActivityForResult(min,REQUEST_CODE_MSGTIP);
            }
        });
        aliasLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                UserProfileEditItemActivity.startActivity(UserProfileActivity.this, UserConstant.KEY_ALIAS, account);
            }
        });
        qrcodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
               // UserProfileEditItemActivity.startActivity(UserProfileActivity.this, UserConstant.KEY_ALIAS, account);
                //TODO 进入二维码页面
                qRcodeEvent.QRcodeEvent(UserProfileActivity.this, userId,account,isFriend);
            }
        });
        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                // UserProfileEditItemActivity.startActivity(UserProfileActivity.this, UserConstant.KEY_ALIAS, account);
                // 举报
                complaintUserEvent.action(UserProfileActivity.this, userId);
            }
        });
        clearmsgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserProfileEditItemActivity.startActivity(UserProfileActivity.this, UserConstant.KEY_ALIAS, account);
                // 清空聊天记录
                String name = UserInfoHelper.getUserName(account);
                EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(UserProfileActivity.this,null ,
                        "确定删除和"+name+"的聊天记录吗？", true,
                        new EasyAlertDialogHelper.OnDialogActionListener() {

                            @Override
                            public void doCancelAction() {

                            }

                            @Override
                            public void doOkAction() {
                                //DialogMaker.showProgressDialog(UserProfileActivity.this, "", true);
                                NIMClient.getService(MsgService.class).clearChattingHistory(account, SessionTypeEnum.P2P);
                                MessageListPanelHelper.getInstance().notifyClearMessages(account);
                            }
                        });
                if (!isFinishing() && !isDestroyedCompatible()) {
                    dialog.show();
                }

            }
        });

        mMessageNotify=  NIMClient.getService(FriendService.class).isNeedMessageNotify(account);
        mMessageNotify();
    }
     private  boolean mMessageNotify=true;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_MSGTIP:
                //设置消息提醒
                int typeTemp=  data.getIntExtra("type",0);

                if(typeTemp==0){
                    mMessageNotify=true;
                }else  if(typeTemp==2){
                    mMessageNotify=false;
                }
                DialogMaker.showProgressDialog(UserProfileActivity.this,
                        getString(R.string.empty), true);

                NIMClient.getService(FriendService.class).setMessageNotify(account, mMessageNotify)
                        .setCallback(new RequestCallback<Void>() {

                            @Override
                            public void onSuccess(Void param) {
                                DialogMaker.dismissProgressDialog();
                                mMessageNotify();
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogMaker.dismissProgressDialog();
                                Log.d(TAG, "muteTeam failed code:" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                DialogMaker.dismissProgressDialog();
                            }
                        });
                break;
            default:
                break;
        }
    }
    private void mMessageNotify(){
        if(mMessageNotify){
            notificationConfigText.setText(getString(com.netease.nim.uikit.R.string.team_notify_all));
        }else{
            notificationConfigText.setText(getString(com.netease.nim.uikit.R.string.team_notify_mute));
        }
    }

    private void initActionbar() {
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        if (!TextUtils.equals(account, DemoCache.getAccount())) {
            toolbarView.setVisibility(View.GONE);
            return;
        } else {
            toolbarView.setVisibility(View.VISIBLE);
        }
        toolbarView.setText(R.string.edit);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                UserProfileSettingActivity.start(UserProfileActivity.this, account);
            }
        });
    }

    private void setToggleBtn(SwitchButton btn, boolean isChecked) {
        btn.setCheck(isChecked);
    }

    private void updateUserInfo() {
        if (NimUIKit.getUserInfoProvider().getUserInfo(account) != null) {
            updateUserInfoView();
            return;
        }

        NimUIKit.getUserInfoProvider().getUserInfoAsync(account, new SimpleCallback<NimUserInfo>() {

            @Override
            public void onResult(boolean success, NimUserInfo result, int code) {
                updateUserInfoView();
            }
        });
    }

    private void updateUserInfoView() {
        //  accountText.setText("帐号：" + account);

        headImageView.loadBuddyAvatar(account);

        if (TextUtils.equals(account, DemoCache.getAccount())) {
            nameText.setText(UserInfoHelper.getUserName(account));
        }

        final NimUserInfo userInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(account);
        if (userInfo == null) {
            LogUtil.e(TAG, "userInfo is null when updateUserInfoView");
            return;
        }

        if (userInfo.getGenderEnum() == GenderEnum.MALE) {
            genderImage.setVisibility(View.VISIBLE);
            genderImage.setBackgroundResource(R.drawable.nim_male);
        } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
            genderImage.setVisibility(View.VISIBLE);
            genderImage.setBackgroundResource(R.drawable.nim_female);
        } else {
            genderImage.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(userInfo.getBirthday())) {
//            birthdayLayout.setVisibility(View.VISIBLE);
//            birthdayText.setText(userInfo.getBirthday());
//        } else {
//            birthdayLayout.setVisibility(View.GONE);
//        }

        if (!TextUtils.isEmpty(userInfo.getMobile())) {
            phoneLayout.setVisibility(View.VISIBLE);
            mobileText.setText(userInfo.getMobile());
        } else {
            phoneLayout.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(userInfo.getEmail())) {
//            emailLayout.setVisibility(View.VISIBLE);
//            emailText.setText(userInfo.getEmail());
//        } else {
//            emailLayout.setVisibility(View.GONE);
//        }


    }

    private void updateUserOperatorView() {
        chatBtn.setVisibility(View.GONE);
       // System.out.println("-------------isFriend="+isFriend);

        if (isFriend==1) {
            accountText.setText("帐号：" + account);
      //  if (NIMClient.getService(FriendService.class).isMyFriend(account)) {
            removeFriendBtn.setVisibility(View.VISIBLE);
            addFriendBtn.setVisibility(View.GONE);
            updateAlias(true);
            updateQrcode(true);
            final NimUserInfo userInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(account);
            if (userInfo == null) {
                LogUtil.e(TAG, "userInfo is null when updateUserInfoView");
                return;
            }
            if (!TextUtils.isEmpty(userInfo.getSignature())) {
                signatureLayout.setVisibility(View.VISIBLE);
                signatureText.setText(userInfo.getSignature());
            } else {
                signatureLayout.setVisibility(View.GONE);
            }
        } else {
            accountText.setText("帐号：" + PhoneUtils.blurPhone(account));

            if (TextUtils.equals(account, DemoCache.getAccount())) {
                addFriendBtn.setVisibility(View.GONE);
                removeFriendBtn.setVisibility(View.GONE);
            }else {
                addFriendBtn.setVisibility(View.VISIBLE);
                removeFriendBtn.setVisibility(View.GONE);
                updateAlias(false);
            }
        }
    }

    private void updateToggleView() {
        if (DemoCache.getAccount() != null && !DemoCache.getAccount().equals(account)) {
            boolean black = NIMClient.getService(FriendService.class).isInBlackList(account);
            boolean notice = NIMClient.getService(FriendService.class).isNeedMessageNotify(account);

            if (blackSwitch == null) {
                blackSwitch = addToggleItemView(KEY_BLACK_LIST, R.string.black_list, black);
            } else {
                setToggleBtn(blackSwitch, black);
            }

//            if (noticeSwitch == null) {
//                noticeSwitch = addToggleItemView(KEY_MSG_NOTICE, R.string.msg_notice, notice);
//            } else {
//                setToggleBtn(noticeSwitch, notice);
//            }

            if (NIMClient.getService(FriendService.class).isMyFriend(account)) {
                RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(account, SessionTypeEnum.P2P);
                boolean isSticky = recentContact != null && CommonUtil.isTagSet(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                if (stickySwitch == null) {
                    stickySwitch = addToggleItemView(KEY_RECENT_STICKY, R.string.recent_sticky, isSticky);
                } else {
                    setToggleBtn(stickySwitch, isSticky);
                }
            }
            updateUserOperatorView();
        }
    }


    private SwitchButton addToggleItemView(String key, int titleResId, boolean initState) {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(R.layout.nim_user_profile_toggle_item, null);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.isetting_item_height));
        vp.setLayoutParams(vlp);

        TextView titleText = vp.findViewById(R.id.user_profile_title);
        titleText.setText(titleResId);

        SwitchButton switchButton = vp.findViewById(R.id.user_profile_toggle);
        switchButton.setCheck(initState);
        switchButton.setOnChangedListener(onChangedListener);
        switchButton.setTag(key);

        toggleLayout.addView(vp);
        return switchButton;
    }

    private void updateAlias(boolean isFriend) {
        if (isFriend) {

            aliasLayout.setVisibility(View.VISIBLE);
            aliasLayout.findViewById(R.id.arrow_right).setVisibility(View.VISIBLE);
            String alias = NimUIKit.getContactProvider().getAlias(account);
            String name = UserInfoHelper.getUserName(account);
            ((TextView) aliasLayout.findViewById(R.id.value)).setText(alias);
            if (!TextUtils.isEmpty(alias)) {
                nickText.setVisibility(View.VISIBLE);
                nameText.setText(alias);
                nickText.setText("昵称：" + name);
            } else {
                nickText.setVisibility(View.GONE);
                nameText.setText(name);
            }
        } else {
            aliasLayout.setVisibility(View.GONE);
            aliasLayout.findViewById(R.id.arrow_right).setVisibility(View.GONE);
            nickText.setVisibility(View.GONE);
            nameText.setText(UserInfoHelper.getUserName(account));
        }
    }
    private void updateQrcode(boolean isFriend) {
        if (isFriend) {
            clearmsgLayout.setVisibility(View.VISIBLE);
            qrcodeLayout.setVisibility(View.VISIBLE);
            qrcodeLayout.findViewById(R.id.arrow_right).setVisibility(View.VISIBLE);
            qrcodeLayout.findViewById(R.id.iv_qrcode).setVisibility(View.VISIBLE);

        }
    }
    private boolean iscancel;
    private SwitchButton.OnChangedListener onChangedListener = new SwitchButton.OnChangedListener() {
        @Override
        public void OnChanged(View v, final boolean checkState) {
            final String key = (String) v.getTag();
            if (KEY_RECENT_STICKY.equals(key)) {
                //查询之前是不是存在会话记录
                RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(account, SessionTypeEnum.P2P);
                //置顶
                if (checkState) {
                    //如果之前不存在，创建一条空的会话记录
                    if (recentContact == null) {
                        // RecentContactsFragment 的 MsgServiceObserve#observeRecentContact 观察者会收到通知
                        NIMClient.getService(MsgService.class).createEmptyRecentContact(account,
                                SessionTypeEnum.P2P,
                                RecentContactsFragment.RECENT_TAG_STICKY,
                                System.currentTimeMillis(),
                                true);
                    }
                    // 之前存在，更新置顶flag
                    else {
                        CommonUtil.addTag(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                        NIMClient.getService(MsgService.class).updateRecentAndNotify(recentContact);
                    }
                }
                //取消置顶
                else {
                    if (recentContact != null) {
                        CommonUtil.removeTag(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                        NIMClient.getService(MsgService.class).updateRecentAndNotify(recentContact);
                    }
                }
                return;
            }

            if (!NetworkUtil.isNetAvailable(UserProfileActivity.this)) {
                ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
                if (key.equals(KEY_BLACK_LIST)) {
                    blackSwitch.setCheck(!checkState);
                } else if (key.equals(KEY_MSG_NOTICE)) {
                    noticeSwitch.setCheck(!checkState);
                }
                return;
            }

            if (key.equals(KEY_BLACK_LIST)) {
                if (checkState) {
                    EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(UserProfileActivity.this,"拉黑" ,
                            "拉黑，您将不在收到对方的消息，并 且你们互相看不到对方动态", true,
                            new EasyAlertDialogHelper.OnDialogActionListener() {

                                @Override
                                public void doCancelAction() {
                                    blackSwitch.setCheck(!checkState);
                                    iscancel=true;
                                }

                                @Override
                                public void doOkAction() {
                                    NIMClient.getService(FriendService.class).addToBlackList(account).setCallback(new RequestCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void param) {
                                            ToastHelper.showToast(UserProfileActivity.this, "加入黑名单成功");
                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            if (code == 408) {
                                                ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
                                            } else {
                                                ToastHelper.showToast(UserProfileActivity.this, "on failed：" + code);
                                            }
                                            blackSwitch.setCheck(!checkState);
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
                            ToastHelper.showToast(UserProfileActivity.this, "移除黑名单成功");
                        }

                        @Override
                        public void onFailed(int code) {
                            if (code == 408) {
                                ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
                            } else {
                                ToastHelper.showToast(UserProfileActivity.this, "on failed:" + code);
                            }
                            blackSwitch.setCheck(!checkState);
                        }

                        @Override
                        public void onException(Throwable exception) {

                        }
                    });
                }
            } else if (key.equals(KEY_MSG_NOTICE)) {
                NIMClient.getService(FriendService.class).setMessageNotify(account, checkState).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        if (checkState) {
                            ToastHelper.showToast(UserProfileActivity.this, "开启消息提醒成功");
                        } else {
                            ToastHelper.showToast(UserProfileActivity.this, "关闭消息提醒成功");
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 408) {
                            ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
                        } else {
                            ToastHelper.showToast(UserProfileActivity.this, "on failed:" + code);
                        }
                        noticeSwitch.setCheck(!checkState);
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        }
    };


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == addFriendBtn) {
                if (FLAG_ADD_FRIEND_DIRECTLY) {
                    doAddFriend("", true);  // 直接加为好友
                } else {
                    onAddFriendByVerify(); // 发起好友验证请求
                }
            } else if (v == removeFriendBtn) {
                onRemoveFriend();
            } else if (v == chatBtn) {
                onChat();
            }
        }
    };

    /**
     * 通过验证方式添加好友
     */
    private void onAddFriendByVerify() {
        final EasyEditDialog requestDialog = new EasyEditDialog(this);
        requestDialog.setEditTextMaxLength(32);
        requestDialog.setTitle(getString(R.string.add_friend_verify_tip));
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
            ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
            return;
        }
        if (!TextUtils.isEmpty(account) && account.equals(DemoCache.getAccount())) {
            ToastHelper.showToast(UserProfileActivity.this, "不能加自己为好友");
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
                    ToastHelper.showToast(UserProfileActivity.this, "添加好友请求发送成功");
                }

                @Override
                public void onFail(int type) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(UserProfileActivity.this, "发起失败！");
                }
            });
        } else {
            NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                    .setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            DialogMaker.dismissProgressDialog();
                            updateUserOperatorView();
                            if (VerifyType.DIRECT_ADD == verifyType) {
                                ToastHelper.showToast(UserProfileActivity.this, "添加好友成功");
                            } else {
                                ToastHelper.showToast(UserProfileActivity.this, "添加好友请求发送成功");
                            }
                        }

                        @Override
                        public void onFailed(int code) {
                            DialogMaker.dismissProgressDialog();
                            if (code == 408) {
                                ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
                            } else {
                                ToastHelper.showToast(UserProfileActivity.this, "on failed:" + code);
                            }
                        }

                        @Override
                        public void onException(Throwable exception) {
                            DialogMaker.dismissProgressDialog();
                        }
                    });
        }
    }

    private void onRemoveFriend() {
        Log.i(TAG, "onRemoveFriend");
        if (!NetworkUtil.isNetAvailable(this)) {
            ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
            return;
        }
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.remove_friend),
                getString(R.string.remove_friend_tip), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        DialogMaker.showProgressDialog(UserProfileActivity.this, "", true);
                        boolean deleteAlias = UserPreferences.isDeleteFriendAndDeleteAlias();
                        if (userId != null) {
                            deleteFriendEvent.deleteFriend(userId,new EventInterface.ApiResponse<Object>() {
                                @Override
                                public void onSuccess(Object data) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(UserProfileActivity.this, R.string.remove_friend_success);
                                    finish();
                                }

                                @Override
                                public void onFail(int type) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(UserProfileActivity.this, "删除失败！");
                                }
                            });
                        }else {

                            NIMClient.getService(FriendService.class).deleteFriend(account, deleteAlias).setCallback(new RequestCallback<Void>() {
                                @Override
                                public void onSuccess(Void param) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(UserProfileActivity.this, R.string.remove_friend_success);
                                    finish();
                                }

                                @Override
                                public void onFailed(int code) {
                                    DialogMaker.dismissProgressDialog();
                                    if (code == 408) {
                                        ToastHelper.showToast(UserProfileActivity.this, R.string.network_is_not_available);
                                    } else {
                                        ToastHelper.showToast(UserProfileActivity.this, "on failed:" + code);
                                    }
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    DialogMaker.dismissProgressDialog();
                                }
                            });
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    private void onChat() {
        //防止连击
        if(DoubleClickUtil.isDoubleClick(1000)){
            return;
        }
        Log.i(TAG, "onChat");
        SessionHelper.startP2PSession(this, account);
    }
    private static EventInterface.QRcodeEvent qRcodeEvent;
    private static EventInterface.AddFriendEvent addFriendEvent;
    private static EventInterface.DeleteFriendEvent deleteFriendEvent;
    private static EventInterface.ComplaintUserEvent complaintUserEvent;
    private static EventInterface.GetUserInfoEvent getUserInfoEvent;

    public static void setAddFriendEvent(EventInterface.AddFriendEvent addFriendEvent) {
        UserProfileActivity.addFriendEvent = addFriendEvent;
    }
    public static void setDeleteFriendEvent(EventInterface.DeleteFriendEvent deleteFriendEvent) {
        UserProfileActivity.deleteFriendEvent = deleteFriendEvent;
    }
    public static void setQRcodeEvent(EventInterface.QRcodeEvent qRcodeEvent) {
        UserProfileActivity.qRcodeEvent = qRcodeEvent;
    }
    public static void setComplaintUserEvent(EventInterface.ComplaintUserEvent complaintUserEvent) {
        UserProfileActivity.complaintUserEvent = complaintUserEvent;
    }
    public static void getUserInfoEvent(EventInterface.GetUserInfoEvent getUserInfoEvent) {
        UserProfileActivity.getUserInfoEvent = getUserInfoEvent;
    }
}
