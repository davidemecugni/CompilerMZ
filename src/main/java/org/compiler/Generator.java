package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.NodeIdent;
import org.compiler.nodes.expressions.NodeIntLit;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.token.tokens.TokenIdent;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private final NodeProgram m_program;
    private long stack_size = 0;
    private Map<String, Long> variables = new HashMap<>();

    public Generator(NodeProgram program) {
        this.m_program = program;
    }

    public String generateStatement(NodeStatement stmt) {
        StringBuilder stmtSB = new StringBuilder();
        switch (stmt) {
            case NodeExit nodeExit -> {
                //prendo l'espressione exit(expr)
                stmtSB.append(generateExpression(stmt.getStmt()));
                stmtSB.append("     ;;exit\n");
                stmtSB.append("     mov rax, 60\n");
                stmtSB.append(pop("rdi"));
                stmtSB.append("     syscall\n");
                stmtSB.append("     ;;/exit\n\n");
            }
            case NodeLet nodeLet -> {
                if (variables != null && variables.containsValue(nodeLet.getIdentifier().getIdent().getName())) {
                    throw new IllegalArgumentException("Identifier already used");
                }
                variables.put(nodeLet.getIdentifier().getIdent().getName(), stack_size);
                stmtSB.append(generateExpression(stmt.getStmt()));
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
                exprSB.append("     ;;value\n");
                exprSB.append("     mov rax, ").append(nodeIntLit.getIntLit().getValue()).append("\n");
                exprSB.append(push("rax")).append("\n");
            }
            case NodeIdent nodeIdent -> {
                /*
                //per controllare se la variabile è presente nella mappa
                if (!variables.containsValue(nodeIdent.getIdent().getName())) {
                    throw new IllegalArgumentException("Undeclared Identifier");
                }
                */

                exprSB.append("     ;;identifier\n");
                long offset = (stack_size - variables.get(nodeIdent.getIdent().getName()) - 1) * 8;
                exprSB.append(push("QWORD [rsp + " + offset + "]")).append("\n");
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
        sb.append("     ;;final exit\n");
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, 0\n");
        sb.append("     syscall\n");
        return sb.toString();
    }

    public void printStmt() {
        for (NodeStatement statement : m_program.getStmts()) {
            System.out.println(statement.getStmt().getExpr().getType().toString());
        }
    }

    /**
     * increase stack location
     * @param reg asm register
     * @return a string
     */
    public String push(String reg) {
        stack_size++;
        return "     push " + reg + "\n";
    }

    /**
     * reduces stack location
     * @param reg asm register
     * @return a string
     */
    public String pop(String reg) {
        stack_size--;
        return "     pop " + reg + "\n";
    }
}