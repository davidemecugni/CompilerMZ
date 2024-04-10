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
    long stack_location;

    public Generator(NodeProgram program) {
        this.m_program = program;
        this.stack_location = 0;
    }

    public String generateStatement(NodeStatement stmt) {
        StringBuilder stmtSB = new StringBuilder();
        switch (stmt) {
            case NodeExit nodeExit -> {
                //prendo l'espressione exit(expr)
                stmtSB.append(generateExpression(stmt.getStmt()));
                stmtSB.append("     ;;exit\n");
                stmtSB.append("     mov rax, 60\n");
                stmtSB.append(pop("rdi")).append("\n");
                stmtSB.append("     syscall\n");
                stmtSB.append("     ;;/exit\n\n");
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
                exprSB.append(push("rax")).append("\n");
            }
            case NodeIdent nodeIdent -> {

            }
            case null, default -> {
                throw new IllegalArgumentException("Unknown expression type in generator");
            }
        }
        return exprSB.toString();
    }

    public String generateProgram() {
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n\n");
        for (NodeStatement statement : m_program.getStmts()) {
            sb.append(generateStatement(statement));
        }
        // Exits 0 by default
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, 0\n");
        sb.append("     syscall\n");
        return sb.toString();
    }

    public void printStmt() {
        for (NodeStatement statement : m_program.getStmts()) {
            System.out.println(statement.getStmt().getExpr().getType().toString());
        }
        stack_location++;
    }

    /**
     * increase stack location
     * @param reg asm register
     * @return a string
     */
    public String push(String reg) {
        stack_location++;
        return "     push " + reg + "\n";
    }

    /**
     * reduces stack location
     * @param reg asm register
     * @return a string
     */
    public String pop(String reg) {
        stack_location--;
        return "     pop " + reg + "\n";
    }
}