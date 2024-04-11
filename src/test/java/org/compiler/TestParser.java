package org.compiler;

import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.token.Tokenizer;
import static org.junit.jupiter.api.Assertions.*;

import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;
import org.junit.jupiter.api.Test;

public class TestParser {

    @Test
    public void testParserExit() {
        Tokenizer validExit = new Tokenizer("exit(69);");
        Parser parserExit = new Parser(validExit.getTokens());

        //control if the first statement is an exit
        assertEquals(parserExit.getTree().getStmts().getFirst().getClass(), NodeExit.class);

        //control if the value of the exit is 69
        TokenIntLit value = (TokenIntLit) parserExit.getTree().getStmts().getFirst().getStmt().getExpr();
        assertEquals(value.getValue(), 69);

        //control of error exit statement
        Tokenizer invalidExit = new Tokenizer("exit 69;");

        //control if the first statement is an exit
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidExit.getTokens()));
    }

    @Test
    public void testParserLet() {
        Tokenizer validLet = new Tokenizer("let a = 10;");
        Parser parserLet = new Parser(validLet.getTokens());

        //control if the first statement is let
        assertEquals(parserLet.getTree().getStmts().getFirst().getClass(), NodeLet.class);

        //control if the identifier is a
        TokenIdent name = ((NodeLet) parserLet.getTree().getStmts().getFirst()).getIdentifier().getIdent();
        assertEquals(name.getName(), "a");

        //control if the value of the identifier is 10
        TokenIntLit value = (TokenIntLit) parserLet.getTree().getStmts().getFirst().getStmt().getExpr();
        assertEquals(value.getValue(), 10);

        //control of error let statement
        Tokenizer invalidIdent1 = new Tokenizer("let a 10;");
        Tokenizer invalidIdent2 = new Tokenizer("let a = 10");
        Tokenizer invalidIdent3 = new Tokenizer("let = 10");
        Tokenizer invalidIdent4 = new Tokenizer("a = ;");
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent2.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent3.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent4.getTokens()));
    }
}
