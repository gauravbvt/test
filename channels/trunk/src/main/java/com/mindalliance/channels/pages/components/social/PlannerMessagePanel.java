package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.PlannerMessage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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

    public PlannerMessagePanel( String id, IModel<PlannerMessage> plannerMessageModel, Updatable updatable ) {
        super( id, plannerMessageModel.getObject().getFromUsername(), updatable );
        this.plannerMessageModel = plannerMessageModel;
        init();
    }


    protected String getCssClass() {
        return getPlannerMessage().isBroadcast()
                ? "broadcast"
                : "private";
    }

    public String getTime() {
        return getPlannerMessage().getShortTimeElapsedString();
    }

    public String getLongTime() {
        return getPlannerMessage().getLongTimeElapsedString();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addMessage( socialItemContainer );
        addSubject( socialItemContainer );
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


    private PlannerMessage getPlannerMessage() {
        return plannerMessageModel.getObject();
    }
}
