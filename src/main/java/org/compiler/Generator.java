package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.NodeIdent;
import org.compiler.nodes.expressions.NodeIntLit;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.token.tokens.TokenIdent;


/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private final NodeProgram m_program;

    public Generator(NodeProgram program) {
        this.m_program = program;
    }

    public String generateStatement(NodeStatement stmt) {
        StringBuilder stmtSB = new StringBuilder();
        switch (stmt) {
        case NodeExit nodeExit -> {
            stmtSB.append("     mov rax, 60\n");
            stmtSB.append("     mov rdi, ").append("0").append("\n");
            stmtSB.append("     syscall");
        }
        case NodeLet nodeLet -> {
            // Handle NodeLet type
        }
        case null, default -> {
            throw new IllegalArgumentException("Unknown statement type in generator");
        }
        }
        return stmtSB.toString();
    }

    public String generateExpression(NodeExpression expr) {
        StringBuilder exprSB = new StringBuilder();
        switch (expr) {
        case NodeIntLit nodeIntLit -> {
            exprSB.append("     mov rax, ").append(nodeIntLit.getIntLit().getValue()).append("\n");
            exprSB.append("     push rax\n");
        }
        case NodeIdent nodeIdent -> {
            // Handle NodeIdent type
        }
        case null, default -> {
            throw new IllegalArgumentException("Unknown expression type in generator");
        }
        }
        return exprSB.toString();
    }

    public String generateProgram() {
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n");
        for (NodeStatement statement : m_program.getStmts()) {
            sb.append(generateStatement(statement));
        }
        // Exits 0 by default
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, 0\n");
        sb.append("     syscall");
        return sb.toString();
    }
}
