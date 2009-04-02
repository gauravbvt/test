package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

/**
 * Pages menu for  a scenario
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:30:46 PM
 */
public class ProjectShowMenuPanel extends MenuPanel {

    public ProjectShowMenuPanel( String s, IModel<? extends Scenario> model ) {
        super( s, model, null );
        init();
    }

    private void init() {
        // add( new ExternalLink( "index", "index.html" ) );
        add( new ExternalLink( "report", "report.html" ) );
    }

}
