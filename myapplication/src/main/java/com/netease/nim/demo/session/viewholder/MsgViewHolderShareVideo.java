package com.netease.nim.demo.session.viewholder;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.VideoBean;
import com.netease.nim.demo.session.extension.ShareVideoAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Created by maojunfeng on 2020-01-04.
 * Description:
 */
public class MsgViewHolderShareVideo extends MsgViewHolderBase {

    private static EventInterface.GoToVideoEvent goToVideoEvent;
    private HeadImageView hivHead;
    private TextView tvName;
    private ImageView ivCover;

    public MsgViewHolderShareVideo(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public static void setGoToVideoEvent(EventInterface.GoToVideoEvent goToVideoEvent) {
        MsgViewHolderShareVideo.goToVideoEvent = goToVideoEvent;
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_live;
    }

    @Override
    protected void inflateContentView() {
        hivHead = view.findViewById(R.id.hiv_head);
        tvName = view.findViewById(R.id.tv_name);
        ivCover = view.findViewById(R.id.iv_cover);
    }

    @Override
    protected void bindContentView() {
        ShareVideoAttachment attachment = ((ShareVideoAttachment) message.getAttachment());
        if (attachment == null) return;
        Log.d("MMM", JSON.toJSONString(message.getAttachment()));
        hivHead.loadAvatar(attachment.getHeadImgStr());
        String desc="";
        if(attachment.getData()!=null&&attachment.getData().desc!=null){
            desc=attachment.getData().desc;
        }
        String textHtml = "<font size='13'>"+ "@"+attachment.getNameStr()+ " </font>"+"<font color=#999999>" +desc + "</font>";
        tvName.setText(Html.fromHtml(textHtml));
        Glide.with(view).load(attachment.getVideoUrl()).into(ivCover);
    }

    @Override
    protected void onItemClick() {
        VideoBean data = ((ShareVideoAttachment) message.getAttachment()).getData();
        if (goToVideoEvent != null&&data!=null ) {
            goToVideoEvent.goToVideo(JSON.toJSONString(data));
        }

    }

    @Override
    protected void setContent() {
        if (!isShowBubble() && !isMiddleItem()) {
            return;
        }
        LinearLayout bodyContainer = view.findViewById(com.netease.nim.uikit.R.id.message_item_body);
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
}
