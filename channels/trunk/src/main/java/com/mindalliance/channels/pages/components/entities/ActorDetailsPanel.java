package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.components.entities.EntityDetailsPanel;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 23, 2009
 * Time: 2:15:22 PM
 */
public class ActorDetailsPanel extends EntityDetailsPanel {

    public ActorDetailsPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        moDetailsDiv.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( (Actor) getEntity() ) ) );
    }


}
