package com.netease.nim.uikit.business.contact.core.provider;

import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.TextQuery;

import java.util.ArrayList;
import java.util.List;

public class ContactDataProvider implements IContactDataProvider {

    private int[] itemTypes;
    private boolean isVipTeam;
    public ContactDataProvider(int... itemTypes) {
        this.itemTypes = itemTypes;
    }

    public ContactDataProvider(boolean isVipTeam,int... itemTypes) {
        this.itemTypes = itemTypes;
        this.isVipTeam = isVipTeam;
    }

    @Override
    public List<AbsContactItem> provide(TextQuery query) {
        List<AbsContactItem> data = new ArrayList<>();

        for (int itemType : itemTypes) {
            data.addAll(provide(itemType, query));
        }

        return data;
    }

    private final List<AbsContactItem> provide(int itemType, TextQuery query) {
        switch (itemType) {
            case ItemTypes.FRIEND:
                return UserDataProvider.provide(query);
            case ItemTypes.TEAM:
            case ItemTypes.TEAMS.ADVANCED_TEAM:
            case ItemTypes.TEAMS.NORMAL_TEAM:
                if(isVipTeam){
                    return TeamDataProvider.provideIsVip(query, itemType);
                }else {
                    return TeamDataProvider.provide(query, itemType);
                }
            case ItemTypes.MSG:
                return MsgDataProvider.provide(query);
            default:
                return new ArrayList<>();
        }
    }
}
