package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyResponse;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Date;

/**
 * Survey reminder panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/14/11
 * Time: 11:21 AM
 */
public class SurveyResponsePanel extends AbstractSocialEventPanel {

    private final IModel<SurveyResponse> surveyResponseModel;

    public SurveyResponsePanel(
            String id,
            IModel<SurveyResponse> surveyResponseModel,
            int index,
            boolean showProfile,
            Updatable updatable ) {
        super( id,
                index,
                showProfile,
                updatable );
        this.surveyResponseModel = surveyResponseModel;
        init();
    }

    @Override
    public Date getDate() {
        return getSurvey().getLaunchDate();
    }

    @Override
    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addSurveyType( socialItemContainer );
        addSurveyLink( socialItemContainer );
        addStatus( socialItemContainer );
        addTime( socialItemContainer );
    }

    private void addStatus( WebMarkupContainer socialItemContainer ) {
        String statusString = getSurveyResponse().getStatusLabel();
        Label statusLabel = new Label( "status", statusString );
        statusLabel.add( new AttributeModifier( "class", new Model<String>( statusString ) ) );
        socialItemContainer.add( statusLabel );
    }

    private void addSurveyType( WebMarkupContainer socialItemContainer ) {
        Label surveyTypeLabel = new Label( "surveyType", getSurvey().getSurveyType() );
        socialItemContainer.add( surveyTypeLabel );
    }

    private void addSurveyLink( WebMarkupContainer socialItemContainer ) {
        ExternalLink surveyLink = new ExternalLink(
                "surveyLink",
                new Model<String>( getSurvey().getSurveyLink( getUser() ) ),
                new Model<String>( getSurvey().getTitle() )
        );
        socialItemContainer.add( surveyLink );

    }

    private void addTime( WebMarkupContainer socialItemContainer ) {
        String time = getTime();
        String timeLabelString = "";
        if ( !time.isEmpty() ) {
            timeLabelString = time;
        }
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        if ( !timeLabelString.isEmpty() ) {
            timeLabel.add( new AttributeModifier(
                    "title",
                    new PropertyModel<String>( this, "longTime" ) ) );
        }
        socialItemContainer.add( timeLabel );
    }

    @Override
    public String getTime() {
        return "Launched " + super.getTime();
    }


    private Survey getSurvey() {
        return getSurveyResponse().getSurvey();
    }

    private SurveyResponse getSurveyResponse() {
        return surveyResponseModel.getObject();
    }
}
