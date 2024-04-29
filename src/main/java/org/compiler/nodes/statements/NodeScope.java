package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;

import java.util.ArrayList;

public class NodeScope extends NodeStatement {
    final ArrayList<NodeStatement> stmts;

    public NodeScope(NodeExpression stmt, ArrayList<NodeStatement> stmts) {
        super(stmt);
        this.stmts = stmts;
    }

    public ArrayList<NodeStatement> getStmts() {
        return stmts;
    }

    @Override
    public String toString() {
        return "NodeScope{" + "stmts=" + stmts + '}';
    }
}
