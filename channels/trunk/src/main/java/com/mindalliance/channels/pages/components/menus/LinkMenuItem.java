package com.mindalliance.channels.pages.components.menus;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Component;

/**
 * A link menu item.
 */
public class LinkMenuItem extends Panel {

    public LinkMenuItem( String s, IModel<String> model, AbstractLink link ) {
        super( s, model );
        link.setMarkupId( "link" );
        add( link.add( new Label( "string", model.getObject() ) ) );
    }

}
