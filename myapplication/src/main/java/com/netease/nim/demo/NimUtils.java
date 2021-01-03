package com.netease.nim.demo;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.session.action.ADAction;
import com.netease.nim.demo.session.action.CollectAction;
import com.netease.nim.demo.session.action.RedPacketAction;
import com.netease.nim.demo.session.viewholder.MsgViewHolderRedPacket;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.activity.P2PMessageActivity;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

public class NimUtils {
    private static final String TAG = NimUtils.class.getSimpleName();
    private static final NimUtils instance = new NimUtils();

    public static NimUtils getInstance() {
        return instance;
    }

    private static Observer<List<RecentContact>> messageObserver;

    public void login(Context context, String account, String token, int retryTimes, LoginCallback callback) {
        // 登录
        NimUIKit.login(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i(TAG, "login success");
                onLoginDone();
                DemoCache.setAccount(account);
                saveLoginInfo(account, token);
                // 初始化消息提醒配置
                initNotificationConfig();
                if (callback != null) callback.handle(true);
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) { //302:用户名或密码错误, 404:对象不存在
                    Toast.makeText(context, "IM账号异常，请与管理员联系", Toast.LENGTH_SHORT).show();
                } else if (code == 415 || code == 0) { //415:客户端网络问题, 0:未知
                    if (retryTimes >= 10) {
                        Toast.makeText(context, "IM登录失败: " + code, Toast.LENGTH_SHORT).show();
                    } else {
                        login(context, account, token, retryTimes + 1, callback);
                        return;
                    }
                } else {
                    Toast.makeText(context, "IM登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
                if (callback != null) callback.handle(false);
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(context, "IM登录异常", Toast.LENGTH_LONG).show();
                onLoginDone();
                if (callback != null) callback.handle(false);
            }
        });
    }

    public interface LoginCallback {

        void handle(boolean success);
    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        System.out.println("---------NimUtils--消息提醒 铃声ring="+statusBarNotificationConfig.ring);
        System.out.println("--------NimUtils---消息提醒 震动 vibrate="+statusBarNotificationConfig.vibrate);
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }

    private void onLoginDone() {
        DialogMaker.dismissProgressDialog();
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    public static void setADEntrance(Class<? extends Activity> entrance) {
        ADAction.setEntrance(entrance);
    }

    public static void setPersonInfo(Class<? extends Activity> clazz) {
        SessionHelper.setUserInfoActivity(clazz);
        P2PMessageActivity.setUserInfoActivity(clazz);
    }

    public static void setRedPacketEntrance(Class<? extends Activity> entrance) {
        RedPacketAction.setEntrance(entrance);
    }
    public static void setCollectEntrance(Class<? extends Activity> entrance) {
        CollectAction.setEntrance(entrance);
    }

    public static void setRedPacket(EventInterface.NimRedPacket redPacket) {
        MsgViewHolderRedPacket.setRedPacket(redPacket::getRP);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig config) {
        DemoCache.setNotificationConfig(config);
        UserPreferences.setStatusConfig(config);
    }

    public static void setRecentContactChangedObserver(EventInterface.RecentContactChangedObserver r) {
        messageObserver = (Observer<List<RecentContact>>) recentContacts -> {
            if (r != null) r.onRecentContactChanged();
        };
    }

    public static void registerRecentContactChangedObserver(boolean register) {
        if (messageObserver == null) return;
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        if (service == null) return;
        service.observeRecentContact(messageObserver, register);
    }

    private static long lastClickTime;

    /**
     * 判断事件出发时间间隔是否超过预定值
     */
    public static boolean isFastDoubleClick(int delay) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < delay) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
