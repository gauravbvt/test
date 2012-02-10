package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.model.UserMessage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Date;

/**
 * User message panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 8, 2010
 * Time: 1:27:54 PM
 */
public class UserMessagePanel extends AbstractSocialEventPanel {
    private IModel<UserMessage> userMessageModel;
    private boolean showReceived;

    public UserMessagePanel(
            String id,
            IModel<UserMessage> userMessageModel,
            boolean showReceived,
            int index,
            Updatable updatable ) {
        super(
                id,
                getMessageUserName( showReceived, userMessageModel ),
                index,
                userMessageModel,
                updatable );
        this.userMessageModel = userMessageModel;
        this.showReceived = showReceived;
        init();
    }

    private static String getInvolvement( boolean showReceived ) {
        return showReceived ? "From " : "To ";
    }

    public String getUserFullName() {
        return getInvolvement( showReceived ) + super.getUserFullNameAndRole();
    }


    private static String getMessageUserName(
            boolean showReceived,
            IModel<UserMessage> userMessageModel ) {
        return showReceived
                ? userMessageModel.getObject().getFromUsername()
                : userMessageModel.getObject().getToUsername();
    }


    protected String getCssClasses() {
        String cssClasses = getUserMessage().isBroadcast( getUser() )
                ? "broadcast"
                : "private";
        return cssClasses + super.getCssClasses();
    }

    @Override
    public Date getDate() {
        return getUserMessage().getCreated();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addSubject( socialItemContainer );
        addMessage( socialItemContainer );
        addTime( socialItemContainer );
    }

    private void addMessage( WebMarkupContainer socialItemContainer ) {
        Label messageLabel = new Label( "text", new Model<String>( getUserMessage().getText() ) );
        socialItemContainer.add( messageLabel );
    }

    private void addSubject( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer subjectContainer = new WebMarkupContainer( "subject" );
        UserMessage userMessage = getUserMessage();
        boolean linked = false;
        String subject = userMessage.getAboutString();
        ModelObject mo = userMessage.getAbout( getQueryService() );
        if ( mo != null ) {
            ModelObjectLink moLink = new ModelObjectLink(
                    "modelObject",
                    new Model<ModelObject>( mo ),
                    new Model<String>( subject ) );
            subjectContainer.add( moLink );
            linked = true;
        }
        if ( !linked ) {
            subjectContainer.add( new Label( "modelObject", subject ) );
        }
        subjectContainer.setVisible( !subject.isEmpty() );
        socialItemContainer.add( subjectContainer );
    }

    private void addTime( WebMarkupContainer socialItemContainer ) {
        String timeLabelString = "("
                + ( getUserMessage().isEmailed() ? "emailed " : "" )
                + getTime()
                + ")";
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        socialItemContainer.add( timeLabel );
    }

    private UserMessage getUserMessage() {
        return userMessageModel.getObject();
    }
}
