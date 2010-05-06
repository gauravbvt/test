package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

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
    private String prefix;

    public ActorBannerPanel(
            String id, Segment segment, ResourceSpec spec, boolean showSegment, String prefix ) {

        this( id, segment, spec, showSegment, null, null, prefix );
    }

    public ActorBannerPanel(
            String id,
            Segment segment,
            ResourceSpec spec,
            boolean showSegment,
            Set<TransmissionMedium> unicasts,
            Collection<Channel> broadcasts,
            String prefix ) {

        super( id );
        this.prefix = prefix;
        setRenderBodyOnly( true );

        if ( spec.getActor() == null )
            spec.setActor( Actor.UNKNOWN );
        Actor actor = spec.getActor();
        boolean canShowSegment = showSegment && segment != null;
        String segmentName = canShowSegment ?
                MessageFormat.format( " (from {0})", segment.getName() )
                : "";
        add(
                new Label( "name", spec.getActorName() ),
                new Label( "segment", segmentName )
                        .setRenderBodyOnly( true )
                        .setVisible( canShowSegment ),
                new WebMarkupContainer( "pic" )
                        .add( new AttributeModifier(
                        "src", new Model<String>( getPictureUrl( actor ) ) ),
                        new AttributeModifier(
                                "alt", new Model<String>( actor == null ? "" : actor.getName() ) ) ),
                new ChannelsBannerPanel( "channels", spec, unicasts, broadcasts ) );
    }

    private String getPictureUrl( ModelObject modelObject ) {
        String url = modelObject.getImageUrl();
        url = url == null ? "images/actor.png" : url;
        return Attachment.addPrefixIfRelative( url, prefix ) ;
    }


}
