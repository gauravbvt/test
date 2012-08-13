package com.mindalliance.channels.pages.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * External link panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 14, 2009
 * Time: 10:35:36 AM
 */
public class ExternalLinkPanel extends Panel {

    private String url;
    private String text;

    public ExternalLinkPanel( String id, String url, String text ) {
        super( id );
        this.url = url;
        this.text = text;
        init();
    }

    private void init() {
        ExternalLink link = new ExternalLink( "link", url, text );
        link.add( new AttributeModifier( "class", true, new Model<String>( "link" ) ) );
        link.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
        add( link );
    }
}
