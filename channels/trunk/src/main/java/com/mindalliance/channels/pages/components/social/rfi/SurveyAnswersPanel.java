package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.services.surveys.QuestionnaireService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.List;

/**
 * Survey answers panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/12
 * Time: 10:14 AM
 */
public class SurveyAnswersPanel extends AbstractUpdatablePanel {

    @SpringBean( name="surveysDao" )
    private SurveysDAO surveysDAO;
    @SpringBean
    private QuestionnaireService questionnaireService;
    private ListView<Question> answerSetListView;
    private WebMarkupContainer answerSetsContainer;
    private boolean readOnly = false;


    public SurveyAnswersPanel( String id, IModel<RFI> rfiModel, boolean readOnly ) {
        super( id, rfiModel );
        this.readOnly = readOnly;
        init();
    }

    public SurveyAnswersPanel( String id, IModel<RFI> rfiModel ) {
        this( id, rfiModel, false );
    }

    private void init() {
        answerSetsContainer = new WebMarkupContainer( "questionsAndAnswersContainer" );
        add( answerSetsContainer );
        addAnswerSets();
        addButtons();
    }

    private void addAnswerSets() {
        final RFI rfi = getRFI();
        answerSetListView = new ListView<Question>(
                "questionsAndAnswers",
                getQuestions()
        ) {
            @Override
            protected void populateItem( ListItem<Question> item ) {
                Question question = item.getModelObject();
                item.add( makeQuestionAnswerPanel( "questionAndAnswer",
                        new Model<Question>( question ),
                        new Model<RFI>( rfi ) ) );
            }
        };
        answerSetsContainer.add( answerSetListView );
    }

    private void addButtons() {
        AjaxLink<String> acceptLink = new AjaxLink<String>( "accept" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( getRFI().isPersisted() ) {
                answerSetListView.visitChildren(
                        AbstractAnswerPanel.class,
                        new IVisitor<AbstractAnswerPanel,Void>() {
                            @Override
                            public void component( AbstractAnswerPanel answerPanel, IVisit iVisit ) {
                                answerPanel.saveChanges();
                                iVisit.dontGoDeeper();
                            }
                        } );
                update( target, new Change( Change.Type.Updated, getRFI(), "submitted" ) );
                }
             }
        };
        acceptLink.setVisible( !readOnly && getRFI().isPersisted() );
        answerSetsContainer.add( acceptLink );
        AjaxLink<String> cancelLink = new AjaxLink<String>( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getRFI() ) );
            }
        };
        cancelLink.setVisible( !readOnly && getRFI().isPersisted() );
        answerSetsContainer.add( cancelLink );

    }

    private Component makeQuestionAnswerPanel( String id, Model<Question> questionModel, Model<RFI> rfiModel ) {
        switch ( questionModel.getObject().getType() ) {
            case STATEMENT:
                return new AnswerStatementPanel( id, questionModel, rfiModel );
            case YES_NO:
                return new AnswerYesNoPanel( id, questionModel, rfiModel );
            case SHORT_FORM:
                return new AnswerShortFormPanel( id, questionModel, rfiModel );
            case LONG_FORM:
                return new AnswerLongFormPanel( id, questionModel, rfiModel );
            case CHOICE:
                return new AnswerChoicePanel( id, questionModel, rfiModel );
            case DOCUMENT:
                return new AnswerDocumentPanel( id, questionModel, rfiModel );
            default:
                throw new RuntimeException( "Not implemented" );
        }
    }

    private List<Question> getQuestions() {
        return questionnaireService.listQuestions( getRFI().getRfiSurvey( getCommunityService() ).getQuestionnaireUid() );
    }

    private RFI getRFI() {
        return (RFI) getModel().getObject();
    }
}
