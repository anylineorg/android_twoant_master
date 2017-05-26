package net.twoant.master.ui.chat.domain;

import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import net.twoant.master.widget.emoji.EmojiUtil;

import java.util.ArrayList;

public class EmojiconExampleGroupData {

    private static final EaseEmojiconGroupEntity DATA = createData();

    private static EaseEmojiconGroupEntity createData() {
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        final int[] emojiResArray = EmojiUtil.EmojiResArray;
        final String[] emojiTextArray = EmojiUtil.EmojiTextArray;
        if (emojiTextArray.length != emojiResArray.length) return emojiconGroupEntity;

        ArrayList<EaseEmojicon> arrayList = new ArrayList<>(emojiResArray.length);
        EaseEmojicon easeEmojicon;
        for (int i = 0, size = emojiResArray.length; i < size; ++i) {
            easeEmojicon = new EaseEmojicon(emojiResArray[i], emojiTextArray[i], Type.NORMAL);
            easeEmojicon.setIdentityCode("em" + (1000 + i + 1));
            arrayList.add(easeEmojicon);
        }

        emojiconGroupEntity.setEmojiconList(arrayList);
        emojiconGroupEntity.setIcon(net.twoant.master.R.drawable.d_keai);
        emojiconGroupEntity.setType(Type.NORMAL);
        return emojiconGroupEntity;
    }


    public static EaseEmojiconGroupEntity getData() {
        return DATA;
    }
}
