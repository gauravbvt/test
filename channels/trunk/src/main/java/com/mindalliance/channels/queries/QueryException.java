// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.queries;

/**
 * Exception resulting for the execution of a query.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class QueryException extends Exception {

    private final Object query;

    /**
     * Default constructor.
     * @param query the query that lead to this exception
     */
    public QueryException( Object query ) {
        super();
        this.query = query;
    }

    /**
     * Default constructor.
     * @param query the query that lead to this exception
     * @param message a description
     */
    public QueryException( Object query, String message ) {
        super( message );
        this.query = query;
    }

    /**
     * Default constructor.
     * @param query the query that lead to this exception
     * @param cause the cause of this exception
     */
    public QueryException( Object query, Throwable cause ) {
        super( cause );
        this.query = query;
    }

    /**
     * Default constructor.
     * @param query the query that lead to this exception
     * @param message a description
     * @param cause the cause of this exception
     */
    public QueryException(
            Object query, String message, Throwable cause ) {

        super( message, cause );
        this.query = query;
    }

    /**
     * Return the query that lead to this exception.
     */
    public Object getQuery() {
        return this.query;
    }
}
