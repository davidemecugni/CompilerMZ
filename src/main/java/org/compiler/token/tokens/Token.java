package org.compiler.token.tokens;

import org.compiler.token.TokenType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a token in the source code A token is a pair consisting of a token name and an optional token value The
 * token name is an enumeration of the possible types of tokens The token value is the actual value of the token in the
 * source code(ex. int_lit -> 42)
 */
public class Token {
    private final TokenType type;

    // Map to convert alphabetic tokens to their corresponding TokenType
    private static final Map<Object, TokenType> wordToTokenMap;
    static {
        // Char only one quote, String double quotes
        wordToTokenMap = new HashMap<>();
        wordToTokenMap.put("exit", TokenType._exit);
        wordToTokenMap.put(';', TokenType.semi);
        wordToTokenMap.put('(', TokenType.open_paren);
        wordToTokenMap.put(')', TokenType.close_paren);
        wordToTokenMap.put('=', TokenType.eq);
        wordToTokenMap.put("let", TokenType.let);
        wordToTokenMap.put('+', TokenType.plus);
        wordToTokenMap.put('*', TokenType.star);
        wordToTokenMap.put('-', TokenType.minus);
        wordToTokenMap.put('/', TokenType.slash);
        // Add more entries as needed
    }

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    /**
     * Converts an alphabetic token to a TokenType
     *
     * @param word
     *       import org.compiler.nodes.expressions.binary_expressions.NodeBin;     used to identify the TokenType
     *
     * @return the corresponding TokenType
     */
    public static Token of(Object word) {
        if (wordToTokenMap.containsKey(word)) {
            return new Token(wordToTokenMap.get(word));
        } else {
            return new TokenIdent(word.toString());
        }
    }

    public int BinaryPrecedence(TokenType type) {
        switch (type) {
            case TokenType.plus, TokenType.minus -> {
                return 0;
            }
            case TokenType.star, TokenType.slash -> {
                return 1;
            }
        }

        return -1;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Token token = (Token) obj;
        return type == token.type;
    }
}
