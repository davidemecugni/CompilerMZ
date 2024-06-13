package org.compiler.nodes.expressions.terms;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * The NodeString class is a subclass of NodeExpression. It represents a string in the AST, which is a type of expression.
 */
public class NodeString extends NodeExpression {

    public NodeString(Token expr) {
        super(expr);
    }
}
