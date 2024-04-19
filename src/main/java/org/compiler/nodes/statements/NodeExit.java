package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;

/**
 * NodeExit is a NodeStatement subclass representing an exit statement in the AST. It contains a single NodeExpression
 */

public class NodeExit extends NodeStatement {
    public NodeExit(NodeExpression expr) {
        super(expr);
    }
}
