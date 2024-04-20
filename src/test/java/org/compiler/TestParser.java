package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.expressions.binary_expressions.NodeBinAdd;
import org.compiler.nodes.expressions.binary_expressions.NodeBinDiv;
import org.compiler.nodes.expressions.binary_expressions.NodeBinMulti;
import org.compiler.nodes.expressions.binary_expressions.NodeBinSub;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeIf;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.nodes.statements.NodeScope;
import org.compiler.token.TokenType;
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

        // control if the first statement is an exit
        assertEquals(parserExit.getTree().getStmts().getFirst().getClass(), NodeExit.class);

        // control if the value of the exit is 69
        TokenIntLit value = (TokenIntLit) parserExit.getTree().getStmts().getFirst().getStmt().getExpr();
        assertEquals(value.getValue(), 69);

        // control of error exit statement
        Tokenizer invalidExit = new Tokenizer("exit 69;");

        // control if the first statement is an exit
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidExit.getTokens()));
    }

    @Test
    public void testParserLet() {
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
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent2.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent3.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIdent4.getTokens()));
    }

    @Test
    public void testParserAdd() {
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
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidAdd1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidAdd2.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidAdd3.getTokens()));
    }

    @Test
    public void testParserMulti() {
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
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidMulti1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidMulti2.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidMulti3.getTokens()));
    }

    @Test
    public void testParserDiv() {
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
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidDiv1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidDiv2.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidDiv3.getTokens()));
    }

    @Test
    public void testParserSub() {
        Tokenizer validSub = new Tokenizer("let a = 10 - 5;");
        Parser parserSub = new Parser(validSub.getTokens());

        // control if the statement is a sub
        assertEquals(parserSub.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinSub.class);

        // control if the token is -
        assertEquals(parserSub.getTree().getStmts().getFirst().getStmt().getExpr().getType(), TokenType.minus);

        // control of error add statement
        Tokenizer invalidSub1 = new Tokenizer("let a = 10 -;");
        Tokenizer invalidSub2 = new Tokenizer("let a = - 5;");
        Tokenizer invalidSub3 = new Tokenizer("let a = 10 5;");
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidSub1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidSub2.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidSub3.getTokens()));
    }

    @Test
    public void testParserParenthesis() {
        Tokenizer validParenthesis = new Tokenizer("let a = (10 + 5) * 2;");
        Parser parserParenthesis = new Parser(validParenthesis.getTokens());

        // control if the nodeTerm is a parenthesis
        assertEquals(parserParenthesis.getTree().getStmts().getFirst().getStmt().getClass(), NodeBinMulti.class);

        // control of error parenthesis statement
        Tokenizer invalidParenthesis1 = new Tokenizer("let a = (10 + 5 * 2;");
        Tokenizer invalidParenthesis2 = new Tokenizer("let a = 10 + 5) * 2;");
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidParenthesis1.getTokens()));
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidParenthesis2.getTokens()));
    }

    @Test void testParserScope() {
        Tokenizer validScope = new Tokenizer("{ exit(10); }");
        Parser parserScope = new Parser(validScope.getTokens());

        //control if the first statement is a scope
        assertEquals(NodeScope.class, parserScope.getTree().getStmts().getFirst().getClass());

        //control error in scopes
        Tokenizer invalidScope = new Tokenizer("{ exit(10); ");
        assertThrows(NullPointerException.class, () -> new Parser(invalidScope.getTokens()));
    }

    @Test
    public void testParserIf() {
        Tokenizer validIf = new Tokenizer("if (x) { exit(1); }");
        Parser parserIf = new Parser(validIf.getTokens());

        //control if the first statement is an if
        assertEquals(NodeIf.class, parserIf.getTree().getStmts().getFirst().getClass());

        //control if the second token is an expression
        assertEquals(NodeIdent.class, parserIf.getTree().getStmts().getFirst().getStmt().getClass());

        //control errors in if statements
        Tokenizer invalidIf1 = new Tokenizer("if { exit(1); }");
        Tokenizer invalidIf2 = new Tokenizer("if (x) { exit(1); ");
        assertThrows(IllegalArgumentException.class, () -> new Parser(invalidIf1.getTokens()));
        assertThrows(NullPointerException.class, () -> new Parser(invalidIf2.getTokens()));
    }
}
