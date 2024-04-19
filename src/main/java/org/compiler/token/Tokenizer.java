package org.compiler.token;

import org.compiler.peekers.PeekIteratorChar;
import org.compiler.token.tokens.Token;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a list of tokens from a string input
 *
 */
public class Tokenizer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private final PeekIteratorChar it;
    private Map<String, TokenType> wordToTokenMap;

    public Tokenizer(String input) {
        this.it = new PeekIteratorChar(input);
        retrieveDialect();
        tokenize();
    }

    private void tokenize() {
        StringBuilder buffer = new StringBuilder();
        while (it.hasNext()) {
            char c = it.next();
            // Integer token
            if (Character.isDigit(c)) {
                buffer.append(c);
                while (it.hasNext() && Character.isDigit(it.peek())) {
                    buffer.append(it.next());
                }
                AddToken(new TokenIntLit(Integer.parseInt(buffer.toString())));
                buffer.setLength(0);
            }
            // Alphabetic token
            else {
                buffer.append(c);
                String word = buffer.toString();
                if (wordToTokenMap.containsKey(buffer.toString())) {
                    if (wordToTokenMap.get(word) == TokenType.comment) {
                        it.ignoreComment(word.charAt(0));
                    } else {
                        AddToken(of(buffer.toString()));
                    }
                    buffer.setLength(0);
                    continue;
                }
                while (it.hasNext() && !Character.isSpaceChar(it.peek())
                        && !wordToTokenMap.containsKey(it.peek().toString())) {
                    buffer.append(it.next());
                }
                word = buffer.toString();
                if (wordToTokenMap.get(word) == TokenType.comment) {
                    it.ignoreComment(word.charAt(0));
                    buffer.setLength(0);
                    continue;
                }
                AddToken(of(word));
                buffer.setLength(0);
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
        return "Tokenizer{" + "tokens=" + tokens + '}';
    }

    private void retrieveDialect() {
        wordToTokenMap = new HashMap<>();
        wordToTokenMap.put("exit", TokenType._exit);
        wordToTokenMap.put(";", TokenType.semi);
        wordToTokenMap.put("(", TokenType.open_paren);
        wordToTokenMap.put(")", TokenType.close_paren);
        wordToTokenMap.put("=", TokenType.eq);
        wordToTokenMap.put("let", TokenType.let);
        wordToTokenMap.put("+", TokenType.plus);
        wordToTokenMap.put("*", TokenType.star);
        wordToTokenMap.put("-", TokenType.minus);
        wordToTokenMap.put("/", TokenType.slash);
        wordToTokenMap.put("@", TokenType.comment);
    }

    Token of(String word) {
        if (wordToTokenMap.containsKey(word)) {
            return new Token(wordToTokenMap.get(word));
        } else {
            return new TokenIdent(word);
        }
    }
}
