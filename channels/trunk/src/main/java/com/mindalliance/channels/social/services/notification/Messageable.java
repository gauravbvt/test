package com.mindalliance.channels.social.services.notification;


import com.mindalliance.channels.core.community.CommunityService;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Something that can be the subject of a message.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 1:53 PM
 */
public interface Messageable {

    enum Format {
        TEXT,
        HTML
    }

    /**
     * Simple date format.
     */
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "M/d/yyyy HH:mm" );

    List<String> getToUserNames( String topic, CommunityService communityService );

    String getFromUsername( String topic );

    String getContent(
            String topic,
            Format format,
            CommunityService communityService );

    String getSubject(
            String topic,
            Format format,
            CommunityService communityService );

    String getCommunityUri();

    String getPlanUri();

    int getPlanVersion();

    String getTypeName();

    String getLabel();


}
