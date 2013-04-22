package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AssignmentData;
import com.mindalliance.channels.api.procedures.RequestData;
import com.mindalliance.channels.core.model.Flow;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Query answer panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/10/13
 * Time: 2:24 PM
 */
public class QueryAnswerPanel extends AbstractDataPanel {

    private final RequestData requestData;
    private final int index;
    private WebMarkupContainer queryContainer;
    private boolean showingMore = false;
    private AjaxLink<String> moreLessButton;
    private CommitmentDataPanel commitmentDataPanel;

    public QueryAnswerPanel( String id, RequestData requestData, int index, ProtocolsFinder finder ) {
        super( id, finder );
        this.requestData = requestData;
        this.index = index;
        init();
    }

    private void init() {
        addQueryContainer();
        addAnswer();
        addChecklistLink();
        addTBD();
        addContacts();
        addMoreLessButton();
        addCommitment();
    }

    private void addQueryContainer() {
        queryContainer = new WebMarkupContainer( "query" );
        String cssClasses = index % 2 == 0 ? "data-table step even-step" : "data-table step odd-step";
        queryContainer.add( new AttributeModifier( "class", cssClasses ) );
        add( queryContainer );
    }

    private void addAnswer() {
        Flow.Intent intent =getSharing().getIntent();
        String message = getSharing().getName();
        if ( message == null ) message = "something";
        String label = "I provide ";
        label += intent == null
                ? "information"
                : intent.getLabel().toLowerCase();
        label += " \"" + message + "\"";
        label += " acquired for or from doing";
        queryContainer.add( new Label( "answer", label ) );
    }

    private Flow getSharing() {
        return requestData.getSharing();
    }

    private void addChecklistLink() {
        queryContainer.add( new ChecklistDataLinkPanel( "checklistLink", getAcquiringAssignmentData(), getFinder() ) );
    }

    private AssignmentData getAcquiringAssignmentData() {
        return requestData.getAssignmentData();
    }

    private void addTBD() {
        Label tbd = new Label( "tbd", "(TBD)" );
        tbd.setVisible( getContacts().isEmpty() );
        queryContainer.add( tbd );
    }

    private List<ContactData> getContacts() {
        return requestData.getContacts();
    }

    private void addContacts() {
        List<ContactData> contacts = getContacts();
        final int lastIndex = contacts.size() - 1;
        WebMarkupContainer contactsContainer = new WebMarkupContainer( "contactsContainer" );
        contactsContainer.setVisible( !contacts.isEmpty() );
        queryContainer.add( contactsContainer );
        String anyOf = contacts.size() > 1 ? "any of" : "";
        contactsContainer.add( new Label( "anyOf", anyOf ) );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                contacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject(), getFinder() ) );
                item.add( new Label( "sep", ( item.getIndex() != lastIndex ) ? "," : "" ) );
            }
        };
        contactsContainer.add( contactsListView );
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
        queryContainer.addOrReplace( moreLessButton );
    }

    private void addCommitment() {
        commitmentDataPanel = new CommitmentDataPanel(
                "commitment",
                requestData,
                false,
                getFinder()
        );
        commitmentDataPanel.setOutputMarkupId( true );
        makeVisible( commitmentDataPanel, showingMore );
        addOrReplace( commitmentDataPanel );
    }

}
