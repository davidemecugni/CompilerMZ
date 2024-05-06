package org.compiler.nodes.statements.conditionals;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.statements.NodeScope;

/**
 * NodeElif represents the elif statement in the AST. It contains a NodeExpression and a NodeScope.
 */

public class NodeElif extends Conditional {

    public NodeElif(NodeExpression stmt, NodeScope node) {
        super(stmt, node);
    }

    @Override
    public String toString() {
        return "NodeIfElif{" + "nodeScopeElif=" + getScope() + '}';
    }
}
