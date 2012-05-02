package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Multiple choice panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 4:36 PM
 */
public class AnswerChoicePanel extends AbstractAnswerPanel {

    public AnswerChoicePanel( String id, IModel<Question> questionModel, IModel<RFI> rfiModel ) {
        super( id, questionModel, rfiModel );
    }

    @Override
    protected void moreInit() {
        addSingleChoice();
        addMultipleChoice();
    }
    private void addSingleChoice() {
        RadioChoice<String> singleChoice = new RadioChoice<String>(
                "singleChoice",
                new PropertyModel<String>( this, "singleChoice" ),
                getQuestion().getAnswerChoices()
        );
        singleChoice.add( new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                setChanged( true );
            }
        } );
        singleChoice.setVisible( !getQuestion().isMultipleAnswers() );
        getContainer().add( singleChoice );
    }

    public String getSingleChoice() {
        return getAnswerSet().getSingleChoice();
    }

    public void setSingleChoice( String choice ) {
        getAnswerSet().selectSingleChoice( choice );
    }

    private void addMultipleChoice() {
        WebMarkupContainer choicesContainer = new WebMarkupContainer( "multiChoicesContainer" );
        choicesContainer.setVisible( getQuestion().isMultipleAnswers() );
        getContainer().add( choicesContainer );
        ListView<AnswerMultipleChoice> choicesListView = new ListView<AnswerMultipleChoice>(
                "multiChoices",
                getMultipleChoices()
        ) {
            @Override
            protected void populateItem( ListItem<AnswerMultipleChoice> item ) {
                item.add( new Label( "choice", new Model<String>( item.getModelObject().getChoice() )) );
                item.add( new AjaxCheckBox(
                        "check",
                        new PropertyModel<Boolean>(item.getModelObject(), "selected")) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        setChanged( true );
                    }
                } );
            }
        };
        choicesContainer.add( choicesListView );
    }

    private List<AnswerMultipleChoice> getMultipleChoices(  ) {
        List<AnswerMultipleChoice> answerChoices = new ArrayList<AnswerMultipleChoice>();
        for (String choice : getQuestion().getAnswerChoices() ) {
            answerChoices.add( new AnswerMultipleChoice( choice ) );
        }
        return answerChoices;
    }

    public class AnswerMultipleChoice implements Serializable {
         private String choice;

        public AnswerMultipleChoice( String choice ) {
            this.choice = choice;
        }

        public String getChoice() {
            return choice;
        }

        public boolean isSelected() {
            return getAnswerSet().isChoiceSelected( choice );
        }

        public void setSelected( boolean selected ) {
           if (selected)
               getAnswerSet().addChoice( choice );
            else
               getAnswerSet().removeChoice( choice );
        }

    }

}
