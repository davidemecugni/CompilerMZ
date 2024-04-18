package org.compiler.nodes.expressions.terms;

import org.compiler.token.tokens.TokenIntLit;

public class NodeIntLit extends NodeTerm {
    public NodeIntLit(TokenIntLit intLit) {
        super(intLit);
    }

    public TokenIntLit getIntLit() {
        return (TokenIntLit) getExpr();
    }
}
