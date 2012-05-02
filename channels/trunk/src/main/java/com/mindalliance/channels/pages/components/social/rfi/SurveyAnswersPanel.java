package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.services.QuestionService;
import com.mindalliance.channels.social.services.SurveysDAO;
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

    @SpringBean
    private QuestionService questionService;
    @SpringBean
    private SurveysDAO surveysDAO;
    private ListView<Question> answerSetListView;
    private WebMarkupContainer answerSetsContainer;

    public SurveyAnswersPanel( String id, IModel<RFI> rfiModel ) {
        super( id, rfiModel );
        init();
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
        };
        answerSetsContainer.add( acceptLink );
        AjaxLink<String> cancelLink = new AjaxLink<String>( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getRFI() ) );
            }
        };
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
        return questionService.listQuestions( getRFI().getRfiSurvey().getQuestionnaire() );
    }

    private RFI getRFI() {
        return (RFI) getModel().getObject();
    }
}
