// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.queries;

import com.mindalliance.channels.Model;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * Support for OGNL queries.
 *
 * @see <a href="http://www.ognl.org/">OGNL website</a>
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class OGNLQueryEngine implements QueryEngine {

    private Model model;

    /**
     * Default constructor.
     */
    public OGNLQueryEngine() {
    }

    /**
     * Perform a single-result query on the model.
     *
     * @param query the query, in the format expected by the
     * implementation
     * @return the result, or null if no result was found.
     * @throws QueryException on errors.
     */
    public Object get( Object query ) throws QueryException {
        try {
            return Ognl.getValue( query, getModel() );

        } catch ( OgnlException ex ) {
            throw new QueryException( query, ex );
        }
    }

    /**
     * Set a property value using a query.
     * @see QueryEngine#set(Object, Object)
     * @param query the query refering to a property
     * @param value the value
     * @throws QueryException on errors
     */
    public void set( Object query, Object value ) throws QueryException {
        try {
            Ognl.setValue( query, value, getModel() );

        } catch ( OgnlException ex ) {
            throw new QueryException( query, ex );
        }
    }

    /**
     * Set the value of model.
     * @param model The new value of model
     */
    public void setModel( Model model ) {
        this.model = model;
    }

    /**
     * Return the value of model.
     */
    public Model getModel() {
        return this.model;
    }
}
