package org.compiler.nodes;

import java.util.List;

/**
 * NodeProgram is a class that represents the root of the AST. It typically contains a list or sequence of statements
 * that make up the entire program.
 */

public class NodeProgram {
    private final List<NodeStatement> stmts;

    public NodeProgram(List<NodeStatement> stmts) {
        this.stmts = stmts;
    }

    public List<NodeStatement> getStmts() {
        return stmts;
    }

    @Override
    public String toString() {
        return "NodeProgram{" + "stmts=" + stmts + '}';
    }
}
