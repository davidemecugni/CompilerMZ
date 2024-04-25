package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicAnd extends NodeBin {
    public NodeBinLogicAnd(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicAnd{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
