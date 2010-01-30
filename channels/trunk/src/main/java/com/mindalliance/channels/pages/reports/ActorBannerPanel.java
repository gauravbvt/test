package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
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
            String id, Segment segment, ResourceSpec spec, boolean showSegment ) {

        this( id, segment, spec, showSegment, null, null );
    }

    public ActorBannerPanel(
            String id, Segment segment, ResourceSpec spec, boolean showSegment,
            Set<TransmissionMedium> unicasts, Collection<Channel> broadcasts ) {

        super( id );
        setRenderBodyOnly( true );

        if ( spec.getActor() == null )
            spec.setActor( Actor.UNKNOWN );

        boolean canShowSegment = showSegment && segment != null;
        String segmentName = canShowSegment ?
                                MessageFormat.format( " (from {0})", segment.getName() )
                              : "";
        add(
                new Label( "name", spec.getActorName() ),
                new Label( "segment", segmentName )
                        .setRenderBodyOnly( true )
                        .setVisible( canShowSegment ),
                new ChannelsBannerPanel( "channels", spec, unicasts, broadcasts ) );
    }
}
