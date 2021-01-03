package com.netease.nim.demo.session.viewholder;

import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.CreditAmountAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Description: 抵用金转账
 */
public class MsgViewHolderCreditTrans extends MsgViewHolderBase {

    private static EventInterface.GoCreditTransEvent goCreditTransEvent;
    private TextView tv_credit_trans_amount;//转账信息：金额，转账给谁
    private RelativeLayout ll_trans_credit;

    public MsgViewHolderCreditTrans(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public static void setGoCreditTransEvent(EventInterface.GoCreditTransEvent goCreditTransEvent) {
        MsgViewHolderCreditTrans.goCreditTransEvent = goCreditTransEvent;
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_credittrans;
    }

    @Override
    protected void inflateContentView() {
        ll_trans_credit= view.findViewById(R.id.ll_trans_credit);//根布局
        tv_credit_trans_amount = view.findViewById(R.id.tv_credit_trans_amount);
    }

    @Override
    protected void bindContentView() {
        CreditAmountAttachment attachment = ((CreditAmountAttachment) message.getAttachment());
        if (attachment == null) {
            return;
        }
        if (isReceivedMessage()) {
            ll_trans_credit.setBackgroundResource(R.drawable.nim_bg_get_trans_credit);
            tv_credit_trans_amount.setText("¥"+attachment.getTransfer_amount()+"\n转账给你");
        }else{
            ll_trans_credit.setBackgroundResource(R.drawable.nim_bg_to_trans_credit);
            String tempname=attachment.getTransfer_username();
            if(tempname!=null&&tempname.length()>6){
                tempname=tempname.substring(0,6)+"...";
            }
            tv_credit_trans_amount.setText("¥"+attachment.getTransfer_amount()+"\n转账给"+tempname);
        }
        Log.d("MMM", JSON.toJSONString(message.getAttachment()));

    }

    @Override
    protected void onItemClick() {
      //  LiveBean data = ((LiveAttachment) message.getAttachment()).getData();
        if (goCreditTransEvent != null ) {
         //   System.out.println("--------------分享--商品id"+((ShareProductAttachment) message.getAttachment()).getGoods_id());
        //    goProductEvent.goToProduct(((ShareProductAttachment) message.getAttachment()).getGoods_id(),((ShareProductAttachment) message.getAttachment()).getShare_token());
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
