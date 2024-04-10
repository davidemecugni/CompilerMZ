package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;

public class NodeExit extends NodeStatement {
    public NodeExit(NodeExpression expr) {
        super(expr);
    }
}
