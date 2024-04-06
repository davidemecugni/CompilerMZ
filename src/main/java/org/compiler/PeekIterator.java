package org.compiler;

import java.util.Iterator;

/**
 * An Iterator with a peek method for just one value.
 */
public class PeekIterator<T> implements Iterator<T>
{
    private final Iterator<T> iterator;

    public PeekIterator (Iterator<T> iterator) { this.iterator = iterator; }

    private boolean peeked = false;
    private T peeked_value = null;

    public boolean hasNext () { return iterator.hasNext () || peeked; }

    public T next ()
    {
        T value;
        if (peeked) {
            peeked = false;
            value = peeked_value;
        }
        else
        if (iterator.hasNext ())
            value = iterator.next();
        else
            value = null;
        return value;
    }

    public T peek ()
    {
        T value;
        if (peeked)
            value = peeked_value;
        else {
            peeked = true;
            if (iterator.hasNext ())
                peeked_value = iterator.next ();
            else
                peeked_value = null;
            value = peeked_value;
        }
        return value;
    }
}