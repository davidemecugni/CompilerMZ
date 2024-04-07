package org.compiler;
import org.compiler.token.Token;
import org.compiler.token.TokenType;
import org.compiler.token.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TestTokenizer {
    @Test
    public void testTokenizer() {
        Tokenizer validExit = new Tokenizer("exit 69;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType._exit), new Token(TokenType.int_lit, "69"), new Token(TokenType.semi)));
        validExit = new Tokenizer(";;;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.semi), new Token(TokenType.semi), new Token(TokenType.semi)));
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("fausto"));
    }
}
