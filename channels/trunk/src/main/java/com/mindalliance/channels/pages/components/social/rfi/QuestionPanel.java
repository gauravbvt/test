package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.services.QuestionService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.List;

/**
 * Question panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/5/12
 * Time: 10:27 AM
 */
public class QuestionPanel extends AbstractUpdatablePanel {

    @SpringBean
    private QuestionService questionService;

    private Component answerOptionsPanel;
    private WebMarkupContainer constraintsContainer;

    public QuestionPanel( String id, IModel<Question> questionModel ) {
        super( id, questionModel );
        init();
    }

    private void init() {
        addQuestionTextField();
        addAnswerTypeChoice();
        addAnswerOptionsPanel();
        addConstraints();
    }

    private void addQuestionTextField() {
        TextField<String> questionText = new TextField<String>(
                "questionText",
                new PropertyModel<String>( this, "questionText" )
        );
        questionText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        } );
        add( questionText );
    }

    private void addAnswerTypeChoice() {
        DropDownChoice<Question.Type> typeChoice = new DropDownChoice<Question.Type>(
                "questionType",
                new PropertyModel<Question.Type>( this, "questionType" ),
                getQuestionTypes(),
                new ChoiceRenderer<Question.Type>() {
                    @Override
                    public Object getDisplayValue( Question.Type type ) {
                        return type.getLabel();
                    }

                    @Override
                    public String getIdValue( Question.Type object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        typeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAnswerOptionsPanel();
                target.add( answerOptionsPanel );
                addConstraints();
                target.add( constraintsContainer );
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        } );
        add( typeChoice );
    }

    private List<Question.Type> getQuestionTypes() {
        return Arrays.asList( Question.Type.values() );
    }

    private void addAnswerOptionsPanel() {
        if ( getQuestionType() == Question.Type.CHOICE ) {
            answerOptionsPanel = new AnswerChoicesPanel(
                    "answerOptions",
                    new Model<Question>( getQuestion() )
            );
        } else {
            answerOptionsPanel = new Label( "answerOptions", "" );
            makeVisible( answerOptionsPanel, false );
        }
        answerOptionsPanel.setOutputMarkupId( true );
        addOrReplace( answerOptionsPanel );
    }

    private void addConstraints() {
        Question question = getQuestion();
        constraintsContainer = new WebMarkupContainer( "constraints" );
        constraintsContainer.setOutputMarkupId( true );
        makeVisible(
                constraintsContainer,
                question.isAnswerRequired()
                        || question.isOpenable()
                        || question.isMultipleable() );
        addOrReplace( constraintsContainer );
        // required
        AjaxCheckBox requiredCheckBox = new AjaxCheckBox(
                "answerRequired",
                new PropertyModel<Boolean>( this, "answerRequired" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        };
        requiredCheckBox.setVisible( question.isRequirable() );
        constraintsContainer.add( requiredCheckBox );
        // open ended
        AjaxCheckBox openEndedCheckBox = new AjaxCheckBox(
                "openEnded",
                new PropertyModel<Boolean>( this, "openEnded" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        };
        openEndedCheckBox.setVisible( question.isOpenable() );
        constraintsContainer.add( openEndedCheckBox );
        // multiple
        AjaxCheckBox multipleCheckBox = new AjaxCheckBox(
                "multiple",
                new PropertyModel<Boolean>( this, "multipleAnswers" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        };
        multipleCheckBox.setVisible( question.isMultipleable() );
        constraintsContainer.add( multipleCheckBox );
    }

    public String getQuestionText() {
        return getQuestion().getText();
    }

    public void setQuestionText( String text ) {
        if ( text != null && !text.isEmpty() ) {
            Question question = getQuestion();
            question.setText( text );
            questionService.save( question );
        }
    }

    public Question.Type getQuestionType() {
        return getQuestion().getType();
    }

    public void setQuestionType( Question.Type type ) {
        Question question = getQuestion();
        question.setType( type );
        questionService.save( question );
    }

    public boolean isAnswerRequired() {
        return getQuestion().isAnswerRequired();
    }

    public void setAnswerRequired( boolean required ) {
        Question question = getQuestion();
        question.setAnswerRequired( required );
        questionService.save( question );
    }

    public boolean isOpenEnded() {
        return getQuestion().isOpenEnded();
    }

    public void setOpenEnded( boolean openEnded ) {
        Question question = getQuestion();
        question.setOpenEnded( openEnded );
        questionService.save( question );
    }

    public boolean isMultipleAnswers() {
        return getQuestion().isMultipleAnswers();
    }

    public void setMultipleAnswers( boolean multiple ) {
        Question question = getQuestion();
        question.setMultipleAnswers( multiple );
        questionService.save( question );
    }


    private Question getQuestion() {
        return (Question) getModel().getObject();
    }


}
