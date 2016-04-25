package com.mentionsandroid.mention;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ningsuhen on 4/24/16.
 */
public class MentionController {
    private final MentionDelegate delegate;
    private final EditText editText;
    private final ListView listView;
    private final MentionTokenizer tokenizer;

    public MentionController(final MentionDelegate delegate, final EditText editText, final ListView listView) {
        this.delegate = delegate;
        this.editText = editText;
        this.listView = listView;
        this.tokenizer = new MentionTokenizer();
        final MentionAdaptor adapter = new MentionAdaptor(delegate.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<MentionSuggestible>(Arrays.asList(new MentionSuggestible[]{})));

        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(adapter);

        editText.addTextChangedListener(tokenizer);
        tokenizer.addOnChangeListener(new MentionTokenizer.ChangeHandler() {
            @Override
            public void changed() {
                final MentionTokenizer.MentionToken suggestion = tokenizer.currentSuggestion;
                if (suggestion == null) {
                    adapter.clear();
                    listView.setVisibility(View.GONE);
                    return;
                }
                delegate.getSuggestions(suggestion.text, new MentionSuggestionsCallback() {
                    @Override
                    public void onReceived(List<MentionSuggestible> suggestibleList) {
                        adapter.clear();
                        adapter.addAll(suggestibleList);
                        adapter.delegate = new MentionAdaptor.MentionAdaptorDelegate() {
                            @Override
                            public void onSuggestionSelected(MentionSuggestible suggestible) {
                                suggestion.convertToMention(suggestible);
                                tokenizer.disable();
                                editText.setText(tokenizer.render());
                                tokenizer.enable();
                                Log.d("editText", tokenizer.render() + " " + tokenizer.render().length() + " " + editText.length() + " " + suggestion.endIndex);
                                editText.setSelection(suggestion.endIndex);
                                tokenizer.currentSuggestion = null;
                                adapter.clear();
                                listView.setVisibility(View.GONE);

                            }
                        };
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

}
