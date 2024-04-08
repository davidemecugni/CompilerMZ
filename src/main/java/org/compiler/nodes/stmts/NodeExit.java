package org.compiler.nodes.stmts;

import org.compiler.nodes.Expr;
import org.compiler.nodes.Stmt;

public class NodeExit extends Stmt {
    public NodeExit(Expr expr) {
        super(expr);
    }
}
