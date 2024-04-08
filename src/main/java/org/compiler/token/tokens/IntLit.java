package org.compiler.token.tokens;

import org.compiler.token.Token;
import org.compiler.token.TokenType;

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
        return "IntLit{" +
                "value=" + value +
                '}';
    }
}
