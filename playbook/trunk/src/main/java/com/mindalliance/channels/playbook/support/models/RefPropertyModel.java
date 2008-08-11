package com.mindalliance.channels.playbook.support.models;

import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008 Time: 5:29:44 PM
 */
public class RefPropertyModel<T extends Serializable>
        implements IChainingModel<T> {

    private Serializable target;
    private final String expression;
    private final T defaultObject;
    private static final long serialVersionUID = 6526008078859470097L;

    public RefPropertyModel( Serializable target, String expression ) {
        this( target, expression, null );
    }

    public RefPropertyModel(
            Serializable target, String expression, T defaultObject ) {
        this.target = target;
        this.expression = expression;
        this.defaultObject = defaultObject;
    }

    @SuppressWarnings( { "unchecked" } )
    public T getObject() {
        Object holder = getPropertyHolder();
        T object = (T) RefUtils.get( holder, expression );
        if ( object == null )
            object = defaultObject;

        return object;
    }

    private Object getPropertyHolder() {
        return target instanceof IModel ?
               ( (IModel<?>) target ).getObject() :
               target;
    }

    public void setObject( T object ) {
        Object holder = getPropertyHolder();
        RefUtils.set( holder, expression, object );
    }

    public void detach() {
        // Detach chained model
        if ( target instanceof IModel )
            ( (IDetachable) target ).detach();
    }

    public void setChainedModel( IModel<?> model ) {
        target = model;
    }

    public String getExpression() {
        return expression;
    }

    public Serializable getTarget() {
        return target;
    }

    public IModel<?> getChainedModel() {
        return target instanceof IModel ? (IModel<?>) target : null;
    }
}