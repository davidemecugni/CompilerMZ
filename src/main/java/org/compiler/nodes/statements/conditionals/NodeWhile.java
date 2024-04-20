package org.compiler.nodes.statements.conditionals;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.statements.NodeScope;

public class NodeWhile extends Conditional{
    public NodeWhile(NodeExpression stmt, NodeScope node) {
        super(stmt, node);
    }

    @Override
    public String toString() {
        return "NodeIfWhile{" + "nodeScopeWhile=" + getScope() + '}';
    }
}
