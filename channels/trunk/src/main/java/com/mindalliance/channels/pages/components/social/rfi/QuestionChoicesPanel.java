package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.services.surveys.QuestionnaireService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Asnwer choices panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/12
 * Time: 11:27 AM
 */
public class QuestionChoicesPanel extends AbstractUpdatablePanel {

    @SpringBean
    private QuestionnaireService questionnaireService;

    private static final int MAX_LENGTH = 30;

    private List<String> answerChoices;

    private WebMarkupContainer answerChoicesContainer;
    private TextField<String> newChoiceField;

    public QuestionChoicesPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addAnswerChoiceList();
        addNewChoiceField();
    }

    private void addAnswerChoiceList() {
        answerChoicesContainer = new WebMarkupContainer( "answerChoicesContainer" );
        answerChoicesContainer.setOutputMarkupId( true );
        answerChoicesContainer.add( new ListView<String>(
                "answerChoices",
                getAnswerChoices()
        ) {
            @Override
            protected void populateItem( final ListItem<String> item ) {
                final String choice = item.getModelObject();
                // index
                item.add( new Label( "index", Integer.toString( getAnswerChoices().indexOf( choice ) + 1 ) ) );
                // Choice label
                Label choiceLabel = new Label(
                        "answerLabel",
                        StringUtils.abbreviate( choice, MAX_LENGTH ) );
                if ( choice.length() > MAX_LENGTH ) {
                    addTipTitle( choiceLabel, new Model<String>( choice ) );
                }
                item.add( choiceLabel );
                // move up
                AjaxLink<String> moveUpLink = new AjaxLink<String>( "moveUp" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        answerChoices = null;
                        moveUp( choice );
                        addAnswerChoiceList();
                        target.add( answerChoicesContainer );
                        update( target, new Change( Change.Type.Updated, getQuestion() ) );
                    }
                };
                moveUpLink.setVisible( !getQuestion().isActivated() && !isFirstChoice( choice ) );
                item.add( moveUpLink );
                // move down
                AjaxLink<String> moveDownLink = new AjaxLink<String>( "moveDown" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        answerChoices = null;
                        moveDown( choice );
                        addAnswerChoiceList();
                        target.add( answerChoicesContainer );
                        update( target, new Change( Change.Type.Updated, getQuestion() ) );
                    }
                };
                moveDownLink.setVisible( !getQuestion().isActivated() && !isLastChoice( choice ) );
                item.add( moveDownLink );
                // delete
                AjaxLink<String> deleteLink = new AjaxLink<String>( "delete" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        answerChoices = null;
                        delete( choice );
                        addAnswerChoiceList();
                        target.add( answerChoicesContainer );
                        update( target, new Change( Change.Type.Updated, getQuestion() ) );
                    }
                };
                deleteLink.setVisible( !getQuestion().isActivated() );
                item.add( deleteLink );
            }

        } );
        addOrReplace( answerChoicesContainer );
    }

    private List<String> getAnswerChoices() {
        if ( answerChoices == null ) {
            answerChoices = getQuestion().getAnswerChoices();
        }
        return answerChoices;
    }

    private boolean isFirstChoice( String choice ) {
        return getAnswerChoices().size() != 0 && getAnswerChoices().get( 0 ).equals( choice );
    }

    private boolean isLastChoice( String choice ) {
        return getAnswerChoices().size() != 0
                && getAnswerChoices().get( getAnswerChoices().size() - 1 ).equals( choice );
    }

    private void moveUp( String choice ) {
        questionnaireService.moveUpAnswerChoice( getQuestion(), choice );
        answerChoices = null;
    }

    private void moveDown( String choice ) {
        questionnaireService.moveDownAnswerChoice( getQuestion(), choice );
        answerChoices = null;
    }

    private void delete( String choice ) {
        questionnaireService.deleteAnswerChoice( getQuestion(), choice );
        answerChoices = null;
    }

    private void addNewChoiceField() {
        newChoiceField = new TextField<String>(
                "newChoice",
                new PropertyModel<String>( this, "newChoice" ) );
        newChoiceField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAnswerChoiceList();
                target.add( answerChoicesContainer );
                target.add( newChoiceField );
                update( target, new Change( Change.Type.Updated, getQuestion() ) );
            }
        } );
        add( newChoiceField );
    }

    public String getNewChoice() {
        return "";
    }

    public void setNewChoice( String choice ) {
        if ( choice != null && !choice.isEmpty() ) {
            questionnaireService.addAnswerChoice( getQuestion(), choice );
            answerChoices = null;
        }
    }

    private Question getQuestion() {
        return questionnaireService.refreshQuestion( (Question) getModel().getObject() );
    }
}
