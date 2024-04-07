package org.compiler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TestTokenizer {
    @Test
    public void testTokenizer() {
        Tokenizer validExit = new Tokenizer("return 69;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType._return), new Token(TokenType.int_lit, "69"), new Token(TokenType.semi)));
        validExit = new Tokenizer(";;;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.semi), new Token(TokenType.semi), new Token(TokenType.semi)));
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("fausto"));
    }
}
