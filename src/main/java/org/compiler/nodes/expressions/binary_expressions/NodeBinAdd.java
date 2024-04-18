package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinAdd extends NodeBin {
    public NodeBinAdd(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinAdd{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
