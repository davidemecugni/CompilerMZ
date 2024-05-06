package org.compiler.peekers;

import org.compiler.token.tokens.CharLineColumn;
import org.compiler.token.tokens.TokenComment;
import org.compiler.token.tokens.TokenString;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The implementation of PeekIterator for characters
 *
 * @see PeekIterator
 */
public class PeekIteratorChar implements PeekIterator<CharLineColumn> {
    private final List<Character> list;
    private int cursor;
    private int spaces;
    private int line;
    private int charsUpToLastNewline;

    public PeekIteratorChar(String s) {
        this.list = s.chars().mapToObj(e -> (char) e).toList();
        this.cursor = 0;
        this.spaces = countSpaces(s);
        this.line = 1;
        this.charsUpToLastNewline = 0;
    }

    /**
     *
     * @return if the iterator has a next element
     */
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
    public TokenComment ignoreComment(String comment_terminal) {
        StringBuilder comment = new StringBuilder();
        boolean multiline = false;
        if (cursor >= list.size()) {
            return null;
        }
        while (cursor < list.size() && list.get(cursor) != '\n') {
            comment.append(list.get(cursor));
            checkForWhitespace();
            cursor++;
        }
        updateLineAndColumnCount(cursor);
        cursor++;
        spaces--;
        if (comment.toString().contains(comment_terminal)) {
            multiline = true;
            while (cursor < list.size() && !comment.toString().contains(comment_terminal + comment_terminal)) {
                comment.append(list.get(cursor));
                checkForWhitespace();
                cursor++;
            }
            if (cursor >= list.size()) {
                throw new NoSuchElementException("Multiline comment has not been closed at line " + line);
            }
        }
        return new TokenComment(comment.toString().replace(comment_terminal, ""), multiline);
    }

    /**
     * Ignores the content in the input
     *
     * @param string_terminal
     *            the string
     *
     * @return the content
     */
    public TokenString ignoreContent(String string_terminal) {
        StringBuilder content = new StringBuilder();
        int column_start = getCurrentColumn() + 1;
        if (cursor >= list.size()) {
            return null;
        }
        while (cursor < list.size() && !content.toString().contains(string_terminal)) {
            content.append(list.get(cursor));
            if (list.get(cursor) == '\n') {
                throw new IllegalArgumentException("Multiline string not supported at line " + line);
            }
            checkForWhitespace();
            cursor++;
        }
        String res = content.toString().replace(string_terminal, "");

        return new TokenString(res, line, column_start, getCurrentColumn() - 1);
    }

    /**
     * Returns the next non-whitespace character
     *
     * @return the next non-whitespace character
     */
    private CharLineColumn getNextNonWhitespaceChar() {
        while (cursor + spaces < list.size() && Character.isWhitespace(list.get(cursor))) {
            spaces--;
            if (list.get(cursor) == '\n') {
                updateLineAndColumnCount(cursor);
            }
            cursor++;
        }
        if (cursor >= list.size()) {
            return null;
        }
        return generateCharLineColumn(list.get(cursor++));
    }

    /**
     * Returns the next element and moves the iterator
     *
     * @return the next element
     */
    public CharLineColumn next() {
        if (!hasNext()) {
            return null;
        }
        return getNextNonWhitespaceChar();
    }

    /**
     * Returns the next element without moving the iterator
     *
     * @return the next element
     */
    public CharLineColumn peek() {
        if (!hasNext()) {
            return null;
        }
        return new CharLineColumn(list.get(cursor), getLine(), getCurrentColumn() + 1);
    }

    /**
     * Returns the next element with an offset without moving the iterator
     *
     * @param offset
     *            the offset
     *
     * @return the next element
     */
    public CharLineColumn peek(int offset) {
        if (cursor + offset >= list.size()) {
            System.out.println("Cursor: " + cursor + " Offset: " + offset + " List size: " + list.size());
            return null;
        } else {
            int line_offset = (int) list.subList(cursor, cursor + offset).stream().filter(e -> e == '\n').count();
            int new_col;
            if (line_offset == 0) {
                new_col = getCurrentColumn() + offset + 1;
            } else {
                new_col = (cursor + offset) - list.subList(cursor, cursor + offset).lastIndexOf('\n');
            }
            return new CharLineColumn(list.get(cursor + offset), line + line_offset, new_col);
        }

    }

    private static int countSpaces(String str) {
        return (int) str.chars().filter(Character::isWhitespace).count();
    }

    /**
     * Return the current cursor
     */
    private void updateLineAndColumnCount(int cursor) {
        line++;
        charsUpToLastNewline = cursor;
    }

    /**
     * Return the current column
     */
    private int getCurrentColumn() {
        if (line > 1) {
            return cursor - charsUpToLastNewline - 1;
        }
        return cursor - charsUpToLastNewline;
    }

    /**
     * Return the current line
     */
    public int getLine() {
        return line;
    }

    /**
     * Generate a CharLineColumn object
     */
    private CharLineColumn generateCharLineColumn(char c) {
        return new CharLineColumn(c, getLine(), getCurrentColumn());
    }

    /**
     * Check for whitespace
     */
    private void checkForWhitespace() {
        if (Character.isWhitespace(list.get(cursor))) {
            spaces--;
            if (list.get(cursor) == '\n') {
                updateLineAndColumnCount(cursor);
            }
        }
    }
}