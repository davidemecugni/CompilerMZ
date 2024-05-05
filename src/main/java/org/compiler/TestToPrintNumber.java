package org.compiler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestToPrintNumber {

    public static void main(String[] args) {
        int numberToPrint = 42;

        String assemblyCode = generateAssemblyCode(numberToPrint);

        // Write the generated assembly code to a file
        try (PrintWriter writer = new PrintWriter("output.asm")) {
            writer.println(assemblyCode);
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        System.out.println("Assembly code generated successfully.");
    }

    public static String generateAssemblyCode(int numberToPrint) {
        StringBuilder assemblyCode = new StringBuilder();

        // Assembly code to call printf for printing an integer
        assemblyCode.append("section .data\n");
        assemblyCode.append("    format db \"%d\", 0\n"); // Format string for integer
        assemblyCode.append("    number dq ").append(numberToPrint).append("\n"); // Integer to print
        assemblyCode.append("section .text\n");
        assemblyCode.append("    extern printf\n");
        assemblyCode.append("    global _start\n");
        assemblyCode.append("_start:\n");
        assemblyCode.append("    mov rdi, format\n");
        assemblyCode.append("    mov rsi, qword [number]\n"); // Load the integer into rsi
        assemblyCode.append("    xor rax, rax\n"); // Clear RAX for variadic call
        assemblyCode.append("    call printf\n");
        assemblyCode.append("    mov rax, 60\n"); // syscall for exit
        assemblyCode.append("    xor rdi, rdi\n"); // return 0 status
        assemblyCode.append("    syscall\n");

        return assemblyCode.toString();
    }
}
