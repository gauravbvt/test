package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.ModelObject;
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
public class ScenarioPagesMenuPanel extends MenuPanel {

    public ScenarioPagesMenuPanel( String s, IModel<? extends ModelObject> model ) {
        super( s, model );
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        add( new ExternalLink( "index", "index.html" ) );
        add( new ExternalLink( "report", "report.html" ) );
    }

}
