package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * The `NodeBinMulti` class is a subclass of `NodeBin`, representing a multiplication operation in the AST. It inherits
 * `left` and `right` operands and overrides the `toString` method for debugging.
 */

public class NodeBinMulti extends NodeBin {
    public NodeBinMulti(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinMulti{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
