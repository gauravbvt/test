package com.mindalliance.channels.db.data;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.services.PageableDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/18/14
 * Time: 3:08 PM
 */
public class PagedDataPeekableIterator<T extends ChannelsDocument> implements Iterator<T> {

    private static final int DEFAULT_PAGE_SIZE = 30;
    private Page<T> page;
    private T next = null;
    private T peek = null;
    private Iterator<T> iterator;
    private PageableDataService<T> pageableDataService;
    private Map<String, Object> params;
    private int pageSize;
    private Sort sort;
    private PlanCommunity planCommunity;
    private int pageNumber = 0;

    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      PlanCommunity planCommunity ) {
        this( pageableDataService, DEFAULT_PAGE_SIZE, planCommunity );
    }


    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      Sort sort,
                                      PlanCommunity planCommunity ) {
        this( pageableDataService, null, DEFAULT_PAGE_SIZE, sort, planCommunity );
    }


    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      int pageSize,
                                      PlanCommunity planCommunity ) {
        this( pageableDataService, null, pageSize, null, planCommunity );
    }

    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      Map<String, Object> params,
                                      PlanCommunity planCommunity ) {
        this( pageableDataService, params, DEFAULT_PAGE_SIZE, planCommunity );
    }


    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      Map<String,Object> params,
                                      Sort sort,
                                      PlanCommunity planCommunity ) {
        this( pageableDataService, params, DEFAULT_PAGE_SIZE, sort, planCommunity );
    }


    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      Map<String,Object> params,
                                      int pageSize,
                                      PlanCommunity planCommunity ) {
        this( pageableDataService, params, pageSize, null, planCommunity );
    }


    public PagedDataPeekableIterator( PageableDataService<T> pageableDataService,
                                      Map<String, Object> params,
                                      int pageSize,
                                      Sort sort,
                                      PlanCommunity planCommunity ) {
        this.pageableDataService = pageableDataService;
        this.params = params;
        this.pageSize = pageSize;
        this.sort = sort;
        this.planCommunity = planCommunity;
        initialize();
    }


    private void initialize() {
        loadNextPage();
        peekAhead();
    }

    private void peekAhead() {
        next = null;
        if ( peek != null ) {
            next = peek;
            peek = null;
        } else {
            if ( hasNextPagedElement() )
                next = nextPagedElement();
        }
        // set peek
        if ( hasNextPagedElement() )
            peek = nextPagedElement();
    }

    private T nextPagedElement() {
        if ( iterator.hasNext() ) {
            return iterator.next();
        } else if ( page.hasNextPage() ) {
            loadNextPage();
            return iterator.next();
        } else {
            return null;
        }
    }

    private void loadNextPage() {
        PageRequest pageRequest = new PageRequest( pageNumber, pageSize, sort );
        page = params == null
                ? pageableDataService.loadPage( pageRequest, planCommunity )
                : pageableDataService.loadPage( pageRequest, params, planCommunity );
        iterator = page.iterator();
        pageNumber = pageNumber + 1;
    }

    private boolean hasNextPagedElement() {
        return iterator.hasNext() || page.hasNextPage();
    }

    /**
     * Show after next element if any, without iterating.
     *
     * @return a T or null;
     */
    public T peek() {
        return peek;
    }


    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
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

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
