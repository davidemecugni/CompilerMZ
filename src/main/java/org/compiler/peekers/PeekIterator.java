package org.compiler.peekers;

import java.util.Iterator;

/**
 * An iterator that allows peeking at the next element
 *
 * @param <E>
 *            the type of elements in the iterator
 *
 * @see PeekIteratorChar
 * @see PeekIteratorToken
 */
public interface PeekIterator<E> extends Iterator<E> {
    /**
     * Returns if the iterator has a next element
     *
     * @return if the iterator has a next element
     */
    boolean hasNext();

    /**
     * Returns the next element without moving the iterator
     *
     * @return the next element
     */
    E peek();

    /**
     * Returns the next element with an offset without moving the iterator
     *
     * @param offset
     *            the offset
     *
     * @return the next element
     */
    E peek(int offset);

    /**
     * Returns the next element and moves the iterator
     *
     * @return the next element
     */
    E next();

    E peekPrevious();

    E peekPrevious(int offset);
}
