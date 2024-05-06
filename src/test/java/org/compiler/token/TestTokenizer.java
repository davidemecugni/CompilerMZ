package org.compiler.token;

import org.compiler.errors.TokenError;
import org.compiler.token.tokens.Token;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestTokenizer {
    @Test
    public void testTokenizer() throws TokenError {
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
    public void testTokenizerComments() throws TokenError {
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
    public void testTokenizerLet() throws TokenError {
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
        assertThrows(TokenError.class, () -> new Tokenizer("let 1x = 10"));
        assertThrows(TokenError.class, () -> new Tokenizer("let 1010x = 10"));
        assertDoesNotThrow(() -> new Tokenizer("let x1 = 10;"));
        assertDoesNotThrow(() -> new Tokenizer("let x1010 = 10"));
        assertDoesNotThrow(() -> new Tokenizer("let x = 10;"));

    }

    @Test
    public void testTokenizeLetEmojis() throws TokenError {
        Tokenizer validLet = new Tokenizer("let 🤔 = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("🤔"),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));

        validLet = new Tokenizer("let 🤡🤡🤡 = 10;");
        assertEquals(validLet.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("🤡🤡🤡"),
                new Token(TokenType.eq), new TokenIntLit("10"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerIf() throws TokenError {
        Tokenizer validIf = new Tokenizer("if(10) { exit 0; }");
        assertEquals(validIf.getTokens(),
                List.of(new Token(TokenType._if), new Token(TokenType.open_paren), new TokenIntLit("10"),
                        new Token(TokenType.close_paren), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("0"), new Token(TokenType.semi), new Token(TokenType.close_curly)));
    }

    @Test
    public void testTokenizerIfElif() throws TokenError {
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
    public void testTokenizerIfElse() throws TokenError {
        Tokenizer validIfElse = new Tokenizer("if(10) { exit 0; } else { exit 1; }");
        assertEquals(validIfElse.getTokens(),
                List.of(new Token(TokenType._if), new Token(TokenType.open_paren), new TokenIntLit("10"),
                        new Token(TokenType.close_paren), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("0"), new Token(TokenType.semi), new Token(TokenType.close_curly),
                        new Token(TokenType._else), new Token(TokenType.open_curly), new Token(TokenType._exit),
                        new TokenIntLit("1"), new Token(TokenType.semi), new Token(TokenType.close_curly)));
    }

    @Test
    public void testTokenizerArithmetic() throws TokenError {
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

    @Test
    public void testTokenizerLogicEq() throws TokenError {
        Tokenizer validLogicEq = new Tokenizer("let a = 10 == 20;");
        assertEquals(validLogicEq.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.logic_eq), new TokenIntLit("20"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerLoginNotEq() throws TokenError {
        Tokenizer validLogicNotEq = new Tokenizer("let a = 10 != 20;");
        assertEquals(validLogicNotEq.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.logic_not_eq), new TokenIntLit("20"), new Token(TokenType.semi)));

    }

    @Test
    public void testTokenizerLogicGt() throws TokenError {
        Tokenizer validLogicGt = new Tokenizer("let a = 10 > 20;");
        assertEquals(validLogicGt.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.logic_gt), new TokenIntLit("20"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerLogicGe() throws TokenError {
        Tokenizer validLogicGe = new Tokenizer("let a = 10 >= 20;");
        assertEquals(validLogicGe.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.logic_ge), new TokenIntLit("20"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerLogicLt() throws TokenError {
        Tokenizer validLogicLt = new Tokenizer("let a = 10 < 20;");
        assertEquals(validLogicLt.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.logic_lt), new TokenIntLit("20"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerLogicLe() throws TokenError {
        Tokenizer validLogicLe = new Tokenizer("let a = 10 <= 20;");
        assertEquals(validLogicLe.getTokens(),
                List.of(new Token(TokenType.let), new TokenIdent("a"), new Token(TokenType.eq), new TokenIntLit("10"),
                        new Token(TokenType.logic_le), new TokenIntLit("20"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerNegativeNumber() throws TokenError {
        Tokenizer validNegativeNumber = new Tokenizer("let a = -10;");
        assertEquals(validNegativeNumber.getTokens(), List.of(new Token(TokenType.let), new TokenIdent("a"),
                new Token(TokenType.eq), new TokenIntLit("-10"), new Token(TokenType.semi)));
    }

    @Test
    public void testTokenizerOverflow() {
        assertThrows(TokenError.class, () -> new Tokenizer("let a = 9223372036854775808;"));
        assertThrows(TokenError.class, () -> new Tokenizer("let a = -9223372036854775808;"));
        assertDoesNotThrow(() -> new Tokenizer("let a = 9223372036854775807;"));
        assertDoesNotThrow(() -> new Tokenizer("let a = -9223372036854775807;"));

    }
}
