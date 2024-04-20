package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;

public class NodeIf extends NodeStatement {
    NodeScope nodeScope;

    public NodeIf(NodeExpression stmt, NodeScope nodeScope) {
        super(stmt);
        this.nodeScope = nodeScope;
    }

    public NodeScope getNodeScope() {
        return nodeScope;
    }

    @Override
    public String toString() {
        return "NodeIf{" + "nodeScope=" + nodeScope + '}';
    }
}
