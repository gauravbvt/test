package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

/**
 * Actor report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 9:05:57 PM
 */
public class ActorBannerPanel extends Panel {

    @SpringBean
    private QueryService queryService;

    public ActorBannerPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario ) {

        this( id, scenario, spec, showScenario, null, null );
    }

    public ActorBannerPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario,
            Set<Medium> unicasts, Collection<Channel> broadcasts ) {

        super( id );
        setRenderBodyOnly( true );

        if ( spec.getActor() == null )
            spec.setActor( Actor.UNKNOWN );
        Actor actor = spec.getActor();
        add( new Label( "name", actor.getName() ) );                                      // NON-NLS

        String title = queryService.getTitle( actor );
        Label titleLabel = new Label( "title", title );                                   // NON-NLS
        titleLabel.setVisible( !title.isEmpty() );
        add( titleLabel );

        boolean canShowScenario = showScenario && scenario != null;
        Label scenarioLabel = new Label( "scenario", canShowScenario ?                    // NON-NLS
                        MessageFormat.format( "(from {0})", scenario.getName() ) : "" );
        scenarioLabel.setVisible( canShowScenario );
        add( scenarioLabel );

        add( new ChannelsBannerPanel( "channels",                                         // NON-NLS
                                      spec, unicasts, broadcasts ) );
    }
}
