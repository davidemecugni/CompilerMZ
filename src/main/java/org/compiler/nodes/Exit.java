package org.compiler.nodes;

/**
 * Represents an exit statement
 * @see Expression
 */
public class Exit {
    public Expression expr;

    public Exit(Expression expr) {
        this.expr = expr;
    }
}
