package org.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tokenizer {
    private final String input;
    private final ArrayList<Token> tokens = new ArrayList<>();

    public Tokenizer(String input) {
        this.input = input;
        tokenize();
    }

    public void tokenize() {
        StringBuilder buffer = new StringBuilder();
        PeekIterator<Character> it = new PeekIterator<>(input.chars().mapToObj(c -> (char) c).iterator());
        while(it.peek() != null) {
             char c = it.next();
             // Skip whitespaces
             if(Character.isWhitespace(c)){
                 continue;
             }
             // Integer token
             else if (Character.isDigit(c)) {
                 buffer.append(c);
                 while(it.peek() != null && Character.isDigit(it.peek())){
                     buffer.append(it.next());
                 }
                 AddToken(TokenType.int_lit, buffer.toString());
                 buffer.setLength(0);
             }
             //Alphabetic token
             else if (Character.isAlphabetic(c)){
                 buffer.append(c);
                 while(it.peek() != null && Character.isAlphabetic(it.peek())){
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
}
