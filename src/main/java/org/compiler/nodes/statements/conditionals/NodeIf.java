package org.compiler.nodes.statements.conditionals;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.statements.NodeScope;

import java.util.ArrayList;

public class NodeIf extends Conditional {
    ArrayList<NodeElif> elifs;
    NodeScope nodeScopeElse;

    public NodeIf(NodeExpression stmt, NodeScope node) {
        super(stmt, node);
        elifs = new ArrayList<>();
    }

    public NodeScope getIfScope() {
        return getScope();
    }

    public void setIfScope(NodeScope ifScope) {
        setScope(ifScope);
    }

    public NodeElif getNthScopeElif(int index) {
        return elifs.get(index);
    }

    public ArrayList<NodeElif> getElifs() {
        return elifs;
    }

    public void addScopeElif(NodeElif elif) {
        elifs.add(elif);
    }

    public NodeScope getScopeElse() {
        return nodeScopeElse;
    }

    public void setScopeElse(NodeScope nodeScopeElse) {
        this.nodeScopeElse = nodeScopeElse;
    }

    public boolean hasElse() {
        return nodeScopeElse != null;
    }

    public int countElif() {
        return elifs.size();
    }

    @Override
    public String toString() {
        return "NodeIf{" + "ifScope=" + getScope() + ", elifs=" + elifs + ", nodeScopeElse=" + nodeScopeElse + '}';
    }
}
