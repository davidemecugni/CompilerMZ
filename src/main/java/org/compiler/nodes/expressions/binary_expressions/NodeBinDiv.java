package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * The `NodeBinDiv` class is a subclass of `NodeBin`, representing a division operation in the AST. It inherits `left`
 * and `right` operands and overrides the `toString` method for debugging.
 */

public class NodeBinDiv extends NodeBin {
    public NodeBinDiv(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinDiv{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
