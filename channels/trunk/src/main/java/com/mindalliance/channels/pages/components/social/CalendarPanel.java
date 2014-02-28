package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.dao.ModelManager;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
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
    private ModelManager modelManager;

    private static final String BG_COLOR = "F6F6F6";

    private static final String SRC = "{0}?showTitle=0&amp;showDate=0&amp;showTabs=0&amp;showCalendars=0&amp;showTz=0&amp;mode=AGENDA&amp;height=300&amp;wkst=1&amp;bgcolor=%23{2}&amp;src={1}&amp;color=%232952A3&amp;ctz={3}&pvttk={4} ";
    private static final String CALENDAR_LINK = "https://www.google.com/calendar/render";

    public CalendarPanel( String id, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return null;  // Todo
    }

    @Override
    public String getHelpTopicId() {
        return null;  // Todo
    }

    protected void init() {
        super.init();
        addCalendarFrame();
        addCalendarLogin();
    }

    private void addCalendarFrame() {
        WebMarkupContainer calendarFrame = new WebMarkupContainer( "calendarFrame" );
        String src = MessageFormat.format( SRC,
                getCalenderHost(),
                getEncodedCalendar(),
                BG_COLOR,
                getEncodedTimeZone(),
                getCalendarPrivateTicket() );
        calendarFrame.add( new AttributeModifier( "src", new Model<String>( src ) ) );
        add( calendarFrame );
    }

    private void addCalendarLogin() {
        WebMarkupContainer loginInfo = new WebMarkupContainer( "loginInfo" );
        ExternalLink calendarLoginLink = new ExternalLink(
                "calendarLink",
                CALENDAR_LINK,
                getCalendar()
                 );
        loginInfo.add( calendarLoginLink );
        add( loginInfo );
        loginInfo.setVisible( isPlanner() );
    }

    private String getCalendarPrivateTicket() {
        return getCollaborationModel().getCommunityCalendarPrivateTicket(
                modelManager.getDefaultCommunityCalendarPrivateTicket() );
    }

    private String getCalenderHost() {
        return getCollaborationModel().getCommunityCalendarHost(
                modelManager.getDefaultCommunityCalendarHost() );
    }

    private String getCalendar() {
      return getCollaborationModel().getCommunityCalendar( modelManager.getDefaultCommunityCalendar() );
    }

    private String getEncodedCalendar() {
        try {
            return URLEncoder.encode( getCalendar(), "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }

    private String getEncodedTimeZone() {
        String timezone = TimeZone.getDefault().getID();
        try {
            return URLEncoder.encode( timezone, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }
}
