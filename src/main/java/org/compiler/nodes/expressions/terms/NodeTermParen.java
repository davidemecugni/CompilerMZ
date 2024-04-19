package org.compiler.nodes.expressions.terms;

import org.compiler.nodes.NodeExpression;
import org.compiler.token.tokens.Token;

/**
 * NodeTermParen is a subclass of NodeTerm in your compiler project.
 * It represents a term within parentheses in the AST. This class has a
 * NodeExpression object, expr, which represents the expression within the parentheses.
 */

public class NodeTermParen extends NodeTerm{
    NodeExpression expr;

    public NodeTermParen(Token token, NodeExpression expr) {
        super(token);
        this.expr = expr;
    }

    public NodeExpression getExprParen() {
        return expr;
    }

    @Override
    public String toString() {
        return "NodeTermParen{" +
                "expr=" + expr +
                '}';
    }
}
