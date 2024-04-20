package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.NodeBinAdd;
import org.compiler.nodes.expressions.binary_expressions.NodeBinDiv;
import org.compiler.nodes.expressions.binary_expressions.NodeBinMulti;
import org.compiler.nodes.expressions.binary_expressions.NodeBinSub;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;
import org.compiler.nodes.expressions.terms.NodeTerm;
import org.compiler.nodes.expressions.terms.NodeTermParen;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
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
    NodeProgram tree;

    public Parser(ArrayList<Token> tokens) {
        this.it = new PeekIteratorToken(tokens);
        parseProgram();
    }

    private void parseProgram() {
        ArrayList<NodeStatement> stmts = new ArrayList<>();
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
        } else {
            throw new IllegalArgumentException("Invalid token in statement");
        }
    }
    private NodeExpression parseExpr(){
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
            case TokenType.plus -> {
                Token token = new Token(TokenType.plus);
                left = new NodeBinAdd(token, left, right);
            }
            case TokenType.minus -> {
                Token token = new Token(TokenType.minus);
                left = new NodeBinSub(token, left, right);
            }
            case TokenType.star -> {
                Token token = new Token(TokenType.star);
                left = new NodeBinMulti(token, left, right);
            }
            case TokenType.slash -> {
                Token token = new Token(TokenType.slash);
                left = new NodeBinDiv(token, left, right);
            }
            }
        }
        return left;
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
            return new NodeIdent((TokenIdent) it.next());
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
