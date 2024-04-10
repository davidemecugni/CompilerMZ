package org.compiler.nodes.expressions;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.TokenIntLit;

public class NodeIntLit extends NodeExpression {
    public NodeIntLit(TokenIntLit intLit) {
        super(intLit);
    }

    public TokenIntLit getIntLit() {
        return (TokenIntLit) getExpr();
    }
}
