package org.compiler.nodes;

import org.compiler.token.tokens.Token;

/**
 * NodeExpression is a class in your compiler project that represents an expression in the AST. It serves as a base
 * class for more specific types of expressions.
 */

public class NodeExpression {
    private final Token expr;

    public NodeExpression(Token expr) {
        this.expr = expr;
    }

    public Token getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "NodeExpression{" + "expr=" + expr + '}';
    }
}
