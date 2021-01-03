package com.netease.nim.uikit.business.session.emoji;

public interface IEmoticonSelectedListener {
    void onEmojiSelected(String key);

    void onStickerSelected(String categoryName, String stickerName);

    void onStickerColletionSelected(String emoji_name, String emoji_id, String emoji_link);
    void onAddEmoji();

}
