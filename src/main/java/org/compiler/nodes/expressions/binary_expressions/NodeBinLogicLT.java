package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicLT extends NodeBin {
    public NodeBinLogicLT(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicLT{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
