package org.compiler.nodes;

import org.compiler.token.tokens.Token;

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
        return "NodeExpression{" +
                "expr=" + expr +
                '}';
    }
}
