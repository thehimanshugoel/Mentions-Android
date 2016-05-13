package com.mentionsandroid.mention;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningsuhen on 4/24/16.
 */
public class MentionTokenizer implements TextWatcher {

    MentionToken root;
    public MentionToken currentSuggestion;
    private List<ChangeHandler> onChangeListeners = new ArrayList<>();
    private boolean disabled = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (disabled) {
            return;
        }
        int startIndex = start;
        int endIndex = start + before;
        int newEndIndex = startIndex + count;
        MentionToken token = findFirstToken(startIndex, s);
        token.removeTokensTill(endIndex);
        CharSequence newValue = s.subSequence(token.startIndex, newEndIndex);
        token.updateText(newValue);
        for (ChangeHandler changeHandler : onChangeListeners) {
            changeHandler.tokensChanged();
        }
    }


    @Override
    public void afterTextChanged(Editable s) {

    }


    private MentionToken findFirstToken(int curIndex, CharSequence s) {
        MentionToken current = root;
        MentionToken last = root;
        while (current != null) {
            if (current.startIndex <= curIndex && current.endIndex > curIndex) {
                return current;
            }
            current = current.next;
            if (current != null) {
                last = current;
            }
        }
        if (last != null && last.type != TokenType.MENTION) {
            return last;
        }
        MentionToken newToken = new MentionToken(s.subSequence(curIndex, s.length()), curIndex);
        if (last != null) {
            last.next = newToken;
        } else {
            root = newToken;
        }
        return newToken;
    }

    public void addOnChangeListener(ChangeHandler changeHandler) {
        this.onChangeListeners.add(changeHandler);
    }

    public String render() {
        MentionToken current = this.root;
        StringBuilder builder = new StringBuilder();
        while (current != null) {
            builder.append(current.text);
            current = current.next;
        }
        return builder.toString();
    }

    public void disable() {
        disabled = true;
    }

    public void enable() {
        disabled = false;
    }

    enum TokenType {
        NORMAL,
        SUGGESTION,
        MENTION
    }

    class MentionToken {
         TokenType type = TokenType.NORMAL;
        public CharSequence text = "";
        public MentionToken next;
        private int startIndex;
        public int endIndex;
        MentionSuggestible suggestible;

        public MentionToken(CharSequence s, int startIndex) {
            this.startIndex = startIndex;
            this.updateText(s);
        }

        public void updateNext() {
            if (next == null) {
                return;
            }
            next.startIndex = this.endIndex;
            next.updateEndIndex();
            next.updateNext();
        }

        public void removeTokensTill(int endIndex) {
            MentionToken current = this.next;
            while (current != null && current.startIndex < endIndex) {
                //remove current
                this.next = current.next;
                current = current.next;
            }
        }

        public void updateText(CharSequence newValue) {

            String newString = newValue.toString();
            if (newString.equals("")) {
                processText(newString);
                Log.d("ERROR", "entered empty String");
                return;
            }
            String[] trialTokens = newString.split("(?=@)", 3);
            String firstToken = trialTokens[0];
            String secondToken = null;
            if (firstToken.equals("")) {
                firstToken = trialTokens[1];
                if (trialTokens.length == 3) {
                    secondToken = trialTokens[2];
                }
            } else {
                if (trialTokens.length == 2) {
                    secondToken = trialTokens[1];
                } else if (trialTokens.length == 3) {
                    secondToken = trialTokens[1] + trialTokens[2];
                }
            }

            this.processText(firstToken);
            if (secondToken != null && !secondToken.equals("")) {
                //create new token of type suggestion
                this.insert(new MentionToken(secondToken, this.endIndex));
            }
        }

        private void processText(String text) {
            if (text.contains("@")) {
                if (this.type == TokenType.NORMAL) {
                    this.convertToSuggestion();
                } else if (this.type == TokenType.MENTION) {
                    this.revertToSuggestion();
                }
            } else {
                if (this.type != TokenType.NORMAL) {
                    this.revertToNormal();
                }
            }
            this.setText(text);
        }

        private void revertToNormal() {
            this.type = TokenType.NORMAL;
            if (MentionTokenizer.this.currentSuggestion == this) {
                MentionTokenizer.this.currentSuggestion = null;
            }
        }

        private void revertToSuggestion() {
            this.type = TokenType.SUGGESTION;
            this.suggestible = null;
            MentionTokenizer.this.currentSuggestion = this;
        }

        private void convertToSuggestion() {
            this.type = TokenType.SUGGESTION;
            MentionTokenizer.this.currentSuggestion = this;
        }

        private void insert(MentionToken token) {
            token.next = this.next;
            this.next = token;
            token.updateEndIndex();
        }

        public void convertToMention(MentionSuggestible suggestible) {
            this.type = TokenType.MENTION;
            this.suggestible = suggestible;
            this.setText("@" + suggestible.getText());
            if (MentionTokenizer.this.currentSuggestion == this) {
                MentionTokenizer.this.currentSuggestion = null;
            }
        }

        private void updateEndIndex() {
            this.endIndex = this.startIndex + this.text.length();
            this.updateNext();
        }

        private void setText(String text) {
            this.text = text;
            updateEndIndex();
        }
    }


    interface ChangeHandler {
        void tokensChanged();
    }
}
