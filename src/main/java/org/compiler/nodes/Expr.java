package org.compiler.nodes;

import org.compiler.token.Token;

public class Expr {
    private final Token expr;
    public Expr(Token expr) {
        this.expr = expr;
    }

    public Token getExpr() {
        return expr;
    }
}
