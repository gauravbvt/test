package com.mindalliance.channels.core.util;

import java.util.Iterator;

/**
 * Peek ahead iterator.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 8, 2010
 * Time: 2:35:52 PM
 */
public class PeekAheadIterator<T> implements Iterator<T> {

    private Iterator<T> iterator;
    private T next = null;
    private T peek = null;

    public PeekAheadIterator( Iterator<T> iterator ) {
        this.iterator = iterator;
        peekAhead();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return next != null;
    }

    /**
     * {@inheritDoc}
     */
    public T next() {
        T result;
        if ( next != null ) {
            result = next;
            peekAhead();
        } else {
            throw new RuntimeException( "Iterator at end" );
        }
        return result;
    }

    /**
     * Show after next element if any, without iterating.
     *
     * @return a T or null;
     */
    public T peek() {
        return peek;
    }

    private void peekAhead() {
        // set next
        next = null;
        if ( peek != null ) {
            next = peek;
            peek = null;
        } else {
            if ( iterator.hasNext() ) next = iterator.next();
        }
        // set peek
        if ( iterator.hasNext() ) peek = iterator.next();
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
