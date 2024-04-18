package org.compiler.nodes.expressions.terms;

import org.compiler.token.tokens.TokenIdent;

public class NodeIdent extends NodeTerm {
    public NodeIdent(TokenIdent ident) {
        super(ident);
    }

    public TokenIdent getIdent() {
        return (TokenIdent) getExpr();
    }

    @Override
    public String toString() {
        return "NodeIdent{" + "expr=" + getExpr() + '}';
    }
}
