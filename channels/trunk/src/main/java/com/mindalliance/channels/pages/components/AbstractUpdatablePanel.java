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
     * Pass update event to parent.
     *
     * @param target  an ajax request target
     * @param context an object indicating context of update
     */
    public void updateWith( AjaxRequestTarget target, Object context ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null ) updatableParent.updateWith( target, context );
    }

}
