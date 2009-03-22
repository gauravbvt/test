package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.ModelObject;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 23, 2009
 * Time: 2:15:22 PM
 */
public class ActorPanel extends EntityPanel {

    public ActorPanel( String id, IModel<? extends ModelObject> model ) {
        super( id, model );
    }

    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        moDetailsDiv.add( new ChannelListPanel( "channels", new Model<Channelable>( (Actor) getEntity() ) ) );
    }


}
