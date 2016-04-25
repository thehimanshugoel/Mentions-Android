package com.mentionsandroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;

import com.mentionsandroid.mention.MentionController;
import com.mentionsandroid.mention.MentionDelegate;
import com.mentionsandroid.mention.MentionSuggestible;
import com.mentionsandroid.mention.MentionSuggestionsCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MentionDelegate {
    MentionController mentionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mentionController = new MentionController(MainActivity.this, R.id.listView, R.id.edtText, android.R.id.text1, android.R.layout.simple_list_item_1);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getSuggestions(CharSequence text, MentionSuggestionsCallback mentionSuggestionsCallback) {
        List<MentionSuggestible> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add((MentionSuggestible) new MentionSuggestibleImpl(i));
        }
        mentionSuggestionsCallback.onReceived(list);
    }
}
