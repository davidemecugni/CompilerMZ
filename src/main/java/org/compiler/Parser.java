package org.compiler;

import org.compiler.errors.TokenError;
import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.BinType;
import org.compiler.nodes.expressions.binary_expressions.NodeBin;
import org.compiler.nodes.expressions.terms.*;
import org.compiler.nodes.statements.NodeAssign;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.nodes.statements.NodeScope;
import org.compiler.nodes.statements.conditionals.Conditional;
import org.compiler.nodes.statements.conditionals.NodeElif;
import org.compiler.nodes.statements.conditionals.NodeIf;
import org.compiler.nodes.statements.conditionals.NodeWhile;
import org.compiler.nodes.statements.functions.BuiltInFunc;
import org.compiler.nodes.statements.functions.NodeBuiltInFunc;
import org.compiler.peekers.PeekIteratorToken;
import org.compiler.token.TokenType;
import org.compiler.token.tokens.Token;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;

import java.util.ArrayList;

/**
 * Represents a parser used to parse a list of tokens into a list of statements(nodes)
 * It checks for the correctness of the structure of the program
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
        } else if (it.peek().getType() == TokenType.print) {
            it.next();
            return parseBuiltInFunc(BuiltInFunc.print);
        } else if (it.peek().getType() == TokenType.read) {
            it.next();
            return parseBuiltInFunc(BuiltInFunc.read);
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
     * This is to ensure the correct order of operations
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
            case TokenType.plus -> left = new NodeBin(curr_token, left, right, BinType.Add);
            case TokenType.minus -> left = new NodeBin(curr_token, left, right, BinType.Sub);
            case TokenType.star -> left = new NodeBin(curr_token, left, right, BinType.Multi);
            case TokenType.slash -> left = new NodeBin(curr_token, left, right, BinType.Div);
            case TokenType.percent -> left = new NodeBin(curr_token, left, right, BinType.Mod);
            case TokenType.logic_eq -> left = new NodeBin(curr_token, left, right, BinType.Eq);
            case TokenType.logic_not_eq -> left = new NodeBin(curr_token, left, right, BinType.NotEq);
            case TokenType.logic_gt -> left = new NodeBin(curr_token, left, right, BinType.GT);
            case TokenType.logic_lt -> left = new NodeBin(curr_token, left, right, BinType.LT);
            case TokenType.logic_ge -> left = new NodeBin(curr_token, left, right, BinType.GE);
            case TokenType.logic_le -> left = new NodeBin(curr_token, left, right, BinType.LE);
            case TokenType.logic_and -> left = new NodeBin(curr_token, left, right, BinType.And);
            case TokenType.logic_or -> left = new NodeBin(curr_token, left, right, BinType.Or);
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
     * Parses a conditional statement(ex. if, elif, while)
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
     * Parses a built-in function (ex. print or read)
     *
     * @param func
     *            the built-in function to parse
     *
     * @return the built-in function parsed
     *
     * @throws TokenError
     *             if the built-in function is invalid
     */
    private NodeBuiltInFunc parseBuiltInFunc(BuiltInFunc func) throws TokenError {
        CheckForType(TokenType.open_paren);
        it.next();

        switch (func) {
        case BuiltInFunc.print -> {
            if (it.peek().getType() == TokenType.quotes) {
                it.next();
                NodeString string = new NodeString(it.next());
                NodeBuiltInFunc nodeBuiltInFunc = new NodeBuiltInFunc(string, BuiltInFunc.print);
                CheckForType(TokenType.quotes);
                it.next();
                CheckForType(TokenType.close_paren);
                it.next();
                CheckForType(TokenType.semi);
                it.next();
                return nodeBuiltInFunc;
            } else if (it.peek().getType() == TokenType.int_lit || it.peek().getType() == TokenType.ident) {
                NodeBuiltInFunc nodeBuiltInFunc = new NodeBuiltInFunc(parseTerm(), BuiltInFunc.print);
                CheckForType(TokenType.close_paren);
                it.next();
                CheckForType(TokenType.semi);
                it.next();
                return nodeBuiltInFunc;
            }
        }
        case BuiltInFunc.read -> {
            CheckForType(TokenType.ident);
            NodeIdent nodeIdent = new NodeIdent((TokenIdent) it.next());
            NodeBuiltInFunc nodeBuiltInFunc = new NodeBuiltInFunc(nodeIdent, BuiltInFunc.read);
            CheckForType(TokenType.close_paren);
            it.next();
            CheckForType(TokenType.semi);
            it.next();
            return nodeBuiltInFunc;
        }
        default -> GenerateErrorMessage("Invalid token, expected string, found ");
        }
        // unreachable
        return null;
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
     * Parses a term of an expression(ex. 5, x, (5+5))
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
