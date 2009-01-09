package com.mindalliance.channels.pages.profiles;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.ModelObjectLink;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 12:28:17 PM
 */
public class ModelObjectLinkPanel extends Panel {

    private ModelObject modelObject;
    private String string;

    public ModelObjectLinkPanel( String id, ModelObject mo ) {
        this( id, mo, mo.toString() );
    }

    public ModelObjectLinkPanel( String id, ModelObject mo, String s ) {
        super( id, new Model<ModelObject>( mo ) );
        modelObject = mo;
        string = s;
        init();
    }

    private void init() {
        Link link = new Link( "link" ) {
            public void onClick() {
                setResponsePage( new RedirectPage( ModelObjectLink.linkFor( modelObject ).getObject() ) );
            }
        };
        add( link );
        link.add( new Label( "string", string ) );
    }
}
