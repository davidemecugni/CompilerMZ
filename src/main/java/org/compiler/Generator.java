package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.*;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;
import org.compiler.nodes.expressions.terms.NodeTerm;
import org.compiler.nodes.expressions.terms.NodeTermParen;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.nodes.statements.NodeScope;
import org.compiler.nodes.statements.conditionals.NodeIf;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private String generated = "";
    private final NodeProgram m_program;
    private long stack_size = 0;
    private final SortedMap<String, Long> variables = new TreeMap<>();
    private final ArrayList<Integer> scopes = new ArrayList<>();
    private int label_counter = 0;

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
        case NodeScope nodeScope -> {
            beginScope();
            stmtSB.append("     ;;begin scope\n\n");
            for (NodeStatement nodeStatement : nodeScope.getStmts()) {
                stmtSB.append(generateStatement(nodeStatement));
            }
            stmtSB.append(endScope());
            stmtSB.append("     ;;/end scope\n\n");
        }
        case NodeIf nodeIf -> {
            String label = create_label();
            int numberOfElifs = nodeIf.countElif();
            String finalLabel = label + (numberOfElifs + 2);
            stmtSB.append("     ;;if\n\n");
            stmtSB.append(generateExpression(nodeIf.getStmt()));
            stmtSB.append(pop("rax"));
            stmtSB.append("     test rax, rax\n");
            int i;
            for (i = 0; i < numberOfElifs + 1; i++) {
                if (i > 0) {
                    stmtSB.append(";;elif\n");
                    stmtSB.append(label).append(i).append(":\n\n");
                    stmtSB.append("     ;;elif condition\n");
                    System.out.println(nodeIf.getNthScopeElif(i - 1));
                    stmtSB.append(generateExpression(nodeIf.getNthScopeElif(i - 1).getStmt()));
                    stmtSB.append(pop("rax"));
                    stmtSB.append("     test rax, rax\n");
                    stmtSB.append("     jz ").append(label).append(i + 1).append("\n\n");
                    stmtSB.append(generateStatement(nodeIf.getNthScopeElif(i - 1).getScope()));
                }

                if (i == 0) {
                    stmtSB.append("     jz ").append(label).append(i + 1).append("\n\n");
                    stmtSB.append(generateStatement(nodeIf.getIfScope()));
                }
                stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
            }
            stmtSB.append(label).append(i).append(":\n\n");
            if (nodeIf.hasElse()) {
                stmtSB.append(generateStatement(nodeIf.getScopeElse()));
                stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
            }
            stmtSB.append(finalLabel).append(":\n\n");
            stmtSB.append("     ;;/if\n\n");
        }
        case null, default -> throw new IllegalArgumentException("Unknown statement type in generator");
        }
        return stmtSB.toString();
    }

    public String generateTerm(NodeTerm expr) {
        StringBuilder termSB = new StringBuilder();
        // Generate the term based on the type
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
        case NodeTermParen nodeTermParen -> termSB.append(generateExpression(nodeTermParen.getExprParen()));
        case null, default -> throw new IllegalArgumentException("Unknown term type in generator");
        }
        return termSB.toString();
    }

    public String generateExpression(NodeExpression expr) {
        StringBuilder exprSB = new StringBuilder();

        // If it's a term, generate the term, otherwise generate the binary expression
        switch (expr) {
        case NodeTerm nodeTerm -> exprSB.append(generateTerm(nodeTerm));
        case NodeBin nodeBin -> exprSB.append(generateBinaryExpression(nodeBin));
        case null, default -> throw new IllegalArgumentException("Unknown expression type in generator");
        }
        return exprSB.toString();
    }

    public String generateBinaryExpression(NodeBin bin_expr) {
        StringBuilder bin_exprSB = new StringBuilder();

        switch (bin_expr) {
        case NodeBinAdd nodeBinAdd -> {
            bin_exprSB.append(generateExpression(nodeBinAdd.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinAdd.getRight()));
            bin_exprSB.append("     ;;addition\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     add rax, rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/addition\n\n");
        }
        // Exit code is 8 bit, so no negative numbers
        case NodeBinSub nodeBinSub -> {
            bin_exprSB.append(generateExpression(nodeBinSub.getRight()));
            bin_exprSB.append(generateExpression(nodeBinSub.getLeft()));
            bin_exprSB.append("     ;;subtraction\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     sub rax, rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/subtraction\n\n");
        }
        case NodeBinMulti nodeBinMulti -> {
            bin_exprSB.append(generateExpression(nodeBinMulti.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinMulti.getRight()));
            bin_exprSB.append("     ;;multiplication\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     mul rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/multiplication\n\n");
        }
        case NodeBinDiv nodeBinDiv -> {
            bin_exprSB.append(generateExpression(nodeBinDiv.getRight()));
            bin_exprSB.append(generateExpression(nodeBinDiv.getLeft()));
            bin_exprSB.append("     ;;division\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     div rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/division\n\n");
        }
        case null, default -> throw new IllegalArgumentException("Unknown binary expression type in generator");
        }

        return bin_exprSB.toString();
    }

    public void generateProgram() {
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

    public void beginScope() {
        scopes.add(variables.size());
    }

    public String endScope() {
        int pop_count = variables.size() - scopes.getLast();
        stack_size -= pop_count;
        for (int i = 0; i < pop_count; i++) {
            variables.remove(variables.lastKey());
        }
        scopes.removeLast();
        return "     add rsp, " + pop_count * 8 + "\n\n";
    }

    public String create_label() {
        return "label" + label_counter++;
    }

    public String getGenerated() {
        return generated;
    }
}