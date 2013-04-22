package com.mindalliance.channels.core;

import java.util.Date;

/**
 * A context in which model object are persisted and managed.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/23/13
 * Time: 1:19 PM
 */
public interface ModelObjectContext {

    /**
     * Get the context's uri.
     * @return a string
     */
    String getUri();

    /**
     * Set or update the lowest ID model objects can be assigned. Keep a persistent record of updates.
     * @param lowerBound a long
     */
    void recordIdShift( long lowerBound );

    /**
     * Return by how much IDs recorded since a given date need to be translated.
     * @param date a date
     * @return a long by which to translate ids recorded on the given date
     */
    long getIdShiftSince( Date date );

    String getCommunityCalendar();

    void setCommunityCalendar( String communityCalendar );

    String getCommunityCalendarHost();

    void setCommunityCalendarHost( String communityCalendarHost );

    String getUserSupportCommunity();

    void setUserSupportCommunity( String supportCommunity );

    String getUserSupportCommunity( String defaultName );

    String getPlannerSupportCommunity();

    void setPlannerSupportCommunity( String plannerSupportCommunity );

    String getCommunityCalendar( String defaultCalendar );

    String getCommunityCalendarHost( String defaultCalendarHost );

    String getCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket );

    String getCommunityCalendarPrivateTicket();

    void setCommunityCalendarPrivateTicket( String communityCalendarPrivateTicket );

}
