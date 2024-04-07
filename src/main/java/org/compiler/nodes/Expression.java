package org.compiler.nodes;

import org.compiler.Token;

public class Expression {
    private final Token int_literal;

    public Expression(Token int_literal) {
        this.int_literal = int_literal;
    }

    public Token getInt_literal() {
        return int_literal;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "int_literal=" + int_literal +
                '}';
    }
}
