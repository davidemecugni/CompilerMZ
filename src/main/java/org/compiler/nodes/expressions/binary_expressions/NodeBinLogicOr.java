package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicOr extends NodeBin {
    public NodeBinLogicOr(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicOr{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
