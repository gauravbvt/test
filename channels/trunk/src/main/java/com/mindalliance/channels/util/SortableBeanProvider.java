package com.mindalliance.channels.util;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

import com.mindalliance.channels.util.PropertyComparator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 1:06:07 PM
 */
public class SortableBeanProvider<T extends Serializable> extends SortableDataProvider<T> {
    /**
     * List from which to provide
     */
    private List<T> list;

    public SortableBeanProvider( List<T> list, String sortProperty ) {
        this.list = list;
        setSort( sortProperty, true );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<T> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<T> sortedList = new ArrayList<T>();
        sortedList.addAll( list );
        Collections.sort( sortedList, new PropertyComparator<T>( sortParam ) );
        return sortedList.subList( first, first + count ).iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    public IModel<T> model( T object ) {
        return new Model<T>( object );
    }
}
