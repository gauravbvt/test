package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.model.PresenceRecord;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Date;

/**
 * Planner presence panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 2:28:30 PM
 */
public class UserPresencePanel extends AbstractSocialEventPanel {

    public UserPresencePanel(
            String id,
            String username,
            int index,
            Updatable updatable ) {
        super( id, username, index, updatable );
        init();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addTime( socialItemContainer );
    }

    @Override
    public Date getDate() {
        PresenceRecord presenceRecord = getLatestPresenceRecord();
        return presenceRecord == null ? null : presenceRecord.getCreated();
    }

    private void addTime( WebMarkupContainer socialItemContainer ) {
        boolean present = isPresent();
        String time = getTime();
        String timeLabelString = "";
        if ( !time.isEmpty() && present ) {
            timeLabelString = time;
        }
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        if ( !timeLabelString.isEmpty() ) {
            timeLabel.add( new AttributeModifier(
                    "title",
                    new PropertyModel<String>( this, "longTime" ) ) );
        }
        timeLabel.setVisible( isPresent() );
        socialItemContainer.add( timeLabel );
        if ( !present & !time.isEmpty() ) {
            getNameLabel().add( new AttributeModifier(
                    "title",
                    new Model<String>( "left " + time ) ) );
        }
    }


}
