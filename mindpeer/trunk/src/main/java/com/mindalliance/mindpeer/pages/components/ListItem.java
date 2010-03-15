// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages.components;

import com.mindalliance.mindpeer.model.Countable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * A read-only clickable right list item.
 */
public class ListItem extends Panel {

    private static final long serialVersionUID = -6076870845520109279L;

    /**
     * Create a new ListItem instance.
     *
     * @param id the template id
     * @param model a Countabble model
     * @param page a page class
     */
    public ListItem( String id, IModel<? extends Countable> model, Class<? extends WebPage> page ) {
        super( id );

        PropertyModel<String> nameModel = new PropertyModel<String>( model, "name" );

        PageParameters parms = new PageParameters();
        parms.put( "name", nameModel.getObject() );
        parms.put( "section", "Comments" );
        Link<String> link = new BookmarkablePageLink<String>( "name-link", page, parms );
        link.setModel( nameModel );

        add( link.add( new Label( "name", nameModel ).setRenderBodyOnly( true ) ),
             new Label( "count", new PropertyModel<Integer>( model, "count" ) ) );
    }
}
