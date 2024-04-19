package org.compiler.token;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.compiler.peekers.PeekIteratorChar;
import org.compiler.token.dialects.Dialect;
import org.compiler.token.tokens.Token;
import org.compiler.token.tokens.TokenIdent;
import org.compiler.token.tokens.TokenIntLit;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            char c = it.next();
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
            AddToken(of(word));
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

    Token of(String word) {
        if (wordToTokenMap.containsKey(word)) {
            return new Token(wordToTokenMap.get(word));
        } else if (word.matches("[0-9]+")) { // check if the word is a number
            return new TokenIntLit(word);
        } else {
            return new TokenIdent(word);
        }
    }
}