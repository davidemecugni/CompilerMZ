package org.compiler;

import java.util.Iterator;

public interface PeekIterator<E> extends Iterator<E> {
    boolean hasNext();
    E peek();
    E next();
}
