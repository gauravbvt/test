package com.mindalliance.channels.social.services.notification;


import java.text.SimpleDateFormat;
import java.util.Date;

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

    String getToUsername();
    String getFromUsername();
    String getContent( Format format, int maxLength );
    String getSubject( Format format, int maxLength );
    String getPlanUri();
    int getPlanVersion();
    Date getWhenNotified();
    String getTypeName();


}
