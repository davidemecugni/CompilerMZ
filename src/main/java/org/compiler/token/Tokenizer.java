package org.compiler.token;

import org.compiler.peekers.PeekIteratorChar;
import org.compiler.token.tokens.TokenIntLit;

import java.util.ArrayList;

/**
 * Generates a list of tokens from a string input
 *
 */
public class Tokenizer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private final PeekIteratorChar it;
    public Tokenizer(String input) {
        this.it =  new PeekIteratorChar(input);
        tokenize();
    }

    public void tokenize() {
        StringBuilder buffer = new StringBuilder();
        while(it.hasNext()){
             char c = it.next();
             // Integer token
             if (Character.isDigit(c)) {
                 buffer.append(c);
                 while(it.hasNext() && Character.isDigit(it.peek())){
                     buffer.append(it.next());
                 }
                 AddToken(new TokenIntLit(Integer.parseInt(buffer.toString())));
                 buffer.setLength(0);
             }
             //Alphabetic token
             else if (Character.isAlphabetic(c)){
                 buffer.append(c);
                 while(it.hasNext() && Character.isAlphabetic(it.peek())){
                     buffer.append(it.next());
                 }
                 Token alphaToken = Token.of(buffer.toString());
                 AddToken(alphaToken);
                 buffer.setLength(0);
             }
             else if(c == '@'){
                 // Single character token
                 it.IgnoreComment();
             }
             else{
                 AddToken(Token.of(c));
            }

        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private void AddToken(Token token) {
        tokens.add(token);
    }

    @Override
    public String toString() {
        return "Tokenizer{" +
                "tokens=" + tokens +
                '}';
    }
}
