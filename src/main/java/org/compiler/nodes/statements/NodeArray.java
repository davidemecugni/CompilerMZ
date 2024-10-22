package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;

public class NodeArray extends NodeStatement {
    private final NodeIdent identifier;

    public NodeArray(NodeExpression expr, NodeIdent identifier) {
        super(expr);
        this.identifier = identifier;
    }

    public NodeIdent getIdentifier() {
        return identifier;
    }

    public StringBuilder generateDataSection(){
        StringBuilder dataSection = new StringBuilder();
        NodeIntLit expr = (NodeIntLit) getStmt();
        dataSection.append("     ").append(identifier.getIdent().getName())
                .append(" times ")
                .append(expr.getIntLit().getValue())
                .append(" db 0\n");
        return dataSection;
    }
    @Override
    public String toString() {
        return "NodeArray{" +
                "identifier=" + identifier +
                '}';
    }
}
