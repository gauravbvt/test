package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 2:40:03 PM
 */
public class RefQueryModel<T> implements IChainingModel<T> {

    private Serializable target;
    private final Query query;
    private static final long serialVersionUID = 2961271377314751579L;

    public RefQueryModel( Serializable target, Query query ) {
        this.target = target;
        this.query = query;
    }

    @SuppressWarnings( { "unchecked" } )
    @Override
    public T getObject() {
        try {
            return (T) query.execute( getHolder() );
        } catch ( Exception e ) {
            LoggerFactory.getLogger( getClass() ).warn(
                    "Failed to eval query $query on $target",
                    e );
            throw new RuntimeException( e );
        }
    }

    public Object getHolder() {
        return target instanceof IModel ?
               ( (IModel<?>) target ).getObject() :
               target;
    }

    /**
     * this model is ReadOnly, setObject is ignored.
     * @param object an object
     */
    public void setObject( T object ) {
        LoggerFactory.getLogger( getClass() ).info(
                "Attempting to set queried $target to $obj" );
    }

    public void detach() {
        // Detach chained model
        if ( target instanceof IDetachable )
            ( (IDetachable) target ).detach();
    }

    public void setChainedModel( IModel<?> model ) {
        target = model;
    }

    public IModel<?> getChainedModel() {
        return target instanceof IModel ? (IModel<?>) target : null;
    }
}