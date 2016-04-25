package com.mentionsandroid.mention;

import android.text.Editable;
import android.text.TextWatcher;

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
            changeHandler.changed();
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
        if(last != null && last.type != TokenType.MENTION){
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
        private TokenType type = TokenType.NORMAL;
        public CharSequence text = "";
        public MentionToken next;
        private int startIndex;
        public int endIndex;
        private MentionSuggestible suggestible;

        public MentionToken(CharSequence s, int startIndex) {
            this.startIndex = startIndex;
            this.updateText(s);
        }

        public void updateNext() {
            if (next == null) {
                return;
            }
            next.startIndex = this.endIndex;
            next.endIndex = next.startIndex + next.text.length();
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
            int atIndex = newString.indexOf("@");
            if (type == TokenType.NORMAL) {
                if (atIndex > -1) {
                    if (atIndex == 0) {
                        //CONVERT NORMAL TO SUGGESTION
                        this.type = TokenType.SUGGESTION;
                        this.text = newValue;
                        this.endIndex = this.startIndex + this.text.length();
                        this.updateNext();
                        MentionTokenizer.this.currentSuggestion = this;
                    } else {
                        //Split NORMAL into NORMAL & SUGGESTION

                        //change text of self
                        String[] subTokens = newString.split("@");
                        this.text = subTokens[0];
                        this.endIndex = this.startIndex + this.text.length();
                        if (subTokens.length > 1) {
                            //create new token of type suggestion
                            String query = "@" + subTokens[1];
                            MentionToken token = new MentionToken(query, this.endIndex);
                            token.type = TokenType.SUGGESTION;
                            token.next = this.next;
                            this.next = token;
                            this.updateNext();
                            MentionTokenizer.this.currentSuggestion = token;
                        } else {
                            this.updateNext();
                        }

                    }
                } else {
                    // JUST UPDATE NORMAL TEXT
                    this.text = newValue;
                    this.endIndex = this.startIndex + this.text.length();
                    this.updateNext();
                }
            } else if (type == TokenType.SUGGESTION) {

                if (atIndex > -1) {
                    if (atIndex == 0) {
                        // JUST UPDATE SUGGESTION QUERY
                        this.text = newValue;
                        this.endIndex = this.startIndex + this.text.length();
                        this.updateNext();
                        MentionTokenizer.this.currentSuggestion = this;
                    } else {
                        //Split SUGGESTION into NORMAL & SUGGESTION

                        //change text of self
                        String[] subTokens = newString.split("(?=@)");
                        this.type = TokenType.NORMAL;
                        this.text = subTokens[0];
                        this.endIndex = this.startIndex + this.text.length();
                        if (subTokens.length > 1) {
                            //create new token of type suggestion
                            String query = "@" + subTokens[1];
                            MentionToken token = new MentionToken(query, this.endIndex);
                            token.type = TokenType.SUGGESTION;
                            token.next = this.next;
                            this.next = token;
                            this.updateNext();
                            MentionTokenizer.this.currentSuggestion = token;
                        } else {
                            this.updateNext();
                        }
                    }

                } else {
                    //CONVERT SUGGESTION TO NORMAL
                    this.type = TokenType.NORMAL;
                    this.text = newValue;
                    this.endIndex = this.startIndex + this.text.length();
                    this.updateNext();
                    MentionTokenizer.this.currentSuggestion = null;
                }

            } else if (type == TokenType.MENTION) {
                //CONVERT MENTION TO SUGGESTION
                this.type = TokenType.SUGGESTION;
                this.text = newValue;
                this.endIndex = this.startIndex + this.text.length();
                this.updateNext();
                MentionTokenizer.this.currentSuggestion = this;
            }
        }

        public void convertToMention(MentionSuggestible suggestible) {
            this.type = TokenType.MENTION;
            this.suggestible = suggestible;
            this.text = "@" + suggestible.getText();
            this.endIndex = this.startIndex + this.text.length();
            this.updateNext();
        }
    }

    static interface ChangeHandler {
        public void changed();
    }
}
