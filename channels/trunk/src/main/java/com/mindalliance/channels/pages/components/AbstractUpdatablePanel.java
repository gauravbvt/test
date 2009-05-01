package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import org.apache.wicket.AttributeModifier;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.QueryService;

import java.text.Collator;
import java.util.Set;

/**
 * Abstract base class of updatable panels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 27, 2009
 * Time: 7:30:31 PM
 */
public class AbstractUpdatablePanel extends Panel implements Updatable {

    /**
     * String comparator for equality tests.
     */
    private static final Collator COMPARATOR = Collator.getInstance();
    /**
     * Model on an identifiable.
     */
    private IModel<? extends Identifiable> model = null;
    /**
     * Ids of expanded model objects.
     */
    private Set<Long> expansions;

    public AbstractUpdatablePanel( String id ) {
        super( id );
    }

    public AbstractUpdatablePanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model );
        this.model = model;
        this.expansions = expansions;
    }

    protected IModel<? extends Identifiable> getModel() {
        return model;
    }

    /**
     * Get the query service.
     *
     * @return a query service
     */
    protected QueryService getQueryService() {
        return Channels.instance().getQueryService();
    }

    /**
     * Set and update a component's visibility.
     *
     * @param target    an ajax request target
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible(
            AjaxRequestTarget target,
            Component component,
            boolean visible ) {
        makeVisible( component, visible );
        target.addComponent( component );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
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
    public void changed( Change change ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null )
            updatableParent.changed( change );
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
        changed( change );
        updateWith( target, change );
    }

    /**
     * Test if strings are equivalent.
     *
     * @param name   the new name
     * @param target the original name
     * @return true if strings are equivalent
     */
    protected static boolean isSame( String name, String target ) {
        return COMPARATOR.compare( name, target ) == 0;
    }

    /**
     * Get the expansions.
     * @return a set of Longs
     */
    protected Set<Long> getExpansions() {
        return expansions;
    }

}
