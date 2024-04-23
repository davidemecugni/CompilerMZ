package org.compiler.token;

import org.compiler.errors.TokenError;
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
    private ArrayList<Token> tokens = new ArrayList<>();
    private final PeekIteratorChar it;
    private final Map<String, TokenType> wordToTokenMap;

    public Tokenizer(String input) throws TokenError {
        this.it = new PeekIteratorChar(input);
        Dialect defaultDialect = new Dialect("default_dialect");
        wordToTokenMap = defaultDialect.getWordToTokenMap();
        tokenize();
        substituteMultiTokenTokens();
    }

    public Tokenizer(String input, String dialectName) throws TokenError {
        this.it = new PeekIteratorChar(input);
        Dialect dialect = new Dialect(dialectName);
        wordToTokenMap = dialect.getWordToTokenMap();
        tokenize();
        substituteMultiTokenTokens();
    }

    private void tokenize() throws TokenError {
        StringBuilder buffer = new StringBuilder();
        while (it.hasNext()) {
            CharLineColumn clc = it.next();
            char c = clc.getChar();
            buffer.append(c);
            String word = buffer.toString();

            int line = clc.getLine();
            int column_start = clc.getColumn();
            // If mono char literal
            if (wordToTokenMap.containsKey(buffer.toString())) {
                if (wordToTokenMap.get(word) == TokenType.comment) {
                    it.ignoreComment(word.charAt(0));
                } else {
                    AddToken(of(buffer.toString(), line, column_start, column_start));
                }
                buffer.setLength(0);
                continue;
            }
            int column_end = 0;
            while (it.hasNext() && !Character.isWhitespace(it.peek().getChar())
                    && !wordToTokenMap.containsKey(String.valueOf(it.peek().getChar()))
                    && !(wordToTokenMap.containsKey(buffer.toString())
                            && wordToTokenMap.get(buffer.toString()) == TokenType.comment)) {
                column_end = it.peek().getColumn();
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
            if (column_end == 0) {
                column_end = column_start;
            }
            AddToken(of(word, line, column_start, column_end));
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

    private Token of(String word, int line, int column_start, int column_end) throws TokenError {
        if (wordToTokenMap.containsKey(word)) {
            return new Token(wordToTokenMap.get(word), line, column_start, column_end);
        } else if (word.matches("[0-9]+")) { // check if the word is a number
            return new TokenIntLit(word, line, column_start, column_end);
        } else if (word.matches("^[^\\d].*")) {
            return new TokenIdent(word, line, column_start, column_end);
        } else {
            throw new TokenError("Unrecognised token type",line,column_start,column_end);
        }
    }

    private void substituteMultiTokenTokens(){
        ArrayList<Token> tokenCopy = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if(token.getType() == TokenType.minus && i+1 < tokens.size() && i -1 > 0) {
                if (tokens.get(i + 1).getType() == TokenType.int_lit) {
                    TokenType prec = tokens.get(i - 1).getType();
                    if (prec == TokenType.open_paren || prec == TokenType.eq || prec == TokenType.plus || prec == TokenType.minus || prec == TokenType.star || prec == TokenType.slash) {
                        TokenIntLit number = (TokenIntLit) tokens.get(i + 1);
                        tokenCopy.add(new TokenIntLit(Integer.toString(-number.getValue()), token.getLine(), token.getColumnStart(), token.getColumnEnd()));
                        ++i;
                        continue;
                    }
                }
            }
            tokenCopy.add(token);
        }
        tokens = tokenCopy;
    }
}