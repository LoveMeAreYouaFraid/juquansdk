package com.netease.nim.uikit.common.http.event;

import android.app.Activity;
import android.content.Context;

import com.netease.nim.uikit.business.team.model.NimGroupBean;
import com.netease.nim.uikit.business.team.model.NimUserIInfoBean;
import com.netease.nim.uikit.common.http.bean.AdBean;

import java.util.List;

/**
 * Created by maojunfeng on 2020-01-03.
 * Description:
 */
public class EventInterface {
    public interface ChangeGroupClassifyEvent {
        public void action(Activity activity, String gid);
    }
    public interface UserProfileEvent {
        public void action(Activity activity, String account);
    }
    public interface StartMainEvent {
        public void action(Activity activity);
    }
    public interface ComplaintUserEvent {
        public void action(Activity activity, String userid);
    }

    public interface GroupQrcodeEvent {
        public void action(Activity activity, String teamId,int vipTeam);
    }
    public interface ComplaintEvent {
        public void action(Activity activity, String gid);
    }

    public interface NimRedPacket {
        //void getRP(String rpId, Context context,ApiResponse<String> apiResponse);

        void getRP(String mSessionId,String s, Context context, ApiResponse<Object> objectApiResponse);
    }

    public interface RedPacket {
        void getRP(String gID, String adID, Context context);
    }

    public interface GoToADDetail {
        public void detail(Activity activity, String adId, String gid);
    }
    public interface GoToAddEmoji {
        public void addEmoji(Activity activity);
    }

    public interface RecentContactChangedObserver {
        void onRecentContactChanged();
    }

    public interface AdInterface {
        void onAdList(List<AdBean> adList);
    }

    public interface GetGroupAd {
        void getGroupAd(String group_id, int page);
    }
    public interface GetLotterySets {
        void getLotterySets(ApiResponse<String> apiResponse);
    }
    public interface GetPickUpMoneySets {
        void getPickUpMoneySets(ApiResponse<String> apiResponse);
    }


    public interface GoToADList {
        void gotoList(Activity activity, String gid);
    }
    public interface GoToLive {
        void gotoLive(Activity activity, int roomId);
    }

    public interface GoToLuckyLottery {
        void gotoLuckyLottery(Activity activity);
    }
    public interface GoToPickUpMoney {
        void gotoPickUpMoney(Activity activity);
    }

    public interface ChangeGroupOwnerEvent {
        void changeGroupOwner(String yunxin_id, String owner, ApiResponse<Object> apiResponse);
    }
    public interface GetGroupInfoEvent {
        void getGroupInfo(String yunxin_id, ApiResponse<NimGroupBean> apiResponse);
    }
    public interface NewGetGroupInfoEvent {
        void getGroupInfo2(Activity activity,String yunxin_id, ApiResponse<NimGroupBean> apiResponse);
    }

    public interface RemoveGroupEvent {
        void removeGroup(String group_id, ApiResponse<Object> apiResponse);
    }

    public interface OutGroupEvent {
        void outGroup(String group_id, ApiResponse<Object> apiResponse);
    }

    public interface InviteGroupEvent {
        void inviteGroup(String group_id, String accounts, ApiResponse<Object> apiResponse);
    }

    public interface KickGroupEvent {
        void kickGroup(String group_id, String account, ApiResponse<Object> apiResponse);
    }

    public interface UpdateGroupEvent {
        void updateGroup(String yunxin_id, String group_name, String information, String announcement, ApiResponse<Object> apiResponse);
    }
    public interface UpdateGroupJoinmodeEvent {
        void updateGroupJoinmode(String yunxin_id, String Joinmode, ApiResponse<Object> apiResponse);
    }

    public interface GoToLiveEvent {
        void goToLive(String data);
    }
    public interface GoToVideoEvent {
        void goToVideo(String data);
    }
    public interface GoProductEvent {
        void goToProduct(String data,String share_token);
    }
    public interface GoCreditTransEvent {
        void goToCreditTrans(String data);
    }

    public interface EachYunxinIdEvent {
        void eachYunxinId(String yunxin_id, ApiResponse<Object> apiResponse);
    }

    /**
     * 添加好友
     */
    public interface AddFriendEvent {
        void addFriend(String userId,String meno, ApiResponse<Object> apiResponse);
    }
    /**
     * 删除好友
     */
    public interface DeleteFriendEvent {
        void deleteFriend(String userId, ApiResponse<Object> apiResponse);
    }
    /**
     *好友的二维码
     */
    public interface QRcodeEvent {
        public void QRcodeEvent(Activity activity,String userId,String account,int isFriend);
    }
    public interface GetUserInfoEvent {
        void getUserInfo(String account, ApiResponse<NimUserIInfoBean> apiResponse);
    }
//    public interface OffsetAmountEvent {
//        public void getOffsetAmountEvent(Activity activity);
//    }

    public interface ApiResponse<T> {
        default void onSuccess(T data) {
        }

        int ParseError = 0;   //数据解析异常
        int NoConnectError = 1;   //无连接异常
        int AuthError = 2;   //用户验证异常
        int NoDataError = 3;   //无数据返回异常
        int BusinessError = 4;   //业务异常
        int TimeOutError = 5;   //超时异常
        int OtherError = 6;   //其他异常

        default void onFail(int type) {
        }
        default void onFail(int type,String errorMsg) {
        }
    }
}
