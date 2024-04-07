package org.compiler;
import org.compiler.nodes.Exit;
public class Generator {
    private final Exit m_root;
    public Generator(Exit exit){
        this.m_root = exit;
    }

    public String generate(){
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\n_start:\n");
        sb.append("     mov rax, SYS_EXIT\n");
        sb.append("     mov rdi, ").append(m_root.expr.getInt_literal().toString()).append("\n");
        sb.append("     syscall");
        return sb.toString();
    }
}
