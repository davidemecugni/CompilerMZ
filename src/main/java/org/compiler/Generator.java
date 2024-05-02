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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates the assembly code from the AST Checks for undeclared identifiers Checks for redeclaration of identifiers
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

    /**
     * Generates the assembly code for the program Exit code is 0 by default if no exit statement is present
     */
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

    /**
     * Generates the assembly code for a statement
     *
     * @param stmt
     *            the statement to generate code for
     *
     * @return the generated assembly code
     */
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

    /**
     * Generates the assembly code for a term
     *
     * @param expr
     *            the term to generate code for
     *
     * @return the generated assembly code
     */
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

    /**
     * Generates the assembly code for an expression
     *
     * @param expr
     *            the expression to generate code for
     *
     * @return the generated assembly code
     */
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

    /**
     * Generates the assembly code for a binary expression
     *
     * @param bin_expr
     *            the binary expression to generate code for
     *
     * @return the generated assembly code
     */
    public String generateBinaryExpression(NodeBin bin_expr) {
        StringBuilder bin_exprSB = new StringBuilder();

        switch (bin_expr.getType()) {
        case BinType.Add -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;addition\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     add rax, rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/addition\n\n");
        }
        // Exit code is 8 bit, so no negative numbers
        case BinType.Sub -> {
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append("     ;;subtraction\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     sub rax, rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/subtraction\n\n");
        }
        case BinType.Multi -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;multiplication\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     imul rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/multiplication\n\n");
        }
        case BinType.Div -> {
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append("     ;;division\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     xor rdx, rdx\n");
            bin_exprSB.append("     idiv rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/division\n\n");
        }
        case BinType.Mod -> {
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append("     ;;modulus\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     xor rdx, rdx\n");
            bin_exprSB.append("     idiv rbx\n");
            bin_exprSB.append(push("rdx"));
            bin_exprSB.append("     ;;/modulus\n\n");
        }
        case BinType.Eq -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rax, rbx\n");
            bin_exprSB.append("     sete al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/equal\n\n");
        }
        case BinType.NotEq -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;not equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rax, rbx\n");
            bin_exprSB.append("     setne al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/not equal\n\n");
        }

        case BinType.GT -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;greater than\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setg al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/greater than\n\n");
        }

        case BinType.LT -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;less than\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setl al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/less than\n\n");
        }

        case BinType.GE -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;greater than or equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setge al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/greater than or equal\n\n");
        }
        case BinType.LE -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;less than or equal\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     cmp rbx, rax\n");
            bin_exprSB.append("     setle al\n");
            bin_exprSB.append("     movzx rbx, al\n");
            bin_exprSB.append(push("rbx"));
            bin_exprSB.append("     ;;/less than or equal\n\n");
        }
        case BinType.And -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;and\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     and rax, rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/and\n\n");
        }
        case BinType.Or -> {
            bin_exprSB.append(generateExpression(bin_expr.getLeft()));
            bin_exprSB.append(generateExpression(bin_expr.getRight()));
            bin_exprSB.append("     ;;or\n");
            bin_exprSB.append(pop("rax"));
            bin_exprSB.append(pop("rbx"));
            bin_exprSB.append("     or rax, rbx\n");
            bin_exprSB.append(push("rax"));
            bin_exprSB.append("     ;;/or\n\n");
        }
        case null, default -> throw new IllegalArgumentException("Unknown binary expression type in generator");
        }

        return bin_exprSB.toString();
    }

    /**
     * Prints the type of each statement, used for debugging
     */
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

    /**
     * Begins a new scope, pushes the current stack size to the scopes stack
     */
    public void beginScope() {
        scopes.add(variables.size());
    }

    /**
     * Ends the current scope, pops all variables from the stack(used for garbage collection)
     *
     * @return a string
     */
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

    /**
     * Creates a progressive label for the .asm
     *
     * @return a string
     */
    public String create_label() {
        return "label" + label_counter++;
    }

    public String getGenerated() {
        return generated;
    }

    /**
     * Returns the key with the highest value in a map, used by the endScope method for garbage collection
     *
     * @param map
     *            the map to search
     *
     * @return the key with the highest value
     */
    public static String getKeyWithHighestValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        return list.getLast().getKey();
    }
}