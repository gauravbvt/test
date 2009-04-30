package com.mindalliance.channels.pages.components.entities;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;

/**
 * A link to an entity panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 1:45:59 PM
 */
public class EntityLink extends AbstractUpdatablePanel {
    /**
     * Entity.
     */
    private ModelObject entity;
    /**
     * Text for link other than entity.getLabel().
     */
    private String text;

    public EntityLink( String id, IModel<? extends ModelObject> model ) {
         this( id, model, null );
    }

    public EntityLink( String id, IModel<? extends ModelObject> model, String text ) {
        super( id, model, null );
        entity = model.getObject();
        this.text = text;
        init();
    }

    private void init() {
        Link link = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, entity ) );
            }
        };
        add( link );
        Label textLabel = new Label( "text", new PropertyModel<String>( this, "text" ) );
        link.add( textLabel );
    }

    public String getText() {
        if ( text == null || text.isEmpty() )
            return entity.getLabel();
        else
            return text;
    }

}
