package org.compiler.token.tokens;

import org.compiler.token.Token;
import org.compiler.token.TokenType;

import java.util.Objects;

public class IntLit extends Token {
    private final int value;

    public IntLit(int value) {
        super(TokenType.int_lit);
        this.value = value;
    }

    public IntLit(String value){
        super(TokenType.int_lit);
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NodeIntLit{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntLit intLit = (IntLit) o;
        return value == intLit.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
