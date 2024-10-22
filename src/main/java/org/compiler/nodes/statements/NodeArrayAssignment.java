package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.expressions.terms.NodeIdent;

public class NodeArrayAssignment extends NodeArray {
    private final NodeExpression expr2;

    public NodeArrayAssignment(NodeExpression expr1, NodeExpression expr2, NodeIdent identifier) {
        super(expr1, identifier);
        this.expr2 = expr2;
    }

    public NodeExpression getExpr1() {
        return getStmt();
    }

    public NodeExpression getExpr2() {
        return expr2;
    }

    @Override
    public String toString() {
        return "NodeArrayAssignment{" +
                "expr1=" + getExpr1() +
                ", expr2=" + getExpr2() +
                ", identifier=" + getIdentifier() +
                '}';
    }
}
