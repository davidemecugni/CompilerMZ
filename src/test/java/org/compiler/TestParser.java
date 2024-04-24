package org.compiler;

import org.compiler.errors.TokenError;
import org.compiler.nodes.expressions.binary_expressions.*;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.nodes.statements.NodeScope;
import org.compiler.nodes.statements.conditionals.NodeIf;
import org.compiler.nodes.statements.conditionals.NodeWhile;
import org.compiler.token.TokenType;
import org.compiler.token.Tokenizer;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;
import org.junit.jupiter.api.Test;

import javax.management.relation.RelationNotification;

import static org.junit.jupiter.api.Assertions.*;

public class TestParser {

    @Test
    public void testParserExit() throws TokenError {
        Tokenizer validExit = new Tokenizer("exit(69);");
        Parser parserExit = new Parser(validExit.getTokens());

        // control if the first statement is an exit
        assertEquals(parserExit.getTree().getStmts().getFirst().getClass(), NodeExit.class);

        // control if the value of the exit is 69
        TokenIntLit value = (TokenIntLit) parserExit.getTree().getStmts().getFirst().getStmt().getExpr();
        assertEquals(value.getValue(), 69);

        // control of error exit statement
        Tokenizer invalidExit = new Tokenizer("exit 69;");

        // control if the first statement is an exit
        assertThrows(TokenError.class, () -> new Parser(invalidExit.getTokens()));
    }

    @Test
    public void testParserLet() throws TokenError {
        Tokenizer validLet = new Tokenizer("let a = 10;");
        Parser parserLet = new Parser(validLet.getTokens());

        // control if the first statement is let
        assertEquals(parserLet.getTree().getStmts().getFirst().getClass(), NodeLet.class);

        // control if the identifier is a
        TokenIdent name = ((NodeLet) parserLet.getTree().getStmts().getFirst()).getIdentifier().getIdent();
        assertEquals(name.getName(), "a");

        // control if the value of the identifier is 10
        TokenIntLit value = (TokenIntLit) parserLet.getTree().getStmts().getFirst().getStmt().getExpr();
        assertEquals(value.getValue(), 10);

        // control of error let statement
        Tokenizer invalidIdent1 = new Tokenizer("let a 10;");
        Tokenizer invalidIdent2 = new Tokenizer("let a = 10");
        Tokenizer invalidIdent3 = new Tokenizer("let = 10");
        Tokenizer invalidIdent4 = new Tokenizer("a = ;");
        assertThrows(TokenError.class, () -> new Parser(invalidIdent1.getTokens()));
        assertThrows(RuntimeException.class, () -> new Parser(invalidIdent2.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidIdent3.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidIdent4.getTokens()));
    }

    @Test
    public void testParserAdd() throws TokenError {
        Tokenizer validAdd = new Tokenizer("let a = 10 + 5;");
        Parser parserAdd = new Parser(validAdd.getTokens());

        // control if the statement is an add
        assertEquals(parserAdd.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinAdd.class);

        // control if the token is +
        assertEquals(parserAdd.getTree().getStmts().getFirst().getStmt().getExpr().getType(), TokenType.plus);

        // control of error add statement
        Tokenizer invalidAdd1 = new Tokenizer("let a = 10 +;");
        Tokenizer invalidAdd2 = new Tokenizer("let a = + 5;");
        Tokenizer invalidAdd3 = new Tokenizer("let a = 10 5;");
        assertThrows(TokenError.class, () -> new Parser(invalidAdd1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidAdd2.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidAdd3.getTokens()));
    }

    @Test
    public void testParserMulti() throws TokenError {
        Tokenizer validMulti = new Tokenizer("let a = 10 * 5;");
        Parser parserMulti = new Parser(validMulti.getTokens());

        // control if the statement is a multi
        assertEquals(parserMulti.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinMulti.class);

        // control if the token is *
        assertEquals(parserMulti.getTree().getStmts().getFirst().getStmt().getExpr().getType(), TokenType.star);

        // control of error add statement
        Tokenizer invalidMulti1 = new Tokenizer("let a = 10 *;");
        Tokenizer invalidMulti2 = new Tokenizer("let a = * 5;");
        Tokenizer invalidMulti3 = new Tokenizer("let a = 10 5;");
        assertThrows(TokenError.class, () -> new Parser(invalidMulti1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidMulti2.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidMulti3.getTokens()));
    }

    @Test
    public void testParserDiv() throws TokenError {
        Tokenizer validDiv = new Tokenizer("let a = 10 / 5;");
        Parser parserDiv = new Parser(validDiv.getTokens());

        // control if the statement is a div
        assertEquals(parserDiv.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinDiv.class);

        // control if the token is /
        assertEquals(parserDiv.getTree().getStmts().getFirst().getStmt().getExpr().getType(), TokenType.slash);

        // control of error add statement
        Tokenizer invalidDiv1 = new Tokenizer("let a = 10 /;");
        Tokenizer invalidDiv2 = new Tokenizer("let a = / 5;");
        Tokenizer invalidDiv3 = new Tokenizer("let a = 10 5;");
        assertThrows(TokenError.class, () -> new Parser(invalidDiv1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidDiv2.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidDiv3.getTokens()));
    }

    @Test
    public void testParserSub() throws TokenError {
        Tokenizer validSub = new Tokenizer("let a = 10 - 5;");
        Parser parserSub = new Parser(validSub.getTokens());

        // control if the statement is a sub
        assertEquals(parserSub.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinSub.class);

        // control if the token is -
        assertEquals(parserSub.getTree().getStmts().getFirst().getStmt().getExpr().getType(), TokenType.minus);

        validSub = new Tokenizer("let a = - 5;");
        parserSub = new Parser(validSub.getTokens());
        assertEquals(parserSub.getTree().getStmts().getFirst().getStmt().getClass(), NodeIntLit.class);

        // control of error add statement
        Tokenizer invalidSub1 = new Tokenizer("let a = 10 -;");
        Tokenizer invalidSub2 = new Tokenizer("let a = 10 5;");
        assertThrows(TokenError.class, () -> new Parser(invalidSub1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidSub2.getTokens()));
    }

    @Test
    public void testParserModulo() throws TokenError {
        Tokenizer validModulo = new Tokenizer("let a = 13 % 5;");
        Parser parserModulo = new Parser(validModulo.getTokens());
        assertEquals(parserModulo.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinMod.class);

        assertEquals(parserModulo.getTree().getStmts().getFirst().getStmt().getExpr().getType(), TokenType.percent);

        Tokenizer invalidModulo1 = new Tokenizer("let a = 13 %;");
        Tokenizer invalidModulo2 = new Tokenizer("let a = % 5;");
        Tokenizer invalidModulo3 = new Tokenizer("let a = 13 5;");
        assertThrows(TokenError.class, () -> new Parser(invalidModulo1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidModulo2.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidModulo3.getTokens()));
    }

    @Test
    public void testParserParenthesis() throws TokenError {
        Tokenizer validParenthesis = new Tokenizer("let a = (10 + 5) * 2;");
        Parser parserParenthesis = new Parser(validParenthesis.getTokens());

        // control if the nodeTerm is a parenthesis
        assertEquals(parserParenthesis.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinMulti.class);

        // control of error parenthesis statement
        Tokenizer invalidParenthesis1 = new Tokenizer("let a = (10 + 5 * 2;");
        Tokenizer invalidParenthesis2 = new Tokenizer("let a = 10 + 5) * 2;");
        assertThrows(TokenError.class, () -> new Parser(invalidParenthesis1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidParenthesis2.getTokens()));
    }

    @Test
    void testParserScope() throws TokenError {
        Tokenizer validScope = new Tokenizer("{ exit(10); }");
        Parser parserScope = new Parser(validScope.getTokens());

        // control if the first statement is a scope
        assertEquals(NodeScope.class, parserScope.getTree().getStmts().getFirst().getClass());

        // control error in scopes
        Tokenizer invalidScope = new Tokenizer("{ exit(10); ");
        assertThrows(RuntimeException.class, () -> new Parser(invalidScope.getTokens()));
    }

    @Test
    public void testParserIf() throws TokenError {
        Tokenizer validIf = new Tokenizer("if (x) { exit(1); }");
        Parser parserIf = new Parser(validIf.getTokens());

        // control if the first statement is an if
        assertEquals(NodeIf.class, parserIf.getTree().getStmts().getFirst().getClass());

        // control if the second token is an expression
        assertEquals(NodeIdent.class, parserIf.getTree().getStmts().getFirst().getStmt().getClass());

        // control errors in if statements
        Tokenizer invalidIf1 = new Tokenizer("if { exit(1); }");
        Tokenizer invalidIf2 = new Tokenizer("if (x) { exit(1); ");
        assertThrows(TokenError.class, () -> new Parser(invalidIf1.getTokens()));
        assertThrows(RuntimeException.class, () -> new Parser(invalidIf2.getTokens()));
    }

    @Test
    public void testParserWhile() throws TokenError {
        Tokenizer validWhile = new Tokenizer("while (10) { let x = 1; }");
        Parser parserWhile = new Parser(validWhile.getTokens());

        // control if the first statement is a while
        assertEquals(NodeWhile.class, parserWhile.getTree().getStmts().getFirst().getClass());

        // control if the second token is an expression
        assertEquals(NodeIntLit.class, parserWhile.getTree().getStmts().getFirst().getStmt().getClass());

        // control errors in while statements
        Tokenizer invalidIWhile1 = new Tokenizer("while 10 { x = x + 1; }");
        Tokenizer invalidIWhile2 = new Tokenizer("while { x = x + 1; }");
        assertThrows(TokenError.class, () -> new Parser(invalidIWhile1.getTokens()));
        assertThrows(TokenError.class, () -> new Parser(invalidIWhile2.getTokens()));
    }
}
