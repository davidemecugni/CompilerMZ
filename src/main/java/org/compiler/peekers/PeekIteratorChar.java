package org.compiler.peekers;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PeekIteratorChar implements PeekIterator<Character> {
    private final Iterator<Character> iterator;
    private boolean peeked = false;
    private Character peeked_value = null;

    public PeekIteratorChar(Iterator<Character> iterator) {
        this.iterator = iterator;
        if (iterator.hasNext()) {
            peeked_value = iterator.next();
            peeked = true;
        }
    }

    public boolean hasNext (){
        return peeked;
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
        if (!peeked) {
            throw new NoSuchElementException();
        }
        Character value = peeked_value;
        if(iterator.hasNext()){
            peeked_value = getNextNonWhitespaceChar();
        }
        else{
            peeked = false;
        }
        return value;
    }

    public Character peek() {
        if (!peeked) {
            throw new NoSuchElementException();
        }
        return peeked_value;
    }
}