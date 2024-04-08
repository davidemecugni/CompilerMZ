package org.compiler.nodes.exprs;

import org.compiler.nodes.Expr;
import org.compiler.token.tokens.Ident;

public class NodeIdent extends Expr {
    public NodeIdent(Ident ident) {
        super(ident);
    }

    public Ident getIdent() {
        return (Ident) getExpr();
    }
}
