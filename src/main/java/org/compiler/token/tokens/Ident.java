package org.compiler.token.tokens;

import org.compiler.token.Token;
import org.compiler.token.TokenType;

public class Ident extends Token {
    private final String name;

    public Ident(String name) {
        super(TokenType.ident);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NodeIdent{" +
                "name='" + name + '\'' +
                '}';
    }
}
