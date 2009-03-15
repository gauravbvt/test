package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import org.apache.wicket.AttributeModifier;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.command.Change;

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
     * Set and update a component's visibility.
     * @param target an ajax request target
     * @param component a component
     * @param visible a boolean
     */
    protected static void makeVisible( AjaxRequestTarget target, Component component, boolean visible ) {
        makeVisible( component, visible );
        target.addComponent( component );
    }

    /**
     * Set a component's visibility.
     * @param component a component
     * @param visible a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add(
                new AttributeModifier(
                        "style",
                        true,
                        new Model<String>( visible ? "display:inline" : "display:none" ) ) );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( AjaxRequestTarget target, Change change ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null )
            updatableParent.changed( target, change );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null ) updatableParent.updateWith( target, change );
    }

    /**
     * Send changed event and then the updateWith event.
     *
     * @param target an ajax request target
     * @param change the nature of the change
     */
    protected void update( AjaxRequestTarget target, Change change ) {
        changed( target, change );
        updateWith( target, change );
    }

}
