package org.compiler.nodes.expressions.terms;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeTerm extends NodeExpression {
    public NodeTerm(Token expr) {
        super(expr);
    }

    @Override
    public String toString() {
        return "NodeTerm{" + "expr=" + getExpr() + '}';
    }
}
