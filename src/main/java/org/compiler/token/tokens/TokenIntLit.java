package org.compiler.token.tokens;

import org.compiler.token.Token;
import org.compiler.token.TokenType;

import java.util.Objects;

public class TokenIntLit extends Token {
    private final int value;

    public TokenIntLit(int value) {
        super(TokenType.int_lit);
        this.value = value;
    }

    public TokenIntLit(String value){
        super(TokenType.int_lit);
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TokenIntLit{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TokenIntLit intLit = (TokenIntLit) o;
        return value == intLit.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}