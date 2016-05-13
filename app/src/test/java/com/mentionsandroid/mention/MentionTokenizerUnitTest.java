package com.mentionsandroid.mention;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(JUnit4.class)
public class MentionTokenizerUnitTest {

    private MentionTokenizer tokenizer;

    @Before
    public void setup() {
        this.tokenizer = new MentionTokenizer();
    }


    @Test
    public void default_values_should_be_null() throws Exception {
        assertNull(tokenizer.currentSuggestion);
        assertNull(tokenizer.root);
        assertEquals(tokenizer.render(), "");
    }

    @Test
    public void tokenizer_should_not_process_when_disabled() throws Exception {
        tokenizer.disable();
        tokenizer.onTextChanged("@1", 0, 0, 2);
        assertNull(tokenizer.root);
        assertNull(tokenizer.currentSuggestion);
        tokenizer.enable();
        tokenizer.onTextChanged("@1", 0, 0, 2);
        assertNotNull(tokenizer.root);
        assertNotNull(tokenizer.currentSuggestion);
    }

    @Test
    public void test_normal_Text_Should_Have_Only_One_Token() throws Exception {
        simulateTyping("some lengthy text ", tokenizer);
        assertNotNull(tokenizer.root);
        assertEquals(tokenizer.root.text, "some lengthy text ");
        assertNull(tokenizer.root.next);
        assertNull(tokenizer.currentSuggestion);
    }

    @Test
    public void text_that_Starts_With_Aderac_Should_Trigger_Suggestion() throws Exception {
        simulateTyping("@some lengthy text ", tokenizer);
        assertNotNull(tokenizer.root);
        assertEquals(tokenizer.root.text, "@some lengthy text ");
        assertNull(tokenizer.root.next);
        assertNotNull(tokenizer.currentSuggestion);
        assertEquals(tokenizer.root, tokenizer.currentSuggestion);
    }

    @Test
    public void text_with_aderac_should_trigger_suggestion() throws Exception {
        simulateTyping("normal text @some lengthy text ", tokenizer);
        assertNotNull(tokenizer.root);
        assertEquals(tokenizer.root.text, "normal text ");
        assertNotNull(tokenizer.root.next);
        assertNotNull(tokenizer.currentSuggestion);
        assertEquals(tokenizer.root.next, tokenizer.currentSuggestion);
        assertNotEquals(tokenizer.root, tokenizer.currentSuggestion);
        assertEquals(tokenizer.currentSuggestion.text, "@some lengthy text ");
    }

    @Test
    public void text_with_multiple_aderac_should_have_multiple_tokens() throws Exception {
        simulateTyping("@first token @second token", tokenizer);
        assertNotNull(tokenizer.root);
        assertEquals(tokenizer.root.text, "@first token ");
        assertNotNull(tokenizer.root.next);
        assertNotNull(tokenizer.currentSuggestion);
        assertEquals(tokenizer.root.next, tokenizer.currentSuggestion);
        assertNotEquals(tokenizer.root, tokenizer.currentSuggestion);
        assertEquals(tokenizer.currentSuggestion.text, "@second token");
    }

    @Test
    public void test_convert_to_mention_should() throws Exception {
        simulateTyping("@first token @second token", tokenizer);
        assertNotNull(tokenizer.root);
        assertNotNull(tokenizer.root.next);
        assertNotNull(tokenizer.currentSuggestion);
        MentionTokenizer.MentionToken token = tokenizer.currentSuggestion;
        MentionSuggestible suggestible = new MentionSuggestible() {
            @Override
            public String getText() {
                return "sample suggestible";
            }
        };
        tokenizer.currentSuggestion.convertToMention(suggestible);
        assertNull(tokenizer.currentSuggestion);
        assertEquals(token.type, MentionTokenizer.TokenType.MENTION);
        assertEquals(token.text, "@" + suggestible.getText());
        assertEquals(token, tokenizer.root.next);
    }

    @Test
    public void editing_mention_at_end_should_convert_it_to_suggestion() throws Exception {
        simulateTyping("@first token @second token", tokenizer);
        assertNotNull(tokenizer.root);
        assertNotNull(tokenizer.root.next);
        assertNotNull(tokenizer.currentSuggestion);
        MentionTokenizer.MentionToken token = tokenizer.currentSuggestion;
        MentionSuggestible suggestible = new MentionSuggestible() {
            @Override
            public String getText() {
                return "sample suggestible";
            }
        };
        tokenizer.currentSuggestion.convertToMention(suggestible);
        assertNull(tokenizer.currentSuggestion);
        assertEquals(token.type, MentionTokenizer.TokenType.MENTION);
        assertEquals(token.text, "@" + suggestible.getText());

        simulateDelete(tokenizer, tokenizer.render().length() - 1, tokenizer.render().length());
        assertEquals(token.type, MentionTokenizer.TokenType.SUGGESTION);
        assertEquals(token.text, "@" + suggestible.getText().substring(0, suggestible.getText().length() - 1));

    }

    @Test
    public void editing_mention_at_middle_should_convert_it_to_suggestion() throws Exception {
        simulateTyping("@first token", tokenizer);
        MentionTokenizer.MentionToken token = tokenizer.currentSuggestion;
        MentionSuggestible suggestible = new MentionSuggestible() {
            @Override
            public String getText() {
                return "sample suggestible";
            }
        };
        tokenizer.currentSuggestion.convertToMention(suggestible);

        assertEquals(token.type, MentionTokenizer.TokenType.MENTION);
        assertEquals(token, tokenizer.root);
        simulateAppend(tokenizer, " @second token");

        MentionTokenizer.MentionToken token2 = tokenizer.currentSuggestion;
        MentionSuggestible suggestible2 = new MentionSuggestible() {
            @Override
            public String getText() {
                return "sample suggestible2";
            }
        };
        token2.convertToMention(suggestible2);

        assertEquals(token2.type, MentionTokenizer.TokenType.MENTION);
        assertEquals(tokenizer.root, token);
        assertEquals(tokenizer.root.next.next, token2);

        simulateDelete(tokenizer, token.endIndex - 1, token.endIndex);
        assertEquals(token.type, MentionTokenizer.TokenType.SUGGESTION);
        assertEquals(token.text, "@" + suggestible.getText().substring(0, suggestible.getText().length() - 1));
        assertEquals(tokenizer.currentSuggestion, token);
    }

    private void simulateAppend(MentionTokenizer tokenizer, String text) {
        String initial = tokenizer.render();
        int initialLength = initial.length();
        for (int i = 0; i < text.length(); i++) {
            String sub = initial + text.substring(0, i + 1);
            tokenizer.onTextChanged(sub, initialLength + i, 0, 1);
        }
    }

    /**
     * @param tokenizer
     * @param startPos
     * @param endPos    excluding endPos Position
     */
    private void simulateDelete(MentionTokenizer tokenizer, int startPos, int endPos) {
        String text = tokenizer.render();
        String newText = text.substring(0, startPos);
        if (endPos < text.length()) {
            newText += text.substring(endPos, text.length());
        }
        tokenizer.onTextChanged(newText, startPos, endPos - startPos, 0);
    }

    private void simulateTyping(String text, MentionTokenizer tokenizer) {
        for (int i = 0; i < text.length(); i++) {
            String sub = text.substring(0, i + 1);
            tokenizer.onTextChanged(sub, i, 0, 1);
        }
    }
}