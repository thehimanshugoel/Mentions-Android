package com.mentionsandroid;

import com.mentionsandroid.mention.MentionSuggestible;

/**
 * Created by ningsuhen on 4/24/16.
 */
public class MentionSuggestibleImpl implements MentionSuggestible {
    private final int id;

    public MentionSuggestibleImpl(int i) {
        this.id = i;
    }

    @Override
    public String getText() {
        return "SuggestibleImpl" + this.id;
    }
}
