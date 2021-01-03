package com.netease.nim.demo.session.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.LiveBean;
import com.netease.nim.demo.session.extension.ShareProductAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Created by maojunfeng on 2020-01-04.
 * Description:
 */
public class MsgViewHolderProduct extends MsgViewHolderBase {

    private static EventInterface.GoProductEvent goProductEvent;
    private ImageView iv_goods_img;//商品图片
    private TextView tv_goods_name;//商品名称
    private TextView tv_goods_price;//商品价格
    private TextView btn_goto_product;//去购买

    public MsgViewHolderProduct(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public static void setGoProductEvent(EventInterface.GoProductEvent goProductEvent) {
        MsgViewHolderProduct.goProductEvent = goProductEvent;
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_product;
    }

    @Override
    protected void inflateContentView() {
        iv_goods_img = view.findViewById(R.id.iv_goods_img);
        tv_goods_name = view.findViewById(R.id.tv_goods_name);
        tv_goods_price = view.findViewById(R.id.tv_goods_price);
        btn_goto_product = view.findViewById(R.id.btn_goto_product);
    }

    @Override
    protected void bindContentView() {

        ShareProductAttachment attachment = ((ShareProductAttachment) message.getAttachment());
        if (attachment == null) return;
        Log.d("MMM", JSON.toJSONString(message.getAttachment()));
        tv_goods_name.setText(attachment.getGoods_title());
        tv_goods_price.setText(attachment.getGoods_price());
        Glide.with(view).load(attachment.getGoods_thumb_url()).into(iv_goods_img);

    }

    @Override
    protected void onItemClick() {
      //  LiveBean data = ((LiveAttachment) message.getAttachment()).getData();
        if (goProductEvent != null ) {
            System.out.println("--------------分享--商品id"+((ShareProductAttachment) message.getAttachment()).getGoods_id());
            goProductEvent.goToProduct(((ShareProductAttachment) message.getAttachment()).getGoods_id(),((ShareProductAttachment) message.getAttachment()).getShare_token());
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
