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
import java.util.Set;

/**
 * Generates a list of tokens from a string input
 *
 */
public class Tokenizer {
    private ArrayList<Token> tokens = new ArrayList<>();
    private final PeekIteratorChar it;
    private final Map<String, TokenType> wordToTokenMap;
    private static final Set<TokenType> multiTokenTokens = Set.of(TokenType.not, TokenType.eq, TokenType.logic_gt,
            TokenType.logic_lt, TokenType.comment);

    public Tokenizer(String input) throws TokenError {
        this.it = new PeekIteratorChar(input);
        Dialect defaultDialect = new Dialect("default_dialect");
        wordToTokenMap = defaultDialect.getWordToTokenMap();
        tokenize(true);
        substituteMultiTokenTokens(true);
    }

    public Tokenizer(String input, String dialectName) throws TokenError {
        this.it = new PeekIteratorChar(input);
        Dialect dialect = new Dialect(dialectName);
        wordToTokenMap = dialect.getWordToTokenMap();
        tokenize(true);
        substituteMultiTokenTokens(true);
    }

    public Tokenizer(String input, String dialectName, boolean forParsing) throws TokenError {
        this.it = new PeekIteratorChar(input);
        Dialect dialect = new Dialect(dialectName);
        wordToTokenMap = dialect.getWordToTokenMap();
        tokenize(forParsing);
        substituteMultiTokenTokens(forParsing);
    }

    private void tokenize(boolean forParsing) throws TokenError {
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
                    if (forParsing) {
                        it.ignoreComment(word);
                    } else {
                        AddToken(it.ignoreComment(word));
                    }
                } else if (wordToTokenMap.get(word) == TokenType.quotes) {
                    AddToken(of(buffer.toString(), line, column_start, column_start));
                    AddToken(it.ignoreContent(word));
                    AddToken(of(buffer.toString(), line, it.peek().getColumn(), it.peek().getColumn()));
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
                            && (wordToTokenMap.get(buffer.toString()) == TokenType.comment
                                    || multiTokenTokens.contains(wordToTokenMap.get(buffer.toString()))))) {
                column_end = it.peek().getColumn();
                buffer.append(it.next().getChar());
            }
            word = buffer.toString();
            // Support for multi-char comment/string literals
            if (wordToTokenMap.containsKey(buffer.toString())) {
                if (wordToTokenMap.get(word) == TokenType.comment) {
                    if (forParsing) {
                        it.ignoreComment(word);
                    } else {
                        AddToken(it.ignoreComment(word));
                    }
                    buffer.setLength(0);
                    continue;
                }
                if (wordToTokenMap.get(word) == TokenType.quotes) {
                    AddToken(new Token(TokenType.quotes));
                    AddToken(it.ignoreContent(word));
                    AddToken(new Token(TokenType.quotes));
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
            throw new TokenError("Unrecognised token type: \"" + word + "\"", line, column_start, column_end);
        }
    }

    private void substituteMultiTokenTokens(boolean forParsing) {
        ArrayList<Token> tokenCopy = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            // Negative number
            if (token.getType() == TokenType.minus && i + 1 < tokens.size() && i - 1 > 0) {
                if (tokens.get(i + 1).getType() == TokenType.int_lit) {
                    TokenType prec = tokens.get(i - 1).getType();
                    if (prec == TokenType.open_paren || prec == TokenType.eq || prec == TokenType.plus
                            || prec == TokenType.minus || prec == TokenType.star || prec == TokenType.slash) {
                        TokenIntLit number = (TokenIntLit) tokens.get(i + 1);
                        tokenCopy.add(new TokenIntLit(Integer.toString(-number.getValue()), token.getLine(),
                                token.getColumnStart(), number.getColumnEnd()));
                        ++i;
                        continue;
                    }
                }
            }
            // != -> logic_not_eq
            if (token.getType() == TokenType.not && i + 1 < tokens.size()
                    && tokens.get(i + 1).getType() == TokenType.eq) {
                tokenCopy.add(new Token(TokenType.logic_not_eq, token.getLine(), token.getColumnStart(),
                        token.getColumnEnd() + 1));
                ++i;
                continue;
            }
            // == -> logic_eq
            if (token.getType() == TokenType.eq && i + 1 < tokens.size()
                    && tokens.get(i + 1).getType() == TokenType.eq) {
                tokenCopy.add(new Token(TokenType.logic_eq, token.getLine(), token.getColumnStart(),
                        token.getColumnEnd() + 1));
                ++i;
                continue;
            }
            // >= -> logic_ge
            if (token.getType() == TokenType.logic_gt && i + 1 < tokens.size()
                    && tokens.get(i + 1).getType() == TokenType.eq) {
                tokenCopy.add(new Token(TokenType.logic_ge, token.getLine(), token.getColumnStart(),
                        token.getColumnEnd() + 1));
                ++i;
                continue;
            }
            // <= -> logic_le
            if (token.getType() == TokenType.logic_lt && i + 1 < tokens.size()
                    && tokens.get(i + 1).getType() == TokenType.eq) {
                tokenCopy.add(new Token(TokenType.logic_le, token.getLine(), token.getColumnStart(),
                        token.getColumnEnd() + 1));
                ++i;
                continue;
            }
            // Convert true to 1 and false to 0
            if (forParsing) {
                if (token.getType() == TokenType._true) {
                    token = new TokenIntLit("1", token.getLine(), token.getColumnStart(), token.getColumnEnd());
                }
                if (token.getType() == TokenType._false) {
                    token = new TokenIntLit("0", token.getLine(), token.getColumnStart(), token.getColumnEnd());
                }
            }
            tokenCopy.add(token);
        }
        tokens = tokenCopy;
    }
}