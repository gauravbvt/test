// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.Set;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.model.support.SuggestionManager;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;

/**
 * <p>An object in the model.</p>
 *
 * <h2>Lifecycle</h2>
 *
 * <ol>
 *      <li>Either:
 *          Get a new instance using a model object factory.
 *          or obtain an object from a query engine.
 *      </li>
 *      <li>View or modify the values.</li>
 *      <li>Save or delete using the DAO.</li>
 * </ol>
 *
 * @see com.mindalliance.channels.model.ModelObjectFactory
 * @see com.mindalliance.channels.model.support.ModelObjectDAO
 * @see com.mindalliance.channels.queries.QueryEngine
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public abstract class AbstractModelObject extends AbstractJavaBean
        implements Cloneable {

    private GUID guid;
    private SuggestionManager suggestions;

    //--------------------------------------
    /**
     * Create a new model object.
     * This should only be called by the factory.
     * @see ModelObjectFactory
     *
     * @param guid the unique guid
     */
    AbstractModelObject( GUID guid ) {
        super();
        this.guid = guid ;
    }

    /**
     * Return the guid of this object.
     */
    public final GUID getGuid() {
        return this.guid;
    }

    //--------------------------------------
    /**
     * Return the suggestions attached to this object.
     */
    public synchronized SuggestionManager getSuggestions() {

        if ( this.suggestions == null )
            this.suggestions = new SuggestionManager( this );
        return this.suggestions;
    }

    //--------------------------------------
    /**
     * Add sub-objects that should be asserted by the rules engine
     * when this object is asserted.
     * This should be overloaded by subclasses.
     *
     * @param objects the set to add to.
     */
    public void contributeAssertableObjects( Set<JavaBean> objects ) {
    }
}
