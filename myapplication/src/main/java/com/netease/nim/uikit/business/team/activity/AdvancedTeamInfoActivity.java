package com.netease.nim.uikit.business.team.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamMemberDataChangedObserver;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.contact.core.item.ContactIdFilter;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.business.session.actions.PickImageAction;
import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.business.team.adapter.TeamMemberAdapter;
import com.netease.nim.uikit.business.team.adapter.TeamMemberAdapter.TeamMemberItem;
import com.netease.nim.uikit.business.team.helper.AnnouncementHelper;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.team.model.Announcement;
import com.netease.nim.uikit.business.team.model.NimGroupBean;
import com.netease.nim.uikit.business.team.ui.TeamInfoGridView;
import com.netease.nim.uikit.business.team.viewholder.TeamMemberHolder;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.media.imagepicker.Constants;
import com.netease.nim.uikit.common.media.imagepicker.ImagePickerLauncher;
import com.netease.nim.uikit.common.media.model.GLImage;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyEditDialog;
import com.netease.nim.uikit.common.ui.dialog.MenuDialog;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.widget.SwitchButton;
import com.netease.nim.uikit.common.util.DoubleClickUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamBeInviteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamInviteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamUpdateModeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 高级群群资料页
 * Created by huangjun on 2015/3/17.
 */
public class AdvancedTeamInfoActivity extends UI implements TAdapterDelegate, TeamMemberAdapter.AddMemberCallback,
        TeamMemberHolder.TeamMemberHolderEventListener {

    private static final int REQUEST_CODE_TRANSFER = 101;

    private static final int REQUEST_CODE_MEMBER_LIST = 102;

    private static final int REQUEST_CODE_CONTACT_SELECT = 103;

    private static final int REQUEST_PICK_ICON = 104;

    private static final int REQUEST_CODE_MSGTIP= 110;
    private static final int REQUEST_CODE_VERIFY= 111;

    private static final int ICON_TIME_OUT = 30000;

    // constant
    private static final String TAG = "RegularTeamInfoActivity";

    private static final String EXTRA_ID = "EXTRA_ID";

    public static final String RESULT_EXTRA_REASON = "RESULT_EXTRA_REASON";

    public static final String RESULT_EXTRA_REASON_QUIT = "RESULT_EXTRA_REASON_QUIT";

    public static final String RESULT_EXTRA_REASON_DISMISS = "RESULT_EXTRA_REASON_DISMISS";

    private static final int TEAM_MEMBERS_SHOW_LIMIT = 5;

    // data
    private TeamMemberAdapter adapter;

    private String teamId;

    private Team team;

    private String creator;

    private List<String> memberAccounts;

    private List<TeamMember> members;

    private List<TeamMemberItem> dataSource;

    private MenuDialog dialog;

    private MenuDialog authenDialog;

    private MenuDialog inviteDialog;

    private MenuDialog teamInfoUpdateDialog;

    private MenuDialog teamInviteeDialog;

    private MenuDialog teamNotifyDialog;

    private List<String> managerList;

    private UserInfoObserver userInfoObserver;

    private AbortableFuture<String> uploadFuture;

    // view
    private View headerLayout;

    private HeadImageView teamHeadImage;

    private TextView teamNameText;

    private TextView teamIdText;

    private TextView teamCreateTimeText;

    private TextView teamBusinessCard; // 我的群名片

    private View layoutMime;

    private View layoutTeamMember;

    private TeamInfoGridView gridView;

    private View layoutTeamName;
    private View layoutTeamNick;

    private View layoutTeamAd;
    private View layoutTeamAdministrator;
    private View layoutTeamComplaint;


    private View layoutTeamIntroduce;

    private View layoutTeamAnnouncement;

    private View layoutTeamExtension;

    private View layoutAuthentication;

    private View layoutNotificationConfig;
    private View layoutClearMsgConfig;


    // 邀请他人权限
    private View layoutInvite;

    private TextView inviteText;

    // 群资料修改权限
    private View layoutInfoUpdate;

    private TextView infoUpdateText;

    // 被邀请人身份验证权限
    private View layoutInviteeAuthen;

    private TextView inviteeAutenText;

    private TextView memberCountText;
    private  View layoutgroupclassify;
    private TextView idEdit;
    private TextView groupClassifyName;
    private TextView groupLeveName;

    private TextView introduceEdit;

    private TextView announcementEdit;

    private TextView extensionTextView;

    private TextView authenticationText;

    private TextView notificationConfigText;

    private  SwitchButton msgstickyBtn;
    private Button btn_group_out;

    // state
    private boolean isSelfAdmin = false;

    private boolean isSelfManager = false;

    public static void start(Context context, String tid) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, tid);
        intent.setClass(context, AdvancedTeamInfoActivity.class);
        context.startActivity(intent);
    }

    /**
     * ************************ TAdapterDelegate **************************
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return TeamMemberHolder.class;
    }

    @Override
    public boolean enabled(int position) {
        return false;
    }

    /**
     * ***************************** Life cycle *****************************
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_advanced_team_info_activity);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        parseIntentData();
        findViews();

        initAdapter();
        loadTeamInfo();
        requestMembers();
        initActionbar();
        registerObservers(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroupData();
    }

    private void getGroupData(){
        mGetGroupInfoEvent.getGroupInfo(teamId, new EventInterface.ApiResponse<NimGroupBean>() {
            @Override
            public void onSuccess(NimGroupBean data) {
                if(data!=null){
                    groupClassifyName.setText( data.classify_name);
                    //
                    if(data.cateid==3){
                        groupLeveName.setText("普通群");
                    }else{
                        groupLeveName.setText("高级群");
                    }
//                    if(data.classify_id==0&&data.owner.equals(NimUIKit.getAccount())){
//                        //提示群主，用户去设置群分类
//                    }
                }
            }

            @Override
            public void onFail(int type) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_VERIFY:
                int typeVerifyTemp=  data.getIntExtra("type",0);
                VerifyTypeEnum mVerifyTypeEnum = VerifyTypeEnum.Free;
                if (typeVerifyTemp==0) {
                    mVerifyTypeEnum = VerifyTypeEnum.Free;
                }else if (typeVerifyTemp==1) {
                    mVerifyTypeEnum = VerifyTypeEnum.Apply;
                }else if (typeVerifyTemp==2) {
                    mVerifyTypeEnum = VerifyTypeEnum.Private;
                }
                setAuthen(mVerifyTypeEnum,typeVerifyTemp);
                break;
            case REQUEST_CODE_MSGTIP:
                //设置消息提醒
              int typeTemp=  data.getIntExtra("type",0);
             //   System.out.println("------------11--群组消息提醒typeTemp="+typeTemp);
                TeamMessageNotifyTypeEnum type = TeamMessageNotifyTypeEnum.All;
                if(typeTemp==0){
                    type = TeamMessageNotifyTypeEnum.All;
                }else  if(typeTemp==1){
                    type = TeamMessageNotifyTypeEnum.Manager;
                }else  if(typeTemp==2){
                    type = TeamMessageNotifyTypeEnum.Mute;
                }
                DialogMaker.showProgressDialog(AdvancedTeamInfoActivity.this,
                        getString(R.string.empty), true);
                NIMClient.getService(TeamService.class).muteTeam(teamId, type)
                        .setCallback(new RequestCallback<Void>() {

                            @Override
                            public void onSuccess(Void param) {
                                DialogMaker.dismissProgressDialog();
                                updateTeamNotifyText(
                                        team.getMessageNotifyType());
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogMaker.dismissProgressDialog();
                                teamNotifyDialog.undoLastSelect();
                                Log.d(TAG, "muteTeam failed code:" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                DialogMaker.dismissProgressDialog();
                            }
                        });
                break;
            case REQUEST_CODE_CONTACT_SELECT:
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    inviteMembers(selected);
                }
                break;
            case REQUEST_CODE_TRANSFER:
                final ArrayList<String> target = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (target != null && !target.isEmpty()) {
                    transferTeam(target.get(0));
                }
                break;
            case AdvancedTeamNicknameActivity.REQ_CODE_TEAM_NAME:
                setBusinessCard(data.getStringExtra(AdvancedTeamNicknameActivity.EXTRA_NAME));
                ((TextView) layoutTeamNick.findViewById(R.id.item_detail)).setText(data.getStringExtra(AdvancedTeamNicknameActivity.EXTRA_NAME));
                break;
            case AdvancedTeamMemberInfoActivity.REQ_CODE_REMOVE_MEMBER:
                boolean isSetAdmin = data.getBooleanExtra(AdvancedTeamMemberInfoActivity.EXTRA_ISADMIN, false);
                boolean isRemoveMember = data.getBooleanExtra(AdvancedTeamMemberInfoActivity.EXTRA_ISREMOVE, false);
                String account = data.getStringExtra(EXTRA_ID);
                refreshAdmin(isSetAdmin, account);
                if (isRemoveMember) {
                    removeMember(account);
                }
                break;
            case REQUEST_CODE_MEMBER_LIST:
                boolean isMemberChange = data.getBooleanExtra(AdvancedTeamMemberActivity.EXTRA_DATA, false);
                if (isMemberChange) {
                    requestMembers();
                }
                break;
            case REQUEST_PICK_ICON:
                onPicked(data);
                //
                break;
            default:
                break;
        }
    }

    private void onPicked(Intent data) {
        if (data == null) {
            return;
        }
        ArrayList<GLImage> images = (ArrayList<GLImage>) data.getSerializableExtra(Constants.EXTRA_RESULT_ITEMS);
        if (images == null || images.isEmpty()) {
            return;
        }
        GLImage image = images.get(0);
        updateTeamIcon(image.getPath());
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (authenDialog != null) {
            authenDialog.dismiss();
        }
        registerObservers(false);
        super.onDestroy();
    }

    private void parseIntentData() {
        teamId = getIntent().getStringExtra(EXTRA_ID);
    }

    private void findViews() {
        headerLayout = findViewById(R.id.team_info_header);
        headerLayout.setOnClickListener(v -> showSelector(R.string.set_head_image, REQUEST_PICK_ICON));
        teamHeadImage = findViewById(R.id.team_head_image);
        teamNameText = findViewById(R.id.team_name);
        teamIdText = findViewById(R.id.team_id);
        teamCreateTimeText = findViewById(R.id.team_create_time);
        layoutMime = findViewById(R.id.team_mime_layout);
        ((TextView) layoutMime.findViewById(R.id.item_title)).setText(R.string.my_team_card);
        teamBusinessCard = layoutMime.findViewById(R.id.item_detail);
        layoutMime.setOnClickListener(v -> AdvancedTeamNicknameActivity.start(AdvancedTeamInfoActivity.this,
                teamBusinessCard.getText().toString()));
        layoutTeamMember = findViewById(R.id.team_memeber_layout);
        ((TextView) layoutTeamMember.findViewById(R.id.item_title)).setText(R.string.team_member);
        memberCountText = layoutTeamMember.findViewById(R.id.item_detail);
        gridView = findViewById(R.id.team_member_grid_view);
        layoutTeamMember.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        memberCountText.setOnClickListener(v -> AdvancedTeamMemberActivity.startActivityForResult(AdvancedTeamInfoActivity.this, teamId,
                REQUEST_CODE_MEMBER_LIST));
        View layoutTeamQrcode = findViewById(R.id.team_qrcode_layout);
        ((TextView) layoutTeamQrcode.findViewById(R.id.item_title)).setText("二维码");
        TextView tvTeamQrcode = layoutTeamQrcode.findViewById(R.id.item_detail);
        tvTeamQrcode.setText("");
        tvTeamQrcode.setVisibility(View.GONE);
        ImageView iv_right_arrow = layoutTeamQrcode.findViewById(R.id.iv_right_arrow);
        iv_right_arrow.setVisibility(View.VISIBLE);
        ImageView iv_right_qrcode = layoutTeamQrcode.findViewById(R.id.iv_right_qrcode);
        iv_right_qrcode.setVisibility(View.VISIBLE);
        layoutTeamQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                //社群二维码
                int vipTeam=0;
                if(team!=null&&team.getMemberLimit()>=2000) {
                    vipTeam=1;
                }
                groupQrcodeEvent.action(AdvancedTeamInfoActivity.this, teamId,vipTeam);
            }
        });

        layoutClearMsgConfig = findViewById(R.id.team_clearmsg_config_layout);
        ((TextView) layoutClearMsgConfig.findViewById(R.id.item_title)).setText("清空聊天记录");
        TextView tvTeamclear = layoutClearMsgConfig.findViewById(R.id.item_detail);
        tvTeamclear.setVisibility(View.GONE);
        layoutClearMsgConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空聊天记录
                EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(AdvancedTeamInfoActivity.this,null ,
                        "确定清空聊天记录吗？", true,
                        new EasyAlertDialogHelper.OnDialogActionListener() {

                            @Override
                            public void doCancelAction() {

                            }

                            @Override
                            public void doOkAction() {
                                //DialogMaker.showProgressDialog(UserProfileActivity.this, "", true);
                                NIMClient.getService(MsgService.class).clearChattingHistory(teamId,
                                        SessionTypeEnum.Team);
                                MessageListPanelHelper.getInstance().notifyClearMessages(teamId);

                            }
                        });
                if (!isFinishing() && !isDestroyedCompatible()) {
                    dialog.show();
                }

            }
        });

        View layoutmsgsticky= findViewById(R.id.team_msgsticky_layout);
        ((TextView) layoutmsgsticky.findViewById(R.id.item_title)).setText("聊天置顶");
        ((TextView) layoutmsgsticky.findViewById(R.id.item_detail)).setVisibility(View.GONE);
        msgstickyBtn=  layoutmsgsticky.findViewById(R.id.item_toggle);
        showMsgSticky();
        msgstickyBtn.setOnChangedListener(new SwitchButton.OnChangedListener() {
            @Override
            public void OnChanged(View v, boolean checkState) {
                RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(teamId, SessionTypeEnum.Team);
                if(checkState){
                    //如果之前不存在，创建一条空的会话记录
                    if (recentContact == null) {
                        // RecentContactsFragment 的 MsgServiceObserve#observeRecentContact 观察者会收到通知
                        NIMClient.getService(MsgService.class).createEmptyRecentContact(teamId,
                                SessionTypeEnum.Team,
                                RecentContactsFragment.RECENT_TAG_STICKY,
                                System.currentTimeMillis(),
                                true);
                    }
                    // 之前存在，更新置顶flag
                    else {
                        CommonUtil.addTag(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                        NIMClient.getService(MsgService.class).updateRecentAndNotify(recentContact);
                    }
                }else{
                    if (recentContact != null) {
                        CommonUtil.removeTag(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                        NIMClient.getService(MsgService.class).updateRecentAndNotify(recentContact);
                    }
                }
            }
        });

        btn_group_out= findViewById(R.id.btn_group_out);
        btn_group_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelfAdmin) {
                    //解散社群
                    if(team!=null&&team.getMemberLimit()>=2000){
                        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(AdvancedTeamInfoActivity.this, null,
                                "超级VIP群不允许解散哟~", true,
                                new EasyAlertDialogHelper.OnDialogActionListener() {

                                    @Override
                                    public void doCancelAction() {

                                    }

                                    @Override
                                    public void doOkAction() {
                                    }
                                });
                        if (!isFinishing() && !isDestroyedCompatible()) {
                            dialog.show();
                        }
                    }else {
                        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(AdvancedTeamInfoActivity.this, null,
                                "确定解散该群吗？", true,
                                new EasyAlertDialogHelper.OnDialogActionListener() {

                                    @Override
                                    public void doCancelAction() {

                                    }

                                    @Override
                                    public void doOkAction() {
                                        dismissTeam();

                                    }
                                });
                        if (!isFinishing() && !isDestroyedCompatible()) {
                            dialog.show();
                        }
                    }


                }else{
                    //删除并退出

                    EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(AdvancedTeamInfoActivity.this,null ,
                            "删除并退出，将不再接受此群信息", true,
                            new EasyAlertDialogHelper.OnDialogActionListener() {

                                @Override
                                public void doCancelAction() {

                                }

                                @Override
                                public void doOkAction() {
                                    quitTeam();

                                }
                            });
                    if (!isFinishing() && !isDestroyedCompatible()) {
                        dialog.show();
                    }
                }
            }
        });

        View layoutgroupLeven= findViewById(R.id.team_groupleven_layout);
        ((TextView) layoutgroupLeven.findViewById(R.id.item_title)).setText("群类型");
        groupLeveName = layoutgroupLeven.findViewById(R.id.item_detail);
        groupLeveName.setText(" ");

        layoutgroupclassify= findViewById(R.id.team_groupclassify_layout);
        ((TextView) layoutgroupclassify.findViewById(R.id.item_title)).setText("社群分类");
        groupClassifyName = layoutgroupclassify.findViewById(R.id.item_detail);
        groupClassifyName.setText(" ");
        layoutgroupclassify.setOnClickListener(view -> {
                   if (isSelfAdmin ) {//|| isSelfManager
                        //选择群分类
                        changeGroupClassifyEvent.action(AdvancedTeamInfoActivity.this,teamId);
                    } else {
                    //     ToastHelper.showToast(this, R.string.no_permission);
                    }
         });
        View layoutTeamId = findViewById(R.id.team_id_layout);
        ((TextView) layoutTeamId.findViewById(R.id.item_title)).setText(R.string.team_id);
        idEdit = layoutTeamId.findViewById(R.id.item_detail);
        layoutTeamName = findViewById(R.id.team_name_layout);
        ((TextView) layoutTeamName.findViewById(R.id.item_title)).setText(R.string.team_name);
        layoutTeamNick = findViewById(R.id.team_nick_layout);
        ((TextView) layoutTeamNick.findViewById(R.id.item_title)).setText("我在本群的昵称");
        layoutTeamIntroduce = findViewById(R.id.team_introduce_layout);
        ((TextView) layoutTeamIntroduce.findViewById(R.id.item_title)).setText(R.string.team_introduce);
        introduceEdit = layoutTeamIntroduce.findViewById(R.id.item_detail);
        introduceEdit.setHint(R.string.team_introduce_hint);
        layoutTeamIntroduce.setOnClickListener(v -> TeamPropertySettingActivity.start(AdvancedTeamInfoActivity.this, teamId, TeamFieldEnum.Introduce,
                team.getIntroduce()));
        layoutTeamAnnouncement = findViewById(R.id.team_announcement_layout);
        ((TextView) layoutTeamAnnouncement.findViewById(R.id.item_title)).setText(R.string.team_annourcement);
        announcementEdit = layoutTeamAnnouncement.findViewById(R.id.item_detail);
        announcementEdit.setHint(R.string.team_announce_hint);
        layoutTeamAnnouncement.setOnClickListener(v -> AdvancedTeamAnnounceActivity.start(AdvancedTeamInfoActivity.this, teamId));
        layoutTeamExtension = findViewById(R.id.team_extension_layout);
        ((TextView) layoutTeamExtension.findViewById(R.id.item_title)).setText(R.string.team_extension);
        extensionTextView = layoutTeamExtension.findViewById(R.id.item_detail);
        extensionTextView.setHint(R.string.team_extension_hint);
        layoutTeamExtension.setOnClickListener(v -> TeamPropertySettingActivity.start(AdvancedTeamInfoActivity.this, teamId, TeamFieldEnum.Extension,
                team.getExtension()));
        // 群消息提醒设置
        initNotify();
        // 身份验证
        findLayoutAuthentication();
        // 邀请他人权限
        findLayoutInvite();
        // 群资料修改权限
        findLayoutInfoUpdate();
        // 被邀请人身份验证
        findLayoutInviteeAuthen();

        findLayoutAd();
        findLayoutAdministrator();
        findLayoutComplaint();
    }

    private void findLayoutAd() {
        layoutTeamAd = findViewById(R.id.team_ad_layout);
        ((TextView) layoutTeamAd.findViewById(R.id.item_title)).setText("广告管理");
        ((TextView) layoutTeamAd.findViewById(R.id.item_detail)).setText("点击管理");
        layoutTeamAd.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("com.juquan.im.remove.group");
            intent.putExtra("group_id", teamId);
            intent.putExtra("type", 6);
            LocalBroadcastManager.getInstance(AdvancedTeamInfoActivity.this).sendBroadcast(intent);
        });
    }

    private void findLayoutAdministrator() {
        layoutTeamAdministrator = findViewById(R.id.team_administrator);
        ((TextView) layoutTeamAdministrator.findViewById(R.id.item_title)).setText("设置管理员");
        ((TextView) layoutTeamAdministrator.findViewById(R.id.item_detail)).setText("点击设置");
        layoutTeamAdministrator.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("com.juquan.im.remove.group");
            intent.putExtra("group_id", teamId);
            intent.putExtra("type", 7);
            LocalBroadcastManager.getInstance(AdvancedTeamInfoActivity.this).sendBroadcast(intent);
        });
    }

    private void findLayoutComplaint() {
        layoutTeamComplaint = findViewById(R.id.team_complaint);
        layoutTeamComplaint.setVisibility(View.VISIBLE);
        ((TextView) layoutTeamComplaint.findViewById(R.id.item_title)).setText("举报该群");
        ((TextView) layoutTeamComplaint.findViewById(R.id.item_detail)).setText(" ");
        layoutTeamComplaint.setOnClickListener(v -> {
            complaintEvent.action(AdvancedTeamInfoActivity.this, teamId);
        });
    }
//    //验证方式设置
//    private void findLayoutVerify() {
//        layoutTeamVerify = findViewById(R.id.team_verify);
//        layoutTeamVerify.setVisibility(View.VISIBLE);
//        ((RelativeLayout) layoutTeamVerify.findViewById(R.id.rl_old)).setVisibility(View.GONE);
//        ((RelativeLayout) layoutTeamVerify.findViewById(R.id.rl_setup_addverify)).setVisibility(View.VISIBLE);
//        CheckBox mCheckBoxNo=  layoutTeamVerify.findViewById(R.id.cb_mine_check_noneed);
//        CheckBox mCheckBoxNeed=  layoutTeamVerify.findViewById(R.id.cb_mine_check_need);
//        mCheckBoxNeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mCheckBoxNo.setChecked(false);
//                    //getP().updateUserAddWay("0");
//                }
//            }
//        });
//        mCheckBoxNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mCheckBoxNeed.setChecked(false);
//                  //  getP().updateUserAddWay("0");
//                }
//            }
//        });
//    }


    /**
     * 打开图片选择器
     */
    private void showSelector(int titleId, final int requestCode) {
        ImagePickerLauncher.pickImage(AdvancedTeamInfoActivity.this, requestCode, titleId);
    }

    /**
     * 群消息提醒设置
     */
    private void initNotify() {
        layoutNotificationConfig = findViewById(R.id.team_notification_config_layout);
        ((TextView) layoutNotificationConfig.findViewById(R.id.item_title)).setText(R.string.team_notification_config);
        notificationConfigText = (TextView) layoutNotificationConfig.findViewById(R.id.item_detail);
        layoutNotificationConfig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
            // showTeamNotifyMenu();
                Intent min=new Intent();
                int type = team.getMessageNotifyType().getValue();
                min.putExtra("type",type);
                min.setClass(AdvancedTeamInfoActivity.this,NIMMsgTipSelectActivity.class);
                startActivityForResult(min,REQUEST_CODE_MSGTIP);

            }
        });
    }


    /**
     * 身份验证布局初始化
     */
    private void findLayoutAuthentication() {
        layoutAuthentication = findViewById(R.id.team_authentication_layout);
        layoutAuthentication.setVisibility(View.GONE);
        ((TextView) layoutAuthentication.findViewById(R.id.item_title)).setText(R.string.team_authentication);
        authenticationText = ((TextView) layoutAuthentication.findViewById(R.id.item_detail));
        layoutAuthentication.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
               // showTeamAuthenMenu();
                Intent min=new Intent();
                int type =  team.getVerifyType().getValue();
                min.putExtra("type",type);
                min.setClass(AdvancedTeamInfoActivity.this,NIMVerifySelectActivity.class);
                startActivityForResult(min,REQUEST_CODE_VERIFY);
            }
        });
    }

    /**
     * 邀请他人权限布局初始化
     */
    private void findLayoutInvite() {
        layoutInvite = findViewById(R.id.team_invite_layout);
        layoutInvite.setVisibility(View.GONE);
        ((TextView) layoutInvite.findViewById(R.id.item_title)).setText(R.string.team_invite);
        inviteText = ((TextView) layoutInvite.findViewById(R.id.item_detail));
        layoutInvite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTeamInviteMenu();
            }
        });
    }

    /**
     * 群资料修改权限布局初始化
     */
    private void findLayoutInfoUpdate() {
        layoutInfoUpdate = findViewById(R.id.team_info_update_layout);
        layoutInfoUpdate.setVisibility(View.GONE);
        ((TextView) layoutInfoUpdate.findViewById(R.id.item_title)).setText(R.string.team_info_update);
        infoUpdateText = layoutInfoUpdate.findViewById(R.id.item_detail);
        layoutInfoUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTeamInfoUpdateMenu();
            }
        });
    }


    /**
     * 被邀请人身份验证布局初始化
     */
    private void findLayoutInviteeAuthen() {
        layoutInviteeAuthen = findViewById(R.id.team_invitee_authen_layout);
        layoutInviteeAuthen.setVisibility(View.GONE);
        ((TextView) layoutInviteeAuthen.findViewById(R.id.item_title)).setText(R.string.team_invitee_authentication);
        inviteeAutenText = layoutInviteeAuthen.findViewById(R.id.item_detail);
        layoutInviteeAuthen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTeamInviteeAuthenMenu();
            }
        });
    }

    private void initActionbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView btn = toolbar.findViewById(R.id.right_btn);
        btn.setVisibility(View.GONE);
        btn.setText("");
//        btn.setBackgroundResource(R.mipmap.ic_menu_dark);
//        btn.setOnClickListener(view -> {
//            showRegularTeamMenu();
//        });
        if (isSelfAdmin)
            layoutTeamName.setOnClickListener(v -> TeamPropertySettingActivity.start(AdvancedTeamInfoActivity.this, teamId, TeamFieldEnum.Name, team.getName()));
    }

    private void initAdapter() {
        memberAccounts = new ArrayList<>();
        members = new ArrayList<>();
        dataSource = new ArrayList<>();
        managerList = new ArrayList<>();
        adapter = new TeamMemberAdapter(this, dataSource, this, null, this);
        adapter.setEventListener(this);
        gridView.setSelector(R.color.transparent);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        gridView.setAdapter(adapter);
    }

    /**
     * 初始化群组基本信息
     */
    private void loadTeamInfo() {
        Team t = NimUIKit.getTeamProvider().getTeamById(teamId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(teamId, new SimpleCallback<Team>() {

                @Override
                public void onResult(boolean success, Team result, int code) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    } else {
                        onGetTeamInfoFailed();
                    }
                }
            });
        }
    }

    private void onGetTeamInfoFailed() {
        ToastHelper.showToast(this, getString(R.string.team_not_exist));
        finish();
    }

    /**
     * 更新群信息
     *
     * @param t
     */
    private void updateTeamInfo(final Team t) {
        this.team = t;
        if (team == null) {
            ToastHelper.showToast(this, getString(R.string.team_not_exist));
            finish();
            return;
        } else {
            creator = team.getCreator();
            if (creator.equals(NimUIKit.getAccount())) {
                isSelfAdmin = true;
            }
            setTextTitle(team.getName());
        }
        teamHeadImage.loadTeamIconByTeam(team);
        teamNameText.setText(team.getName());

        teamIdText.setText(team.getId());
        eachYunxinIdEvent.eachYunxinId(team.getId(), new EventInterface.ApiResponse<Object>() {
            @Override
            public void onSuccess(Object data) {
                idEdit.setText(data.toString());
            }

            @Override
            public void onFail(int type) {
                idEdit.setText(team.getId());
            }
        });
        teamCreateTimeText.setText(TimeUtil.getTimeShowString(team.getCreateTime(), true));
        ((TextView) layoutTeamName.findViewById(R.id.item_detail)).setText(team.getName());
        NimUIKit.getTeamProvider().fetchTeamMember(teamId, NimUIKit.getAccount(), (success, member, code) -> {
            if (success && member != null) {
                ((TextView) layoutTeamNick.findViewById(R.id.item_detail)).setText(member.getTeamNick() != null ? member.getTeamNick() : getString(R.string.team_nickname_none));
                layoutTeamNick.setOnClickListener(view -> {
                    if (team.isMyTeam() || isSelfManager) {
                        AdvancedTeamNicknameActivity.start(AdvancedTeamInfoActivity.this, "");
                    } else {
                        ToastHelper.showToast(this, R.string.no_permission);
                    }
                });
            }
        });
//        NimUIKit.getTeamProvider().fetchTeamMember(teamId, NimUIKit.getAccount(), (success, member, code) -> {
//            if (success && member != null) {
//                layoutgroupclassify.setOnClickListener(view -> {
//                    if (team.isMyTeam() || isSelfManager) {
//                        //选择群分类
//                        changeGroupClassifyEvent.action(AdvancedTeamInfoActivity.this,teamId);
//                    } else {
//                        ToastHelper.showToast(this, R.string.no_permission);
//                    }
//                });
//            }
//        });
        introduceEdit.setText(team.getIntroduce());
        extensionTextView.setText(team.getExtension());
        memberCountText.setText(String.format("共%d人", team.getMemberCount()));
        setAnnouncement(team.getAnnouncement());
        setAuthenticationText(team.getVerifyType());
        updateTeamNotifyText(team.getMessageNotifyType());
        updateInviteText(team.getTeamInviteMode());
        updateInfoUpateText(team.getTeamUpdateMode());
        updateBeInvitedText(team.getTeamBeInviteMode());
        if(team.isMyTeam()){
            btn_group_out.setVisibility(View.VISIBLE);
        }else{
            btn_group_out.setVisibility(View.GONE);
        }
        showGroupBtn();
    }

    /**
     * 更新群成员信息
     *
     * @param m
     */
    private void updateTeamMember(final List<TeamMember> m) {
        if (m != null && m.isEmpty()) {
            return;
        }
        updateTeamBusinessCard(m);
        addTeamMembers(m, true);
    }

    /**
     * 更新我的群名片
     *
     * @param m
     */
    private void updateTeamBusinessCard(List<TeamMember> m) {
        for (TeamMember teamMember : m) {
            if (teamMember != null && teamMember.getAccount().equals(NimUIKit.getAccount())) {
                teamBusinessCard.setText(teamMember.getTeamNick() != null ? teamMember.getTeamNick() : "");
            }
        }
    }

    /**
     * 添加群成员到列表
     *
     * @param m     群成员列表
     * @param clear 是否清除
     */
    private void addTeamMembers(final List<TeamMember> m, boolean clear) {
        if (m == null || m.isEmpty()) {
            return;
        }
        isSelfManager = false;
        isSelfAdmin = false;
        if (clear) {
            this.members.clear();
            this.memberAccounts.clear();
        }
        // add
        if (this.members.isEmpty()) {
            this.members.addAll(m);
        } else {
            for (TeamMember tm : m) {
                if (!this.memberAccounts.contains(tm.getAccount())) {
                    this.members.add(tm);
                }
            }
        }
        // sort
        Collections.sort(this.members, TeamHelper.teamMemberComparator);
        // accounts, manager, creator
        this.memberAccounts.clear();
        this.managerList.clear();
        for (TeamMember tm : members) {
            if (tm == null) {
                continue;
            }
            if (tm.getType() == TeamMemberType.Manager) {
                managerList.add(tm.getAccount());
            }
            if (tm.getAccount().equals(NimUIKit.getAccount())) {
                if (tm.getType() == TeamMemberType.Manager) {
                    isSelfManager = true;
                } else if (tm.getType() == TeamMemberType.Owner) {
                    isSelfAdmin = true;
                    creator = NimUIKit.getAccount();
                }
            }
            this.memberAccounts.add(tm.getAccount());
        }
        updateAuthenView();
        updateTeamMemberDataSource();
    }

    /**
     * 更新身份验证是否显示
     */
    private void updateAuthenView() {
        if (isSelfAdmin || isSelfManager) {
            layoutAuthentication.setVisibility(View.VISIBLE);
           // layoutInvite.setVisibility(View.VISIBLE);
          //  layoutInfoUpdate.setVisibility(View.VISIBLE);
            //layoutInviteeAuthen.setVisibility(View.VISIBLE);
            layoutTeamAd.setVisibility(View.VISIBLE);
            layoutTeamAdministrator.setVisibility(isSelfAdmin ? View.VISIBLE : View.GONE);
            announcementEdit.setHint(R.string.without_content);
        } else {
            layoutAuthentication.setVisibility(View.GONE);
          //  layoutInvite.setVisibility(View.GONE);
         //   layoutInfoUpdate.setVisibility(View.GONE);
         //   layoutInviteeAuthen.setVisibility(View.GONE);
            layoutTeamAd.setVisibility(View.GONE);
            layoutTeamAdministrator.setVisibility(View.GONE);
            introduceEdit.setHint(R.string.without_content);
            announcementEdit.setHint(R.string.without_content);
        }
    }

    /**
     * 更新成员信息
     */
    private void updateTeamMemberDataSource() {
        if (members.size() > 0) {
            gridView.setVisibility(View.VISIBLE);
            layoutTeamMember.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.GONE);
            layoutTeamMember.setVisibility(View.GONE);
            return;
        }
        dataSource.clear();
        // add item
        if (team.getTeamInviteMode() == TeamInviteModeEnum.All || isSelfAdmin || isSelfManager) {
            dataSource.add(
                    new TeamMemberItem(TeamMemberAdapter.TeamMemberItemTag.ADD, null, null, null));
        }
        // member item
        int count = 0;
        String identity = null;
        for (String account : memberAccounts) {
            int limit = TEAM_MEMBERS_SHOW_LIMIT;
            if (team.getTeamInviteMode() == TeamInviteModeEnum.All || isSelfAdmin || isSelfManager) {
                limit = TEAM_MEMBERS_SHOW_LIMIT - 1;
            }
            if (count < limit) {
                identity = getIdentity(account);
                dataSource.add(new TeamMemberItem(TeamMemberAdapter.TeamMemberItemTag.NORMAL, teamId,
                        account, identity));
            }
            count++;
        }
        // refresh
        adapter.notifyDataSetChanged();
        memberCountText.setText(String.format("共%d人", count));
    }

    private String getIdentity(String account) {
        String identity;
        if (creator.equals(account)) {
            identity = TeamMemberHolder.OWNER;
        } else if (managerList.contains(account)) {
            identity = TeamMemberHolder.ADMIN;
        } else {
            identity = null;
        }
        return identity;
    }

    /**
     * *************************** 加载&变更数据源 ********************************
     */
    private void requestMembers() {
        NimUIKit.getTeamProvider().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {

            @Override
            public void onResult(boolean success, List<TeamMember> members, int code) {
                if (success && members != null && !members.isEmpty()) {
                    updateTeamMember(members);
                }
            }
        });
    }
    /**
     * ************************** 群信息变更监听 **************************
     */
    /**
     * 注册群信息更新监听
     *
     * @param register
     */
    private void registerObservers(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamMemberDataChangedObserver(teamMemberObserver, register);
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataObserver, register);
        registerUserInfoChangedObserver(register);
    }

    TeamMemberDataChangedObserver teamMemberObserver = new TeamMemberDataChangedObserver() {

        @Override
        public void onUpdateTeamMember(List<TeamMember> m) {
            for (TeamMember mm : m) {
                for (TeamMember member : members) {
                    if (mm.getAccount().equals(member.getAccount())) {
                        members.set(members.indexOf(member), mm);
                        break;
                    }
                }
            }
            addTeamMembers(m, false);
        }

        @Override
        public void onRemoveTeamMember(List<TeamMember> members) {
            for (TeamMember member : members) {
                removeMember(member.getAccount());
            }
        }
    };

    TeamDataChangedObserver teamDataObserver = new TeamDataChangedObserver() {

        @Override
        public void onUpdateTeams(List<Team> teams) {
            for (Team team : teams) {
                if (team.getId().equals(teamId)) {
                    updateTeamInfo(team);
                    updateTeamMemberDataSource();
                    break;
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
            if (team.getId().equals(teamId)) {
                AdvancedTeamInfoActivity.this.team = team;
                finish();
            }
        }
    };
    /**
     * ******************************* Action *********************************
     */

    /**
     * 从联系人选择器发起邀请成员
     */
    @Override
    public void onAddMember() {
        ContactSelectActivity.Option option = TeamHelper.getContactSelectOption(memberAccounts);
        NimUIKit.startContactSelector(AdvancedTeamInfoActivity.this, option, REQUEST_CODE_CONTACT_SELECT);
    }

    /**
     * 从联系人选择器选择群转移对象
     */
    private void onTransferTeam() {
        if (memberAccounts.size() <= 1) {
            ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.team_transfer_without_member);
            return;
        }
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = "选择群转移的对象";
        option.type = ContactSelectActivity.ContactSelectType.TEAM_MEMBER;
        option.teamId = teamId;
        option.multi = false;
        option.maxSelectNum = 1;
        ArrayList<String> includeAccounts = new ArrayList<>();
        includeAccounts.addAll(memberAccounts);
        option.itemFilter = new ContactIdFilter(includeAccounts, false);
        NimUIKit.startContactSelector(this, option, REQUEST_CODE_TRANSFER);
        dialog.dismiss();
    }

    /**
     * 邀请群成员
     */
    private void inviteMembers(ArrayList<String> accounts) {
        inviteGroupEvent.inviteGroup(teamId, accounts.toString(), new EventInterface.ApiResponse<Object>() {
            @Override
            public void onSuccess(Object data) {
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, "添加群成员成功");
            }

            @Override
            public void onFail(int type) {
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, "invite members failed, type=" + type);
            }
        });
    }

    /**
     * 转让群
     *
     * @param account 转让的帐号
     */
    private void transferTeam(final String account) {
        TeamMember member = NimUIKit.getTeamProvider().getTeamMember(teamId, account);
        if (member == null) {
            ToastHelper.showToast(AdvancedTeamInfoActivity.this, "成员不存在");
            return;
        }
        if (member.isMute()) {
            ToastHelper.showToastLong(AdvancedTeamInfoActivity.this, "该成员已被禁言，请先取消禁言");
            return;
        }
        changeGroupOwnerEvent.changeGroupOwner(teamId, account, new EventInterface.ApiResponse<Object>() {
            @Override
            public void onSuccess(Object data) {
                creator = account;
                updateTeamMember(NimUIKit.getTeamProvider().getTeamMemberList(teamId));
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.team_transfer_success);
            }

            @Override
            public void onFail(int type) {
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.team_transfer_failed);
                Log.e(TAG, "team transfer failed, type=" + type);
            }
        });
    }

    /**
     * 非群主退出群
     */
    private void quitTeam() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        outGroupEvent.outGroup(teamId, new EventInterface.ApiResponse<Object>() {
            @Override
            public void onSuccess(Object data) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.quit_team_success);
                quitTeamClearData();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_QUIT));
                mStartMainEvent.action(AdvancedTeamInfoActivity.this);
                finish();
            }

            @Override
            public void onFail(int type) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.quit_team_failed);
            }
        });
    }
    private void quitTeamClearData() {
        NIMClient.getService(MsgService.class).clearChattingHistory(teamId, SessionTypeEnum.Team);
        NIMClient.getService(MsgService.class).deleteRecentContact2(teamId,SessionTypeEnum.Team);
        MessageListPanelHelper.getInstance().notifyClearMessages(teamId);
//        List<RecentContact> rcs=    NIMClient.getService(MsgService.class).queryRecentContactsBlock();
//        if(rcs!=null&&rcs.size()>0) {
//            for (int i=0;i<rcs.size();i++) {
//                if(rcs.get(i).getContactId().equals(teamId)){
//                    NIMClient.getService(MsgService.class).deleteRecentContact(rcs.get(i));
//                }
//
//            }
//        }
    }

    /**
     * 群主解散群(直接退出)
     */
    private void dismissTeam() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        removeGroupEvent.removeGroup(teamId, new EventInterface.ApiResponse<Object>() {
            @Override
            public void onSuccess(Object data) {
                DialogMaker.dismissProgressDialog();
                quitTeamClearData();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_DISMISS));
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.dismiss_team_success);
                mStartMainEvent.action(AdvancedTeamInfoActivity.this);
                finish();
            }

            @Override
            public void onFail(int type) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.dismiss_team_failed);
            }
        });
    }
    /**
     * ******************************* Event *********************************
     */
    private void showMsgSticky() {
        //查询之前是不是存在会话记录
        RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(teamId, SessionTypeEnum.Team);
        if(recentContact==null||!CommonUtil.isTagSet(recentContact, RecentContactsFragment.RECENT_TAG_STICKY)){
            msgstickyBtn.setCheck(false);
        }else{
            msgstickyBtn.setCheck(true);
        }
        msgstickyBtn.setVisibility(View.VISIBLE);
    }
    private void showGroupBtn() {
        if (isSelfAdmin) {
            btn_group_out.setText("解散社群");
        }else{
            btn_group_out.setText("删除并退出");
        }


    }
    /**
     * 显示菜单
     */
    private void showRegularTeamMenu() {
        //查询之前是不是存在会话记录
        RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(teamId, SessionTypeEnum.Team);
        List<String> btnNames = new ArrayList<>();
        if (isSelfAdmin) {
            btnNames.add(getString(R.string.dismiss_team));
            btnNames.add(getString(R.string.transfer_team));
            btnNames.add((recentContact == null || !CommonUtil.isTagSet(recentContact, RecentContactsFragment.RECENT_TAG_STICKY)) ? "置顶" : "取消置顶");
            btnNames.add(getString(R.string.cancel));
        } else {
            btnNames.add(getString(R.string.quit_team));
            btnNames.add((recentContact == null || !CommonUtil.isTagSet(recentContact, RecentContactsFragment.RECENT_TAG_STICKY)) ? "置顶" : "取消置顶");
            btnNames.add(getString(R.string.cancel));
        }
        dialog = new MenuDialog(this, btnNames, new MenuDialog.MenuDialogOnButtonClickListener() {

            @Override
            public void onButtonClick(String name) {
                if (name.equals(getString(R.string.quit_team))) {
                    quitTeam();
                } else if (name.equals(getString(R.string.dismiss_team))) {
                    dismissTeam();
                } else if (name.equals(getString(R.string.transfer_team))) {
                    onTransferTeam();
                } else if (name.equals("置顶")) {
                    //如果之前不存在，创建一条空的会话记录
                    if (recentContact == null) {
                        // RecentContactsFragment 的 MsgServiceObserve#observeRecentContact 观察者会收到通知
                        NIMClient.getService(MsgService.class).createEmptyRecentContact(teamId,
                                SessionTypeEnum.Team,
                                RecentContactsFragment.RECENT_TAG_STICKY,
                                System.currentTimeMillis(),
                                true);
                    }
                    // 之前存在，更新置顶flag
                    else {
                        CommonUtil.addTag(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                        NIMClient.getService(MsgService.class).updateRecentAndNotify(recentContact);
                    }
                } else if (name.equals("取消置顶")) {
                    if (recentContact != null) {
                        CommonUtil.removeTag(recentContact, RecentContactsFragment.RECENT_TAG_STICKY);
                        NIMClient.getService(MsgService.class).updateRecentAndNotify(recentContact);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showTeamNotifyMenu() {
        if (teamNotifyDialog == null) {
            List<String> btnNames = TeamHelper.createNotifyMenuStrings();
            int type = team.getMessageNotifyType().getValue();
            teamNotifyDialog = new MenuDialog(AdvancedTeamInfoActivity.this, btnNames, type, 3,
                    new MenuDialog.MenuDialogOnButtonClickListener() {

                        @Override
                        public void onButtonClick(String name) {
                            teamNotifyDialog.dismiss();
                            TeamMessageNotifyTypeEnum type = TeamHelper.getNotifyType(name);
                            if (type == null) {
                                return;
                            }
                            DialogMaker.showProgressDialog(AdvancedTeamInfoActivity.this,
                                    getString(R.string.empty), true);
                            NIMClient.getService(TeamService.class).muteTeam(teamId, type)
                                    .setCallback(new RequestCallback<Void>() {

                                        @Override
                                        public void onSuccess(Void param) {
                                            DialogMaker.dismissProgressDialog();
                                            updateTeamNotifyText(
                                                    team.getMessageNotifyType());
                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            DialogMaker.dismissProgressDialog();
                                            teamNotifyDialog.undoLastSelect();
                                            Log.d(TAG, "muteTeam failed code:" + code);
                                        }

                                        @Override
                                        public void onException(Throwable exception) {
                                            DialogMaker.dismissProgressDialog();
                                        }
                                    });
                        }
                    });
        }
        teamNotifyDialog.show();
    }

    /**
     * 显示验证菜单
     */
//    private void showTeamAuthenMenu() {
//        if (authenDialog == null) {
//            List<String> btnNames = TeamHelper.createAuthenMenuStrings();
//            int type = team.getVerifyType().getValue();
//            authenDialog = new MenuDialog(AdvancedTeamInfoActivity.this, btnNames, type, 3,
//                    new MenuDialog.MenuDialogOnButtonClickListener() {
//
//                        @Override
//                        public void onButtonClick(String name) {
//                            authenDialog.dismiss();
//                            if (name.equals(getString(R.string.cancel))) {
//                                return; // 取消不处理
//                            }
//                            VerifyTypeEnum type = TeamHelper.getVerifyTypeEnum(name);
//                            if (type != null) {
//                                setAuthen(type,0);
//                            }
//
//                        }
//                    });
//        }
//        authenDialog.show();
//    }

    /**
     * 显示邀请他人权限菜单
     */
    private void showTeamInviteMenu() {
        if (inviteDialog == null) {
            List<String> btnNames = TeamHelper.createInviteMenuStrings();
            int type = team.getTeamInviteMode().getValue();
            inviteDialog = new MenuDialog(AdvancedTeamInfoActivity.this, btnNames, type, 2,
                    new MenuDialog.MenuDialogOnButtonClickListener() {

                        @Override
                        public void onButtonClick(String name) {
                            inviteDialog.dismiss();
                            if (name.equals(getString(R.string.cancel))) {
                                return; // 取消不处理
                            }
                            TeamInviteModeEnum type = TeamHelper.getInviteModeEnum(name);
                            if (type != null) {
                                updateInviteMode(type);
                            }
                        }
                    });
        }
        inviteDialog.show();
    }

    // 显示群资料修改权限菜单
    private void showTeamInfoUpdateMenu() {
        if (teamInfoUpdateDialog == null) {
            List<String> btnNames = TeamHelper.createTeamInfoUpdateMenuStrings();
            int type = team.getTeamUpdateMode().getValue();
            teamInfoUpdateDialog = new MenuDialog(AdvancedTeamInfoActivity.this, btnNames, type, 2,
                    new MenuDialog.MenuDialogOnButtonClickListener() {

                        @Override
                        public void onButtonClick(String name) {
                            teamInfoUpdateDialog.dismiss();
                            if (name.equals(getString(R.string.cancel))) {
                                return; // 取消不处理
                            }
                            TeamUpdateModeEnum type = TeamHelper.getUpdateModeEnum(name);
                            if (type != null) {
                                updateInfoUpdateMode(type);
                            }
                        }
                    });
        }
        teamInfoUpdateDialog.show();
    }

    // 显示被邀请人身份验证菜单
    private void showTeamInviteeAuthenMenu() {
        if (teamInviteeDialog == null) {
            List<String> btnNames = TeamHelper.createTeamInviteeAuthenMenuStrings();
            int type = team.getTeamBeInviteMode().getValue();
            teamInviteeDialog = new MenuDialog(AdvancedTeamInfoActivity.this, btnNames, type, 2,
                    new MenuDialog.MenuDialogOnButtonClickListener() {

                        @Override
                        public void onButtonClick(String name) {
                            teamInviteeDialog.dismiss();
                            if (name.equals(getString(R.string.cancel))) {
                                return; // 取消不处理
                            }
                            TeamBeInviteModeEnum type = TeamHelper.getBeInvitedModeEnum(
                                    name);
                            if (type != null) {
                                updateBeInvitedMode(type);
                            }
                        }
                    });
        }
        teamInviteeDialog.show();
    }


    /**
     * 设置我的名片
     *
     * @param nickname 群昵称
     */
    private void setBusinessCard(final String nickname) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        NIMClient.getService(TeamService.class).updateMemberNick(teamId, NimUIKit.getAccount(), nickname).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        teamBusinessCard.setText(nickname);
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    @Override
    public void onHeadImageViewClick(String account) {
        // 打开群成员信息详细页面
        if(isSelfAdmin||isSelfManager) {
            AdvancedTeamMemberInfoActivity.startActivityForResult(AdvancedTeamInfoActivity.this, account, teamId);
        }else{
            userProfileEvent.action(AdvancedTeamInfoActivity.this, account);
        }
    }

    /**
     * 设置群公告
     *
     * @param announcement 群公告
     */
    private void setAnnouncement(String announcement) {
        Announcement a = AnnouncementHelper.getLastAnnouncement(teamId, announcement);
        if (a == null) {
            announcementEdit.setText("");
        } else {
            announcementEdit.setText(a.getTitle());
        }
    }

    /**
     * 设置验证模式
     *
     * @param type 验证类型
     */
    private void setAuthen(final VerifyTypeEnum type,int typeVerifyTemp) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty));
        NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.VerifyType, type).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        setAuthenticationText(type);
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                        EventInterface.ApiResponse<Object> apiResponse = new EventInterface.ApiResponse<Object>() {
                            @Override
                            public void onSuccess(Object data) {
                                //ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
//                                DialogMaker.dismissProgressDialog();
//                                ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                            }

                            @Override
                            public void onFail(int type) {
                               // ToastHelper.showToast(AdvancedTeamInfoActivity.this, String.format(getString(R.string.update_failed), type));
                            }
                        };
                        mUpdateGroupJoinmodeEvent.updateGroupJoinmode(teamId,String.valueOf(typeVerifyTemp),apiResponse);
                    }

                    @Override
                    public void onFailed(int code) {
                        authenDialog.undoLastSelect(); // 撤销选择
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 设置验证模式detail显示
     *
     * @param type 验证类型
     */
    private void setAuthenticationText(VerifyTypeEnum type) {
        authenticationText.setText(TeamHelper.getVerifyString(type));
    }

    private void updateTeamNotifyText(TeamMessageNotifyTypeEnum typeEnum) {
        if (typeEnum == TeamMessageNotifyTypeEnum.All) {
            notificationConfigText.setText(getString(R.string.team_notify_all));
        } else if (typeEnum == TeamMessageNotifyTypeEnum.Manager) {
            notificationConfigText.setText(getString(R.string.team_notify_manager));
        } else if (typeEnum == TeamMessageNotifyTypeEnum.Mute) {
            notificationConfigText.setText(getString(R.string.team_notify_mute));
        }
    }

    /**
     * 更新邀请他人权限
     *
     * @param type 邀请他人类型
     */
    private void updateInviteMode(final TeamInviteModeEnum type) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty));
        NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.InviteMode, type).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        updateInviteText(type);
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                    }

                    @Override
                    public void onFailed(int code) {
                        inviteDialog.undoLastSelect(); // 撤销选择
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 更新邀请他人detail显示
     *
     * @param type 邀请他人类型
     */
    private void updateInviteText(TeamInviteModeEnum type) {
        inviteText.setText(TeamHelper.getInviteModeString(type));
    }

    /**
     * 更新群资料修改权限
     *
     * @param type 群资料修改类型
     */
    private void updateInfoUpdateMode(final TeamUpdateModeEnum type) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty));
        NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.TeamUpdateMode, type).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        updateInfoUpateText(type);
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                    }

                    @Override
                    public void onFailed(int code) {
                        teamInfoUpdateDialog.undoLastSelect(); // 撤销选择
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 更新群资料修改detail显示
     *
     * @param type 群资料修改类型
     */
    private void updateInfoUpateText(TeamUpdateModeEnum type) {
        infoUpdateText.setText(TeamHelper.getInfoUpdateModeString(type));

    }

    /**
     * 更新被邀请人权限
     *
     * @param type 被邀请人类型
     */
    private void updateBeInvitedMode(final TeamBeInviteModeEnum type) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty));
        NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.BeInviteMode, type).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        updateBeInvitedText(type);
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                    }

                    @Override
                    public void onFailed(int code) {
                        teamInviteeDialog.undoLastSelect(); // 撤销选择
                        DialogMaker.dismissProgressDialog();
                        ToastHelper.showToast(AdvancedTeamInfoActivity.this,
                                String.format(getString(R.string.update_failed), code));
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
    }

    /**
     * 更新被邀请人detail显示
     *
     * @param type 被邀请人类型
     */
    private void updateBeInvitedText(TeamBeInviteModeEnum type) {
        inviteeAutenText.setText(TeamHelper.getBeInvitedModeString(type));
    }

    /**
     * 移除群成员成功后，删除列表中的群成员
     *
     * @param account 被删除成员帐号
     */
    private void removeMember(String account) {
        if (TextUtils.isEmpty(account)) {
            return;
        }
        memberAccounts.remove(account);
        for (TeamMember m : members) {
            if (m.getAccount().equals(account)) {
                members.remove(m);
                break;
            }
        }
        memberCountText.setText(String.format("共%d人", members.size()));
        for (TeamMemberItem item : dataSource) {
            if (item.getAccount() != null && item.getAccount().equals(account)) {
                dataSource.remove(item);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 是否设置了管理员刷新界面
     *
     * @param isSetAdmin
     * @param account
     */
    private void refreshAdmin(boolean isSetAdmin, String account) {
        if (isSetAdmin) {
            if (managerList.contains(account)) {
                return;
            }
            managerList.add(account);
            updateTeamMemberDataSource();
        } else {
            if (managerList.contains(account)) {
                managerList.remove(account);
                updateTeamMemberDataSource();
            }
        }
    }

    private void registerUserInfoChangedObserver(boolean register) {
        if (register) {
            if (userInfoObserver == null) {
                userInfoObserver = new UserInfoObserver() {

                    @Override
                    public void onUserInfoChanged(List<String> accounts) {
                        adapter.notifyDataSetChanged();
                    }
                };
            }
            NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, true);
        } else {
            NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, false);
        }
    }

    /**
     * 更新头像
     */
    private void updateTeamIcon(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file == null) {
            return;
        }
        DialogMaker.showProgressDialog(this, null, null, true, new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(R.string.team_update_cancel);
            }
        }).setCanceledOnTouchOutside(true);
        LogUtil.i(TAG, "start upload icon, local file path=" + file.getAbsolutePath());
        new Handler().postDelayed(outimeTask, ICON_TIME_OUT);
        uploadFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadFuture.setCallback(new RequestCallbackWrapper<String>() {

            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i(TAG, "upload icon success, url =" + url);
                    NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.ICON, url).setCallback(
                            new RequestCallback<Void>() {

                                @Override
                                public void onSuccess(Void param) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.update_success);
                                    onUpdateDone();
                                }

                                @Override
                                public void onFailed(int code) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(AdvancedTeamInfoActivity.this,
                                            String.format(getString(R.string.update_failed), code));
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    DialogMaker.dismissProgressDialog();
                                }
                            }); // 更新资料
                } else {
                    ToastHelper.showToast(AdvancedTeamInfoActivity.this, R.string.team_update_failed);
                    onUpdateDone();
                }
            }
        });
    }

    private void cancelUpload(int resId) {
        if (uploadFuture != null) {
            uploadFuture.abort();
            ToastHelper.showToast(AdvancedTeamInfoActivity.this, resId);
            onUpdateDone();
        }
    }

    private Runnable outimeTask = new Runnable() {

        @Override
        public void run() {
            cancelUpload(R.string.team_update_failed);
        }
    };

    private void onUpdateDone() {
        uploadFuture = null;
        DialogMaker.dismissProgressDialog();
    }
    private static EventInterface.ChangeGroupClassifyEvent changeGroupClassifyEvent;
    private static EventInterface.ComplaintEvent complaintEvent;
    private static EventInterface.ChangeGroupOwnerEvent changeGroupOwnerEvent;
    private static EventInterface.RemoveGroupEvent removeGroupEvent;
    private static EventInterface.OutGroupEvent outGroupEvent;
    private static EventInterface.InviteGroupEvent inviteGroupEvent;
    private static EventInterface.EachYunxinIdEvent eachYunxinIdEvent;
    private static EventInterface.GroupQrcodeEvent groupQrcodeEvent;
    private static EventInterface.GetGroupInfoEvent mGetGroupInfoEvent;
    private static EventInterface.UserProfileEvent userProfileEvent;
    public static void setChangeGroupClassifyEvent(EventInterface.ChangeGroupClassifyEvent changeGroupClassifyEvent) {
        AdvancedTeamInfoActivity.changeGroupClassifyEvent = changeGroupClassifyEvent;
    }

    public static void setUserProfileEventEvent(EventInterface.UserProfileEvent userProfileEvent) {
        AdvancedTeamInfoActivity.userProfileEvent = userProfileEvent;
    }

    public static void setGroupQrcodeEvent(EventInterface.GroupQrcodeEvent groupQrcodeEvent) {
        AdvancedTeamInfoActivity.groupQrcodeEvent = groupQrcodeEvent;
    }

    public static void setComplaintEvent(EventInterface.ComplaintEvent complaintEvent) {
        AdvancedTeamInfoActivity.complaintEvent = complaintEvent;
    }

    public static void setChangeGroupOwnerEvent(EventInterface.ChangeGroupOwnerEvent changeGroupOwnerEvent) {
        AdvancedTeamInfoActivity.changeGroupOwnerEvent = changeGroupOwnerEvent;
    }

    public static void setRemoveGroupEvent(EventInterface.RemoveGroupEvent removeGroupEvent) {
        AdvancedTeamInfoActivity.removeGroupEvent = removeGroupEvent;
    }

    public static void setOutGroupEvent(EventInterface.OutGroupEvent outGroupEvent) {
        AdvancedTeamInfoActivity.outGroupEvent = outGroupEvent;
    }

    public static void setInviteGroupEvent(EventInterface.InviteGroupEvent inviteGroupEvent) {
        AdvancedTeamInfoActivity.inviteGroupEvent = inviteGroupEvent;
    }

    public static void setEachYunxinIdEvent(EventInterface.EachYunxinIdEvent eachYunxinIdEvent) {
        AdvancedTeamInfoActivity.eachYunxinIdEvent = eachYunxinIdEvent;
    }
    public static void getGroupInfoEvent(EventInterface.GetGroupInfoEvent mGetGroupInfoEvent) {
        AdvancedTeamInfoActivity.mGetGroupInfoEvent = mGetGroupInfoEvent;
    }
    private static EventInterface.StartMainEvent mStartMainEvent;
    public static void startMainEvent(EventInterface.StartMainEvent mStartMainEvent) {
        AdvancedTeamInfoActivity.mStartMainEvent = mStartMainEvent;
    }

    private static EventInterface.UpdateGroupJoinmodeEvent mUpdateGroupJoinmodeEvent;

    public static void setUpdateGroupJoinmodeEvent(EventInterface.UpdateGroupJoinmodeEvent mUpdateGroupJoinmodeEvent) {
        AdvancedTeamInfoActivity.mUpdateGroupJoinmodeEvent = mUpdateGroupJoinmodeEvent;
    }
}
