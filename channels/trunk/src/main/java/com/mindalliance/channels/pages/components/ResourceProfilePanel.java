package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.analysis.profiling.Resource;

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
    private Resource resource;

    public ResourceProfilePanel( String id, IModel<Resource> model ) {
        super( id, model );
        resource = model.getObject();
        init();
    }

    private void init() {
        add( new ResourceIssuesTablePanel( "issues", new Model<Resource>( resource ) ) );
        add( new PlaybookPanel( "playbook", new Model<Resource>( resource ) ) );
        add( new DirectoryPanel( "directory", new Model<Resource>( resource ) ) );
    }
}
