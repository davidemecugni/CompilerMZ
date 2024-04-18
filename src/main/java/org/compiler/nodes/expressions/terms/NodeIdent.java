package org.compiler.nodes.expressions.terms;

import org.compiler.token.tokens.TokenIdent;

/**
 * The NodeIdent class is a subclass of NodeTerm. It represents an identifier in the AST, which is a type of term node.
 * This class has a method to retrieve the identifier token.
 */

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
