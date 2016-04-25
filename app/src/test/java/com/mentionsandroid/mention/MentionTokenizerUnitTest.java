package com.mentionsandroid.mention;


import org.junit.Assert;
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
    public void testTokenizerShouldNotChangeWhenDisabled() throws Exception {
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
    public void testNormalTextShouldHaveOnlyOneToken() throws Exception {
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

    private void simulateTyping(String text, MentionTokenizer tokenizer) {
        for (int i = 0; i < text.length(); i++) {
            String sub = text.substring(0, i + 1);
            tokenizer.onTextChanged(sub, i, 0, 1);
        }
    }
}