package org.compiler.token;

import org.compiler.token.tokens.IntLit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TestTokenizer {
    @Test
    public void testTokenizer() {
        Tokenizer validExit = new Tokenizer("exit 69;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType._exit), new IntLit( "69"), new Token(TokenType.semi)));
        validExit = new Tokenizer(";;;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.semi), new Token(TokenType.semi), new Token(TokenType.semi)));
        validExit = new Tokenizer("()");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.open_paren), new Token(TokenType.close_paren)));
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("fausto"));
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("exxit 10;"));
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("exit 10.0;"));
    }
}
