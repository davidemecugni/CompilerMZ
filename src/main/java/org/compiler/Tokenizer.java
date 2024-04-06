package org.compiler;

import java.util.ArrayList;
import java.util.Iterator;

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
        while(it.peek()) {
             char c = it.next();
             if(c == '\0'){
                 System.out.println("Non funzia");
             }
             if(Character.isWhitespace(c)){
                 continue;
             }
             else if (Character.isDigit(c)) {
                 buffer.append(c);
                 while(Character.isDigit(it.peek())){
                     buffer.append(it.next());
                 }
                 AddToken(TokenType.int_lit, buffer.toString());
                 buffer.setLength(0);
             }
             else if (Character.isAlphabetic(c)){
                 buffer.append(c);
                 while(Character.isAlphabetic(it.peek())){
                     buffer.append(it.next());
                 }
                 System.out.println(buffer.toString());
                 switch (buffer.toString()){
                     case "return":
                         AddToken(TokenType._return);
                         break;
                     default:
                         System.out.printf(buffer.toString());
                         throw new IllegalArgumentException("Illegal alphabetic token");
                 }
                 buffer.setLength(0);
             }
             else if(c == ';'){
                 AddToken(TokenType.semi);
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
}
