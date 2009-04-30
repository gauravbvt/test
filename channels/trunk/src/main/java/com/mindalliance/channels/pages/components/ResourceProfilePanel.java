package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.model.ResourceSpec;

/**
 * A resource's profile
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 14, 2009
 * Time: 10:58:29 AM
 */
public class ResourceProfilePanel extends Panel {
    /**
     * Resource profiled
     */
    private ResourceSpec resourceSpec;

    public ResourceProfilePanel( String id, IModel model ) {
        super( id, model );
        resourceSpec = (ResourceSpec)model.getObject();
        init();
    }

    private void init() {
        /*add( new ContactInfoPanel( "contact-info", new Model( resourceSpec ) , null) );
        add( new ResourceIssuesTablePanel( "issues", new Model( resourceSpec ), null ) );
        add( new PlaysTablePanel( "playbook", new Model( resourceSpec ) , null) );
        add( new DirectoryPanel( "directory", new Model( resourceSpec ), null ) );*/
    }
}
