package org.compiler;

import java.util.Iterator;

public class PeekIteratorChar implements PeekIterator<Character> {
    private final Iterator<Character> iterator;
    private boolean peeked = false;
    private Character peeked_value = null;

    public PeekIteratorChar(Iterator<Character> iterator) { this.iterator = iterator; }

    public boolean hasNext () {
        return iterator.hasNext() || peeked;
    }

    private Character getNextNonWhitespaceChar() {
        Character value = null;
        if (iterator.hasNext()) {
            value = iterator.next();
            while (value != null && Character.isWhitespace(value)) {
                if (iterator.hasNext()) {
                    value = iterator.next();
                } else {
                    value = null;
                }
            }
        }
        return value;
    }

    public Character next() {
        if (peeked) {
            peeked = false;
            return peeked_value;
        } else {
            return getNextNonWhitespaceChar();
        }
    }

    public Character peek() {
        if (!peeked) {
            peeked = true;
            peeked_value = getNextNonWhitespaceChar();
        }
        return peeked_value;
    }
}