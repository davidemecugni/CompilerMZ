package org.compiler.peekers;

import org.compiler.token.tokens.Token;

import java.util.List;

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

    @Override
    public Token next() {
        if (!hasNext()) {
            return null;
        }
        return list.get(cursor++);
    }

    @Override
    public Token peek() {
        if (!hasNext()) {
            return null;
        }
        return list.get(cursor);
    }

    @Override
    public Token peek(int offset) {
        if (cursor + offset >= list.size()) {
            return null;
        }
        return list.get(cursor + offset);
    }

    public Token peekPrevious() {
        return peekPrevious(1);
    }

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
