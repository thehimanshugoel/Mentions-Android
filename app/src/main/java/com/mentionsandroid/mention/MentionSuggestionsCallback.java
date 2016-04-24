package com.mentionsandroid.mention;

import java.util.List;

/**
 * Created by ningsuhen on 4/24/16.
 */
public interface MentionSuggestionsCallback {
    public void onReceived(List<MentionSuggestible> suggestibleList);
}
