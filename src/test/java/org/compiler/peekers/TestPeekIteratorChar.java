package org.compiler.peekers;

import org.compiler.token.tokens.CharLineColumn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPeekIteratorChar {

    @Test
    public void testPeekIteratorCharSingleLine() {
        String s = "hello world!";
        PeekIteratorChar peek = new PeekIteratorChar(s);
        assertEquals(new CharLineColumn('h', 1, 1), peek.peek());
        assertEquals(new CharLineColumn('o', 1, 5), peek.peek(4));
        assertTrue(peek.hasNext());
        assertEquals(new CharLineColumn('h', 1, 1), peek.peek());
        int col = 1;
        for (char c : new char[] { 'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd', '!' }) {
            assertEquals(new CharLineColumn(c, 1, col), peek.next());
            if (col == 5) {
                col++;
            }
            col++;
        }
        assertFalse(peek.hasNext());
        assertNull(peek.peek());
        assertNull(peek.next());
    }

    @Test
    public void testPeekIteratorCharWithLineCol() {
        String s = "hello\nworld!\nletx=10;";
        PeekIteratorChar peek = new PeekIteratorChar(s);
        assertEquals(new CharLineColumn('h', 1, 1), peek.peek());
        assertEquals(new CharLineColumn('o', 1, 5), peek.peek(4));
        assertTrue(peek.hasNext());
        int col = 1;
        int line = 1;
        int pointer = 1;
        for (char c : new char[] { 'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd', '!', 'l', 'e', 't', 'x', '=', '1',
                '0', ';' }) {
            if (c == 'h') {
                // l is at offset 13 in the string
                assertEquals(new CharLineColumn('l', 3, 1), peek.peek(13));
                assertEquals(new CharLineColumn('e', 3, 2), peek.peek(14));
                assertEquals(new CharLineColumn('t', 3, 3), peek.peek(15));
                assertEquals(new CharLineColumn('e', 1, 2), peek.peek(1));
            }

            if (c == 'x') {
                assertEquals(new CharLineColumn('x', 3, 4), peek.peek());
                assertEquals(new CharLineColumn('=', 3, 5), peek.peek(1));
            }

            assertEquals(new CharLineColumn(c, line, col), peek.next());
            if (pointer == 5 || c == '!') {
                line++;
                col = 1;
                pointer++;
                continue;
            }

            pointer++;
            col++;
        }
    }
}
