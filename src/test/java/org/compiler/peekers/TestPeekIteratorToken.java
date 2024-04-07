package org.compiler.peekers;

import org.compiler.token.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class TestPeekIteratorToken {

    @Test
    public void testPeekIteratorChar() {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType._exit));
        tokens.add(new Token(TokenType.int_lit, "42"));
        tokens.add(new Token(TokenType.semi));

        PeekIteratorToken pic = new PeekIteratorToken(tokens.iterator());
        assertTrue(pic.hasNext());
        assertEquals(pic.peek(), new Token(TokenType._exit));
        assertEquals(pic.next(), new Token(TokenType._exit));
        assertTrue(pic.hasNext());
        assertEquals(pic.peek(), new Token(TokenType.int_lit, "42"));
        assertEquals(pic.next(), new Token(TokenType.int_lit, "42"));
        assertTrue(pic.hasNext());
        assertEquals(pic.peek(), new Token(TokenType.semi));
        assertEquals(pic.next(), new Token(TokenType.semi));
        assertFalse(pic.hasNext());
    }
}