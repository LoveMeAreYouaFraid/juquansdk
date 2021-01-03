package com.netease.nim.uikit.business.session.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.CheckInDialog;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.model.main.CustomPushContentProvider;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.ait.AitManager;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.actions.ImageAction;
import com.netease.nim.uikit.business.session.actions.LocationAction;
import com.netease.nim.uikit.business.session.actions.VideoAction;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.input.InputPanel;
import com.netease.nim.uikit.business.session.module.list.MessageListPanelEx;
import com.netease.nim.uikit.business.session.viewholder.ADViewHolder;
import com.netease.nim.uikit.business.team.model.NimUserIInfoBean;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.robot.model.NimRobotInfo;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;
import com.netease.nimlib.sdk.robot.model.RobotMsgType;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 聊天界面基类
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class MessageFragment extends TFragment implements ModuleProxy {

    public String avatarUrl;
    public String nickName;

    public static final int AD_IMAGE_TEXT = 4029;
    public static final int AD_VIDEO = 4030;
    public static MessageFragment fragment;
    private View rootView;

    private SessionCustomization customization;

    protected static final String TAG = "MessageActivity";

    // p2p对方Account或者群id
    protected String sessionId;

    protected SessionTypeEnum sessionType;

    // modules
    protected InputPanel inputPanel;
    protected MessageListPanelEx messageListPanel;

    protected AitManager aitManager;
    private MyBroadcastReceiver mBroadcastReceiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragment = this;
        parseIntent();
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ADViewHolder.ADRequestEntity entity = new ADViewHolder.ADRequestEntity();
            entity.conversion(intent.getStringExtra("data"));
            entity.type = 10;
            sendMessage(JSON.toJSONString(entity));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.juquan.im.send.ad");
        mBroadcastReceiver = new MyBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);
        rootView = inflater.inflate(R.layout.nim_message_fragment, container, false);
        return rootView;
    }

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();

        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        if(inputPanel!=null) {
            inputPanel.onPause();
        }
        if(messageListPanel!=null) {
            messageListPanel.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        messageListPanel.onResume();
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageListPanel.onDestroy();
        registerObservers(false);
        if (inputPanel != null) {
            inputPanel.onDestroy();
        }
        if (aitManager != null) {
            aitManager.reset();
        }
    }

    public boolean onBackPressed() {
        if(inputPanel!=null&&inputPanel.collapse(true)){
            return true;
        }else   if(messageListPanel!=null&&messageListPanel.onBackPressed()){
            return true;
        }else{
            return false;
        }
       // return inputPanel.collapse(true) || messageListPanel.onBackPressed();
    }

    public void refreshMessageList() {
        messageListPanel.refreshMessageList();
    }
    private int isFriend=-1;
    private boolean  isService;
    private void parseIntent() {
        Bundle arguments = getArguments();
        isService= arguments.getBoolean("isService",false);
        sessionId = arguments.getString(Extras.EXTRA_ACCOUNT);
        sessionType = (SessionTypeEnum) arguments.getSerializable(Extras.EXTRA_TYPE);
        IMMessage anchor = (IMMessage) arguments.getSerializable(Extras.EXTRA_ANCHOR);

        customization = (SessionCustomization) arguments.getSerializable(Extras.EXTRA_CUSTOMIZATION);
        Container container = new Container(getActivity(), sessionId, sessionType, this, true);

        if (messageListPanel == null) {
            messageListPanel = new MessageListPanelEx(container, rootView, anchor, false, false);
        } else {
            messageListPanel.reload(container, anchor);
        }
        if (sessionType == SessionTypeEnum.Team) {
            InvocationFuture<Team> teamInvocationFuture = NIMClient.getService(TeamService.class).searchTeam(sessionId);
            teamInvocationFuture.setCallback(new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    MessageFragment.this.avatarUrl = team.getIcon();
                    MessageFragment.this.nickName = team.getName();
                }

                @Override
                public void onFailed(int i) {

                }

                @Override
                public void onException(Throwable throwable) {

                }
            });
        } else {
            NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(sessionId);
            if (userInfo == null) {
                ToastHelper.showToast(getContext(), "找不到该账号");
                return;
            }
            avatarUrl = userInfo.getAvatar();
            nickName = userInfo.getName();
        }

        if (inputPanel == null) {
            inputPanel = new InputPanel(container, rootView, getActionList());
            inputPanel.setCustomization(customization);
        } else {
            inputPanel.reload(container, customization);
        }

        initAitManager();

        inputPanel.switchRobotMode(NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(sessionId) != null);

        registerObservers(true);

        if (customization != null) {
            messageListPanel.setChattingBackground(customization.backgroundUri, customization.backgroundColor);
        }
       // DialogMaker.showProgressDialog(getActivity(), null, false);
        if (sessionType == SessionTypeEnum.P2P&&!isService) {
            getUserInfoEvent.getUserInfo(sessionId, new EventInterface.ApiResponse<NimUserIInfoBean>() {
                @Override
                public void onSuccess(NimUserIInfoBean data) {
                    //  DialogMaker.dismissProgressDialog();
                    if (data != null) {
                        isFriend = data.is_friend;
                    }
                }

                @Override
                public void onFail(int type) {
                    // DialogMaker.dismissProgressDialog();
                }
            });
        }
    }
    private static EventInterface.GetUserInfoEvent getUserInfoEvent;
//    private static EventInterface.OffsetAmountEvent mOffsetAmountEvent;
//    public static void getOffsetAmountEvent(EventInterface.OffsetAmountEvent mOffsetAmountEvent) {
//        MessageFragment.mOffsetAmountEvent = mOffsetAmountEvent;
//    }
    public static void getUserInfoEvent(EventInterface.GetUserInfoEvent getUserInfoEvent) {
        MessageFragment.getUserInfoEvent = getUserInfoEvent;
    }

    private void initAitManager() {
        UIKitOptions options = NimUIKitImpl.getOptions();
        if (options.aitEnable) {
            aitManager = new AitManager(getContext(), options.aitTeamMember && sessionType == SessionTypeEnum.Team ? sessionId : null, options.aitIMRobot);
            inputPanel.addAitTextWatcher(aitManager);
            aitManager.setTextChangeListener(inputPanel);
        }
    }

    /**
     * ************************* 消息收发 **********************************
     */
    // 是否允许发送消息
    protected boolean isAllowSendMessage(final IMMessage message) {
        return customization.isAllowSendMessage(message);
    }


    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        // 已读回执监听
        if (NimUIKitImpl.getOptions().shouldHandleReceipt) {
            service.observeMessageReceipt(messageReceiptObserver, register);
        }
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            onMessageIncoming(messages);
        }
    };

    private void onMessageIncoming(List<IMMessage> messages) {
        if (CommonUtil.isEmpty(messages)) {
            return;
        }
        messageListPanel.onIncomingMessage(messages);
        // 发送已读回执
        messageListPanel.sendReceipt();
    }

    /**
     * 已读回执观察者
     */
    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            messageListPanel.receiveReceipt();
        }
    };

    private int i = 0;

    /**
     * ********************** implements ModuleProxy *********************
     */


//    if(msg.getSessionType()==SessionTypeEnum.P2P){
//
//    }else{
//
//    }

    @Override
    public boolean sendMessage(IMMessage message) {
      //  System.out.println("-------------messageListPanel.isService="+messageListPanel.isService);
        if(messageListPanel!=null&&messageListPanel.isService){
            isService=messageListPanel.isService;
        }
       // System.out.println("-------------isService="+isService);
      //  System.out.println("-------------isFriend="+isFriend);
        if(!isService&&isFriend!=1&&message.getSessionType()==SessionTypeEnum.P2P){
            ToastHelper.showToast(getActivity(),"请先添加好友");
            return true;
        }
        if (isAllowSendMessage(message)) {
            appendTeamMemberPush(message);
            addRemoteExtension(message);
            message = changeToRobotMsg(message);
            final IMMessage msg = message;
            appendPushConfig(message);
            // send message to server and save to db
            NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {

                }

                @Override
                public void onFailed(int code) {
                    sendFailWithBlackList(code, msg);
                }

                @Override
                public void onException(Throwable exception) {

                }
            });

        } else {
            // 替换成tip
            message = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());
            message.setContent("该消息无法发送");
            message.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).saveMessageToLocal(message, false);
        }

        messageListPanel.onMsgSend(message);
        if (aitManager != null) {
            aitManager.reset();
        }
        //getDate();  //废弃改为 捡钱功能了
        return true;
    }


    private void getDate() {
//        if (getActivity() != null) {
//            mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//        }
//        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int date = c.get(Calendar.DATE);
//
//        String data = year + "/" + (month + 1) + "/" + date;
//        if (getActivity() != null) {
//            SharedPreferences sp = getActivity().getSharedPreferences("time", Context.MODE_PRIVATE);
//            int num = sp.getInt(data, 0);
//            num++;
//            System.out.println("-------每天活跃度-num="+num);
//            if (num == 5) {
//                mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//              //  (new CheckInDialog(getActivity())).show();
//            }else  if (num == 10) {
//                mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//               // (new CheckInDialog(getActivity())).show();
//            }else  if (num == 15) {
//                mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//             //   (new CheckInDialog(getActivity())).show();
//            }else  if (num == 20) {
//                mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//              //  (new CheckInDialog(getActivity())).show();
//            }else  if (num == 25) {
//                mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//              //  (new CheckInDialog(getActivity())).show();
//            }else  if (num > 25) {
//                mOffsetAmountEvent.getOffsetAmountEvent(getActivity());
//            }
//            sp.edit().putInt(data, num).apply();
//        }
    }


    private void addRemoteExtension(IMMessage message) {
        Map<String, Object> remoteExtension = message.getRemoteExtension();
        if (remoteExtension == null) {
            remoteExtension = new HashMap<>();
        }
        Map<String, Object> ext = new HashMap<>();
        ext.put("avatarUrl", avatarUrl);
        ext.put("nickName", nickName);
        if(isService) {
            ext.put("msgType", "mall");
        }
        remoteExtension.put("ext", ext);
        message.setRemoteExtension(remoteExtension);
    }

    public void sendMessage(String content) {
        if (TextUtils.isEmpty(content))
            return;
        IMMessage msg = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.Team, null, (MsgAttachment) b -> content);
        int type = 0;
        try {
            JSONObject json = new JSONObject(content);
            type = json.getInt("type");
        } catch (Exception e) {
            //
        }
        String pushContent;
        switch (type) {
            case 10:
                pushContent = "[图文广告]";
                break;
            case 11:
                pushContent = "[视频广告]";
                break;
            default:
                pushContent = "[自定义消息]";
        }
        msg.setPushContent(pushContent);
        setPrivateValue(msg, "n", content);
        sendMessage(msg);
    }

    private static void setPrivateValue(Object obj, String field, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            //
        }
    }


    // 被对方拉入黑名单后，发消息失败的交互处理
    private void sendFailWithBlackList(int code, IMMessage msg) {
        if (code == ResponseCode.RES_IN_BLACK_LIST) {
            // 如果被对方拉入黑名单，发送的消息前不显示重发红点
            msg.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).updateIMMessageStatus(msg);
            messageListPanel.refreshMessageList();
            // 同时，本地插入被对方拒收的tip消息
            IMMessage tip = MessageBuilder.createTipMessage(msg.getSessionId(), msg.getSessionType());
            tip.setContent(getActivity().getString(R.string.black_list_send_tip));
            tip.setStatus(MsgStatusEnum.success);
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableUnreadCount = false;
            tip.setConfig(config);
            NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
        }
    }

    private void appendTeamMemberPush(IMMessage message) {
        if (aitManager == null) {
            return;
        }
        if (sessionType == SessionTypeEnum.Team) {
            List<String> pushList = aitManager.getAitTeamMember();
            if (pushList == null || pushList.isEmpty()) {
                return;
            }
            MemberPushOption memberPushOption = new MemberPushOption();
            memberPushOption.setForcePush(true);
            memberPushOption.setForcePushContent(message.getContent());
            memberPushOption.setForcePushList(pushList);
            message.setMemberPushOption(memberPushOption);
        }
    }

    private IMMessage changeToRobotMsg(IMMessage message) {
        if (aitManager == null) {
            return message;
        }
        if (message.getMsgType() == MsgTypeEnum.robot) {
            return message;
        }
        if (isChatWithRobot()) {
            if (message.getMsgType() == MsgTypeEnum.text && message.getContent() != null) {
                String content = message.getContent().equals("") ? " " : message.getContent();
                message = MessageBuilder.createRobotMessage(message.getSessionId(), message.getSessionType(), message.getSessionId(), content, RobotMsgType.TEXT, content, null, null);
            }
        } else {
            String robotAccount = aitManager.getAitRobot();
            if (TextUtils.isEmpty(robotAccount)) {
                return message;
            }
            String text = message.getContent();
            String content = aitManager.removeRobotAitString(text, robotAccount);
            content = content.equals("") ? " " : content;
            message = MessageBuilder.createRobotMessage(message.getSessionId(), message.getSessionType(), robotAccount, text, RobotMsgType.TEXT, content, null, null);

        }
        return message;
    }

    private boolean isChatWithRobot() {
        return NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(sessionId) != null;
    }

    private void appendPushConfig(IMMessage message) {
        CustomPushContentProvider customConfig = NimUIKitImpl.getCustomPushContentProvider();
        if (customConfig == null) {
            return;
        }
        String content = customConfig.getPushContent(message);
        Map<String, Object> payload = customConfig.getPushPayload(message);
        if (!TextUtils.isEmpty(content)) {
            message.setPushContent(content);
        }
        if (payload != null) {
            message.setPushPayload(payload);
        }

    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.scrollToBottom();
    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    @Override
    public void onItemFooterClick(IMMessage message) {
        if (aitManager == null) {
            return;
        }
        if (messageListPanel.isSessionMode()) {
            RobotAttachment attachment = (RobotAttachment) message.getAttachment();
            NimRobotInfo robot = NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(attachment.getFromRobotAccount());
            aitManager.insertAitRobot(robot.getAccount(), robot.getName(), inputPanel.getEditSelectionStart());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AD_IMAGE_TEXT || requestCode == AD_VIDEO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
            }
            return;
        }

        if (aitManager != null) {
            aitManager.onActivityResult(requestCode, resultCode, data);
        }
        inputPanel.onActivityResult(requestCode, resultCode, data);
        messageListPanel.onActivityResult(requestCode, resultCode, data);
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        actions.add(new VideoAction());
        actions.add(new LocationAction());
        if (customization != null && customization.actions != null) {
            actions.addAll(customization.actions);
        }
        return actions;
    }
}
