package org.compiler;

import org.compiler.nodes.Program;

/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private final Program m_program;
    public Generator(Program program){
        this.m_program = program;
    }

    public String generate(){
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n");
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, "); //TBD
        sb.append("     syscall");
        return sb.toString();
    }
}
