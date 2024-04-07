package org.compiler;

import java.util.Iterator;

/**
 * An Iterator with a peek method for just one value.
 */
public class PeekIterator<Character> implements Iterator<Character>
{
    private final Iterator<Character> iterator;

    public PeekIterator (Iterator<Character> iterator) { this.iterator = iterator; }

    private boolean peeked = false;
    private Character peeked_value = null;

    public boolean hasNext () { return iterator.hasNext () || peeked; }

    public Character next ()
    {
        Character value;
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

    public Character peek ()
    {
        Character value;
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