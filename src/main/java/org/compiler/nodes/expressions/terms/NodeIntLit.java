package org.compiler.nodes.expressions.terms;

import org.compiler.token.tokens.TokenIntLit;

/**
 * The NodeIntLit class is a subclass of NodeTerm. It represents an integer literal in the AST, which is a type of term
 * node. This class has a method to retrieve the integer literal value.
 */

public class NodeIntLit extends NodeTerm {
    public NodeIntLit(TokenIntLit intLit) {
        super(intLit);
    }

    public TokenIntLit getIntLit() {
        return (TokenIntLit) getExpr();
    }

    @Override
    public String toString() {
        return "NodeIntLit{" + "expr=" + getExpr() + '}';
    }
}
