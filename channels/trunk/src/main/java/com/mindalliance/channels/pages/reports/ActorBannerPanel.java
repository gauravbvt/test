package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

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

        boolean canShowScenario = showScenario && scenario != null;
        String scenarioName = canShowScenario ?
                                MessageFormat.format( " (from {0})", scenario.getName() )
                              : "";
        add(
                new Label( "name", spec.getActorName() ),
                new Label( "scenario", scenarioName )
                        .setRenderBodyOnly( true )
                        .setVisible( canShowScenario ),
                new ChannelsBannerPanel( "channels", spec, unicasts, broadcasts ) );
    }
}
