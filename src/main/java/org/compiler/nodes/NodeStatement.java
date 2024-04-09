package org.compiler.nodes;

public class NodeStatement {
    NodeExpression stmt;

    public NodeStatement(NodeExpression stmt) {
        this.stmt = stmt;
    }

    public NodeExpression getStmt() {
        return stmt;
    }
}
