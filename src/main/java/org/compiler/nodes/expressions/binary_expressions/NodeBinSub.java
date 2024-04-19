package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * The `NodeBinSub` class is a subclass of `NodeBin`, representing a subtraction operation in the AST. It inherits
 * `left` and `right` operands and overrides the `toString` method for debugging.
 */

public class NodeBinSub extends NodeBin{
    public NodeBinSub(Token expr, NodeExpression left, NodeExpression right) {
        super(expr, left, right);
    }

    @Override
    public String toString() {
        return "NodeBinSub{" + "left=" + getLeft() + ", right=" + getRight() + '}';
    }
}
