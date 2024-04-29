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
 * Represents a parser used to parse a list of tokens into a list of statements(nodes)
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

    /**
     * Parses the program
     *
     * @throws TokenError
     *             if the program is invalid
     */
    private void parseProgram() throws TokenError {
        while (it.hasNext()) {
            stmts.add(parseStmt());
        }
        tree = new NodeProgram(stmts);
    }

    /**
     * Parses a statement of any type(eg. let x = 5; OR exit(x); OR { let x = 5; exit(x); })
     *
     * @return the statement parsed
     *
     * @throws TokenError
     *             if the statement is invalid
     */
    private NodeStatement parseStmt() throws TokenError {
        checkForNext();
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
                CheckForType(TokenType.open_curly);
                it.next(); // consume curly braces
                nodeIf.setScopeElse(parseScope());
            }
            return nodeIf;
        } else if (it.peek().getType() == TokenType._while) {
            it.next();
            Conditional conditional = parseCondition();
            return new NodeWhile(conditional.getStmt(), conditional.getScope());
        } else {
            GenerateErrorMessage("Invalid token in statement of type ");
            return null;
        }
    }

    /**
     * Parses an expression(the initial precedence is 0)
     *
     * @return the expression parsed
     *
     * @throws TokenError
     *             if the expression is invalid
     */
    private NodeExpression parseExpr() throws TokenError {
        return parseExpr(0);
    }

    /**
     * Parses an expression using the Precedence Climbing Algorithm
     *
     * @param minPrec
     *            the minimum precedence of the expression
     *
     * @return the expression parsed
     *
     * @throws TokenError
     *             if the expression is invalid
     */
    private NodeExpression parseExpr(int minPrec) throws TokenError {
        // Saves the left term
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
            checkForNext();
            Token op = it.next();
            int nextMinPrec = prec + 1;
            NodeExpression right = parseExpr(nextMinPrec);
            switch (op.getType()) {
            case TokenType.plus -> left = new NodeBinAdd(curr_token, left, right);
            case TokenType.minus -> left = new NodeBinSub(curr_token, left, right);
            case TokenType.star -> left = new NodeBinMulti(curr_token, left, right);
            case TokenType.slash -> left = new NodeBinDiv(curr_token, left, right);
            case TokenType.percent -> left = new NodeBinMod(curr_token, left, right);
            case TokenType.logic_eq -> left = new NodeBinLogicEq(curr_token, left, right);
            case TokenType.logic_not_eq -> left = new NodeBinLogicNotEq(curr_token, left, right);
            case TokenType.logic_gt -> left = new NodeBinLogicGT(curr_token, left, right);
            case TokenType.logic_lt -> left = new NodeBinLogicLT(curr_token, left, right);
            case TokenType.logic_ge -> left = new NodeBinLogicGE(curr_token, left, right);
            case TokenType.logic_le -> left = new NodeBinLogicLE(curr_token, left, right);
            case TokenType.logic_and -> left = new NodeBinLogicAnd(curr_token, left, right);
            case TokenType.logic_or -> left = new NodeBinLogicOr(curr_token, left, right);
            default -> GenerateErrorMessage("Invalid token in expression of type ");
            }
        }
        return left;
    }

    /**
     * Parses an assign statement(eg. x = 5;)
     *
     * @return the assign statement parsed
     *
     * @throws TokenError
     *             if the assign statement is invalid
     */
    private NodeAssign parseAssign() throws TokenError {
        TokenIdent ident = (TokenIdent) it.next();
        it.next();
        NodeExpression expr = parseExpr();
        CheckForType(TokenType.semi);
        it.next();
        return new NodeAssign(expr, ident);
    }

    /**
     * Parses a scope of statements(eg. { let x = 5; exit(x); })
     *
     * @return the scope parsed
     *
     * @throws TokenError
     *             if the scope is invalid
     */
    private NodeScope parseScope() throws TokenError {
        ArrayList<NodeStatement> statements = new ArrayList<>();
        while (it.hasNext() && it.peek().getType() != TokenType.close_curly) {
            checkForNext();
            statements.add(parseStmt());
        }
        CheckForType(TokenType.close_curly);
        it.next();
        return new NodeScope(null, statements);
    }

    /**
     * Parses a conditional statement(eg. if, elif, while)
     *
     * @return the conditional statement parsed
     *
     * @throws TokenError
     *             if the conditional statement is invalid
     */
    private Conditional parseCondition() throws TokenError {
        CheckForType(TokenType.open_paren);
        it.next();
        NodeExpression expr = parseExpr();
        CheckForType(TokenType.close_paren);
        it.next(); // consume close_paren
        CheckForType(TokenType.open_curly);
        it.next(); // consume open_curly brace
        NodeScope scope = parseScope();
        return new Conditional(expr, scope);
    }

    /**
     * Parses an exit statement
     *
     * @return the exit statement parsed
     *
     * @throws TokenError
     *             if the exit statement is invalid
     */
    private NodeExit parseExit() throws TokenError {
        NodeExpression expr;
        CheckForType(TokenType.open_paren);
        expr = parseExpr();
        NodeExit exit = new NodeExit(expr);
        CheckForType(TokenType.semi);
        it.next();
        return exit;
    }

    /**
     * Parses a let statement
     *
     * @return the let statement parsed
     *
     * @throws TokenError
     *             if the let statement is invalid
     */
    private NodeLet parseLet() throws TokenError {
        NodeIdent ident;
        CheckForType(TokenType.ident);
        ident = new NodeIdent((TokenIdent) it.next());
        CheckForType(TokenType.eq);
        it.next();
        NodeExpression expr = parseExpr();
        CheckForType(TokenType.semi);
        it.next();
        return new NodeLet(expr, ident);
    }

    /**
     * Parses a term of an expression(eg. 5, x, (5+5))
     *
     * @return the term parsed
     *
     * @throws TokenError
     *             if the term is invalid
     */
    private NodeTerm parseTerm() throws TokenError {
        checkForNext();
        if (it.peek().getType() == TokenType.int_lit) {
            return new NodeIntLit((TokenIntLit) it.next());
        } else if (it.peek().getType() == TokenType.ident) {
            TokenIdent ident = (TokenIdent) it.next();
            return new NodeIdent(ident);
        } else if (it.peek().getType() == TokenType.open_paren) {
            it.next();
            NodeExpression expr = parseExpr();
            if (expr == null) {
                GenerateErrorMessage("Invalid structure, expected expression, found ");
            }
            CheckForType(TokenType.close_paren);
            return new NodeTermParen(it.next(), expr);
        } else {
            GenerateErrorMessage("Invalid token, expected number, variable or expression, found ");
            return null;
        }
    }

    public NodeProgram getTree() {
        return tree;
    }

    /**
     * Checks if the next token is of the type specified, if not throws an error
     *
     * @param type
     *            the type of the token to check
     *
     * @throws TokenError
     *             if the next token is not of the type specified
     */
    private void CheckForType(TokenType type) throws TokenError {
        checkForNext();
        if (it.peek().getType() != type) {
            throw new TokenError("Invalid Token, expected " + type + ", found " + it.peek().getType(),
                    it.peek().getLine(), it.peek().getColumnStart(), it.peek().getColumnEnd());
        }
    }

    /**
     * Generates an error message with the message specified
     *
     * @param message
     *            the message to display
     *
     * @throws TokenError
     *             the error message generated
     */
    private void GenerateErrorMessage(String message) throws TokenError {
        throw new TokenError(message + it.peek().getType(), it.peek().getLine(), it.peek().getColumnStart(),
                it.peek().getColumnEnd());
    }

    /**
     * Checks if there is a next token, if not throws an error
     */
    private void checkForNext() {
        if (!it.hasNext()) {
            throw new RuntimeException("Invalid structure, expected token at line: " + it.peekPrevious().getLine()
                    + " and column: " + it.peekPrevious().getColumnEnd());
        }
    }

    @Override
    public String toString() {
        return "Parser{" + "it=" + it + ", tree=" + tree + '}';
    }
}
