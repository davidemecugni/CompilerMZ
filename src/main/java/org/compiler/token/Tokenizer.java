package org.compiler.token;

import org.compiler.peekers.PeekIteratorChar;
import org.compiler.token.dialects.Dialect;
import org.compiler.token.tokens.CharLineColumn;
import org.compiler.token.tokens.Token;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;

import java.util.ArrayList;
import java.util.Map;

/**
 * Generates a list of tokens from a string input
 *
 */
public class Tokenizer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private final PeekIteratorChar it;
    private final Map<String, TokenType> wordToTokenMap;

    public Tokenizer(String input) {
        this.it = new PeekIteratorChar(input);
        Dialect defaultDialect = new Dialect("default_dialect");
        wordToTokenMap = defaultDialect.getWordToTokenMap();
        tokenize();
    }

    public Tokenizer(String input, String dialectName) {
        this.it = new PeekIteratorChar(input);
        Dialect dialect = new Dialect(dialectName);
        wordToTokenMap = dialect.getWordToTokenMap();
        tokenize();
    }

    private void tokenize() {
        StringBuilder buffer = new StringBuilder();
        while (it.hasNext()) {
            CharLineColumn clc = it.next();
            char c = clc.getChar();
            buffer.append(c);
            String word = buffer.toString();

            int line = clc.getLine();
            int startingColumn = clc.getColumn();
            // If mono char literal
            if (wordToTokenMap.containsKey(buffer.toString())) {
                if (wordToTokenMap.get(word) == TokenType.comment) {
                    it.ignoreComment(word.charAt(0));
                } else {
                    AddToken(of(buffer.toString(), line, startingColumn));
                }
                buffer.setLength(0);
                continue;
            }
            while (it.hasNext() && !Character.isWhitespace(it.peek().getChar())
                    && !wordToTokenMap.containsKey(String.valueOf(it.peek().getChar()))
                    && !(wordToTokenMap.containsKey(buffer.toString())
                            && wordToTokenMap.get(buffer.toString()) == TokenType.comment)) {
                buffer.append(it.next().getChar());
            }
            word = buffer.toString();
            // Support for multi-char comment literals
            if (wordToTokenMap.containsKey(buffer.toString())) {
                if (wordToTokenMap.get(word) == TokenType.comment) {
                    it.ignoreComment(word.charAt(0));
                    buffer.setLength(0);
                    continue;
                }
            }
            AddToken(of(word, line, startingColumn));
            buffer.setLength(0);
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

    Token of(String word, int line, int column) {
        if (wordToTokenMap.containsKey(word)) {
            return new Token(wordToTokenMap.get(word), line, column);
        } else if (word.matches("[0-9]+")) { // check if the word is a number
            return new TokenIntLit(word, line, column);
        } else if (word.matches("^[^\\d].*")) {
            return new TokenIdent(word, line, column);
        } else {
            throw new IllegalArgumentException("Invalid variable name: " + word);
        }
    }
}