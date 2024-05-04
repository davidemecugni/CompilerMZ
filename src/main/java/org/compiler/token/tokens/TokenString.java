package org.compiler.token.tokens;

import org.compiler.token.TokenType;

public class TokenString extends Token {

    private final String content;

    public TokenString(String content) {
        super(TokenType.quotes);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "TokenString{" + "content='" + content + '\'' + '}';
    }
}
