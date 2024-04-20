package org.compiler.token;

import org.compiler.token.tokens.Token;
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
        assertEquals(validExit.getTokens(),
                List.of(new Token(TokenType._exit), new TokenIntLit("69"), new Token(TokenType.semi)));
        assertNotEquals(validExit.getTokens(),
                List.of(new Token(TokenType._exit), new TokenIntLit("70"), new Token(TokenType.semi)));
        validExit = new Tokenizer(";;;");
        assertEquals(validExit.getTokens(),
                List.of(new Token(TokenType.semi), new Token(TokenType.semi), new Token(TokenType.semi)));
        validExit = new Tokenizer("()");
        assertEquals(validExit.getTokens(), List.of(new Token(TokenType.open_paren), new Token(TokenType.close_paren)));
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
        assertEquals(List.of(new TokenIntLit("10"), new Token(TokenType.semi)), validComment.getTokens());
        validComment = new Tokenizer("@@ -0,0 +1,32 @@\n\n@@\nciao\n@@exit 100;");
        assertEquals(List.of(new Token(TokenType._exit), new TokenIntLit("100"), new Token(TokenType.semi)),
                validComment.getTokens());

        assertThrows(NoSuchElementException.class, () -> new Tokenizer("@@"));
        assertThrows(NoSuchElementException.class, () -> new Tokenizer("@@ 10;"));
        assertThrows(NoSuchElementException.class, () -> new Tokenizer("@@ 10;\n\n@"));
    }

    @Test
    public void testTokenizerLet() {
        Tokenizer validLet = new Tokenizer("let x = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("x"),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));
        validLet = new Tokenizer("let x = 10; let y = 20;");
        assertEquals(validLet.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("y"),
                        new Token(TokenType.eq), new TokenIntLit("20"), new Token(TokenType.semi)));
        validLet = new Tokenizer("let x = 10; let y = 20; let z = 30;");
        assertEquals(validLet.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("y"),
                        new Token(TokenType.eq), new TokenIntLit("20"), new Token(TokenType.semi),
                        new Token(TokenType.let), new TokenIdent("z"), new Token(TokenType.eq), new TokenIntLit("30"),
                        new Token(TokenType.semi)));
        validLet = new Tokenizer("let x = 10; let y = 20; let z = 30; let w = 40;");
        assertEquals(validLet.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("y"),
                        new Token(TokenType.eq), new TokenIntLit("20"), new Token(TokenType.semi),
                        new Token(TokenType.let), new TokenIdent("z"), new Token(TokenType.eq), new TokenIntLit("30"),
                        new Token(TokenType.semi), new Token(TokenType.let), new TokenIdent("w"),
                        new Token(TokenType.eq), new TokenIntLit("40"), new Token(TokenType.semi)));

        validLet = new Tokenizer("let variable = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("variable"),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));
        validLet = new Tokenizer("let let = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new Token(TokenType.let),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));
        validLet = new Tokenizer("letx=10;");
        assertEquals(validLet.getTokens(), List.of(new TokenIdent("letx"), new Token(TokenType.eq),
                new TokenIntLit("10"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerInvalidLet() {
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("let 1x = 10"));
        assertThrows(IllegalArgumentException.class, () -> new Tokenizer("let 1010x = 10"));
        assertDoesNotThrow(() -> new Tokenizer("let x1 = 10"));
    }

    @Test
    public void testTokenizeLetEmojis() {
        Tokenizer validLet = new Tokenizer("let 🤔 = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("🤔"),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));

        validLet = new Tokenizer("let 🤡🤡🤡 = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("🤡🤡🤡"),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerIf() {
        Tokenizer validIf = new Tokenizer("if(10) { exit 0; }");
        assertEquals(validIf.getTokens(),
                List.of(new Token(TokenType._if), new Token(TokenType.open_paren), new TokenIntLit("10"),
                        new Token(TokenType.close_paren), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("0"), new Token(TokenType.semi), new Token(TokenType.close_curly)));
    }

    @Test
    public void testTokenizerIfElif() {
        Tokenizer validIfElif = new Tokenizer("if(10) { exit 0; } elif(20) { exit 1; }");
        assertEquals(validIfElif.getTokens(),
                List.of(new Token(TokenType._if), new Token(TokenType.open_paren), new TokenIntLit("10"),
                        new Token(TokenType.close_paren), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("0"), new Token(TokenType.semi), new Token(TokenType.close_curly),
                        new Token(TokenType.elif), new Token(TokenType.open_paren), new TokenIntLit("20"),
                        new Token(TokenType.close_paren), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("1"), new Token(TokenType.semi), new Token(TokenType.close_curly)));
    }

    @Test
    public void testTokenizerIfElse() {
        Tokenizer validIfElse = new Tokenizer("if(10) { exit 0; } else { exit 1; }");
        assertEquals(validIfElse.getTokens(),
                List.of(new Token(TokenType._if), new Token(TokenType.open_paren), new TokenIntLit("10"),
                        new Token(TokenType.close_paren), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("0"), new Token(TokenType.semi), new Token(TokenType.close_curly),
                        new Token(TokenType._else), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("1"), new Token(TokenType.semi), new Token(TokenType.close_curly)));
    }

    @Test
    public void testTokenizerArithmetic() {
        Tokenizer validArithmetic = new Tokenizer("let a = 10 + 20 + (29 * 10);");
        assertEquals(validArithmetic.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.plus), new TokenIntLit("20"), new Token(TokenType.plus),
                        new Token(TokenType.open_paren), new TokenIntLit("29"), new Token(TokenType.star),
                        new TokenIntLit("10"), new Token(TokenType.close_paren), new Token(TokenType.semi)));
        validArithmetic = new Tokenizer("let x=10*20((");
        assertEquals(validArithmetic.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("x"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.star), new TokenIntLit("20"), new Token(TokenType.open_paren),
                        new Token(TokenType.open_paren)));
    }
}
