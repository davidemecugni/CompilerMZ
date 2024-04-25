package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinLogicGE extends NodeBin {
    public NodeBinLogicGE(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinLogicGE{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}

