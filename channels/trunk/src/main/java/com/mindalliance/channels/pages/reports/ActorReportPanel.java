package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;

/**
 * Actor report panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 9:05:57 PM
 */
public class ActorReportPanel extends Panel {

    /** A scenario. */
    private Scenario scenario;

    /** An actor. */
    private Actor actor;

    public ActorReportPanel( String id, Scenario scenario, Actor actor ) {
        super( id );
        this.actor = actor;
        this.scenario = scenario;
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        add( new Label( "name", actor.getName() + ":" ) );                                // NON-NLS
        String scenarioString = scenario == null ?
                            ""  : MessageFormat.format( "({0})", scenario.getName() );
        Label scenarioLabel = new Label( "scenario", scenarioString );                    // NON-NLS
        scenarioLabel.setVisible( scenario != null );
        add( scenarioLabel );

        String desc = actor.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        add( new ChannelsReportPanel( "channels",                                         // NON-NLS
                                      new Model<Channelable>( ResourceSpec.with( actor ) ) ) );
    }

}
