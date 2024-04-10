package org.compiler.peekers;

import org.compiler.token.*;
import org.compiler.token.tokens.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestPeekIteratorToken {

    @Test
    public void testPeekIteratorChar() {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType._exit));
        tokens.add(new TokenIntLit("42"));
        tokens.add(new Token(TokenType.semi));

        PeekIteratorToken peek = new PeekIteratorToken(tokens);
        assertTrue(peek.hasNext());
        assertEquals(peek.peek(), new Token(TokenType._exit));
        assertEquals(peek.next(), new Token(TokenType._exit));
        assertTrue(peek.hasNext());
        assertEquals(peek.peek(), new TokenIntLit("42"));
        assertEquals(peek.next(), new TokenIntLit("42"));
        assertTrue(peek.hasNext());
        assertEquals(peek.peek(), new Token(TokenType.semi));
        assertEquals(peek.next(), new Token(TokenType.semi));
        assertFalse(peek.hasNext());
        assertNull(peek.peek());
        assertNull(peek.next());
    }
}