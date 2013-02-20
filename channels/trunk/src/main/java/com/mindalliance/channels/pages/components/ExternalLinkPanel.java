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
    private boolean newPage;

    public ExternalLinkPanel( String id, String url, String text ) {
        this( id, url, text, true );
    }

    public ExternalLinkPanel( String id, String url, String text, boolean newPage ) {
        super( id );
        this.url = url;
        this.text = text;
        this.newPage = newPage;
        init();
    }

    private void init() {
        ExternalLink link = new ExternalLink( "link", url, text );
        link.add( new AttributeModifier( "class", new Model<String>( "link" ) ) );
        if ( newPage)
            link.add( new AttributeModifier( "target", new Model<String>( "_" ) ) );
        add( link );
    }
}
