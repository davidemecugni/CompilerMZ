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
        assertEquals("global _start\n_start:\n\n     ;;final exit\n     mov rax, 60\n     mov rdi, 0\n     syscall\n",
                res);

    }

    @Test
    public void testGeneratorExit() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("exit(42);");
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String res = generator.getGenerated();
        assertEquals(
                "global _start\n_start:\n\n     ;;value\n     mov rax, 42\n     push rax\n\n     ;;exit\n     mov rax, 60\n     pop rdi\n     syscall\n     ;;/exit\n\n     ;;final exit\n     mov rax, 60\n     mov rdi, 0\n     syscall\n",
                res);
        tokenizer = new Tokenizer("exit(0); exit(255);");
        parser = new Parser(tokenizer.getTokens());
        generator = new Generator(parser.getTree());
        res = generator.getGenerated();
        assertEquals(
                "global _start\n_start:\n\n     ;;value\n     mov rax, 0\n     push rax\n\n     ;;exit\n     mov rax, 60\n     pop rdi\n     syscall\n     ;;/exit\n\n     ;;value\n     mov rax, 255\n     push rax\n\n     ;;exit\n     mov rax, 60\n     pop rdi\n     syscall\n     ;;/exit\n\n     ;;final exit\n     mov rax, 60\n     mov rdi, 0\n     syscall\n",
                res);
    }

    @Test
    public void testGeneratorLet() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("let x = 42;");
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String res = generator.getGenerated();
        assertEquals(
                "global _start\n_start:\n\n     ;;value\n     mov rax, 42\n     push rax\n\n     ;;final exit\n     mov rax, 60\n     mov rdi, 0\n     syscall\n",
                res);
        tokenizer = new Tokenizer("let x = 42; let y = 255;");
        parser = new Parser(tokenizer.getTokens());
        generator = new Generator(parser.getTree());
        res = generator.getGenerated();
        assertEquals(
                "global _start\n_start:\n\n     ;;value\n     mov rax, 42\n     push rax\n\n     ;;value\n     mov rax, 255\n     push rax\n\n     ;;final exit\n     mov rax, 60\n     mov rdi, 0\n     syscall\n",
                res);
        tokenizer = new Tokenizer("let x = 42; let y = x;");
        parser = new Parser(tokenizer.getTokens());
        generator = new Generator(parser.getTree());
        res = generator.getGenerated();
        assertEquals(
                "global _start\n_start:\n\n     ;;value\n     mov rax, 42\n     push rax\n\n     ;;identifier\n     push QWORD [rsp + 0]\n\n     ;;final exit\n     mov rax, 60\n     mov rdi, 0\n     syscall\n",
                res);
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
