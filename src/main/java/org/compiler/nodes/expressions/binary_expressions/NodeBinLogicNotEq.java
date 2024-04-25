package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicNotEq extends NodeBin {
    public NodeBinLogicNotEq(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicNotEq{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
