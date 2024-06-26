package org.compiler;

import org.compiler.errors.TokenError;
import org.compiler.nodes.NodeExpression;
import org.compiler.nodes.NodeProgram;
import org.compiler.nodes.NodeStatement;
import org.compiler.nodes.expressions.binary_expressions.BinType;
import org.compiler.nodes.expressions.binary_expressions.NodeBin;
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
import org.compiler.nodes.statements.functions.BuiltInFunc;
import org.compiler.nodes.statements.functions.NodeBuiltInFunc;
import org.compiler.token.TokenType;
import org.compiler.token.tokens.TokenString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates the assembly code from the AST. Checks for undeclared identifiers. Checks for redeclaration of identifiers
 */
public class Generator {
    private String generated = "";
    private final NodeProgram m_program;
    private int stack_size = 0;
    private final Map<String, Integer> variables = new HashMap<>();
    private final ArrayList<Integer> scopes = new ArrayList<>();
    private int label_counter = 0;
    private final StringBuilder sbData = new StringBuilder();
    private int msgCounter = 1;
    private boolean callPrintAssemblyFunc = false;
    private boolean callAtoi = false;

    public Generator(NodeProgram program) throws TokenError {
        this.m_program = program;
        generateProgram();
    }

    /**
     * Generates the assembly code for the program. Exit code is 0 by default if no exit statement is present
     */
    public void generateProgram() throws TokenError {
        StringBuilder sb = new StringBuilder();
        sbData.append("section .data\n");
        sbData.append("     minus_sign db '-'\n");
        sbData.append("     buffer db 20 dup(0)\n");
        sbData.append("     newline db 0x0a\n");
        sb.append("section .text\n");
        sb.append("     global main\n\nmain:\n");
        for (NodeStatement statement : m_program.getStmts()) {
            sb.append(generateStatement(statement));
        }
        // Exits 0 by default
        sb.append("     ;;final exit\n");
        sb.append(mov("rax", "60"));
        sb.append(mov("rdi", "0"));
        sb.append("     syscall\n\n");

        if (callPrintAssemblyFunc) {
            printAssemblyFunc(sb);
        }
        if (callAtoi) {
            atoiAssemblyFunc(sb);
        }

        sbData.append("\n");
        sbData.append(sb);
        generated = sbData.toString();
    }

    /**
     * Generates the assembly code for a statement
     *
     * @param stmt
     *            the statement to generate code for
     *
     * @return the generated assembly code
     */
    public String generateStatement(NodeStatement stmt) throws TokenError {

        StringBuilder stmtSB = new StringBuilder();
        switch (stmt) {
        case NodeExit ignored -> {
            stmtSB.append(generateExpression(stmt.getStmt()));
            stmtSB.append("     ;;exit\n");
            stmtSB.append(mov("rax", "60"));
            stmtSB.append(pop("rdi"));
            stmtSB.append("     syscall\n");
            stmtSB.append("     ;;/exit\n\n");
        }
        case NodeLet nodeLet -> {
            if (variables.containsKey(nodeLet.getIdentifier().getIdent().getName())) {
                throw new TokenError("Redeclared Identifier: " + nodeLet.getIdentifier().getIdent().getName(),
                        nodeLet.getIdentifier().getIdent().getLine(),
                        nodeLet.getIdentifier().getIdent().getColumnStart(),
                        nodeLet.getIdentifier().getIdent().getColumnEnd());
            }
            variables.put(nodeLet.getIdentifier().getIdent().getName(), stack_size);
            stmtSB.append(generateExpression(stmt.getStmt()));
        }
        case NodeAssign nodeAssign -> {
            if (!variables.containsKey(nodeAssign.getTokenIdent().getName())) {
                throw new TokenError("Undeclared Identifier: " + nodeAssign.getTokenIdent().getName(),
                        nodeAssign.getTokenIdent().getLine(), nodeAssign.getTokenIdent().getColumnStart(),
                        nodeAssign.getTokenIdent().getColumnEnd());
            }
            long offset = (stack_size - variables.get(nodeAssign.getTokenIdent().getName()) - 1) * 8L;
            stmtSB.append(generateExpression(nodeAssign.getStmt()));
            stmtSB.append(pop("rax"));
            stmtSB.append(mov("[rsp + " + offset + "]", "rax"));
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
                stmtSB.append("     jz ").append(label).append("x0").append("\n\n");
            }
            stmtSB.append("     jz ").append(label).append("x").append(numberOfElifs).append("\n");
            stmtSB.append(generateStatement(nodeIf.getIfScope()));
            stmtSB.append("     jmp ").append(finalLabel).append("\n\n");
            int i;
            for (i = 0; i < numberOfElifs; i++) {
                stmtSB.append("     ;;elif(label: ").append(label).append(")\n");
                stmtSB.append(label).append("x").append(i).append(":\n\n");
                stmtSB.append("     ;;elif condition\n");
                stmtSB.append(generateExpression(nodeIf.getNthScopeElif(i).getStmt()));
                stmtSB.append(pop("rax"));
                stmtSB.append("     test rax, rax\n");
                stmtSB.append("     jz ").append(label).append("x").append(i + 1).append("\n");
                stmtSB.append("     ;;/elif condition\n");
                stmtSB.append(generateStatement(nodeIf.getNthScopeElif(i).getScope()));
                stmtSB.append("     ;;/elif(label: ").append(label).append(")\n");
            }
            stmtSB.append(label).append("x").append(i).append(":\n\n");
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
        case NodeBuiltInFunc nodeBuiltInFunc -> {
            switch (nodeBuiltInFunc.getFunc()) {
            case BuiltInFunc.print -> {
                if (nodeBuiltInFunc.getStmt().getExpr().getType() == TokenType.string_lit) {
                    TokenString content = (TokenString) nodeBuiltInFunc.getStmt().getExpr();
                    toPrint(content.getContent(), stmtSB);
                } else if (nodeBuiltInFunc.getStmt().getExpr().getType() == TokenType.int_lit) {
                    NodeIntLit nodeIntLit = (NodeIntLit) nodeBuiltInFunc.getStmt();
                    String number = Long.toString(nodeIntLit.getIntLit().getValue());
                    toPrint(number, stmtSB);
                } else if (nodeBuiltInFunc.getStmt().getExpr().getType() == TokenType.ident) {
                    NodeIdent nodeIdent = (NodeIdent) nodeBuiltInFunc.getStmt();
                    long offset = findOffset(nodeIdent, stmtSB);
                    stmtSB.append(mov("rax", "[rsp + " + offset + "]"));
                    callPrintAssemblyFunc = true;
                    stmtSB.append("     call print_number\n\n");
                    printNewLine(stmtSB);
                }
            }
            case BuiltInFunc.read -> {
                NodeIdent nodeIdent = (NodeIdent) nodeBuiltInFunc.getStmt();
                long offset = findOffset(nodeIdent, stmtSB);
                callAtoi = true;
                stmtSB.append("     ;;read\n");
                stmtSB.append(mov("rax", "0"));
                stmtSB.append(mov("rdi", "0"));
                stmtSB.append(mov("rsi", "buffer"));
                stmtSB.append(mov("rdx", "20"));
                stmtSB.append("     syscall\n\n");
                stmtSB.append(mov("rdi", "buffer"));
                stmtSB.append("     call atoi\n");
                stmtSB.append(mov("QWORD[rsp + " + offset + "]", "rax"));
                stmtSB.append("     ;;/read\n\n");
            }
            }
        }
        case null, default -> throw new IllegalArgumentException("Unknown statement type in generator");
        }
        return stmtSB.toString();
    }

    /**
     * It generates the assembly code for a string to print
     *
     * @param value
     *            the actual value to print
     * @param sb
     *            the StringBuilder Object to append the assembly code
     */
    private void toPrint(String value, StringBuilder sb) {
        sbData.append("     msg").append(msgCounter).append(" db ").append("'").append(value).append("'")
                .append(", 0x0a\n");
        sb.append("     ;;print\n");
        sb.append(mov("rax", "1"));
        sb.append(mov("rdi", "1"));
        sb.append(mov("rsi", "msg" + msgCounter));
        msgCounter++;
        String length = Integer.toString(value.length());
        sb.append(mov("rdx", length));
        sb.append("     syscall\n");
        sb.append("     ;;/print\n\n");
        printNewLine(sb);
    }

    /**
     * It generates the assembly code to print a newline
     *
     * @param sb
     *            the StringBuilder Object to append the assembly code
     */
    private void printNewLine(StringBuilder sb) {
        sb.append("     ;;print newline\n");
        sb.append(mov("rax", "1"));
        sb.append(mov("rdi", "1"));
        sb.append(mov("rsi", "newline"));
        sb.append(mov("rdx", "1"));
        sb.append("     syscall\n");
        sb.append("     ;;/print newline\n\n");
    }

    /**
     * It generates the assembly code to print an ident
     *
     * @param sb
     *            the StringBuilder Object to append the assembly code
     */
    private void printAssemblyFunc(StringBuilder sb) {
        sb.append("print_number:\n");
        sb.append("     ;; check if number is negative\n");
        sb.append(mov("rbx", "rax"));
        sb.append("     cmp rax, 0\n");
        sb.append("     jge .positive\n");
        sb.append("     ;; if negative, print minus sign and make number positive\n");
        sb.append(mov("rax", "1"));
        sb.append(mov("rdi", "1"));
        sb.append(mov("rsi", "minus_sign"));
        sb.append(mov("rdx", "1"));
        sb.append("     syscall\n");
        sb.append(mov("rax", "rbx"));
        sb.append("     neg rax\n");
        sb.append(".positive:\n");
        sb.append(mov("rdi", "buffer + 19"));
        sb.append(mov("byte [rdi]", "0xA"));
        sb.append("     sub rdi, 1\n");
        sb.append(".next_digit:\n");
        sb.append("     xor rdx, rdx\n");
        sb.append(mov("rcx", "10"));
        sb.append("     div rcx\n");
        sb.append("     add dl, '0'\n");
        sb.append(mov("[rdi]", "dl"));
        sb.append("     sub rdi, 1\n");
        sb.append("     test rax, rax\n");
        sb.append("     jnz .next_digit\n\n");
        sb.append("     ;;print\n");
        sb.append("     add rdi, 1\n");
        sb.append(mov("rsi", "rdi"));
        sb.append(mov("rdi", "1"));
        sb.append(mov("rdx", "buffer + 19"));
        sb.append("     sub rdx, rsi\n");
        sb.append(mov("rax", "1"));
        sb.append("     syscall\n");
        sb.append("     ;;/print\n\n");
        sb.append("     ret\n\n");
    }

    /**
     * It generates the assembly code to convert a string to an integer(atoi)
     *
     * @param sb
     *            the StringBuilder Object to append the assembly code
     */
    private void atoiAssemblyFunc(StringBuilder sb) {
        sb.append("atoi:\n");
        sb.append("     xor rax, rax\n");
        sb.append("     xor rcx, rcx\n");
        sb.append("     xor rdi, rdi\n");
        sb.append("     xor rbx, rbx\n");
        sb.append("     mov dl, byte [rsi]\n");
        sb.append("     cmp dl, '-'\n");
        sb.append("     jne .not_negative\n");
        sb.append("     inc rsi\n");
        sb.append("     mov rdi, 1\n");
        sb.append(".not_negative:\n");
        sb.append(".next_char:\n");
        sb.append("     add rbx, 1\n");
        sb.append(mov("cl", "byte [rsi]"));
        sb.append("     inc rsi\n");
        sb.append("     cmp cl, '0'\n");
        sb.append("     jl .done\n");
        sb.append("     cmp cl, '9'\n");
        sb.append("     jg .done\n");
        sb.append("     sub cl, '0'\n");
        sb.append("     imul rax, rax, 10\n");
        sb.append("     add rax, rcx\n");
        sb.append("     jmp .next_char\n\n");
        sb.append(".error:\n");
        sb.append(mov("rax", "-1"));
        sb.append("     ret\n\n");
        sb.append(".done:\n");
        sb.append("     cmp rbx, 1\n");
        sb.append("     je .error\n");
        sb.append("     test rdi, rdi\n");
        sb.append("     jz .not_negative_result\n");
        sb.append("     neg rax\n");
        sb.append(".not_negative_result:\n");
        sb.append("     ret\n\n");
    }

    /**
     * Generates the assembly code for an expr
     *
     * @param expr
     *            the expression to generate code for
     *
     * @return the generated assembly code
     */
    public String generateTerm(NodeTerm expr) throws TokenError {
        StringBuilder termSB = new StringBuilder();
        // Generate the term based on the type
        switch (expr) {
        case NodeIntLit nodeIntLit -> {
            String value = Long.toString(nodeIntLit.getIntLit().getValue());
            termSB.append("     ;;value\n");
            termSB.append(mov("rax", value));
            termSB.append(push("rax")).append("\n");
        }
        case NodeIdent nodeIdent -> {
            long offset = findOffset(nodeIdent, termSB);
            termSB.append(push("QWORD [rsp + " + offset + "]")).append("\n");
        }
        case NodeTermParen nodeTermParen -> termSB.append(generateExpression(nodeTermParen.getExprParen()));
        case null, default -> throw new IllegalArgumentException("Unknown term type in generator");
        }
        return termSB.toString();
    }

    /**
     * Finds the offset of a variable in the stack
     *
     * @param nodeIdent
     *            the identifier to find the offset for
     * @param sb
     *            the StringBuilder object to append the assembly code
     *
     * @return the offset of the variable in the stack
     *
     * @throws TokenError
     *             if the variable is not declared
     */
    private long findOffset(NodeIdent nodeIdent, StringBuilder sb) throws TokenError {
        if (!variables.containsKey(nodeIdent.getIdent().getName())) {
            throw new TokenError("Undeclared Identifier: " + nodeIdent.getIdent().getName(),
                    nodeIdent.getIdent().getLine(), nodeIdent.getIdent().getColumnStart(),
                    nodeIdent.getIdent().getColumnEnd());
        }
        sb.append("     ;;identifier\n");
        long offset = (stack_size - variables.get(nodeIdent.getIdent().getName()) - 1) * 8L;
        if (offset < 0) {
            throw new TokenError("Variable might not have been initialized: " + nodeIdent.getIdent().getName(),
                    nodeIdent.getIdent().getLine(), nodeIdent.getIdent().getColumnStart(),
                    nodeIdent.getIdent().getColumnEnd());
        }
        return offset;
    }

    /**
     * Generates the assembly code for an expression
     *
     * @param expr
     *            the expression to generate code for
     *
     * @return the generated assembly code
     */
    public String generateExpression(NodeExpression expr) throws TokenError {
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
     * Generates the assembly code for a binary expression(+,-,*,/,%,==,!=,>,\<,>=,\<=)
     *
     * @param bin_expr
     *            the binary expression to generate code for
     *
     * @return the generated assembly code
     */
    public String generateBinaryExpression(NodeBin bin_expr) throws TokenError {
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
     * increase stack location, used to store variables
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
     * reduces stack location, used to remove variables
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
     * Utility function to move a value to a register
     *
     * @param reg
     *            the register to move the value to
     * @param par
     *            the value to move
     *
     * @return an ASM string
     */
    private String mov(String reg, String par) {
        return "     mov " + reg + ", " + par + "\n";
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