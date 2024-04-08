package org.compiler.peekers;

import org.compiler.token.Token;

import java.util.List;
import java.util.NoSuchElementException;

public class PeekIteratorToken implements PeekIterator<Token>{
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
    public Token next(){
        if(!hasNext()){
            throw new NoSuchElementException("No next element");
        }
        return list.get(cursor++);
    }

    @Override
    public Token peek() {
        if(!hasNext()){
            throw new NoSuchElementException("No peekable element");
        }
        return list.get(cursor);
    }

    @Override
    public Token peek(int offset){
        if(cursor + offset >= list.size()){
            throw new NoSuchElementException("Offset is too large");
        }
        return list.get(cursor + offset);
    }

}
