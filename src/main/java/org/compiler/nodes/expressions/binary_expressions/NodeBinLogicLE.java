package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicLE extends NodeBin {
    public NodeBinLogicLE(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicLE{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}

