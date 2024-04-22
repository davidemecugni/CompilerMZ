package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.*;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;
import org.compiler.nodes.expressions.terms.NodeTerm;
import org.compiler.nodes.expressions.terms.NodeTermParen;
import org.compiler.nodes.statements.NodeAssign;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.nodes.statements.NodeScope;
import org.compiler.nodes.statements.conditionals.Conditional;
import org.compiler.nodes.statements.conditionals.NodeElif;
import org.compiler.nodes.statements.conditionals.NodeIf;
import org.compiler.nodes.statements.conditionals.NodeWhile;
import org.compiler.peekers.PeekIteratorToken;
import org.compiler.token.TokenType;
import org.compiler.token.tokens.Token;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;

import java.util.ArrayList;

/**
 * Represents a parser
 *
 * @see PeekIteratorToken
 */
public class Parser {
    private final PeekIteratorToken it;
    private NodeProgram tree;
    private final ArrayList<NodeStatement> stmts = new ArrayList<>();

    public Parser(ArrayList<Token> tokens) {
        this.it = new PeekIteratorToken(tokens);
        parseProgram();
    }

    private void parseProgram() {
        while (it.hasNext()) {
            stmts.add(parseStmt());
        }
        tree = new NodeProgram(stmts);
    }

    private NodeStatement parseStmt() {
        if (it.hasNext() && it.peek().getType() == TokenType._exit) {
            it.next();
            return parseExit();
        } else if (it.hasNext() && it.peek().getType() == TokenType.let) {
            it.next();
            return parseLet();
        } else if (it.hasNext() && it.peek().getType() == TokenType.ident && it.hasNext()
                && it.peek(1).getType() == TokenType.eq) {
            return parseAssign();
        } else if (it.hasNext() && it.peek().getType() == TokenType.open_curly) {
            it.next();
            return parseScope();
        } else if (it.hasNext() && it.peek().getType() == TokenType._if) {
            it.next();
            Conditional conditional = parseCondition();
            NodeIf nodeIf = new NodeIf(conditional.getStmt(), conditional.getScope());
            while (it.hasNext() && it.peek().getType() == TokenType.elif) {
                it.next();
                Conditional conditionalElif = parseCondition();
                nodeIf.addScopeElif(new NodeElif(conditionalElif.getStmt(), conditionalElif.getScope()));
            }
            if (it.hasNext() && it.peek().getType() == TokenType._else) {
                it.next();
                it.next(); // consume curly braces
                nodeIf.setScopeElse(parseScope());
            }
            return nodeIf;
        } else if (it.hasNext() && it.peek().getType() == TokenType._while) {
            it.next();
            Conditional conditional = parseCondition();
            return new NodeWhile(conditional.getStmt(), conditional.getScope());
        } else {
            throw new IllegalArgumentException("Invalid token in statement");
        }
    }

    private NodeExpression parseExpr() {
        return parseExpr(0);
    }

    private NodeExpression parseExpr(int minPrec) {
        // salva il primo termine

        NodeExpression left = parseTerm();

        // Precedence Climbing Algorithm
        while (it.hasNext()) {
            Token curr_token = it.peek();
            int prec;
            if (curr_token != null) {
                prec = curr_token.getPrecedence();
                if (prec < minPrec) {
                    break;
                }
            } else {
                break;
            }
            Token op = it.next();
            int nextMinPrec = prec + 1;
            NodeExpression right = parseExpr(nextMinPrec);
            switch (op.getType()) {
            case TokenType.plus -> left = new NodeBinAdd(curr_token, left, right);
            case TokenType.minus -> left = new NodeBinSub(curr_token, left, right);
            case TokenType.star -> left = new NodeBinMulti(curr_token, left, right);
            case TokenType.slash -> left = new NodeBinDiv(curr_token, left, right);
            case TokenType.percent -> left = new NodeBinMod(curr_token, left, right);
            }
        }
        return left;
    }

    private NodeAssign parseAssign() {
        TokenIdent ident = (TokenIdent) it.next();
        it.next();
        NodeExpression expr = parseExpr();
        if (it.next().getType() != TokenType.semi) {
            throw new IllegalArgumentException("Semicolon expected");
        }
        return new NodeAssign(expr, ident);
    }

    private NodeScope parseScope() {
        ArrayList<NodeStatement> statements = new ArrayList<>();
        while (it.peek().getType() != TokenType.close_curly) {
            if (!it.hasNext()) {
                throw new IllegalArgumentException("Curly braces not closed");
            }
            statements.add(parseStmt());
        }
        it.next();
        return new NodeScope(null, statements);
    }

    private Conditional parseCondition() {
        if (it.peek().getType() != TokenType.open_paren) {
            throw new IllegalArgumentException("invalid token after if statement, expected parenthesis");
        }
        it.next();
        NodeExpression expr = parseExpr();
        if (it.peek().getType() != TokenType.close_paren) {
            throw new IllegalArgumentException("Parenthesis not closed");
        }
        it.next(); // consume close_paren
        it.next(); // consume open_curly brace
        NodeScope scope = parseScope();

        return new Conditional(expr, scope);
    }

    private NodeExit parseExit() {
        NodeExpression expr;
        if (!it.hasNext() || it.next().getType() != TokenType.open_paren) {
            throw new IllegalArgumentException("Invalid token after exit, expected open parenthesis");
        }
        expr = parseExpr();
        NodeExit exit = new NodeExit(expr);
        if (!it.hasNext() || it.next().getType() != TokenType.close_paren) {
            throw new IllegalArgumentException("Parenthesis not closed");
        }
        if (!it.hasNext() || it.next().getType() != TokenType.semi) {
            throw new IllegalArgumentException("Semicolon not present");
        }
        return exit;
    }

    private NodeLet parseLet() {

        NodeIdent ident;
        if (!it.hasNext() || it.peek().getType() != TokenType.ident) {
            throw new IllegalArgumentException("Invalid token after let, expected identifier");
        }
        ident = new NodeIdent((TokenIdent) it.next());
        if (!it.hasNext() || it.next().getType() != TokenType.eq) {
            throw new IllegalArgumentException("Invalid token after ident, expected equal sign");
        }
        NodeExpression expr = parseExpr();
        if (!it.hasNext() || it.next().getType() != TokenType.semi) {
            throw new IllegalArgumentException("Semicolon not present");
        }
        return new NodeLet(expr, ident);
    }

    // controlla se c'è un termine e se è un int_lit o un ident
    private NodeTerm parseTerm() {
        if (it.hasNext() && it.peek().getType() == TokenType.int_lit) {
            return new NodeIntLit((TokenIntLit) it.next());
        }
        if (it.hasNext() && it.peek().getType() == TokenType.ident) {
            TokenIdent ident = (TokenIdent) it.next();
            return new NodeIdent(ident);
        }

        if (it.hasNext() && it.peek().getType() == TokenType.open_paren) {
            it.next();
            NodeExpression expr = parseExpr();
            if (expr == null) {
                throw new IllegalArgumentException("Expected expression");
            }
            if (!it.hasNext() || it.peek().getType() != TokenType.close_paren) {
                throw new IllegalArgumentException("Parenthesis not closed");
            }
            return new NodeTermParen(it.next(), expr);
        } else {
            throw new IllegalArgumentException("Invalid token term");
        }
    }

    public NodeProgram getTree() {
        return tree;
    }

    @Override
    public String toString() {
        return "Parser{" + "it=" + it + ", tree=" + tree + '}';
    }
}
