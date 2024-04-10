package org.compiler.nodes;

import java.util.List;

public class NodeProgram {
    private final List<NodeStatement> stmts;

    public NodeProgram(List<NodeStatement> stmts) {
        this.stmts = stmts;
    }

    public List<NodeStatement> getStmts() {
        return stmts;
    }
}
