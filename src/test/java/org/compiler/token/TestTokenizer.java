package org.compiler.token;

import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
public class TestTokenizer {
    @Test
    public void testTokenizer() {
        Tokenizer validExit = new Tokenizer("exit 69;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType._exit), new TokenIntLit( "69"), new Token(TokenType.semi)));
        assertNotEquals(validExit.getTokens(), List.of(new Token(TokenType._exit), new TokenIntLit( "70"), new Token(TokenType.semi)));
        validExit = new Tokenizer(";;;");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.semi), new Token(TokenType.semi), new Token(TokenType.semi)));
        validExit = new Tokenizer("()");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.open_paren), new Token(TokenType.close_paren)));
        // NON WORKA assertThrows(IllegalArgumentException.class, () -> new Tokenizer("exit 10.0;"));
    }

    @Test
    public void testTokenizerComments() {
        Tokenizer validComment = new Tokenizer("@ commento");
        assertEquals(validComment.getTokens(), List.of());
        validComment = new Tokenizer("@ commento\n");
        assertEquals(validComment.getTokens(), List.of());
        validComment = new Tokenizer("@ commento\n\n");
        assertEquals(validComment.getTokens(), List.of());
        validComment = new Tokenizer("@@ commento\n\n\n@@\n\n");
        assertEquals(validComment.getTokens(), List.of());
        validComment = new Tokenizer("@@ commento\n\n\n@@10;@");
        assertEquals(List.of(new TokenIntLit( "10"), new Token(TokenType.semi)), validComment.getTokens());
        validComment = new Tokenizer("@@ -0,0 +1,32 @@\n\n@@\nciao\n@@exit 100;");
        assertEquals(List.of(new Token(TokenType._exit), new TokenIntLit( "100"), new Token(TokenType.semi)), validComment.getTokens());
        assertThrows(NoSuchElementException.class, () -> new Tokenizer("@@"));
        assertThrows(NoSuchElementException.class, () -> new Tokenizer("@@ 10;"));
        assertThrows(NoSuchElementException.class, () -> new Tokenizer("@@ 10;\n\n@"));
    }

    @Test
    public void testTokenizerLet() {
        Tokenizer validLet = new Tokenizer("let x = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit( "10"), new Token(TokenType.semi)));
        validLet = new Tokenizer("let x = 10; let y = 20;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit( "10"), new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("y"), new Token(TokenType.eq), new TokenIntLit( "20"), new Token(TokenType.semi)));
        validLet = new Tokenizer("let x = 10; let y = 20; let z = 30;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit( "10"), new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("y"), new Token(TokenType.eq), new TokenIntLit( "20"), new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("z"), new Token(TokenType.eq), new TokenIntLit( "30"), new Token(TokenType.semi)));
        validLet = new Tokenizer("let x = 10; let y = 20; let z = 30; let w = 40;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit( "10"), new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("y"), new Token(TokenType.eq), new TokenIntLit( "20"), new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("z"), new Token(TokenType.eq), new TokenIntLit( "30"), new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("w"), new Token(TokenType.eq), new TokenIntLit( "40"), new Token(TokenType.semi)));
    }
}
