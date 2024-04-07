package org.compiler;


import org.compiler.nodes.*;
import org.compiler.peekers.PeekIteratorToken;

import java.util.ArrayList;

public class Parser {
    private final PeekIteratorToken it;

    public Parser(ArrayList<Token> tokens) {
        this.it = new PeekIteratorToken(tokens.iterator());
    }
    public Exit parse(){
        Exit exit = null;
        while(it.hasNext()){
            if(it.next().getType() == TokenType._exit){
                System.out.println("Found exit");
                Expression expr = parseExpression();
                System.out.println("After parsing");
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
        System.out.println(it.toString());
        if(it.hasNext() && it.peek().getType() == TokenType.int_lit){
            return new Expression(it.next());
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
