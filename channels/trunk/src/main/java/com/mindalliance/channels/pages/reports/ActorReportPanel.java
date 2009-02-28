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
    private ResourceSpec spec;

    /** True if scenario should be displayed. */
    private boolean showScenario;

    public ActorReportPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario ) {

        super( id );
        this.spec = spec;
        this.scenario = scenario;
        this.showScenario = showScenario;
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        Actor actor = spec.getActor() == null ? Actor.UNKNOWN : spec.getActor();
        add( new Label( "name",                                                           // NON-NLS
                        MessageFormat.format( "{0}:", actor.getName() ) ) );

        boolean canShowScenario = showScenario && scenario != null;
        Label scenarioLabel = new Label( "scenario", canShowScenario ?                    // NON-NLS
                                MessageFormat.format( "({0})", scenario.getName() ) : "" );
        scenarioLabel.setVisible( canShowScenario );
        add( scenarioLabel );

        String desc = spec.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        add( new ChannelsReportPanel( "channels", new Model<Channelable>( spec ) ) );     // NON-NLS

    }

}
