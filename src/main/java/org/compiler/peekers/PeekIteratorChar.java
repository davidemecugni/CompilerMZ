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
     * Ignores the comment in the input
     * [at] for a single line comment
     * [at][at] for a multiline comment closed by [at][at]
     * @throws NoSuchElementException if the comment is not closed
     */
    public void IgnoreComment() {
        if(cursor >= list.size()){
            return;
        }
        if(list.get(cursor) != '@'){
            for(; cursor < list.size(); cursor++){
                if(list.get(cursor) == '\n'){
                    cursor++;
                    break;
                }
            }
        }
        else {
            cursor++;
            while (cursor < list.size() && list.get(cursor) != '@') {
                cursor++;
            }
            if( (cursor + 2) >= list.size() || list.get(cursor + 1) != '@') {
                throw new NoSuchElementException("Multiline comment has not been closed");
            }
            cursor+=2;
        }
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
            return null;
        }
        return getNextNonWhitespaceChar();
    }

    public Character peek() {
        if (!hasNext()) {
            return null;
        }
        return list.get(cursor);
    }

    public Character peek(int offset) {
        if (cursor + offset >= list.size()) {
            return null;
        }
        return list.get(cursor + offset);
    }
}