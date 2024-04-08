package org.compiler.nodes.exprs;

import org.compiler.nodes.Expr;
import org.compiler.token.tokens.IntLit;

public class NodeIntLit extends Expr {
    public NodeIntLit(IntLit intLit) {
        super(intLit);
    }

    public IntLit getIntLit() {
        return (IntLit) getExpr();
    }
}
