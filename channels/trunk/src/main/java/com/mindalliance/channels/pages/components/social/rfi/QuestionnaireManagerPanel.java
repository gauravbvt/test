package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.services.surveys.QuestionnaireService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/6/12
 * Time: 12:43 PM
 */
public class QuestionnaireManagerPanel extends AbstractUpdatablePanel {

    private static final int MAX_LENGTH = 50;

    @SpringBean
    private QuestionnaireService questionnaireService;


    private Question selectedQuestion;
    private WebMarkupContainer questionsContainer;
    private Component questionPanel;
    private List<Question> questions;
    private TextField<String> nameField;

    public QuestionnaireManagerPanel( String id, IModel<Questionnaire> questionnaireModel ) {
        super( id, questionnaireModel );
        init();
    }

    private void init() {
        if ( !getQuestions().isEmpty() ) {
            selectedQuestion = getQuestions().get( 0 );
        }
        addNameField();
        addAboutChoice();
        addQuestionList();
        addQuestionPanel();
        addNewQuestionButton();
    }

    private void addNameField() {
        nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "questionnaireName" ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addQuestionList();
                target.add( questionsContainer );
                target.add( nameField );
                update( target, new Change( Change.Type.Updated, getQuestionnaire() ) );
            }
        } );
        nameField.setOutputMarkupId( true );
        nameField.setEnabled( !getQuestionnaire().isActive() );
        addOrReplace( nameField );
    }

    private void addAboutChoice() {
        DropDownChoice<String> aboutChoice = new DropDownChoice<String>(
                "about",
                new PropertyModel<String>( this, "about" ),
                getAboutChoices() );
        aboutChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestionnaire() ) );
            }
        } );
        aboutChoice.setEnabled( !getQuestionnaire().isActive() );
        add( aboutChoice );
    }

    private List<String> getAboutChoices() {
        List<String> choices = new ArrayList<String>();
        choices.addAll( ModelObject.CLASS_LABELS );
        return choices;
    }


    private void addQuestionList() {
        questionsContainer = new WebMarkupContainer( "questionsContainer" );
        questionsContainer.setOutputMarkupId( true );
        questionsContainer.add( new ListView<Question>(
                "questions",
                getQuestions()
        ) {
            @Override
            protected void populateItem( final ListItem<Question> item ) {
                final Question question = item.getModelObject();
                // index
                item.add( new Label( "index", Integer.toString( question.getIndex() + 1 ) ) );
                // question label, abbreviated
                AjaxLink<String> questionLink = new AjaxLink<String>( "select" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        selectedQuestion = question;
                        addQuestionPanel();
                        target.add( questionPanel );
                    }
                };
                item.add( questionLink );
                String questionText = getQuestionText( question );
                Label questionLabel = new Label(
                        "questionLabel",
                        StringUtils.abbreviate( questionText, MAX_LENGTH ) );
                if ( questionText.length() > MAX_LENGTH ) {
                    addTipTitle( questionLabel, new Model<String>( question.getText() ) );
                }
                questionLink.add( questionLabel );
                // move up
                AjaxLink<String> moveUpLink = new AjaxLink<String>( "moveUp" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        questions = null;
                        moveUp( question );
                        addQuestionList();
                        target.add( questionsContainer );
                    }
                };
                moveUpLink.setVisible( !isFirstQuestion( question ) );
                item.add( moveUpLink );
                // move down
                AjaxLink<String> moveDownLink = new AjaxLink<String>( "moveDown" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        questions = null;
                        moveDown( question );
                        addQuestionList();
                        target.add( questionsContainer );
                    }
                };
                moveDownLink.setVisible( !isLastQuestion( question ) );
                item.add( moveDownLink );
            }

        } );
        addOrReplace( questionsContainer );
    }

    private String getQuestionText( Question question ) {
        String text = question.getText();
        return ( !question.isActivated() ? "(Inactive) " : "" ) + ( text.isEmpty() ? "--TBD--" : text );
    }


    private void moveUp( Question question ) {
        questionnaireService.moveUp( question );
    }

    private void moveDown( Question question ) {
        questionnaireService.moveDown( question );
    }

    private boolean isLastQuestion( Question question ) {
        List<Question> qs = getQuestions();
        return !qs.isEmpty() && question.equals( qs.get( qs.size() - 1 ) );
    }

    private boolean isFirstQuestion( Question question ) {
        List<Question> qs = getQuestions();
        return !qs.isEmpty() && question.equals( qs.get( 0 ) );
    }

    private List<Question> getQuestions() {
        if ( questions == null ) {
            questions = questionnaireService.listQuestions( getQuestionnaire().getUid() );
        }
        return questions;
    }

    private void addQuestionPanel() {
        Question question = getQuestion();
        if ( question == null ) {
            questionPanel = new Label( "question", "" );
        } else {
            questionPanel = new QuestionPanel( "question", new Model<Question>( question ) );
        }
        questionPanel.setOutputMarkupId( true );
        makeVisible( questionPanel, question != null );
        addOrReplace( questionPanel );
    }

    private void addNewQuestionButton() {
        AjaxLink<String> addQuestionButton = new AjaxLink<String>( "add" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                questions = null;
                selectedQuestion = addQuestion();
                addQuestionList();
                target.add( questionsContainer );
                addQuestionPanel();
                target.add( questionPanel );
            }
        };
        add( addQuestionButton );
    }

    private Question addQuestion() {
        Questionnaire questionnaire = questionnaireService.load( getQuestionnaire().getUid() );
        return questionnaireService.addNewQuestion( getUser(), questionnaire );
    }

    public String getQuestionnaireName() {
        return getQuestionnaire().getName();
    }

    public void setQuestionnaireName( String name ) {
        if ( name != null && !name.isEmpty() ) {
            String safeName = ChannelsUtils.cleanUpPhrase( name );
            Questionnaire questionnaire = getQuestionnaire();
            questionnaire.setName( safeName );
            questionnaireService.save( questionnaire );
        }
    }

    public String getAbout() {
        return getQuestionnaire().getAbout();
    }

    public void setAbout( String about ) {
        Questionnaire questionnaire = getQuestionnaire();
        questionnaire.setAbout( about );
        questionnaireService.save( questionnaire );
    }

    private Questionnaire getQuestionnaire() {
        Questionnaire questionnaire = (Questionnaire) getModel().getObject();
        return questionnaireService.refresh( questionnaire );
    }

    @SuppressWarnings("unchecked")
    private Question getQuestion() {
/*
        if ( selectedQuestion != null )
            questionService.refresh( selectedQuestion );
*/
        return selectedQuestion == null
                ? null
                : (Question) CollectionUtils.find(
                getQuestionnaire().getQuestions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Question) object ).getUid().equals( selectedQuestion.getUid() );
                    }
                }
        );
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() && change.isForInstanceOf( Questionnaire.class ) ) {
            addNameField();
            target.add( nameField );
            modified( getQuestionnaire() );
        }
        if ( change.isUpdated() && change.isForInstanceOf( Question.class ) ) {
            questions = null;
            addQuestionList();
            target.add( questionsContainer );
            if ( change.isForProperty( "deleted" ) ) {
                selectedQuestion = null;
                addQuestionList();
                target.add( questionsContainer );
                addQuestionPanel();
                target.add( questionPanel );
            }
            modified( getQuestionnaire() );
        }
        super.updateWith( target, new Change( Change.Type.Updated, getQuestionnaire() ), updated );
    }

    private void modified( Questionnaire questionnaire ) {
        questionnaire.setLastModified( new Date() );
        questionnaireService.save( questionnaire );
    }

}
