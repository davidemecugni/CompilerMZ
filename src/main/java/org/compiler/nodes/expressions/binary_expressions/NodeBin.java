package org.compiler.nodes.expressions.binary_expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * The `NodeBin` class in your compiler project represents a binary expression node in the Abstract Syntax Tree (AST).
 * It extends `NodeExpression` and has two properties: `left` and `right`, which are the operands of the binary
 * expression. The class also provides getter methods for these properties and overrides the `toString` method for
 * debugging purposes.
 */
public class NodeBin extends NodeExpression {
    private final NodeExpression left;
    private final NodeExpression right;

    public NodeBin(Token expr, NodeExpression left, NodeExpression right) {
        super(expr);
        this.left = left;
        this.right = right;
    }

    public NodeExpression getLeft() {
        return left;
    }

    public NodeExpression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "NodeBinary{" + "left=" + left + ", right=" + right + '}';
    }
}
