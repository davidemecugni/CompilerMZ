package org.compiler.nodes.statements;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;
import org.compiler.token.tokens.TokenIdent;

public class NodeAssign extends NodeStatement {
    final TokenIdent tokenIdent;

    public NodeAssign(NodeExpression stmt, TokenIdent tokenIdent) {
        super(stmt);
        this.tokenIdent = tokenIdent;
    }

    public TokenIdent getTokenIdent() {
        return tokenIdent;
    }

    @Override
    public String toString() {
        return "NodeAssign{" + "tokenIdent=" + tokenIdent + '}';
    }
}
