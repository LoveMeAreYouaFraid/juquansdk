package com.netease.nim.uikit.business.session.adapter;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.superteam.SuperTeam;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionAdapter extends BaseAdapter {
    private List<RecentContact> recentContacts = new ArrayList<>();
    private Map<String, Boolean> map = new HashMap<>();
    private Context context;

    public SessionAdapter(Context context, List<RecentContact> recentContacts) {
        this.context = context;
        this.recentContacts = recentContacts;

    }

    @Override
    public int getCount() {
        return recentContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return recentContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null && convertView.getTag() != null)
            viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nim_session, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.face = convertView.findViewById(R.id.item_friend_img);
            viewHolder.name = convertView.findViewById(R.id.session_name);
            viewHolder.radio = convertView.findViewById(R.id.radio);
            convertView.setTag(viewHolder);
        }
        viewHolder.name.setText(UserInfoHelper.getUserTitleName(recentContacts.get(position).getContactId(), recentContacts.get(position).getSessionType()));
        if (recentContacts.get(position).getSessionType() == SessionTypeEnum.P2P) {
            viewHolder.face.loadBuddyAvatar(recentContacts.get(position).getContactId());
        } else if (recentContacts.get(position).getSessionType() == SessionTypeEnum.Team) {
            Team team = NimUIKit.getTeamProvider().getTeamById(recentContacts.get(position).getContactId());
            viewHolder.face.loadTeamIconByTeam(team);
        } else if (recentContacts.get(position).getSessionType() == SessionTypeEnum.SUPER_TEAM) {
            SuperTeam team = NimUIKit.getSuperTeamProvider().getTeamById(recentContacts.get(position).getContactId());
            viewHolder.face.loadSuperTeamIconByTeam(team);
        }
        if (null != map.get(recentContacts.get(position).getContactId()) && map.get(recentContacts.get(position).getContactId())) {
            viewHolder.radio.setChecked(true);
        } else {
            viewHolder.radio.setChecked(false);
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView name;
        private HeadImageView face;
        private RadioButton radio;
    }

    public void re(Map<String, Boolean> map) {
        this.map = map;
        notifyDataSetChanged();
    }
}
