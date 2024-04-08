package org.compiler;


import org.compiler.nodes.*;
import org.compiler.peekers.PeekIteratorToken;
import org.compiler.token.Token;
import org.compiler.token.TokenType;
import org.compiler.token.tokens.IntLit;

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
    public Exit parse(){
        Exit exit = null;
        while(it.hasNext()){
            if(it.next().getType() == TokenType._exit){
                Expression expr = parseExpression();
                if(expr != null){
                    exit = new Exit(expr);
                }
                else{
                    throw new IllegalArgumentException("Invalid expression");
                }
                if( ! it.hasNext() || it.next().getType() != TokenType.semi){
                    throw new IllegalArgumentException("Invalid token after expression");
                }
            }
        }
        return exit;
    }
    private Expression parseExpression(){
        if(it.hasNext() && it.peek().getType() == TokenType.int_lit){
            return new Expression((IntLit) it.next());
        }
        else{
            return null;
        }
    }

    @Override
    public String toString() {
        return "Parser{" +
                "it=" + it +
                '}';
    }
}
