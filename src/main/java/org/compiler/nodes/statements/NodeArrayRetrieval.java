package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.expressions.terms.NodeIdent;

public class NodeArrayRetrieval extends NodeArray{

    public NodeArrayRetrieval(NodeExpression expr, NodeIdent identifier) {
        super(expr, identifier);
    }

    @Override
    public String toString() {
        return "NodeArrayRetrieval{" +
                "identifier=" + getIdentifier() +
                '}';
    }
}
