package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.PlannerMessage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Date;

/**
 * Planner message panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 8, 2010
 * Time: 1:27:54 PM
 */
public class PlannerMessagePanel extends AbstractSocialEventPanel {
    private IModel<PlannerMessage> plannerMessageModel;
    private boolean showReceived;

    public PlannerMessagePanel(
            String id,
            IModel<PlannerMessage> plannerMessageModel,
            boolean showReceived,
            int index,
            Updatable updatable ) {
        super(
                id,
                getMessageUserName( showReceived, plannerMessageModel ),
                index,
                plannerMessageModel,
                updatable );
        this.plannerMessageModel = plannerMessageModel;
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
            IModel<PlannerMessage> plannerMessageModel ) {
        return showReceived
                ? plannerMessageModel.getObject().getFromUsername()
                : plannerMessageModel.getObject().getToUsername();
    }


    protected String getCssClasses() {
        String cssClasses = getPlannerMessage().isBroadcast()
                ? "broadcast"
                : "private";
        return cssClasses + super.getCssClasses();
    }

    @Override
    public Date getDate() {
        return getPlannerMessage().getDate();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addSubject( socialItemContainer );
        addMessage( socialItemContainer );
        addTime( socialItemContainer );
    }

    private void addMessage( WebMarkupContainer socialItemContainer ) {
        Label messageLabel = new Label( "text", new Model<String>( getPlannerMessage().getText() ) );
        socialItemContainer.add( messageLabel );
    }

    private void addSubject( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer subjectContainer = new WebMarkupContainer( "subject" );
        PlannerMessage plannerMessage = getPlannerMessage();
        boolean linked = false;
        String subject = plannerMessage.getAboutString();
        ModelObject mo = plannerMessage.getAbout( getQueryService() );
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
                + ( getPlannerMessage().isEmailed() ? "emailed " : "" )
                + getTime()
                + ")";
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        socialItemContainer.add( timeLabel );
    }

    private PlannerMessage getPlannerMessage() {
        return plannerMessageModel.getObject();
    }
}
