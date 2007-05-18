// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.Model;
import com.mindalliance.channels.model.support.SuggestionManager;
import com.mindalliance.channels.util.AbstractJavaBean;

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
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 */
public abstract class ModelElement extends AbstractJavaBean
        implements Cloneable {

    private String about;
    private Model model;
    private SuggestionManager suggestions;

    //--------------------------------------
    /**
     * Create a new model object.
     * This should only be called by the factory.
     * @see ModelObjectFactory
     */
    ModelElement() {
        super();
    }

    /**
     * Default constructor.
     * @param model the model
     */
    public ModelElement( Model model ) {
        this();
        setModel( model );
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

    /**
     * Return the value of about.
     */
    public String getAbout() {
        return this.about;
    }

    /**
     * Set the value of about.
     * @param about The new value of about
     */
    public void setAbout( String about ) {
        this.about = about;
    }

    /**
     * Return the value of model.
     */
    @DisplayAs( direct = "in model {1}",
                reverse = "contains {1}",
                reverseMany = "contains:" )
    public Model getModel() {
        return this.model;
    }

    /**
     * Set the value of model.
     * @param model The new value of model
     */
    public void setModel( Model model ) {
        this.model = model;
    }
}
