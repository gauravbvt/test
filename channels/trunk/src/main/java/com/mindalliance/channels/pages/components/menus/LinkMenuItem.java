package com.mindalliance.channels.pages.components.menus;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * A link menu item.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 9:36:07 AM
 */
public class LinkMenuItem extends Panel {

    public LinkMenuItem( String s, IModel<String> model, Link link ) {
        super( s, model );
        IModel<String> model1 = model;
        link.setMarkupId( "link" );
        add( link );
        link.add( new Label("string", model.getObject()));
    }

}
