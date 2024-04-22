package org.compiler;

import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.*;
import org.compiler.nodes.expressions.terms.NodeIdent;
import org.compiler.nodes.expressions.terms.NodeIntLit;
import org.compiler.nodes.expressions.terms.NodeTerm;
import org.compiler.nodes.expressions.terms.NodeTermParen;
import org.compiler.nodes.statements.NodeAssign;
import org.compiler.nodes.statements.NodeExit;
import org.compiler.nodes.statements.NodeLet;
import org.compiler.nodes.statements.NodeScope;
import org.compiler.nodes.statements.conditionals.NodeIf;
import org.compiler.nodes.statements.conditionals.NodeWhile;

import java.util.*;

/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private String generated = "";
    private final NodeProgram m_program;
    private int stack_size = 0;
    private final Map<String, Integer> variables = new HashMap<>();
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
        case NodeAssign nodeAssign -> {
            if (!variables.containsKey(nodeAssign.getTokenIdent().getName())) {
                throw new IllegalArgumentException("Undeclared Identifier");
            }
            long offset = (stack_size - variables.get(nodeAssign.getTokenIdent().getName()) - 1) * 8L;
            stmtSB.append(generateExpression(nodeAssign.getStmt()));
            stmtSB.append(pop("rax"));
            stmtSB.append("     mov [rsp + ").append(offset).append("], rax\n");
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
            String finalLabel = label + "final";
            stmtSB.append("     ;;if\n\n");
            stmtSB.append(generateExpression(nodeIf.getStmt()));
            stmtSB.append(pop("rax"));
            stmtSB.append("     test rax, rax\n");
            if (numberOfElifs != 0) {
                stmtSB.append("     jz ").append(label).append(0).append("\n\n");
            }
            stmtSB.append(generateStatement(nodeIf.getIfScope()));
            stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
            int i;
            for (i = 0; i < numberOfElifs; i++) {
                stmtSB.append("     ;;elif\n");
                stmtSB.append(label).append(i).append(":\n\n");
                stmtSB.append("     ;;elif condition\n");
                stmtSB.append(generateExpression(nodeIf.getNthScopeElif(i).getStmt()));
                stmtSB.append(pop("rax"));
                stmtSB.append("     test rax, rax\n");
                stmtSB.append("     jz ").append(label).append(i + 1).append("\n");
                stmtSB.append("     ;;/elif condition\n");
                stmtSB.append(generateStatement(nodeIf.getNthScopeElif(i).getScope()));
                stmtSB.append("     ;;/elif\n");
            }
            stmtSB.append(label).append(i).append(":\n\n");
            if (nodeIf.hasElse()) {
                stmtSB.append("     ;;else\n");
                stmtSB.append(generateStatement(nodeIf.getScopeElse()));
                stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
                stmtSB.append("     ;;/else\n");
            }
            stmtSB.append(finalLabel).append(":\n\n");
            stmtSB.append("     ;;/if\n\n");
        }
        case NodeWhile nodeWhile -> {
            String labelStart = create_label();
            String labelEnd = create_label();

            stmtSB.append("     ;;while\n");
            stmtSB.append(labelStart).append(":\n");
            stmtSB.append(generateExpression(nodeWhile.getStmt()));
            stmtSB.append(pop("rax"));
            stmtSB.append("     test rax, rax\n");
            stmtSB.append("     jz ").append(labelEnd).append("\n");
            stmtSB.append(generateStatement(nodeWhile.getScope()));
            stmtSB.append("     jmp ").append(labelStart).append("\n");
            stmtSB.append(labelEnd).append(":\n");
            stmtSB.append("     ;;/while\n");

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
                throw new IllegalArgumentException("Identifier " + nodeIdent.getIdent().getName() + " not found");
            }
            termSB.append("     ;;identifier\n");
            long offset = (stack_size - variables.get(nodeIdent.getIdent().getName()) - 1) * 8L;
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
        case NodeBinMod nodeBinMod -> {
            bin_exprSB.append(generateExpression(nodeBinMod.getRight()));
            bin_exprSB.append(generateExpression(nodeBinMod.getLeft()));
            bin_exprSB.append("     ;;modulus\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     xor rdx, rdx\n");
            bin_exprSB.append("     div rbx\n");
            bin_exprSB.append(push("rdx"));
            bin_exprSB.append("     ;;/modulus\n\n");
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
        String out = "";
        if (pop_count != 0) {
            out = "     add rsp, " + pop_count * 8 + "\n\n";
        }
        stack_size -= pop_count;
        for (int i = 0; i < pop_count; i++) {
            variables.remove(getKeyWithHighestValue(variables));
        }
        scopes.removeLast();
        return out;
    }

    public String create_label() {
        return "label" + label_counter++;
    }

    public String getGenerated() {
        return generated;
    }

    public static String getKeyWithHighestValue(Map<String, Integer> map) {
        // Initialize variables to keep track of the key and value with the highest value
        String keyWithHighestValue = null;
        int highestValue = Integer.MIN_VALUE;

        // Iterate through the entries of the map
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            // If the value of the current entry is higher than the highest value encountered so far
            if (entry.getValue() > highestValue) {
                // Update the key and highest value
                keyWithHighestValue = entry.getKey();
                highestValue = entry.getValue();
            }
        }

        // Return the key associated with the highest value
        return keyWithHighestValue;
    }
}