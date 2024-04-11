package org.compiler;

import org.compiler.token.Tokenizer;
import org.junit.jupiter.api.Test;

public class TestGenerator {
    @Test
    public void testGenerator() {
        Tokenizer tokenizer = new Tokenizer("exit 42;");
        Parser parser = new Parser(tokenizer.getTokens());
        Generator generator = new Generator(parser.getTree());
        String res = generator.getGenerated();
        System.out.println(res);



    }
}
