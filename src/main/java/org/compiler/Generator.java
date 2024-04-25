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
            stmtSB.append("     ;;if(label: ").append(label).append(")\n\n");
            stmtSB.append(generateExpression(nodeIf.getStmt()));
            stmtSB.append(pop("rax"));
            stmtSB.append("     test rax, rax\n");
            if (numberOfElifs != 0) {
                stmtSB.append("     jz ").append(label).append(0).append("\n\n");
            }
            stmtSB.append("     jz ").append(label).append(numberOfElifs).append("\n");
            stmtSB.append(generateStatement(nodeIf.getIfScope()));
            stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
            int i;
            for (i = 0; i < numberOfElifs; i++) {
                stmtSB.append("     ;;elif(label: ").append(label).append(")\n");
                stmtSB.append(label).append(i).append(":\n\n");
                stmtSB.append("     ;;elif condition\n");
                stmtSB.append(generateExpression(nodeIf.getNthScopeElif(i).getStmt()));
                stmtSB.append(pop("rax"));
                stmtSB.append("     test rax, rax\n");
                stmtSB.append("     jz ").append(label).append(i + 1).append("\n");
                stmtSB.append("     ;;/elif condition\n");
                stmtSB.append(generateStatement(nodeIf.getNthScopeElif(i).getScope()));
                stmtSB.append("     ;;/elif(label: ").append(label).append(")\n");
            }
            stmtSB.append(label).append(i).append(":\n\n");
            if (nodeIf.hasElse()) {
                stmtSB.append("     ;;else(label: ").append(label).append(")\n");
                stmtSB.append(generateStatement(nodeIf.getScopeElse()));
                stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
                stmtSB.append("     ;;/else(label: ").append(label).append(")\n");
            }
            stmtSB.append(finalLabel).append(":\n\n");
            stmtSB.append("     ;;/if(label: ").append(label).append(")\n\n");
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
            bin_exprSB.append("     imul rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/multiplication\n\n");
        }
        case NodeBinDiv nodeBinDiv -> {
            bin_exprSB.append(generateExpression(nodeBinDiv.getRight()));
            bin_exprSB.append(generateExpression(nodeBinDiv.getLeft()));
            bin_exprSB.append("     ;;division\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     idiv rbx\n");
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
            bin_exprSB.append("     idiv rbx\n");
            bin_exprSB.append(push("rdx"));
            bin_exprSB.append("     ;;/modulus\n\n");
        }
        case NodeBinLogicEq nodeBinLogicEq -> {
            bin_exprSB.append(generateExpression(nodeBinLogicEq.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinLogicEq.getRight()));
            bin_exprSB.append("     ;;equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rax, rbx\n");
            bin_exprSB.append("     sete al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/equal\n\n");
        }
        case NodeBinLogicNotEq nodeBinLogicNotEq -> {
            bin_exprSB.append(generateExpression(nodeBinLogicNotEq.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinLogicNotEq.getRight()));
            bin_exprSB.append("     ;;not equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rax, rbx\n");
            bin_exprSB.append("     setne al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/not equal\n\n");
        }

        case NodeBinLogicGT nodeBinLogicGT -> {
            bin_exprSB.append(generateExpression(nodeBinLogicGT.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinLogicGT.getRight()));
            bin_exprSB.append("     ;;greater than\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setg al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/greater than\n\n");
        }

        case NodeBinLogicLT nodeBinLogicLT -> {
            bin_exprSB.append(generateExpression(nodeBinLogicLT.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinLogicLT.getRight()));
            bin_exprSB.append("     ;;less than\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setl al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/less than\n\n");
        }

        case NodeBinLogicGE nodeBinLogicGE -> {
            bin_exprSB.append(generateExpression(nodeBinLogicGE.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinLogicGE.getRight()));
            bin_exprSB.append("     ;;greater than or equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setge al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/greater than or equal\n\n");
        }
        case NodeBinLogicLE nodeBinLogicLE -> {
            bin_exprSB.append(generateExpression(nodeBinLogicLE.getLeft()));
            bin_exprSB.append(generateExpression(nodeBinLogicLE.getRight()));
            bin_exprSB.append("     ;;less than or equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setle al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/less than or equal\n\n");
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
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        return list.getLast().getKey();
    }
}