package org.compiler.nodes.expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.TokenIdent;

public class NodeIdent extends NodeExpression {
    public NodeIdent(TokenIdent ident) {
        super(ident);
    }

    public TokenIdent getIdent() {
        return (TokenIdent) getExpr();
    }
}
