package org.compiler.peekers;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
public class TestPeekIteratorChar {

    @Test
    public void testPeekIteratorChar() {
        String s = "hello world!";
        PeekIteratorChar peek = new PeekIteratorChar(s);
        assertTrue(peek.hasNext());
        assertEquals(peek.peek(), 'h');
        for (char c : new char[]{'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd', '!'}) {
            assertEquals(peek.next(), c);
        }
        assertFalse(peek.hasNext());
        assertThrows(NoSuchElementException.class, peek::next);
    }
}
