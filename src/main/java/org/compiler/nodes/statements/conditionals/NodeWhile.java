package org.compiler.nodes.statements.conditionals;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.statements.NodeScope;

/**
 * NodeWhile represents a while loop in the AST. It contains a NodeExpression that represents the condition of the while
 * loop
 */

public class NodeWhile extends Conditional {
    public NodeWhile(NodeExpression stmt, NodeScope node) {
        super(stmt, node);
    }

    @Override
    public String toString() {
        return "NodeIfWhile{" + "nodeScopeWhile=" + getScope() + '}';
    }
}
