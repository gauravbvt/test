package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.dao.PlanManager;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.TimeZone;

/**
 * Calendar panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/14/11
 * Time: 9:31 AM
 */
public class CalendarPanel extends AbstractSocialListPanel {

    @SpringBean
    private PlanManager planManager;

    private static final String BG_COLOR = "F6F6F6";

    private static final String SRC = "{0}?showTitle=0&amp;showDate=0&amp;showTabs=0&amp;showCalendars=0&amp;showTz=0&amp;mode=AGENDA&amp;height=300&amp;wkst=1&amp;bgcolor=%23{2}&amp;src={1}&amp;color=%232952A3&amp;ctz={3}";

    public CalendarPanel( String id, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    protected void init() {
        super.init();
        WebMarkupContainer calendarFrame = new WebMarkupContainer( "calendarFrame" );
        String src = MessageFormat.format( SRC,
                getCalenderHost(),
                getCalendar(),
                BG_COLOR,
                getTimeZone() );
        calendarFrame.add( new AttributeModifier( "src", true, new Model<String>( src ) ) );
        add( calendarFrame );
    }

    private String getCalenderHost() {
        return getPlan().getCommunityCalendarHost(
                planManager.getDefaultCommunityCalendarHost() );
    }

    private String getCalendar() {
        try {
            return URLEncoder.encode( getPlan().getCommunityCalendar(
                    planManager.getDefaultCommunityCalendar() ),
                    "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }

    private String getTimeZone() {
        String timezone = TimeZone.getDefault().getID();
        try {
            return URLEncoder.encode( timezone, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }
}
