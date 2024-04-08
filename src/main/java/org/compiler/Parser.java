package org.compiler;


import org.compiler.nodes.*;
import org.compiler.nodes.exprs.NodeIdent;
import org.compiler.nodes.exprs.NodeIntLit;
import org.compiler.nodes.stmts.NodeExit;
import org.compiler.peekers.PeekIteratorToken;
import org.compiler.token.Token;
import org.compiler.token.TokenType;
import org.compiler.token.tokens.Ident;
import org.compiler.token.tokens.IntLit;
import org.compiler.nodes.stmts.NodeLet;
import java.util.ArrayList;

/**
 * Represents a parser
 * @see PeekIteratorToken
 */
public class Parser {
    private final PeekIteratorToken it;

    public Parser(ArrayList<Token> tokens) {
        this.it = new PeekIteratorToken(tokens);
    }

    public Program parseProgram(){
        ArrayList<Stmt> stmts = new ArrayList<>();
        while(it.hasNext()){
            stmts.add(parseStmt());
        }
        return new Program(stmts);
    }

    private Stmt parseStmt(){
        if(it.hasNext() && it.next().getType() == TokenType._exit){
            return parseExit();
        }
        else if(it.hasNext() && it.next().getType() == TokenType.let){
            return parseLet();
        }
        else{
            throw new IllegalArgumentException("Invalid token in statement");
        }
    }
    private Expr parseExpr(){
        if(it.hasNext() && it.peek().getType() == TokenType.int_lit){
            return new NodeIntLit((IntLit) it.next());
        }
        else if(it.hasNext() && it.peek().getType() == TokenType.ident){
            return new NodeIdent((Ident) it.next());
        }
        else{
            throw new IllegalArgumentException("Invalid token in expression");
        }
    }
    private NodeExit parseExit(){
        Expr expr;
        if( ! it.hasNext() || it.next().getType() != TokenType.open_paren){
            throw new IllegalArgumentException("Invalid token after exit, expected open parenthesis");
        }
        expr = parseExpr();
        NodeExit exit = new NodeExit(expr);
        if( ! it.hasNext() || it.next().getType() != TokenType.close_paren){
            throw new IllegalArgumentException("Parenthesis not closed");
        }
        if( ! it.hasNext() || it.next().getType() != TokenType.semi){
            throw new IllegalArgumentException("Semicolon not present");
        }
        return exit;
    }

    private NodeLet parseLet(){
        NodeIdent ident;
        Expr expr;
        if( ! it.hasNext() || it.peek().getType() != TokenType.ident){
            throw new IllegalArgumentException("Invalid token after let, expected identifier");
        }
        ident = new NodeIdent((Ident)it.next());
        if( ! it.hasNext() || it.next().getType() != TokenType.eq){
            throw new IllegalArgumentException("Invalid token after ident, expected equal sign");
        }
        expr = parseExpr();
        if( ! it.hasNext() || it.next().getType() != TokenType.semi){
            throw new IllegalArgumentException("Semicolon not present");
        }
        return new NodeLet(expr, ident);
    }
    @Override
    public String toString() {
        return "Parser{" +
                "it=" + it +
                '}';
    }
}
