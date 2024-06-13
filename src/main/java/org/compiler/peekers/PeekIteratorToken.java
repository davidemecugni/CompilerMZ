package org.compiler.peekers;

import org.compiler.token.tokens.Token;

import java.util.List;

/**
 * PeekIteratorToken is a class that implements PeekIterator for Token objects. It is used to iterate over a list of
 * Token objects and peek at the next Token
 */

public class PeekIteratorToken implements PeekIterator<Token> {
    private final List<Token> list;
    private int cursor;

    public PeekIteratorToken(List<Token> list) {
        this.list = list;
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < list.size();
    }

    /**
     * Returns the next Token consuming it
     *
     * @return the next Token
     */
    @Override
    public Token next() {
        if (!hasNext()) {
            return null;
        }
        return list.get(cursor++);
    }

    /**
     * Peeks at the next Token
     *
     * @return the peeked Token
     */
    @Override
    public Token peek() {
        if (!hasNext()) {
            return null;
        }
        return list.get(cursor);
    }

    /**
     * Peeks at the next Token with an offset
     *
     * @param offset
     *            the offset
     *
     * @return the peeked Token with the offset
     */
    @Override
    public Token peek(int offset) {
        if (cursor + offset >= list.size()) {
            return null;
        }
        return list.get(cursor + offset);
    }

    /**
     * Peeks at the previous Token
     *
     * @return the previous Token
     */
    public Token peekPrevious() {
        return peekPrevious(1);
    }

    /**
     * Peeks at the previous Token with an offset
     *
     * @param offset
     *            the offset to peek at
     *
     * @return the previous Token with the offset
     */
    public Token peekPrevious(int offset) {
        if (cursor - offset < 0) {
            return null;
        }
        return list.get(cursor - offset);
    }

    @Override
    public String toString() {
        return "PeekIteratorToken{" + "list=" + list + ", cursor=" + cursor + '}';
    }
}
