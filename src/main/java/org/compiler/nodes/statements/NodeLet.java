package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.terms.NodeIdent;

/**
 * NodeLet represents a let statement in the AST.
 * It contains an identifier and an expression.
 * Used for variable assignment.
 */

public class NodeLet extends NodeStatement {
    private final NodeIdent identifier;

    public NodeLet(NodeExpression stmt, NodeIdent identifier) {
        super(stmt);
        this.identifier = identifier;
    }

    public NodeIdent getIdentifier() {
        return identifier;
    }
}
