package org.compiler.nodes.stmts;

import org.compiler.nodes.Expr;
import org.compiler.nodes.Stmt;
import org.compiler.nodes.exprs.NodeIdent;

public class NodeLet extends Stmt {
    private final NodeIdent identifier;

    public NodeLet(Expr stmt, NodeIdent identifier) {
        super(stmt);
        this.identifier = identifier;
    }

    public NodeIdent getIdentifier() {
        return identifier;
    }
}
