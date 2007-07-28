// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import java.util.Iterator;
import java.util.Map;

import com.mindalliance.channels.JavaBean;

/**
 * A queryable JavaBean.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @param <T> the type of the results
 */
public interface Queryable<T> extends JavaBean {

    /**
     * Execute the query in the context of itself with variable
     * bindings and return all resulting elements.
     *
     * @param query A Query
     * @param bindings Bindings for variables referenced in the query
     * @return An iterator on resulting elements
     */
    Iterator<T> findAll( Query query, Map bindings );

    /**
     * Execute the query in the context of itself with variable
     * bindings and return first resulting element.
     *
     * @param query A Query
     * @param bindings Bindings for variables referenced in the query
     * @return An element
     */
    T findOne( Query query, Map bindings );
}
