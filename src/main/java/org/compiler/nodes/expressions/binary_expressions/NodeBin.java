package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBin extends NodeExpression{
    private final NodeExpression left;
    private final NodeExpression right;

    public NodeBin(Token expr, NodeExpression left, NodeExpression right) {
        super(expr);
        this.left = left;
        this.right = right;
    }

    public NodeExpression getLeft() {
        return left;
    }

    public NodeExpression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "NodeBinary{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}

