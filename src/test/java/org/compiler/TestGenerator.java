package org.compiler;

import org.compiler.errors.TokenError;
import org.compiler.token.Tokenizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestGenerator {
    @Test
    public void testGeneratorEmpty() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("");
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String res = generator.getGenerated();
        assertEquals("""
                section .data
                     minus_sign db '-'
                     buffer db 20 dup(0)
                     newline db 0x0a

                section .text
                     global main

                main:
                     ;;final exit
                     mov rax, 60
                     mov rdi, 0
                     syscall

                """, res);

    }

    @Test
    public void testGeneratorExit() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("exit(42);");
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String res = generator.getGenerated();
        assertEquals("""
                section .data
                     minus_sign db '-'
                     buffer db 20 dup(0)
                     newline db 0x0a

                section .text
                     global main

                main:
                     ;;value
                     mov rax, 42
                     push rax

                     ;;exit
                     mov rax, 60
                     pop rdi
                     syscall
                     ;;/exit

                     ;;final exit
                     mov rax, 60
                     mov rdi, 0
                     syscall

                """, res);
        tokenizer = new Tokenizer("exit(0); exit(255);");
        parser = new Parser(tokenizer.getTokens());
        generator = new Generator(parser.getTree());
        res = generator.getGenerated();
        assertEquals("""
                section .data
                     minus_sign db '-'
                     buffer db 20 dup(0)
                     newline db 0x0a

                section .text
                     global main

                main:
                     ;;value
                     mov rax, 0
                     push rax

                     ;;exit
                     mov rax, 60
                     pop rdi
                     syscall
                     ;;/exit

                     ;;value
                     mov rax, 255
                     push rax

                     ;;exit
                     mov rax, 60
                     pop rdi
                     syscall
                     ;;/exit

                     ;;final exit
                     mov rax, 60
                     mov rdi, 0
                     syscall

                """, res);
    }

    @Test
    public void testGeneratorLet() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("let x = 42;");
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String res = generator.getGenerated();
        assertEquals("""
                section .data
                     minus_sign db '-'
                     buffer db 20 dup(0)
                     newline db 0x0a

                section .text
                     global main

                main:
                     ;;value
                     mov rax, 42
                     push rax

                     ;;final exit
                     mov rax, 60
                     mov rdi, 0
                     syscall

                """, res);
        tokenizer = new Tokenizer("let x = 42; let y = 255;");
        parser = new Parser(tokenizer.getTokens());
        generator = new Generator(parser.getTree());
        res = generator.getGenerated();
        assertEquals("""
                section .data
                     minus_sign db '-'
                     buffer db 20 dup(0)
                     newline db 0x0a

                section .text
                     global main

                main:
                     ;;value
                     mov rax, 42
                     push rax

                     ;;value
                     mov rax, 255
                     push rax

                     ;;final exit
                     mov rax, 60
                     mov rdi, 0
                     syscall

                """, res);
        tokenizer = new Tokenizer("let x = 42; let y = x;");
        parser = new Parser(tokenizer.getTokens());
        generator = new Generator(parser.getTree());
        res = generator.getGenerated();
        assertEquals("""
                section .data
                     minus_sign db '-'
                     buffer db 20 dup(0)
                     newline db 0x0a

                section .text
                     global main

                main:
                     ;;value
                     mov rax, 42
                     push rax

                     ;;identifier
                     push QWORD [rsp + 0]

                     ;;final exit
                     mov rax, 60
                     mov rdi, 0
                     syscall

                """, res);
        tokenizer = new Tokenizer("let x = 42; let y = 255; let x = 0;");
        parser = new Parser(tokenizer.getTokens());
        Parser finalParser = parser;
        assertThrows(IllegalArgumentException.class, () -> new Generator(finalParser.getTree()));
    }

    @Test
    public void testGeneratorIdentity() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("exit(x);");
        Parser parser = new Parser(tokenizer.getTokens());
        Parser finalParser = parser;
        assertThrows(IllegalArgumentException.class, () -> new Generator(finalParser.getTree()));

        tokenizer = new Tokenizer("let x = x;");
        parser = new Parser(tokenizer.getTokens());
        Parser finalParser1 = parser;
        assertThrows(IllegalArgumentException.class, () -> new Generator(finalParser1.getTree()));

        tokenizer = new Tokenizer("let x = 42; let variable = variable;");
        parser = new Parser(tokenizer.getTokens());
        Parser finalParser2 = parser;
        assertThrows(IllegalArgumentException.class, () -> new Generator(finalParser2.getTree()));
    }
}
