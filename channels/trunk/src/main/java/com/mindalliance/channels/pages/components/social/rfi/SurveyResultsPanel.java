package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Panel for viewing the results of a survey.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/8/12
 * Time: 2:40 PM
 */
public class SurveyResultsPanel extends AbstractUpdatablePanel {

    private static final boolean SHARED_ONLY = true;

    private static final boolean INCLUDE_SELF = true;

    @SpringBean( name="surveysDao" )
    private SurveysDAO surveysDAO;

    @SpringBean
    private RFIService rfiService;

    private Question selectedQuestion;
    private Component questionResultsPanel;

    public SurveyResultsPanel( String id, Model<RFISurvey> rfiSurveyModel ) {
        super( id, rfiSurveyModel );
        init();
    }

    @Override
    // Use the domain community
    public CommunityService getCommunityService() {
        return getCommunityService( getDomainPlanCommunity() );
    }


    private void init() {
        addCounts();
        addQuestions();
        addQuestionResultsPanel();
    }

    private void addCounts() {
        int participationCount = rfiService.findParticipants( getCommunityService(), getRFISurvey() ).size();
        int answeredCount = surveysDAO.findAnsweringRFIs( getCommunityService(), getRFISurvey() ).size();
        Label countLabel = new Label(
                "count",
                ( answeredCount
                        + " out of "
                        + participationCount
                        + " survey "
                        + ( participationCount > 1 ? "participants " : "participant " ) )
                        + "answered" );
        add( countLabel );

    }

    private void addQuestions() {
        DropDownChoice<Question> questionsChoice = new DropDownChoice<Question>(
                "questions",
                new PropertyModel<Question>( this, "selectedQuestion" ),
                getAnswerableQuestions(),
                new IChoiceRenderer<Question>() {
                    @Override
                    public Object getDisplayValue( Question question ) {
                        return ChannelsUtils.convertTemplate(
                                question.getText(),
                                getRFISurvey().getAbout( getCommunityService() ) );
                    }

                    @Override
                    public String getIdValue( Question object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        questionsChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addQuestionResultsPanel();
                target.add( questionResultsPanel );
            }
        } );
        add( questionsChoice );
    }

    private List<Question> getAnswerableQuestions() {
        return surveysDAO.listAnswerableQuestions( getRFISurvey() );
    }

    private void addQuestionResultsPanel() {
        if ( selectedQuestion == null ) {
            questionResultsPanel = new Label( "questionResults", "" );
        } else {
            Map<String, Set<String>> results = surveysDAO.processAnswers(
                    getCommunityService(),
                    getRFISurvey(),
                    getSelectedQuestion(),
                    !SHARED_ONLY,
                    null
            );
            questionResultsPanel = new QuestionResultsPanel( "questionResults", results );
        }
        questionResultsPanel.setOutputMarkupId( true );
        addOrReplace( questionResultsPanel );
    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion( Question question ) {
        selectedQuestion = question;
    }

    public RFISurvey getRFISurvey() {
        return (RFISurvey) getModel().getObject();
    }

}
