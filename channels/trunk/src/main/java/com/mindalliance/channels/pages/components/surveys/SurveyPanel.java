package com.mindalliance.channels.pages.components.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyException;
import com.mindalliance.channels.surveys.SurveyService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Survey panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 29, 2009
 * Time: 12:18:36 PM
 */
public class SurveyPanel extends AbstractUpdatablePanel {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( SurveyPanel.class );

    @SpringBean
    /**
     * Survey service.
     */
    private SurveyService surveyService;
    /**
     * Survey model.
     */
    private Survey survey;
    private boolean known;
    private WebMarkupContainer surveyDataContainer;
    private Component surveyContactsPanel;

    public SurveyPanel( String id, IModel<Survey> surveyModel ) {
        super( id, surveyModel );
        survey = surveyModel.getObject();
        init();
    }

    private void init() {
        survey.resetData();
        known = survey.updateSurveyData( surveyService, getPlan() );
        addHeaderLabels();
        addFooterLabels();
        addContactsPanel();
        surveyDataContainer = new WebMarkupContainer( "surveyData" );
        add( surveyDataContainer );
        addStats();
        addLinks();
        addLaunchButton();
        addCloseButton();
        addCancelButton();
        surveyDataContainer.setVisible( known );
    }

    private void addFooterLabels() {
        add( new Label(
                "status",
                new Model<String>( survey.isLaunched()
                        ? "Launched"
                        : survey.isClosed()
                        ? "Closed"
                        : "Created" ) ) );
        add( new Label( "date", new Model<String>( survey.getFormattedStatusDate() ) ) );
        add( new Label( "issuer", new Model<String>( survey.getIssuer() ) ) );
    }

    private void addHeaderLabels() {
        add( new Label( "statusTitle", new Model<String>( getStatusTitle() ) ) );
        add( new Label( "about", new Model<String>( survey.getTitle() ) ) );
    }

    private String getStatusTitle() {
        switch ( survey.getStatus() ) {
            case In_design:
                return "New";
            case Launched:
                return "Launched";
            case Closed:
                return "Closed";
            default:
                throw new RuntimeException( "Unknown status" );
        }
    }

    private void addContactsPanel() {
        if ( known ) {
            surveyContactsPanel = new SurveyContactsPanel( "contacts", new Model<Survey>( survey ) );
        } else {
            surveyContactsPanel = new Label(
                    "contacts",
                    new Model<String>( "Survey not found or survey service not accessible." ) );
        }
        add( surveyContactsPanel );
    }

    private void addStats() {
        survey.updateSurveyData( surveyService, getPlan() );
        surveyDataContainer.add( new Label(
                "toBeContacted",
                new Model<String>(
                        known ? ( "" + survey.getToBeContactedCount() ) : "" ) ) );
        surveyDataContainer.add( new Label(
                "contacted",
                new Model<String>(
                        known ? ( "" + survey.getContactedCount() ) : "" ) ) );
        surveyDataContainer.add( new Label(
                "inProgress",
                new Model<String>(
                        known ? ( "" + survey.getSurveyData().getCountInProgress() ) : "" ) ) );
        surveyDataContainer.add( new Label(
                "completed",
                new Model<String>(
                        known ? ( "" + survey.getSurveyData().getCountCompleted() ) : "" ) ) );
        surveyDataContainer.add( new Label(
                "abandoned",
                new Model<String>(
                        known ? ( "" + survey.getSurveyData().getCountAbandoned() ) : "" ) ) );
    }

    private void addLinks() {
        survey.updateSurveyData( surveyService, getPlan() );
        ExternalLink previewLink = new ExternalLink(
                "preview",
                known ? survey.getSurveyData().getPreviewLink() : "#" );
        previewLink.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
        surveyDataContainer.add( previewLink );
        ExternalLink reportingLink = new ExternalLink(
                "reporting",
                known ? survey.getSurveyData().getReportingLink() : "#" );
        reportingLink.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
        reportingLink.setVisible( survey.isLaunched() || survey.isClosed() );
        surveyDataContainer.add( reportingLink );
    }

    private void addLaunchButton() {
        IndicatingAjaxFallbackLink launchLink = new IndicatingAjaxFallbackLink( "launch" ) {
            public void onClick( AjaxRequestTarget target ) {
                try {
                    if ( surveyContactsPanel instanceof SurveyContactsPanel ) {
                        ( (SurveyContactsPanel) surveyContactsPanel ).updateContacts( target );
                    }
                    surveyService.launchSurvey( survey, getPlan(), getQueryService() );
                } catch ( SurveyException e ) {
                    LOG.warn("Failed lo launch survey", e);
                    target.prependJavaScript( "alert(\"Oops! Failed to launch survey.\")" );
                }
                update( target, new Change( Change.Type.Updated, survey ) );
            }
        };
        launchLink.setVisible( survey.canBeLaunched() );
        surveyDataContainer.add( launchLink );
    }

    private void addCloseButton() {
        ConfirmedAjaxFallbackLink closeLink = new ConfirmedAjaxFallbackLink(
                "close",
                "Close the survey?" ) {
            public void onClick( AjaxRequestTarget target ) {
                try {
                    surveyService.closeSurvey( survey, getPlan() );
                } catch ( SurveyException e ) {
                    target.prependJavaScript( "alert(\"Oops! Failed to close survey.\")" );
                }
                update( target, new Change( Change.Type.Updated, survey ) );
            }
        };
        closeLink.setVisible( survey.isLaunched() );
        surveyDataContainer.add( closeLink );
    }

    private void addCancelButton() {
        ConfirmedAjaxFallbackLink cancelLink = new ConfirmedAjaxFallbackLink(
                "cancel",
                "Cancel the survey?" ) {
            public void onClick( AjaxRequestTarget target ) {
                try {
                    surveyService.deleteSurvey( survey );
                } catch ( SurveyException e ) {
                    target.prependJavaScript( "alert(\"Oops! Failed to cancel survey.\")" );
                }
                update( target, new Change( Change.Type.Removed, survey ) );
            }
        };
        cancelLink.setVisible( survey.canBeCancelled() );
        surveyDataContainer.add( cancelLink );
    }

}
