package org.compiler.nodes.statements.conditionals;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.statements.NodeScope;

/**
 * Represents a conditional statement in the AST. It has a condition and a scope. Examples are: if, else, while, etc.
 */

public class Conditional extends NodeStatement {
    final private NodeScope scope;

    public Conditional(NodeExpression stmt, NodeScope node) {
        super(stmt);
        this.scope = node;
    }

    public NodeScope getScope() {
        return scope;
    }
}
