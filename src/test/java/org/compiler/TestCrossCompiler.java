package org.compiler;

import org.compiler.errors.TokenError;
import org.compiler.token.Tokenizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCrossCompiler {
    @Test
    public void testCrossCompilerFromDefaultToEmilian() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("let x = 5 ;\nexit(x);");
        CrossCompiler crossCompiler = new CrossCompiler(tokenizer.getTokens(), "emilian");
        String crossCompiledCode = crossCompiler.getCrossCompiledCode();
        assertEquals("métter x cumpàagn 5 ;\ndesmàtter ( x ) ;\n", crossCompiledCode);
    }

    @Test
    public void testCrossCompilerFromEmilianToDefault() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("""
                {
                    métter x cumpàagn 5 ;
                    fintàant ( x < 10) {
                        x cumpàagn x + 1 ;
                    }
                    desmàtter ( x ) ;
                }
                """, "emilian");
        CrossCompiler crossCompiler = new CrossCompiler(tokenizer.getTokens(), "default_dialect");
        String crossCompiledCode = crossCompiler.getCrossCompiledCode();
        assertEquals("""
                {
                    let x = 5 ;
                    while ( x < 10 ) {
                        x = x + 1 ;
                    }
                    exit ( x ) ;
                }
                """, crossCompiledCode);
    }

    @Test
    public void testCrossCompilerFromDefaultToEmilianWithComments() throws TokenError {
        Tokenizer tokenizer = new Tokenizer("""
                @ this is a comment
                let x = 5 ;
                @ this is another comment
                while(x<10){
                    x=x+1;
                    @@
                    Adding
                    @@
                }
                exit(x);
                """, "default_dialect", false);
        CrossCompiler crossCompiler = new CrossCompiler(tokenizer.getTokens(), "emilian");
        String crossCompiledCode = crossCompiler.getCrossCompiledCode();
        assertEquals("""
                comèint this is a comment
                métter x cumpàagn 5 ;
                comèint this is another comment
                fintàant ( x lé_più_cicch 10 ) {
                    x cumpàagn x più 1 ;
                    comèintcomèint
                    Adding
                    comèintcomèint
                }
                desmàtter ( x ) ;
                """, crossCompiledCode);
    }
}
