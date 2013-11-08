package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.messages.UserStatement;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
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
abstract public class UserStatementPanel extends AbstractSocialEventPanel {
    private IModel<? extends UserStatement> userStatementModel;

    public UserStatementPanel(
            String id,
            IModel<? extends UserStatement> userStatementModel,
            int index,
            boolean showProfile,
            boolean allowMessageDelete,
            Updatable updatable ) {
        super( id,
                index,
                userStatementModel,
                showProfile,
                allowMessageDelete,
                updatable );
        this.userStatementModel = userStatementModel;
        init();
    }

    public UserStatementPanel(
            String id,
            IModel<? extends UserStatement> userStatementModel,
            int index,
            boolean showProfile,
            Updatable updatable ) {
        super( id,
                index,
                userStatementModel,
                showProfile,
                updatable );
        this.userStatementModel = userStatementModel;
        init();
    }

    @Override
    public Date getDate() {
        return getUserStatement().getCreated();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addSubject( socialItemContainer );
        addMessage( socialItemContainer );
        addTime( socialItemContainer );
    }

    private void addMessage( WebMarkupContainer socialItemContainer ) {
        Label messageLabel = new Label( "text", new Model<String>( getUserStatement().messageContent() ) );
        socialItemContainer.add( messageLabel );
    }

    private void addSubject( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer subjectContainer = new WebMarkupContainer( "subject" );
        UserStatement userMessage = getUserStatement();
        boolean linked = false;
        String subject = userMessage.getMoLabel();
        ModelObject mo = userMessage.getAbout( getCommunityService() );
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
        String timeLabelString = "(" + getTimeLabel() + ")";
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        socialItemContainer.add( timeLabel );
    }

    protected String getTimeLabel() {
        return getTime();
    }

    protected UserStatement getUserStatement() {
        return userStatementModel.getObject();
    }
}
