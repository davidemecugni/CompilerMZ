package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinMod extends NodeBin {
    public NodeBinMod(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinMod{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
