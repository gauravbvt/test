package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;
import java.util.Set;

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

    /** Restrict shown media to these. If null, show everything. */
    private Set<Medium> showMedia;

    public ActorReportPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario ) {

        this( id, scenario, spec, showScenario, null );
        this.spec = spec;
    }

    public ActorReportPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario,
            Set<Medium> showMedia ) {

        super( id );
        this.spec = spec;
        this.scenario = scenario;
        this.showScenario = showScenario;
        this.showMedia = showMedia;
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        Actor actor = spec.getActor() == null ? Actor.UNKNOWN : spec.getActor();
        add( new Label( "name", actor.getName() ) );                                      // NON-NLS

        String title = "";
        Label titleLabel = new Label( "title", ", " + title );                            // NON-NLS
        titleLabel.setVisible( !title.isEmpty() );
        add( titleLabel );

        boolean canShowScenario = showScenario && scenario != null;
        Label scenarioLabel = new Label( "scenario", canShowScenario ?                    // NON-NLS
                                MessageFormat.format( "(from {0})", scenario.getName() ) : "" );
        scenarioLabel.setVisible( canShowScenario );
        add( scenarioLabel );

        String desc = spec.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        add( new ChannelsReportPanel( "channels",                                         // NON-NLS
                                      new Model<Channelable>( actor ), showMedia ) );
    }

}
