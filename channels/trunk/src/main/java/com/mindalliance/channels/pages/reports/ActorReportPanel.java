package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Channelable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Actor report panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 9:05:57 PM
 */
public class ActorReportPanel extends Panel {
    /**
     * An actor
     */
    private Actor actor;

    public ActorReportPanel( String id, IModel<Actor> model ) {
        super( id, model );
        actor = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "name", actor.getName() ) );
        add( new Label( "description", actor.getDescription() ) );
        add( new ChannelsReportPanel( "channels", new Model<Channelable>( ResourceSpec.with( actor ) ) ) );
    }

}
