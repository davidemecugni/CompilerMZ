package org.compiler.peekers;

import org.compiler.token.Token;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PeekIteratorToken implements PeekIterator<Token>{
    private final Iterator<Token> iterator;
    private boolean peeked = false;
    private Token peeked_value = null;

    public PeekIteratorToken(Iterator<Token> iterator) {
        this.iterator = iterator;
        if(iterator.hasNext()){
            peeked_value = iterator.next();
            peeked = true;
        }
    }

    @Override
    public boolean hasNext() {
        return peeked;
    }
    @Override
    public Token next(){
        if(!peeked){
            throw new NoSuchElementException();
        }
        Token value = peeked_value;
        if(iterator.hasNext()){
            peeked_value = iterator.next();
        }
        else{
            peeked = false;
        }
        return value;
    }

    @Override
    public Token peek() {
        if(!peeked){
            throw new NoSuchElementException();
        }
        return peeked_value;
    }
}
