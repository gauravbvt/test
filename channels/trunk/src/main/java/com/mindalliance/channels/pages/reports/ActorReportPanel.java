package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.Channels;
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
public class ActorReportPanel extends Panel {

    /** A scenario. */
    private Scenario scenario;

    /** An actor. */
    private ResourceSpec spec;

    /** True if scenario should be displayed. */
    private boolean showScenario;

    /** Restrict shown media to these. If null, show everything. */
    private final Set<Medium> unicasts;
    private final Collection<Channel> broadcasts;

    public ActorReportPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario ) {

        this( id, scenario, spec, showScenario, null, null );
        this.spec = spec;
    }

    public ActorReportPanel(
            String id, Scenario scenario, ResourceSpec spec, boolean showScenario,
            Set<Medium> unicasts, Collection<Channel> broadcasts ) {

        super( id );
        this.spec = spec;
        this.scenario = scenario;
        this.showScenario = showScenario;
        this.unicasts = unicasts;
        this.broadcasts = broadcasts;
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        Actor actor = spec.getActor() == null ? Actor.UNKNOWN : spec.getActor();
        add( new Label( "name", actor.getName() ) );                                      // NON-NLS

        String title = getTitle( actor );
        Label titleLabel = new Label( "title", title );                                   // NON-NLS
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
                                      spec, unicasts, broadcasts ) );
    }

    private String getTitle( Actor actor ) {
        for ( Job job : ( (Channels) getApplication() ).getDqo().findAllJobs( actor ) ) {
            String title = job.getTitle().trim();
            if ( !title.isEmpty() )
                return title;
        }

        return "";
    }
}
