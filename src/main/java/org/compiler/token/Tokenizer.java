package org.compiler.token;

import org.compiler.peekers.PeekIteratorChar;

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
                 AddToken(TokenType.int_lit, buffer.toString());
                 buffer.setLength(0);
             }
             //Alphabetic token
             else if (Character.isAlphabetic(c)){
                 buffer.append(c);
                 while(it.hasNext() && Character.isAlphabetic(it.peek())){
                     buffer.append(it.next());
                 }
                 AddToken(Token.of(buffer.toString()));
                 buffer.setLength(0);
             }
             else{
                 // Single character token
                 AddToken(Token.of(c));
             }

        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private void AddToken(TokenType type) {
        tokens.add(new Token(type));
    }
    private void AddToken(TokenType type, String value) {
        tokens.add(new Token(type, value));
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
