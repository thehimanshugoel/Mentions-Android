package com.mentionsandroid.mention;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.mentionsandroid.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ningsuhen on 4/24/16.
 */
public class MentionController implements MentionTokenizer.ChangeHandler {
    private final MentionDelegate delegate;
    private final EditText editText;
    private final ListView listView;
    private final MentionTokenizer tokenizer;
    private final MentionAdaptor adapter;

    public MentionController(final MentionDelegate delegate, int listViewRid, int editTextRid, int textViewRid, int imageViewRid, int listItemLayoutId) {
        this.delegate = delegate;
        editText = (EditText) delegate.getActivity().findViewById(editTextRid);
        listView = (ListView) delegate.getActivity().findViewById(listViewRid);
        adapter = new MentionAdaptor(delegate.getContext(), listItemLayoutId, textViewRid, imageViewRid, new ArrayList<MentionSuggestible>(Arrays.asList(new MentionSuggestible[]{})));
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(adapter);
        tokenizer = new MentionTokenizer();
        editText.addTextChangedListener(tokenizer);
        tokenizer.addOnChangeListener(this);
    }

    /**
     *
     */
    @Override
    public void tokensChanged() {
        final MentionTokenizer.MentionToken suggestion = tokenizer.currentSuggestion;
        if (suggestion == null) {
            adapter.clear();
            listView.setVisibility(View.GONE);
            return;
        }
        MentionController.this.delegate.getSuggestions(suggestion.text, new MentionSuggestionsCallback() {
            @Override
            public void onReceived(List<MentionSuggestible> suggestibleList) {
                adapter.clear();

                adapter.delegate = new MentionAdaptor.MentionAdaptorDelegate() {
                    @Override
                    public void onSuggestionSelected(MentionSuggestible suggestible) {
                        suggestion.convertToMention(suggestible);

                        tokenizer.disable();

                        editText.setText(tokenizer.render());
                        editText.setSelection(suggestion.endIndex);

                        tokenizer.enable();

                        adapter.clear();
                        listView.setVisibility(View.GONE);
                    }
                };

                adapter.addAll(suggestibleList);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    public MentionTokenizer getTokenizer() {
        return tokenizer;
    }
}
