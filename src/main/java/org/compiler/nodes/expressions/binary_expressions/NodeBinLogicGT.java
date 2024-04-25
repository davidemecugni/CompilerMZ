package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicGT extends NodeBin {
    public NodeBinLogicGT(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicGT{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
