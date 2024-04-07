package org.compiler;


import org.compiler.nodes.*;
import java.util.ArrayList;

public class Parser {
    private final PeekIteratorToken it;

    public Parser(ArrayList<Token> tokens) {
        this.it = new PeekIteratorToken(tokens.iterator());
    }
    private Exit parse(){
        Exit exit = null;
        while(it.hasNext()){
            if(it.next().getType() == TokenType._return){
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
            return new Expression(it.next());
        }
        else{
            return null;
        }
    }
}
