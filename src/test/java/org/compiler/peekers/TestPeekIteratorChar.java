package org.compiler.peekers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class TestPeekIteratorChar {

    @Test
    public void testPeekIteratorChar() {
        String s = "hello world!";
        PeekIteratorChar pic = new PeekIteratorChar(s.chars().mapToObj(c -> (char) c).iterator());
        assertTrue(pic.hasNext());
        assertEquals(pic.peek(), 'h');
        for (char c : new char[]{'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd', '!'}) {
            assertEquals(pic.next(), c);
        }
        assertFalse(pic.hasNext());
    }
}
