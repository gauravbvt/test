package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 27, 2009
 * Time: 7:30:31 PM
 */
public class AbstractUpdatablePanel extends Panel implements Updatable {

    public AbstractUpdatablePanel( String id ) {
        super( id );
    }

    public AbstractUpdatablePanel( String id, IModel<?> model ) {
        super( id, model );
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target ) {
        updateWith( target );
    }

    /**
     * Pass update event to parent
     * @param target  an ajax request target
     */
    protected void updateWith( AjaxRequestTarget target ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null ) updatableParent.update( target );
    }

}
