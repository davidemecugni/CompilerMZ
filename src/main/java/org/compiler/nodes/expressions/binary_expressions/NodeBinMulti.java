package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

public class NodeBinMulti extends NodeBin{
    public NodeBinMulti(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinMulti{" +
                "left=" + getLeft() +
                ", right=" + getRight() +
                '}';
    }
}
