package org.compiler.nodes;

import java.util.List;

public class Program {
    private final List<Stmt> stmts;

    public Program(List<Stmt> stmts) {
        this.stmts = stmts;
    }

    public List<Stmt> getStmts() {
        return stmts;
    }
}
