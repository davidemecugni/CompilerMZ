package org.compiler.nodes.expressions.terms;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * The NodeTerm class is a part of the Abstract Syntax Tree (AST). It represents a term node in the AST, which is a
 * component of the language. This class extends NodeExpression.
 */

public class NodeTerm extends NodeExpression {
    public NodeTerm(Token expr) {
        super(expr);
    }

    @Override
    public String toString() {
        return "NodeTerm{" + "expr=" + getExpr() + '}';
    }
}
