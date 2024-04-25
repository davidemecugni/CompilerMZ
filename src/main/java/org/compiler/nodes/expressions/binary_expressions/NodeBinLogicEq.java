package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicEq extends NodeBin {
    public NodeBinLogicEq(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicEq{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}