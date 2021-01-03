package com.netease.nim.uikit.business.session.viewholder;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.media.imagepicker.loader.GlideImageLoader;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;

import org.json.JSONObject;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ADViewHolder extends MsgViewHolderBase {
    TextView title;
    ImageView img[] = new ImageView[3];
    ImageView iv_video_bg;
    TextView tv_time;
    View cl_video;
    View tv_red;
    View tv_add;
    ConstraintLayout constraintLayout;
    LinearLayout ll_img;

    private ADRequestEntity adRequestEntity;
    private static EventInterface.RedPacket redPacket;
    private static EventInterface.GoToADDetail goToADDetail;
    public static void setGoToADDetail(EventInterface.GoToADDetail goToADDetail) {
        ADViewHolder.goToADDetail = goToADDetail;
    }

    public ADViewHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public static void setRedPacket(EventInterface.RedPacket redPacket) {
        ADViewHolder.redPacket = redPacket;
    }

    @Override
    protected int getContentResId() {
        return R.layout.layout_ad_viewholder;
    }

    @Override
    protected void inflateContentView() {
        title = findViewById(R.id.tv_title);
        img[0] = findViewById(R.id.iv_img_1);
        img[1] = findViewById(R.id.iv_img_2);
        img[2] = findViewById(R.id.iv_img_3);
        iv_video_bg = findViewById(R.id.iv_video_bg);
        tv_time = findViewById(R.id.tv_time);
        cl_video = findViewById(R.id.cl_video);
        tv_red = findViewById(R.id.tv_red);
        tv_add = findViewById(R.id.tv_add);
        constraintLayout = findViewById(R.id.cl);
        ll_img = findViewById(R.id.ll_img);
    }

    @Override
    protected void bindContentView() {
        JSONObject messageCustomContent = MsgViewHolderCobraBase.getMessageCustomContent(message);
        adRequestEntity = JSON.parseObject(messageCustomContent.toString(), ADRequestEntity.class);
        if (adRequestEntity.ad_type == 1) {
            cl_video.setVisibility(View.VISIBLE);
            ll_img.setVisibility(View.GONE);
            Glide.with(iv_video_bg.getContext()).load(adRequestEntity.imgs).into(iv_video_bg);
        } else {
            cl_video.setVisibility(View.GONE);
            ll_img.setVisibility(View.VISIBLE);
            GlideImageLoader.displayVideo(img[0], adRequestEntity.adCover);
        }
        title.setText(adRequestEntity.adTitle);
        constraintLayout.setOnClickListener(v -> {
            goToADDetail.detail((Activity) context, adRequestEntity.advertisement_id, adRequestEntity.groupId);
        });
        if (adRequestEntity.account != null && adRequestEntity.account.equals(NimUIKit.getAccount())) {
            tv_red.setVisibility(View.INVISIBLE);
            tv_add.setVisibility(View.INVISIBLE);
            tv_add.setOnClickListener(null);
        } else {
            tv_red.setOnClickListener(v -> {
                if (redPacket != null) {
                    redPacket.getRP(adRequestEntity.groupId, adRequestEntity.advertisement_id, context);
                }
            });
            tv_red.setVisibility(View.VISIBLE);
            tv_add.setVisibility(View.VISIBLE);
            tv_add.setOnClickListener(v -> {
                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(adRequestEntity.account, VerifyType.VERIFY_REQUEST, "哥,加个好友吧.. ")).setCallback(new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        ToastHelper.showToast(context, "发起成功！等待对方同意");
                    }

                    @Override
                    public void onFailed(int i) {
                        ToastHelper.showToast(context, "发起失败！");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                    }
                });
            });
        }

    }

    @Override
    protected void setContent() {
        if (!isShowBubble() && !isMiddleItem()) {
            return;
        }

        LinearLayout bodyContainer = (LinearLayout) view.findViewById(R.id.message_item_body);

        // 调整container的位置
        int index = isReceivedMessage() ? 0 : 4;
        if (bodyContainer.getChildAt(index) != contentContainer) {
            bodyContainer.removeView(contentContainer);
            bodyContainer.addView(contentContainer, index);
        }

        if (isMiddleItem()) {
            setGravity(bodyContainer, Gravity.CENTER);
        } else {
            if (isReceivedMessage()) {
                setGravity(bodyContainer, Gravity.LEFT);
                //contentContainer.setBackgroundResource(leftBackground());
            } else {
                setGravity(bodyContainer, Gravity.RIGHT);
                // contentContainer.setBackgroundResource(rightBackground());
            }
        }

    }

    /**
     * 发广告收集到的所有参数
     */
    public static class ADRequestEntity {

        public int type;
        public String dateStr;
        public String releaseName;
        public String releaseHeadImg;
        public String imAccount;

        public int step;
        public String groupId;

        public String redPacketAmount;
        public String redPacketCount;
        public String redPacketName;

        public String adCover;
        public String adTitle;

        public String adContent;
        public String advertisement_id;
        public String imgs;
        public int ad_type;
        public String account;

        public void conversion(String data) {
            if (!TextUtils.isEmpty(data)) {
                ADRequestEntity adRequestEntity = JSON.parseObject(data, ADRequestEntity.class);
                if (adRequestEntity != null) {
                    this.type = adRequestEntity.type;
                    this.dateStr = adRequestEntity.dateStr;
                    this.imAccount = adRequestEntity.imAccount;
                    this.groupId = adRequestEntity.groupId;
                    this.redPacketAmount = adRequestEntity.redPacketAmount;
                    this.redPacketCount = adRequestEntity.redPacketCount;
                    this.redPacketName = adRequestEntity.redPacketName;
                    this.adCover = adRequestEntity.adCover;
                    this.adTitle = adRequestEntity.adTitle;
                    this.adContent = adRequestEntity.adContent;
                    this.advertisement_id = adRequestEntity.advertisement_id;
                    this.imgs = adRequestEntity.imgs;
                    this.ad_type = adRequestEntity.ad_type;
                    this.account = adRequestEntity.account;
                }
            }
        }
    }
}
