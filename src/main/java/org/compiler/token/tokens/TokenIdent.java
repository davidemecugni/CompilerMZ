package org.compiler.token.tokens;

import org.compiler.token.Token;
import org.compiler.token.TokenType;

public class TokenIdent extends Token {
    private final String name;

    public TokenIdent(String name) {
        super(TokenType.ident);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TokenIdent{" + "name='" + name + '\'' + '}';
    }
}
