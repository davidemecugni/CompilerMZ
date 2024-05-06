package org.compiler.nodes.statements.functions;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeStatement;

/**
 * NodeBuiltInFunc is a NodeStatement subclass representing a built-in function call in the AST. It contains a single
 * NodeExpression and a BuiltInFunc.
 */

public class NodeBuiltInFunc extends NodeStatement {

    private final BuiltInFunc func;

    public NodeBuiltInFunc(NodeExpression stmt, BuiltInFunc func) {
        super(stmt);
        this.func = func;
    }

    public BuiltInFunc getFunc() {
        return func;
    }

    @Override
    public String toString() {
        return "NodeBuiltInFunc{" + "func=" + func + '}';
    }
}
