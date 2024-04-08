package org.compiler.peekers;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The implementation of PeekIterator for characters
 * @see PeekIterator
 */
public class PeekIteratorChar implements PeekIterator<Character>{
    private final List<Character> list;
    private int cursor;

    public PeekIteratorChar(String s) {
        this.list = s.chars().mapToObj(e -> (char) e).toList();
        this.cursor = 0;
    }

    public boolean hasNext (){
        int non_whitespace_cursor = cursor;
        while (non_whitespace_cursor < list.size() && Character.isWhitespace(list.get(non_whitespace_cursor))) {
            non_whitespace_cursor++;
        }
        return non_whitespace_cursor < list.size();
    }
    /**
     * Returns the next non-whitespace character
     * @return the next non-whitespace character
     */
    private Character getNextNonWhitespaceChar() {
        while (cursor < list.size() && Character.isWhitespace(list.get(cursor))) {
            cursor++;
        }
        return list.get(cursor++);
    }


    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }
        return getNextNonWhitespaceChar();
    }

    public Character peek() {
        if (!hasNext()) {
            throw new NoSuchElementException("No peekable element");
        }
        return list.get(cursor);
    }

    public Character peek(int offset) {
        if (cursor + offset >= list.size()) {
            throw new NoSuchElementException("Offset is too large");
        }
        return list.get(cursor + offset);
    }
}