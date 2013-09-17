package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractFlowData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.api.procedures.checklist.CommunicationStepData;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Communication step data panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 6:27 PM
 */
public class CommunicationStepDataPanel extends AbstractDataPanel {

    private final ChecklistStepData stepData;
    private boolean showingMore = false;
    private AjaxLink<String> moreLessButton;
    private CommitmentDataPanel commitmentDataPanel;

    public CommunicationStepDataPanel( String id, ChecklistStepData stepData, ProtocolsFinder finder ) {
        super( id, finder );
        this.stepData = stepData;
        init();
    }

    private void init() {
        addRequired();
        addCommunication();
        addToTBD();
        addContacts();
        addMoreLessButton();
        addCommitment();
    }

    private void addRequired() {
        Label requiredLabel = new Label( "required", getStep().isRequired() ? " - Required" : " - Optional" );
        add( requiredLabel );
    }

    private void addCommunication() {
        Flow.Intent intent = getStep().getSharing().getIntent();
        String message = getStep().getSharing().getName();
        if ( message == null ) message = "something";
        String label = getStep().isNotification()
                ? "Send "
                : getStep().isRequest()
                ? "Ask for "
                : "Answer with ";
        label += intent == null
                ? "information"
                : intent.getLabel().toLowerCase();
        label += " \"" + message + "\"";
        label += getStep().isRequest()
                ? " from "
                : " to ";
        add( new Label( "comm", label ) );
    }

    private void addToTBD() {
        Label tbd = new Label( "tbd", "(TBD)" );
        tbd.setVisible( getContacts().isEmpty() );
        add( tbd );
    }

    private void addContacts() {
        List<ContactData> contacts = getContacts();
        final int lastIndex = contacts.size() - 1;
        WebMarkupContainer contactsContainer = new WebMarkupContainer( "contactsContainer" );
        contactsContainer.setVisible( !contacts.isEmpty() );
        add( contactsContainer );
        String anyOf = contacts.size() > 1 ? "any of" : "";
        contactsContainer.add( new Label( "anyOf", anyOf ) );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                contacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject(), getFinder(), false ) );
                item.add( new Label( "sep", ( item.getIndex() != lastIndex ) ? "," : "" ) );
            }
        };
        contactsContainer.add( contactsListView );
    }

    private List<ContactData> getContacts() {
        return new ArrayList<ContactData>( stepData.allContacts() );
    }

    private void addMoreLessButton() {
        moreLessButton = new AjaxLink<String>( "moreLessButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingMore = !showingMore;
                addMoreLessButton();
                target.add( moreLessButton );
                addCommitment();
                target.add( commitmentDataPanel );
            }
        };
        moreLessButton.setOutputMarkupId( true );
        moreLessButton.add( new Label( "moreLess", showingMore ? "Less" : "More" ) );
        moreLessButton.add( new AttributeModifier( "class", "more" ) );
        addOrReplace( moreLessButton );
    }

    private void addCommitment() {
        commitmentDataPanel = new CommitmentDataPanel(
                "commitment",
                getFlowData(),
                !isReceived(),
                getFinder()
        );
        commitmentDataPanel.setOutputMarkupId( true );
        makeVisible( commitmentDataPanel, showingMore );
        addOrReplace( commitmentDataPanel );
    }

    private boolean isReceived() {
        return !getStep().isRequest();
    }

    private AbstractFlowData getFlowData() {
        return getCommunicationStepData().getFlowData();
    }

    private CommunicationStepData getCommunicationStepData() {
        return getStep().isRequest()
                ? stepData.getRequestStep()
                : getStep().isNotification()
                ? stepData.getNotificationStep()
                : stepData.getAnswerStep();
    }

    private CommunicationStep getStep() {
        return (CommunicationStep) stepData.getStep();
    }

}
