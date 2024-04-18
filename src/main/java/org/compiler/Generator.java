package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.NodeBin;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;
import org.compiler.nodes.expressions.terms.NodeTerm;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private String generated = "";
    private final NodeProgram m_program;
    private long stack_size = 0;
    private final Map<String, Long> variables = new HashMap<>();

    public Generator(NodeProgram program) {
        this.m_program = program;
        generateProgram();
    }

    public String generateStatement(NodeStatement stmt) {
        StringBuilder stmtSB = new StringBuilder();
        switch (stmt) {
        case NodeExit nodeExit -> {
            stmtSB.append(generateExpression(stmt.getStmt()));
            stmtSB.append("     ;;exit\n");
            stmtSB.append("     mov rax, 60\n");
            stmtSB.append(pop("rdi"));
            stmtSB.append("     syscall\n");
            stmtSB.append("     ;;/exit\n\n");
        }
        case NodeLet nodeLet -> {
            if (variables.containsKey(nodeLet.getIdentifier().getIdent().getName())) {
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

    public String generateTerm(NodeTerm expr) {
        StringBuilder termSB = new StringBuilder();
        switch (expr) {
        case NodeIntLit nodeIntLit -> {
            termSB.append("     ;;value\n");
            termSB.append("     mov rax, ").append(nodeIntLit.getIntLit().getValue()).append("\n");
            termSB.append(push("rax")).append("\n");
        }
        case NodeIdent nodeIdent -> {
            if (!variables.containsKey(nodeIdent.getIdent().getName())) {
                throw new IllegalArgumentException("Identifier not found");
            }

            termSB.append("     ;;identifier\n");

            long offset = (stack_size - variables.get(nodeIdent.getIdent().getName()) - 1) * 8;
            if (offset < 0) {
                throw new IllegalArgumentException("Variable might not have been initialized");
            }

            termSB.append(push("QWORD [rsp + " + offset + "]")).append("\n");
        }
        case null, default -> throw new IllegalArgumentException("Unknown term type in generator");
        }
        return termSB.toString();
    }

    public String generateExpression(NodeExpression expr) {
        StringBuilder exprSB = new StringBuilder();

        switch (expr) {
        case NodeTerm nodeTerm -> {
            exprSB.append(generateTerm(nodeTerm));
        }
        case NodeBin nodeBin -> {
            exprSB.append(generateExpression(nodeBin.getLeft()));
            exprSB.append(generateExpression(nodeBin.getRight()));
            exprSB.append("     ;;addition\n");
            exprSB.append(pop("rax"));
            exprSB.append(pop("rbx"));
            exprSB.append("     add rax, rbx\n");
            exprSB.append(push("rax"));
            exprSB.append("     ;;/addition\n\n");
        }
        case null, default -> {
            throw new IllegalArgumentException("Unknown expression type in generator");
        }
        }
        return exprSB.toString();
    }

    public void generateProgram() {
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n\n");
        System.out.println(m_program.getStmts());
        for (NodeStatement statement : m_program.getStmts()) {
            sb.append(generateStatement(statement));
        }
        // Exits 0 by default
        sb.append("     ;;final exit\n");
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, 0\n");
        sb.append("     syscall\n");
        generated = sb.toString();
    }

    public void printStmt() {
        for (NodeStatement statement : m_program.getStmts()) {
            System.out.println(statement.getStmt().getExpr().getType().toString());
        }
    }

    /**
     * increase stack location
     *
     * @param reg
     *            asm register
     *
     * @return a string
     */
    public String push(String reg) {
        stack_size++;
        return "     push " + reg + "\n";
    }

    /**
     * reduces stack location
     *
     * @param reg
     *            asm register
     *
     * @return a string
     */
    public String pop(String reg) {
        stack_size--;
        return "     pop " + reg + "\n";
    }

    public String getGenerated() {
        return generated;
    }
}