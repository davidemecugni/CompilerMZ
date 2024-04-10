package org.compiler;

import org.compiler.nodes.*;
import org.compiler.nodes.expressions.*;
import org.compiler.nodes.statements.*;


/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private final NodeProgram m_program;
    public Generator(NodeProgram program){
        this.m_program = program;
    }

    public String generateStatement(NodeStatement stmt){
        switch (stmt) {
            case NodeExit nodeExit -> {
                // Handle NodeExit type
            }
            case NodeLet nodeLet -> {
                // Handle NodeLet type
            }
            case null, default -> {
                throw new IllegalArgumentException("Unknown statement type in generator");
            }
        }
        return "";
    }

    public String generateExpression(NodeExpression expr){
        switch (expr) {
            case NodeIntLit nodeIntLit -> {
                // Handle NodeIdent type
            }
            case NodeIdent nodeIdent -> {
                // Handle NodeIdent type
            }
            case null, default -> {
                throw new IllegalArgumentException("Unknown expression type in generator");
            }
        }
        return "";
    }
    public String generateProgram(){
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n");
        for(NodeStatement statement : m_program.getStmts()){
            sb.append(generateStatement(statement));
        }
        //Exits 0 by default
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, 100\n");
        sb.append("     syscall");
        return sb.toString();
    }
}
