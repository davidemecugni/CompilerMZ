package org.compiler.nodes;

/**
 * NodeStatement is a class that represents a statement in the AST.
 * It contains a NodeExpression object, stmt, which represents the expression
 * associated with the statement.
 */

public class NodeStatement {
    NodeExpression stmt;

    public NodeStatement(NodeExpression stmt) {
        this.stmt = stmt;
    }

    public NodeExpression getStmt() {
        return stmt;
    }

    @Override
    public String toString() {
        return "NodeStatement{" + "stmt=" + stmt + '}';
    }
}
