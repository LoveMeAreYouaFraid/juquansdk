package com.netease.nim.uikit.business.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamMemberDataChangedObserver;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.session.fragment.MessageFragment;
import com.netease.nim.uikit.business.session.fragment.TeamMessageFragment;
import com.netease.nim.uikit.business.session.viewholder.ADViewHolder;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderFactory;
import com.netease.nim.uikit.business.team.activity.AdvancedTeamInfoActivity;
import com.netease.nim.uikit.business.team.model.NimGroupBean;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.http.bean.AdBean;
import com.netease.nim.uikit.common.http.event.EventInterface;
import com.netease.nim.uikit.common.ui.dialog.SetGroupClassifyDialog;
import com.netease.nim.uikit.common.ui.widget.AnimationImageView;
import com.netease.nim.uikit.common.ui.widget.GroupAnimationImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 社群界面
 * <p/>
 * Created by huangjun on 2015/3/5.
 */
public class TeamMessageActivity extends BaseMessageActivity {
    private static List<AdBean> adList = new ArrayList<>();
    private static long time = 7000;//ms
    // model
    private Team team;

    private View invalidTeamTipView;

    private TextView invalidTeamTipText;

    private TeamMessageFragment fragment;

    private Class<? extends Activity> backToClass;

    public static void start(Context context, String tid, SessionCustomization customization,
                             Class<? extends Activity> backToClass, IMMessage anchor) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.putExtra(Extras.EXTRA_BACK_TO_CLASS, backToClass);
        if (anchor != null) {
            intent.putExtra(Extras.EXTRA_ANCHOR, anchor);
        }
        intent.setClass(context, TeamMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }

    protected void findViews() {
        tv_peoplenum= findView(R.id.tv_peoplenum);
        head_img= findView(R.id.head_img);
        rl_team_living= findView(R.id.rl_team_living);

        invalidTeamTipView = findView(R.id.invalid_team_tip);
        invalidTeamTipText = findView(R.id.invalid_team_text);
        iv_group_redad = findViewById(R.id.iv_group_redad);
        iv_group_redad_close = findViewById(R.id.iv_group_redad_close);

        iv_group_jianqian = findViewById(R.id.iv_group_jianqian);
        iv_group_jianqian_close = findViewById(R.id.iv_group_jianqian_close);

        iv_group_choujiang = findViewById(R.id.iv_group_choujiang);
        iv_group_choujiang_close = findViewById(R.id.iv_group_choujiang_close);
       // Glide.with(this).load(R.mipmap.hongbao).into(iv_group_redad);
        iv_group_redad.setOnClickListener(view -> goToADList.gotoList(TeamMessageActivity.this, sessionId));
        iv_group_choujiang.setOnClickListener(view -> gotoLuckyLottery.gotoLuckyLottery(TeamMessageActivity.this));
        iv_group_jianqian.setOnClickListener(view -> goToPickUpMoney.gotoPickUpMoney(TeamMessageActivity.this));
        iv_group_jianqian_close.setOnClickListener(view -> HideJianqian());
        iv_group_redad_close.setOnClickListener(view -> HideRedad());
        iv_group_choujiang_close.setOnClickListener(view -> HideChoujiang());
        //获取背景，并将其强转成AnimationDrawable
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_group_redad.getBackground();
        //判断是否在运行
        if(!animationDrawable.isRunning()){
            //开启帧动画
            animationDrawable.start();
        }
        AnimationDrawable animationDrawable2 = (AnimationDrawable) iv_group_choujiang.getBackground();
        //判断是否在运行
        if(!animationDrawable2.isRunning()){
            //开启帧动画
            animationDrawable2.start();
        }
        AnimationDrawable animationDrawable3 = (AnimationDrawable) iv_group_jianqian.getBackground();
        //判断是否在运行
        if(!animationDrawable3.isRunning()){
            //开启帧动画
            animationDrawable3.start();
        }

    }
    private void HideRedad(){
        iv_group_redad.setVisibility(View.GONE);
        iv_group_redad_close.setVisibility(View.GONE);
    }
    private void HideChoujiang(){
        iv_group_choujiang.setVisibility(View.GONE);
        iv_group_choujiang_close.setVisibility(View.GONE);
    }
    private void HideJianqian(){
        iv_group_jianqian.setVisibility(View.GONE);
        iv_group_jianqian_close.setVisibility(View.GONE);
    }
    private void setGotoLive(){
      if(mNimGroupBean!=null){
          goToLive.gotoLive(TeamMessageActivity.this, mNimGroupBean.room_id);
      }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backToClass = (Class<? extends Activity>) getIntent().getSerializableExtra(Extras.EXTRA_BACK_TO_CLASS);
        findViews();
        registerTeamUpdateObserver(true);
      //  adInterface = adList -> ad(adList);
        adInterface = adList -> adShow(adList);
        getLotterySets.getLotterySets(new EventInterface.ApiResponse<String>() {
            @Override
            public void onSuccess(String data) {
                if(data==null){
                    return;
                }
                if(data.equals("1")) {
                    iv_group_choujiang.setVisibility(View.VISIBLE);
                    iv_group_choujiang_close.setVisibility(View.VISIBLE);
                } else   if(data.equals("2")){
                    iv_group_choujiang.setVisibility(View.GONE);
                    iv_group_choujiang_close.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int type) {

            }
        });
        getPickUpMoneySets.getPickUpMoneySets(new EventInterface.ApiResponse<String>() {
            @Override
            public void onSuccess(String data) {
                if(data==null){
                    return;
                }
                if(data.equals("1")) {
                    iv_group_jianqian.setVisibility(View.VISIBLE);
                    iv_group_jianqian_close.setVisibility(View.VISIBLE);
                } else   if(data.equals("2")){
                    iv_group_jianqian.setVisibility(View.GONE);
                    iv_group_jianqian_close.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int type) {

            }
        });
        getGroupData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerTeamUpdateObserver(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTeamInfo();
        getGroupAd.getGroupAd(sessionId, adPage);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mHandler != null) {
            mHandler.removeCallbacks(r);
        }
        if (vcl != null) {
            vcl.setVisibility(View.GONE);
        }
        adPage = 1;
        sessionId = intent.getStringExtra(Extras.EXTRA_ACCOUNT);
        fragment = (TeamMessageFragment) switchContent(fragment());
    }

    /**
     * 请求群基本信息
     */
    private void requestTeamInfo() {
        // 请求群基本信息
        Team t = NimUIKit.getTeamProvider().getTeamById(sessionId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(sessionId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result, int code) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    } else {
                        onRequestTeamInfoFailed(code);
                    }
                }
            });
        }
    }

    private void onRequestTeamInfoFailed(int code) {
//        群相关错误码
//        801	群人数达到上限	如果「单次邀请进群人数」大于「群最高人数」减去「群内已有人数」时，会返回错误码801，该次邀请的账号全都无法入群。
//        802	没有权限
//        803	群不存在
//        804	用户不在群
//        805	群类型不匹配
//        806	创建群数量达到限制
//        807	群成员状态错误
//        808	申请成功
//        809	已经在群内
//        810	邀请成功
        switch (code) {
            case 803:
                ToastHelper.showToast(TeamMessageActivity.this, "群组已解散!");
                break;
            default:
                ToastHelper.showToast(TeamMessageActivity.this, "获取群组信息失败!");
        }
        finish();
    }

    /**
     * R
     * 更新群信息
     *
     * @param d
     */
    private void updateTeamInfo(final Team d) {
        if (d == null) {
            return;
        }

        team = d;
        fragment.setTeam(team);
        setTextTitle(team == null ? sessionId : team.getName() + "(" + team.getMemberCount() + "人)");

        invalidTeamTipText.setText(team.getType() == TeamTypeEnum.Normal ? R.string.normal_team_invalid_tip : R.string.team_invalid_tip);
        invalidTeamTipView.setVisibility(team.isMyTeam() ? View.GONE : View.VISIBLE);
    }

    /**
     * 注册群信息更新监听
     *
     * @param register
     */
    private void registerTeamUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
        NimUIKit.getTeamChangedObservable().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver, register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage((Observer<List<IMMessage>>) imMessages -> {
            for (IMMessage imMessage : imMessages) {
                if (MsgViewHolderFactory.getViewHolderByType(imMessage) == ADViewHolder.class) {
                    getGroupAd.getGroupAd(sessionId, adPage);
                }
            }
        }, register);
    }

    /**
     * 群资料变动通知和移除群的通知（包括自己退群和群被解散）
     */
    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            if (team == null) {
                return;
            }
            for (Team t : teams) {
                if (t.getId().equals(team.getId())) {
                    updateTeamInfo(t);
                    break;
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
            if (team == null) {
                return;
            }
            if (team.getId().equals(TeamMessageActivity.this.team.getId())) {
                updateTeamInfo(team);
            }
        }
    };

    /**
     * 群成员资料变动通知和移除群成员通知
     */
    TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamMemberDataChangedObserver() {

        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            fragment.refreshMessageList();
        }

        @Override
        public void onRemoveTeamMember(List<TeamMember> member) {
        }
    };

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            fragment.refreshMessageList();
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            fragment.refreshMessageList();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            fragment.refreshMessageList();
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            fragment.refreshMessageList();
        }
    };

    @Override
    protected MessageFragment fragment() {
        // 添加fragment
        Bundle arguments = getIntent().getExtras();
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.Team);
        fragment = new TeamMessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.nim_team_message_activity;
    }

    @Override
    protected void initToolBar() {
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.empty;
        setToolBar(R.id.toolbar, options);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView btn = toolbar.findViewById(R.id.right_btn);
        btn.setVisibility(View.VISIBLE);
        btn.setText("");
        btn.setBackgroundResource(R.mipmap.ic_menu_dark);
        btn.setOnClickListener(view -> AdvancedTeamInfoActivity.start(this, team.getId()));
    }

    @Override
    protected boolean enableSensor() {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backToClass != null) {
            Intent intent = new Intent();
            intent.setClass(this, backToClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_im_team, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_group_info) {
//            AdvancedTeamInfoActivity.start(this, team.getId());
//        }
////        int itemId = ;
////        if (itemId == R.id.menu_complaint) {
////            if (complaintEvent != null) {
////                complaintEvent.action(this, team.getId());
////            }
////        } elseelse if (itemId == R.id.menu_quit) {
////
////        }
//        return true;
//    }

    private static EventInterface.ComplaintEvent complaintEvent;

    public static void setComplaintEvent(EventInterface.ComplaintEvent complaintEvent) {
        TeamMessageActivity.complaintEvent = complaintEvent;
    }

    @Override
    protected boolean isTeamType() {
        return true;
    }

    TextView tv_peoplenum;
    RelativeLayout rl_team_living;
    GroupAnimationImageView head_img;
    ImageView iv_group_jianqian_close;
    ImageView iv_group_jianqian;
    ImageView iv_group_choujiang_close;
    ImageView iv_group_choujiang;
    ImageView iv_group_redad_close;
    ImageView iv_group_redad;
    ConstraintLayout vcl;
    TextView title;
    TextView tv_content;
    ImageView iv_video_bg;
    ImageView iv_ic_play;
    TextView tvMore;
    ImageView tv_red;
    ConstraintLayout vcl2;
    TextView title2;
    TextView tv_content2;
    ImageView iv_video_bg2;
    ImageView iv_ic_play2;
    TextView tvMore2;
    ImageView tv_red2;
    Handler mHandler = new Handler();
    private int index = 0;
    private int adPage = 1;
    private boolean hasMoreAd = false;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            index++;
            if (index >= adList.size()) {
                index = 0;
            }
            if (index == adList.size() - 6 && hasMoreAd) {
                adPage++;
                getGroupAd.getGroupAd(sessionId, adPage);
            }
            setAdBean(true);
            mHandler.postDelayed(this, time);
        }
    };

    public void adShow(List<AdBean> list) {
        if(list!=null&&list.size()>0){
            iv_group_redad.setVisibility(View.VISIBLE);
            iv_group_redad_close.setVisibility(View.VISIBLE);
        }else{
            iv_group_redad.setVisibility(View.GONE);
            iv_group_redad_close.setVisibility(View.GONE);
        }
    }
    public void ad(List<AdBean> list) {
        if (list == null || list.size() == 0) {
            hasMoreAd = false;
            return;
        } else hasMoreAd = list.size() >= 10;
        mHandler.removeCallbacks(r);
        if (adPage == 1) adList.clear();
        adList.addAll(list);

        vcl = findViewById(R.id.vcl);
        vcl2 = findViewById(R.id.vcl2);
        vcl.setVisibility(View.VISIBLE);
        title = vcl.findViewById(R.id.tv_title);
        title2 = vcl2.findViewById(R.id.tv_title2);
        tv_content = vcl.findViewById(R.id.tv_content);
        tv_content2 = vcl2.findViewById(R.id.tv_content2);
        iv_video_bg = vcl.findViewById(R.id.iv_video_bg);
        iv_video_bg2 = vcl2.findViewById(R.id.iv_video_bg2);
        iv_ic_play = vcl.findViewById(R.id.iv_ic_play);
        iv_ic_play2 = vcl2.findViewById(R.id.iv_ic_play2);
        tv_red = vcl.findViewById(R.id.tv_red);
        tv_red2 = vcl2.findViewById(R.id.tv_red2);
        tvMore = vcl.findViewById(R.id.tv_more);
        tvMore2 = vcl2.findViewById(R.id.tv_more2);
        setAdBean(false);
        mHandler.postDelayed(r, time);
    }

    public void setAdBean(boolean anim) {
        if (isDestroyed()) return;
        if (adList.size() <= index) {
            index = 0;
            mHandler.postDelayed(r, time);
            return;
        }
        if (anim && adList.size() > 1) {
            setAdView(tvMore2, tv_red2, iv_ic_play2, iv_video_bg2, title2, tv_content2, vcl2);
            int type = TranslateAnimation.RELATIVE_TO_SELF;
            TranslateAnimation translateAnimation2 = new TranslateAnimation(type, 1, type, 0, type, 0, type, 0);
            translateAnimation2.setDuration(800);
            translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    vcl2.setVisibility(View.INVISIBLE);
                    setAdView(tvMore, tv_red, iv_ic_play, iv_video_bg, title, tv_content, vcl);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            TranslateAnimation translateAnimation1 = new TranslateAnimation(type, 0, type, -1, type, 0, type, 0);
            translateAnimation1.setDuration(800);
            translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            vcl.startAnimation(translateAnimation1);
            vcl2.setVisibility(View.VISIBLE);
            vcl2.startAnimation(translateAnimation2);
        } else {
            setAdView(tvMore, tv_red, iv_ic_play, iv_video_bg, title, tv_content, vcl);
        }
    }

    private void setAdView(TextView tvMore, ImageView tv_red, ImageView iv_ic_play, ImageView iv_video_bg, TextView title, TextView tv_content, ConstraintLayout vcl) {
        tvMore.setOnClickListener(view -> goToADList.gotoList(TeamMessageActivity.this, sessionId));
       // Glide.with(this).load(R.mipmap.ic_ad_red_gif).into(tv_red);
        iv_ic_play.setVisibility(adList.get(index).getType() == 1 ? View.VISIBLE : View.GONE);
        Glide.with(this).asDrawable().apply(new RequestOptions()
                .error(R.mipmap.bga_pp_ic_holder_light)
                .placeholder(R.mipmap.bga_pp_ic_holder_light)
                .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(20))))
                .load(adList.get(index).getMedias().get(0)).into(iv_video_bg);
        title.setText(adList.get(index).getTitle());
        tv_content.setText(adList.get(index).getUser_name());
        vcl.setOnClickListener(v -> goToADDetail.detail(TeamMessageActivity.this, String.valueOf(adList.get(index).getId()), sessionId));
        tv_red.setOnClickListener(v -> {
            if (redPacket != null) {
                redPacket.getRP(sessionId, String.valueOf(adList.get(index).getId()), this);
            }
        });
    }

    private NimGroupBean mNimGroupBean;
    private void getGroupData(){
        mGetGroupInfoEvent.getGroupInfo2(TeamMessageActivity.this,sessionId, new EventInterface.ApiResponse<NimGroupBean>() {
            @Override
            public void onSuccess(NimGroupBean data) {
                if(data!=null&&data.play_alive==1){
                    mNimGroupBean=data;
                    //群主正在直播
                    Glide.with(head_img.getContext())
                            .load(data.headimgurl)
                            .circleCrop()
                            .placeholder(R.mipmap.ic_avatar_default)
                            .into(head_img.getImageView());
                    head_img.setEnablePlay(true);
                    tv_peoplenum.setText(data.alive_peoplenum+"人正在观看");
                    rl_team_living.setVisibility(View.VISIBLE);
                    rl_team_living.setOnClickListener(view -> setGotoLive());
                }else{
                    rl_team_living.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int type) {
                if(type==-99){
                    finish();
                }
            }
        });
    }


    private static EventInterface.GoToADDetail goToADDetail;
    private static EventInterface.RedPacket redPacket;
    private static EventInterface.GetGroupAd getGroupAd;
    private static EventInterface.GoToADList goToADList;
    private static EventInterface.AdInterface adInterface;
    private static EventInterface.GoToLuckyLottery gotoLuckyLottery;
    private static EventInterface.GoToPickUpMoney goToPickUpMoney;
    private static EventInterface.GetPickUpMoneySets getPickUpMoneySets;

    private static EventInterface.GetLotterySets getLotterySets;

    private static EventInterface.GoToLive goToLive;
    public static void setGoToLive(EventInterface.GoToLive goToLive) {
        TeamMessageActivity.goToLive = goToLive;
    }
    public static void getLotterySets(EventInterface.GetLotterySets getLotterySets) {
        TeamMessageActivity.getLotterySets = getLotterySets;
    }
    public static void getPickUpMoneySets(EventInterface.GetPickUpMoneySets getPickUpMoneySets) {
        TeamMessageActivity.getPickUpMoneySets = getPickUpMoneySets;
    }

    public static void setGoToADDetail(EventInterface.GoToADDetail goToADDetail1) {
        goToADDetail = goToADDetail1;
    }

    public static void setGoToADList(EventInterface.GoToADList goToADList) {
        TeamMessageActivity.goToADList = goToADList;
    }
    public static void setGoToLuckyLottery(EventInterface.GoToLuckyLottery gotoLuckyLottery) {
        TeamMessageActivity.gotoLuckyLottery = gotoLuckyLottery;
    }
    public static void setGoToPickUpMoney(EventInterface.GoToPickUpMoney goToPickUpMoney) {
        TeamMessageActivity.goToPickUpMoney = goToPickUpMoney;
    }

    public static void setRedPacket(EventInterface.RedPacket redPacket1) {
        redPacket = redPacket1;
    }

    public static void setAdList(List<AdBean> adList) {
        adInterface.onAdList(adList);
    }

    public static void getADList(EventInterface.GetGroupAd getGroupAd) {
        TeamMessageActivity.getGroupAd = getGroupAd;
    }

    public static void setTime(long time) {
        TeamMessageActivity.time = time;
    }

    private static EventInterface.NewGetGroupInfoEvent mGetGroupInfoEvent;
    public static void getGroupInfoEvent(EventInterface.NewGetGroupInfoEvent mGetGroupInfoEvent) {
        TeamMessageActivity.mGetGroupInfoEvent = mGetGroupInfoEvent;
    }

}

