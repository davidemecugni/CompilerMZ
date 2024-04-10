package org.compiler.peekers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPeekIteratorChar {

    @Test
    public void testPeekIteratorChar() {
        String s = "hello world!";
        PeekIteratorChar peek = new PeekIteratorChar(s);
        assertEquals('h', peek.peek());
        assertEquals('o', peek.peek(4));
        assertTrue(peek.hasNext());
        assertEquals(peek.peek(), 'h');
        for (char c : new char[] { 'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd', '!' }) {
            assertEquals(peek.next(), c);
        }
        assertFalse(peek.hasNext());
        assertNull(peek.peek());
        assertNull(peek.next());
    }
}
