package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.link.ExternalLink;

import java.text.MessageFormat;

/**
 * Pages menu for a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 1:36:54 PM
 */
public class PartPagesMenuPanel extends MenuPanel {

    public PartPagesMenuPanel( String s, IModel<? extends Part> model ) {
        super( s, model );
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        add( new ExternalLink( "profile", MessageFormat.format(                       // NON-NLS
                "resource.html?scenario={0,number,0}&part={1,number,0}",           // NON-NLS
                getScenario().getId(), getPart().getId() ) ) );
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

    private Scenario getScenario() {
        return getPart().getScenario();
    }
}
