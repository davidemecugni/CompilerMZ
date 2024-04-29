package org.compiler.peekers;

import org.compiler.token.tokens.CharLineColumn;
import org.compiler.token.tokens.TokenComment;

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
        cursor++;
        spaces--;
        updateLineAndColumnCount(cursor);
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
        /*
         * if (list.get(cursor) != comment_terminal.) { for (; cursor < list.size(); cursor++) { if
         * (Character.isWhitespace(list.get(cursor))) { spaces--; } if (list.get(cursor) == '\n') {
         * updateLineAndColumnCount(cursor); cursor++; break; } comment.append(list.get(cursor)); } } else { multiline =
         * true; cursor++; while (cursor < list.size() && list.get(cursor) != comment_terminal) { if
         * (Character.isWhitespace(list.get(cursor))) { spaces--; if (list.get(cursor) == '\n') {
         * updateLineAndColumnCount(cursor); } } comment.append(list.get(cursor)); cursor++; } if ((cursor + 2) >=
         * list.size() || list.get(cursor + 1) != comment_terminal) { throw new
         * NoSuchElementException("Multiline comment has not been closed at line " + line); } cursor += 2; } return new
         * TokenComment(comment.toString(), multiline);
         */
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

    public CharLineColumn next() {
        if (!hasNext()) {
            return null;
        }
        return getNextNonWhitespaceChar();
    }

    public CharLineColumn peek() {
        if (!hasNext()) {
            return null;
        }
        return new CharLineColumn(list.get(cursor), getLine(), getCurrentColumn() + 1);
    }

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

    private void updateLineAndColumnCount(int cursor) {
        line++;
        charsUpToLastNewline = cursor;
    }

    private int getCurrentColumn() {
        if (line > 1) {
            return cursor - charsUpToLastNewline - 1;
        }
        return cursor - charsUpToLastNewline;
    }

    public int getLine() {
        return line;
    }

    private CharLineColumn generateCharLineColumn(char c) {
        return new CharLineColumn(c, getLine(), getCurrentColumn());
    }

    private void checkForWhitespace() {
        if (Character.isWhitespace(list.get(cursor))) {
            spaces--;
            if (list.get(cursor) == '\n') {
                updateLineAndColumnCount(cursor);
            }
        }
    }
}