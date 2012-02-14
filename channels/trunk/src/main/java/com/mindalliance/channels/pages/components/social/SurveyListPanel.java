package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.surveys.SurveyException;
import com.mindalliance.channels.surveys.SurveyResponse;
import com.mindalliance.channels.surveys.SurveyService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Survey list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/14/11
 * Time: 9:34 AM
 */
public class SurveyListPanel extends AbstractSocialListPanel {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( SurveyListPanel.class );

 /*   @SpringBean    */
    private SurveyService surveyService;

    private static final int A_FEW = 5;
    private static final int MORE = 5;
    private int numberToShow = A_FEW;
    private boolean allShown = true;
    private WebMarkupContainer surveyRemindersContainer;
    private WebMarkupContainer noSurveyReminderContainer;
    private AjaxFallbackLink showAFew;
    private AjaxFallbackLink showMore;
    private AjaxFallbackLink showHideCompletedLink;
    private Label showHideCompletedLabel;
    private Date whenLastRefreshed;
    private boolean showCompleted = true;
    private final Updatable updatable;
    private String noReminderMessage;
    private List<SurveyResponse> surveyResponses;

    public SurveyListPanel( String id, Updatable updatable, boolean collapsible ) {
        super( id, collapsible );
        this.updatable = updatable;
        init();
    }

    protected void init() {
        super.init();
        noReminderMessage = "No surveys";
        surveyRemindersContainer = new WebMarkupContainer( "surveyRemindersContainer" );
        surveyRemindersContainer.setOutputMarkupId( true );
        add( surveyRemindersContainer );
        addSurveyReminders();
        addShowHideCompletedLink();
        addShowHideCompletedLabel();
        addShowMore();
        addShowAFew();
        adjustComponents();
        whenLastRefreshed = new Date();
    }

    private void addShowHideCompletedLabel() {
        showHideCompletedLabel = new Label(
                "hideShowCompleted",
                showCompleted ? "hide completed" : "show all" );
        showHideCompletedLabel.setOutputMarkupId( true );
        showHideCompletedLink.addOrReplace( showHideCompletedLabel );
    }

    private void addShowHideCompletedLink() {
        showHideCompletedLink = new AjaxFallbackLink( "hideShowCompletedLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                showCompleted = !showCompleted;
                noReminderMessage = showCompleted ? "no surveys" : "no uncompleted surveys";
                surveyResponses = null;
                addShowHideCompletedLabel();
                addSurveyReminders();
                target.add( showHideCompletedLabel );
                adjustComponents( target );
            }
        };
        add( showHideCompletedLink );

    }

    private void addShowMore() {
        showMore = new IndicatingAjaxFallbackLink( "showMore" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow += MORE;
                surveyResponses = null;
                addSurveyReminders();
                adjustComponents( target );
            }
        };
        showMore.setOutputMarkupId( true );
        surveyRemindersContainer.add( showMore );
    }

    private void addShowAFew() {
        showAFew = new IndicatingAjaxFallbackLink( "showFew" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow = A_FEW;
                showAFew.setEnabled( false );
                surveyResponses = null;
                addSurveyReminders();
                adjustComponents( target );
            }
        };
        showAFew.setOutputMarkupId( true );
        surveyRemindersContainer.add( showAFew );
    }

    private void addSurveyReminders() {
        List<SurveyResponse> surveyReminders = getSurveyReminders();
        ListView<SurveyResponse> surveyReminderListView = new ListView<SurveyResponse>(
                "surveyReminders",
                surveyReminders ) {
            protected void populateItem( ListItem<SurveyResponse> item ) {
                SurveyResponse surveyReminder = item.getModelObject();
                SurveyResponsePanel plannerMessagePanel = new SurveyResponsePanel(
                        "surveyReminder",
                        new Model<SurveyResponse>( surveyReminder ),
                        item.getIndex(),
                        updatable );
                item.add( plannerMessagePanel );
            }
        };
        surveyRemindersContainer.addOrReplace( surveyReminderListView );
        noSurveyReminderContainer = new WebMarkupContainer( "noSurveyReminders" );
        noSurveyReminderContainer.setOutputMarkupId( true );
        noSurveyReminderContainer.add( new Label( "message", noReminderMessage ) );
        addOrReplace( noSurveyReminderContainer );
    }

    private void adjustComponents( AjaxRequestTarget target ) {
        adjustComponents();
        target.add( surveyRemindersContainer );
        target.add( showAFew );
        target.add( showMore );
        target.add( noSurveyReminderContainer );
    }

    private void adjustComponents() {
        List<SurveyResponse> surveyResponses = getSurveyReminders();
        makeVisible( noSurveyReminderContainer, surveyResponses.isEmpty() );
        makeVisible( surveyRemindersContainer, !surveyResponses.isEmpty() );
        makeVisible( showMore, !allShown );
        makeVisible( showAFew, surveyResponses.size() > A_FEW );
    }

    private List<SurveyResponse> getSurveyReminders() {
        if ( surveyResponses == null ) {
            ChannelsUser user = getUser();
            try {
                surveyResponses = surveyService.findSurveysResponses(
                        user,
                        numberToShow + 1,
                        showCompleted );
                allShown = surveyResponses.size() <= numberToShow;
                if ( surveyResponses.size() > numberToShow ) {
                    surveyResponses.remove( numberToShow );
                }
                Collections.sort( surveyResponses, new Comparator<SurveyResponse>() {
                    @Override
                    public int compare( SurveyResponse sr1, SurveyResponse sr2 ) {
                        return sr2.getSurvey().getLaunchDate().compareTo( sr1.getSurvey().getLaunchDate() );
                    }
                } );
            } catch ( SurveyException e ) {
                LOG.warn( "Failed to get survey responses", e );
                surveyResponses = new ArrayList<SurveyResponse>();
                noReminderMessage = "Service failure!";
            }
        }
        return surveyResponses;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        surveyResponses = null;
        addSurveyReminders();
        adjustComponents( target );
        whenLastRefreshed = new Date();
    }


}
