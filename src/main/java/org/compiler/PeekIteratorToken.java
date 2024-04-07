package org.compiler;

import java.util.Iterator;

public class PeekIteratorToken implements PeekIterator<Token>{
    private final Iterator<Token> iterator;
    private boolean peeked = false;
    private Token peeked_value = null;

    public PeekIteratorToken(Iterator<Token> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() || peeked;
    }
    @Override
    public Token next(){
        if(!peeked){
            peeked = true;
            peeked_value = getNext();
        }
        return peeked_value;
    }

    @Override
    public Token peek() {
        if(!peeked){
            peeked = true;
            peeked_value = getNext();
        }
        return peeked_value;
    }

    private Token getNext(){
        Token value = null;
        if(iterator.hasNext()){
            value = iterator.next();
        }
        return value;
    }

}
