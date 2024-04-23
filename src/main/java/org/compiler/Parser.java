package org.compiler;

import org.compiler.errors.TokenError;
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

    public Parser(ArrayList<Token> tokens) throws TokenError {
        this.it = new PeekIteratorToken(tokens);
        parseProgram();
    }

    private void parseProgram() throws TokenError {
        while (it.hasNext()) {
            stmts.add(parseStmt());
        }
        tree = new NodeProgram(stmts);
    }

    private NodeStatement parseStmt() throws TokenError {
        if(!it.hasNext()){
            throw new RuntimeException("Invalid structure, expected token");
        }
        if (it.peek().getType() == TokenType._exit) {
            it.next();
            return parseExit();
        } else if (it.peek().getType() == TokenType.let) {
            it.next();
            return parseLet();
        } else if (it.peek().getType() == TokenType.ident && it.peek(1) != null
                && it.peek(1).getType() == TokenType.eq) {
            return parseAssign();
        } else if (it.peek().getType() == TokenType.open_curly) {
            it.next();
            return parseScope();
        } else if (it.peek().getType() == TokenType._if) {
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
                if(!it.hasNext()){
                    throw new RuntimeException("Invalid structure, expected open curly brace of else");
                }
                if (it.peek().getType() != TokenType.open_curly) {
                    throw new TokenError("Invalid token in statement of type " + it.peek().getType() + ", curly braces required", it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
                }
                it.next(); // consume curly braces
                nodeIf.setScopeElse(parseScope());
            }
            return nodeIf;
        } else if (it.peek().getType() == TokenType._while) {
            it.next();
            Conditional conditional = parseCondition();
            return new NodeWhile(conditional.getStmt(), conditional.getScope());
        } else {
            throw new TokenError("Invalid token in statement of type " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
    }

    private NodeExpression parseExpr() throws TokenError {
        return parseExpr(0);
    }

    private NodeExpression parseExpr(int minPrec) throws TokenError {
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
            if(!it.hasNext()){
                throw new RuntimeException("Invalid structure, expected operation token");
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
            default -> throw new TokenError("Invalid token in expression of type " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
            }
        }
        return left;
    }

    private NodeAssign parseAssign() throws TokenError {
        TokenIdent ident = (TokenIdent) it.next();
        it.next();
        NodeExpression expr = parseExpr();
        if (it.next().getType() != TokenType.semi) {
            throw new TokenError("Invalid token in statement of type " + it.peek().getType() + ", semicolon required", it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        return new NodeAssign(expr, ident);
    }

    private NodeScope parseScope() throws TokenError {
        ArrayList<NodeStatement> statements = new ArrayList<>();
        while (it.hasNext() && it.peek().getType() != TokenType.close_curly) {
            if (!it.hasNext()) {
                throw new TokenError("Curly braces not closed", it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
            }
            statements.add(parseStmt());
        }
        if(!it.hasNext()) {
            throw new RuntimeException("Invalid structure, expected close curly brace");
        }
        if (it.peek().getType() != TokenType.close_curly) {
            throw new TokenError("Invalid token in statement of type " + it.peek().getType() + ", curly braces required", it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next();
        return new NodeScope(null, statements);
    }

    private Conditional parseCondition() throws TokenError {
        if (it.peek().getType() != TokenType.open_paren) {
            throw new TokenError("Invalid token after if statement, expected parenthesis, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next();
        NodeExpression expr = parseExpr();
        if (it.peek().getType() != TokenType.close_paren) {
            throw new TokenError("Invalid token, expected close parenthesis, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next(); // consume close_paren
        it.next(); // consume open_curly brace
        NodeScope scope = parseScope();

        return new Conditional(expr, scope);
    }

    private NodeExit parseExit() throws TokenError {
        NodeExpression expr;
        if(!it.hasNext()){
            throw new RuntimeException("Invalid structure, expected open parenthesis");
        }
        if (it.next().getType() != TokenType.open_paren) {
            throw new TokenError("Invalid token after exit, expected open parenthesis, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        expr = parseExpr();
        NodeExit exit = new NodeExit(expr);
        if(!it.hasNext()){
            throw new RuntimeException("Invalid structure, expected close parenthesis and semicolon");
        }
        if (it.peek().getType() != TokenType.close_paren) {
            throw new TokenError("Invalid token after exit, expected close parenthesis, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next();
        if(!it.hasNext()){
            throw new RuntimeException("Invalid structure, expected semicolon");
        }
        if (it.peek().getType() != TokenType.semi) {
            throw new TokenError("Invalid token, expected semicolon, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next();
        return exit;
    }

    private NodeLet parseLet() throws TokenError {

        NodeIdent ident;
        if (!it.hasNext()) {
            throw new RuntimeException("Invalid token, expected identity token(reserved keyword?)");
        }
        if(it.peek().getType() != TokenType.ident){
            throw new TokenError("Invalid token, expected identity token(reserved keyword?), provided " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        ident = new NodeIdent((TokenIdent) it.next());
        if (!it.hasNext()) {
            throw new RuntimeException("Invalid token, expected equal sign");
        }
        if(it.peek().getType() != TokenType.eq){
            throw new TokenError("Invalid token, expected equal sign, provided " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next();
        NodeExpression expr = parseExpr();
        if (!it.hasNext()) {
            throw new RuntimeException("Invalid token, expected semicolon");
        }
        if(it.peek().getType() != TokenType.semi){
            throw new TokenError("Invalid token, expected semicolon, provided " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
        it.next();
        return new NodeLet(expr, ident);
    }

    // controlla se c'è un termine e se è un int_lit o un ident
    private NodeTerm parseTerm() throws TokenError {
        if(!it.hasNext()){
            throw new RuntimeException("Invalid structure, expected open parenthesis or number or variable");
        }
        if (it.peek().getType() == TokenType.int_lit) {
            return new NodeIntLit((TokenIntLit) it.next());
        }
        else if (it.peek().getType() == TokenType.ident) {
            TokenIdent ident = (TokenIdent) it.next();
            return new NodeIdent(ident);
        }
        else if (it.peek().getType() == TokenType.open_paren) {
            it.next();
            NodeExpression expr = parseExpr();
            if (expr == null) {
                throw new TokenError("Expected expression, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
            }
            if(!it.hasNext()){
                throw new RuntimeException("Invalid structure, expected close parenthesis");
            }
            if (it.peek().getType() != TokenType.close_paren) {
                throw new TokenError("Invalid token, expected close parenthesis, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
            }
            return new NodeTermParen(it.next(), expr);
        } else {
            throw new TokenError("Invalid token, expected number, variable or expression, found " + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
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
