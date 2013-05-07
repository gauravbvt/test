package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.services.surveys.QuestionnaireService;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
    private QuestionnaireService questionnaireService;
    
    @SpringBean
    private RFIService rfiService;

    private Component answerOptionsPanel;
    private WebMarkupContainer constraintsContainer;
    private TextField<String> questionTextField;
    private DropDownChoice<Question.Type> typeChoice;
    private WebMarkupContainer requiredContainer;
    private AjaxCheckBox requiredCheckBox;
    private WebMarkupContainer openEndedContainer;
    private AjaxCheckBox openEndedCheckBox;
    private WebMarkupContainer multipleContainer;
    private AjaxCheckBox multipleCheckBox;
    private AjaxLink<String> deactivateIt;
    private AjaxLink<String> deleteIt;
    private AjaxLink<String> activateIt;
    private WebMarkupContainer answeredContainer;

    public QuestionPanel( String id, IModel<Question> questionModel ) {
        super( id, questionModel );
        init();
    }

    private void init() {
        addQuestionTextField();
        addAnswerTypeChoice();
        addAnswerOptionsPanel();
        addConstraints();
        addAnswered();
        addRetirement();
        updateFields();
    }

    private void updateFields() {
        Question question = getQuestion();
        questionTextField.setEnabled( canBeChanged( question ) );
        typeChoice.setEnabled( canBeChanged( question ) );
        requiredCheckBox.setEnabled( canBeChanged( question ) );
        makeVisible( requiredContainer, question.isRequirable() );
        openEndedCheckBox.setEnabled( canBeChanged( question ) );
        makeVisible( openEndedContainer, question.isOpenable() );
        multipleCheckBox.setEnabled( canBeChanged( question ) );
        multipleContainer.setVisible( question.isMultipleable() );
        makeVisible( deactivateIt, getQuestion().isActivated() );
        makeVisible( deleteIt, canBeDeleted() );
        makeVisible( activateIt, !getQuestion().isActivated() );
        makeVisible( answeredContainer, getQuestion().isAnswerable() );
    }

    private void updateFields( AjaxRequestTarget target ) {
        updateFields();
        target.add( this );
    }


    private void addQuestionTextField() {
        questionTextField = new TextField<String>(
                "questionText",
                new PropertyModel<String>( this, "questionText" )
        );
        questionTextField.setOutputMarkupId( true );
        questionTextField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        } );
        add( questionTextField );
    }

    private boolean canBeChanged( Question question ) {
        return !question.isActivated();

    }

    private void addAnswerTypeChoice() {
        typeChoice = new DropDownChoice<Question.Type>(
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
        typeChoice.setOutputMarkupId( true );
        typeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAnswerOptionsPanel();
                target.add( answerOptionsPanel );
                addConstraints();
                updateFields();
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
            answerOptionsPanel = new QuestionChoicesPanel(
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
        addRequired();
        addOpenEnded();
        addMultiple();
    }

    private void addRequired() {
        requiredContainer = new WebMarkupContainer( "requiredContainer" );
        requiredCheckBox = new AjaxCheckBox(
                "required",
                new PropertyModel<Boolean>( this, "answerRequired" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        };
        requiredContainer.add( requiredCheckBox );
        constraintsContainer.add( requiredContainer );
    }

    private void addOpenEnded() {
        openEndedContainer = new WebMarkupContainer( "openEndedContainer" );
        openEndedCheckBox = new AjaxCheckBox(
                "openEnded",
                new PropertyModel<Boolean>( this, "openEnded" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        };
        openEndedContainer.add( openEndedCheckBox );
        constraintsContainer.add( openEndedContainer );
    }

    private void addMultiple() {
        multipleContainer = new WebMarkupContainer( "multipleContainer" );

        multipleCheckBox = new AjaxCheckBox(
                "multiple",
                new PropertyModel<Boolean>( this, "multipleAnswers" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        };
        multipleContainer.add( multipleCheckBox );
        constraintsContainer.add( multipleContainer );
    }

    private void addAnswered() {
        answeredContainer = new WebMarkupContainer( "answeredContainer" );
        answeredContainer.setOutputMarkupId( true );
        answeredContainer.setVisible( getQuestion().isAnswerable() );
        answeredContainer.add( new Label( "answered", getAnsweredLabel() ) );
        addOrReplace( answeredContainer );
    }

    private void addRetirement() {
        WebMarkupContainer retirementContainer = new WebMarkupContainer( "retirementContainer" );
        retirementContainer.setOutputMarkupId( true );
        // activate it
        activateIt = new AjaxLink<String>( "activateIt" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setActivated( true );
                addAnswerOptionsPanel();
                target.add( answerOptionsPanel );
                updateFields( target );
                update( target, new Change( Change.Type.Updated, getQuestion(), "activated" ) );
            }
        };
        retirementContainer.add( activateIt );
        // deactivate it
        deactivateIt = new AjaxLink<String>( "deactivateIt" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setActivated( false );
                updateFields( target );
                addAnswerOptionsPanel();
                target.add( answerOptionsPanel );
                update( target, new Change( Change.Type.Updated, getQuestion(), "activated" ) );
            }
        };
        retirementContainer.add( deactivateIt );
        addOrReplace( retirementContainer );
        // delete it
        deleteIt = new AjaxLink<String>( "deleteIt" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Question question = getQuestion();
                deleteQuestion();
                update( target, new Change( Change.Type.Updated, question, "deleted" ) );
            }
        };
        retirementContainer.add( deleteIt );
        addOrReplace( retirementContainer );
    }

    private void deleteQuestion() {
        if ( canBeDeleted() ) {
            Question question = getQuestion();
            questionnaireService.deleteQuestion( question );
        }
    }

    private boolean canBeDeleted() {
        return !getQuestion().isActivated() && rfiService.getAnswerCount( getQuestion() ) == 0;
    }

    private String getAnsweredLabel() {
        int count = rfiService.getAnswerCount( getQuestion() );
        return count > 0
                ? "has been answered " + count + " times"
                : "has not yet been answered";
    }

    public String getQuestionText() {
        return getQuestion().getText();
    }

    public void setQuestionText( String text ) {
        if ( text != null && !text.isEmpty() ) {
            Question question = getQuestion();
            question.setText( text );
            questionnaireService.updateQuestion( question );
        }
    }

    public Question.Type getQuestionType() {
        return getQuestion().getType();
    }

    public void setQuestionType( Question.Type type ) {
        Question question = getQuestion();
        question.setType( type );
        questionnaireService.updateQuestion( question );
    }

    public boolean isAnswerRequired() {
        return getQuestion().isAnswerRequired();
    }

    public void setAnswerRequired( boolean required ) {
        Question question = getQuestion();
        question.setAnswerRequired( required );
        questionnaireService.updateQuestion( question );
    }

    public boolean isOpenEnded() {
        return getQuestion().isOpenEnded();
    }

    public void setOpenEnded( boolean openEnded ) {
        Question question = getQuestion();
        question.setOpenEnded( openEnded );
        questionnaireService.updateQuestion( question );
    }

    public boolean isMultipleAnswers() {
        return getQuestion().isMultipleAnswers();
    }

    public void setMultipleAnswers( boolean multiple ) {
        Question question = getQuestion();
        question.setMultipleAnswers( multiple );
        questionnaireService.updateQuestion( question );
    }

    public boolean isActivated() {
        return getQuestion().isActivated();
    }

    public void setActivated( boolean val ) {
        Question question = getQuestion();
        question.setActivated( val );
        questionnaireService.updateQuestion( question );
    }

    private Question getQuestion() {
        // Todo - causes a lot a repetitive db accesses
        return questionnaireService.refreshQuestion( (Question) getModel().getObject() );
    }

}
