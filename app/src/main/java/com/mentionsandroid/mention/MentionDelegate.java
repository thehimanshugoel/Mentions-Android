package com.mentionsandroid.mention;

import android.app.Activity;
import android.content.Context;

/**
 * Created by ningsuhen on 4/24/16.
 */
public interface MentionDelegate {
    Context getContext();
    Activity getActivity();
    void getSuggestions(CharSequence text, MentionSuggestionsCallback mentionSuggestionsCallback);
}
