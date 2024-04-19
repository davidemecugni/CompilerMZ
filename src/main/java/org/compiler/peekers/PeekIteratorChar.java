package org.compiler.peekers;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The implementation of PeekIterator for characters
 *
 * @see PeekIterator
 */
public class PeekIteratorChar implements PeekIterator<Character> {
    private final List<Character> list;
    private int cursor;
    private int spaces;

    public PeekIteratorChar(String s) {
        this.list = s.chars().mapToObj(e -> (char) e).toList();
        this.cursor = 0;
        this.spaces = countSpaces(s);
    }

    public boolean hasNext() {
        return cursor + spaces < list.size();
    }

    /**
     * Ignores the comment in the input [at] for a single line comment [at][at] for a multiline comment closed by
     * [at][at]
     *
     * @throws NoSuchElementException
     *             if the comment is not closed
     */
    public void ignoreComment(char comment_terminal) {
        if (cursor >= list.size()) {
            return;
        }
        if (list.get(cursor) != comment_terminal) {
            for (; cursor < list.size(); cursor++) {
                if (Character.isWhitespace(list.get(cursor))) {
                    spaces--;
                }
                if (list.get(cursor) == '\n') {
                    cursor++;
                    break;
                }
            }
        } else {
            cursor++;
            while (cursor < list.size() && list.get(cursor) != comment_terminal) {
                if (Character.isWhitespace(list.get(cursor))) {
                    spaces--;
                }
                cursor++;
            }
            if ((cursor + 2) >= list.size() || list.get(cursor + 1) != comment_terminal) {
                throw new NoSuchElementException("Multiline comment has not been closed");
            }
            cursor += 2;
        }
    }

    /**
     * Returns the next non-whitespace character
     *
     * @return the next non-whitespace character
     */
    private Character getNextNonWhitespaceChar() {
        while (cursor + spaces < list.size() && Character.isWhitespace(list.get(cursor))) {
            spaces--;
            cursor++;
        }
        if (cursor >= list.size()) {
            return null;
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

    private static int countSpaces(String str) {
        return (int) str.chars().filter(Character::isWhitespace).count();
    }
}