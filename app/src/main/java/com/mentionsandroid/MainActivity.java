package com.mentionsandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
        EditText editText = (EditText) findViewById(R.id.edtText);
        ListView listView = (ListView) findViewById(R.id.listView);
        mentionController = new MentionController(MainActivity.this, editText, listView);
    }

    @Override
    public Context getContext() {
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
