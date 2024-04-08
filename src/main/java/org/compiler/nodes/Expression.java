package org.compiler.nodes;

import org.compiler.token.tokens.IntLit;

/**
 * Represents an expression TBC
 */
public record Expression(IntLit int_literal) {

    @Override
    public String toString() {
        return "Expression{" +
                "int_literal=" + int_literal +
                '}';
    }
}
