// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.queries;

import com.mindalliance.channels.Model;

/**
 * A language-specific query maker.
 *
 * Various implementations will support various query languages.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface QueryEngine {

    /**
     * Perform a query on the model.
     *
     * @param query the query, in the format expected by the
     * implementation
     * @return the result, or null if no result was found.
     * This result may be a collection.
     * @throws QueryException on errors.
     */
    Object get( Object query ) throws QueryException ;

    /**
     * Set a value in the model.
     * @param query a query specifying which value to set
     * @param value the new value of the target of the query
     * @throws QueryException on errors.
     */
    void set( Object query, Object value ) throws QueryException ;

    /**
     * Return the model queried by this engine.
     */
    Model getModel();
}
