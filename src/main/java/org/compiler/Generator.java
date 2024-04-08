package org.compiler;
import org.compiler.nodes.Exit;

/**
 * Generates a string representation of the assembly code
 */
public class Generator {
    private final Exit m_root;
    public Generator(Exit exit){
        this.m_root = exit;
    }

    public String generate(){
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n");
        sb.append("     mov rax, 60\n");
        sb.append("     mov rdi, ").append(m_root.expr.int_literal().getValue()).append("\n");
        sb.append("     syscall");
        return sb.toString();
    }
}
