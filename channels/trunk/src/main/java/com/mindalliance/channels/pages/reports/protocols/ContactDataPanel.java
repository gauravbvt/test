package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.SecurityClassificationData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Contact data panel
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 1:35 PM
 */
public class ContactDataPanel extends AbstractDataPanel {
    private ContactData contactData;
    private List<ChannelData> workAddresses;
    private List<ChannelData> personalAddresses;
    private ActorData agentData;


    public ContactDataPanel( String id, ContactData contactData, ProtocolsFinder finder ) {
        super( id, finder );
        this.contactData = contactData;
        initData();
        init();
    }

    private void initData() {
        workAddresses = contactData.getWorkChannels();
        personalAddresses = contactData.getPersonalChannels();
        agentData = findInScope( ActorData.class, contactData.getEmployment().getActorId() );
    }

    private void init() {
        add( makeAnchor( "anchor", contactData.anchor() ) );
        addName();
        addEmployment();
        addFeedbackPanel();
        addWorkAddresses();
        addPersonalAddresses();
        addAvailability();
        addLanguages();
        addClearances();
        addSupervisors();
    }

    private void addName() {
        String userFullName = contactData.getUserFullName();
        Label userFullNameLabel = new Label( "name", userFullName == null ? "" : userFullName + ", " );
        userFullNameLabel.setVisible( userFullName != null );
        add(  userFullNameLabel );
    }

    private void addEmployment() {
        add( new Label( "employment", contactData.getEmployment().getLabel() ) );
    }

    private void addFeedbackPanel() {
        String userFullName = contactData.getUserFullName();
        UserFeedbackPanel feedbackPanel = new UserFeedbackPanel(
                "feedback",
                null,
                "Feedback",
                Feedback.CHECKLISTS,
                "contact " + (userFullName == null ? "" : userFullName)
        );
        add( feedbackPanel );
    }


    private void addWorkAddresses() {
        WebMarkupContainer addressContainer = new WebMarkupContainer( "work" );
        addressContainer.setVisible( hasWorkAddresses() );
        add( addressContainer );
        addressContainer.add(
                hasWorkAddresses()
                        ? new ContactAddressesPanel( "addresses", workAddresses, getFinder() )
                        : new Label( "addresses", "" )
        );
    }

    private void addPersonalAddresses() {
        WebMarkupContainer addressContainer = new WebMarkupContainer( "personal" );
        addressContainer.setVisible( hasPersonalAddresses() );
        add( addressContainer );
        addressContainer.add(
                hasPersonalAddresses()
                        ? new ContactAddressesPanel( "addresses", personalAddresses, getFinder() )
                        : new Label( "addresses", "" )
        );
    }

    private boolean hasWorkAddresses() {
        return workAddresses != null && !workAddresses.isEmpty();
    }

    private boolean hasPersonalAddresses() {
        return personalAddresses != null && !personalAddresses.isEmpty();
    }


    private void addAvailability() {
        WebMarkupContainer availability = new WebMarkupContainer( "availability" );
        add( availability );
        availability.add(
                agentData != null
                        ? agentData.getAvailability().getAlways()
                            ? new Label("available", "Always" )
                            : new AvailabilityDataPanel( "available", agentData.getAvailability(), getFinder() )
                        : new Label("available", "" ) );
        availability.setVisible( agentData != null );
    }

    private void addLanguages() {
        WebMarkupContainer languages = new WebMarkupContainer( "languages" );
        add( languages );
        languages.add( new Label(
                "speaks",
                agentData != null
                        ? agentData.getLanguagesLabel()
                        : "???" ) );
    }

    private void addClearances() {
        List<SecurityClassificationData> clearances =
                agentData == null
                        ? new ArrayList<SecurityClassificationData>()
                        : agentData.getClassifications();
        WebMarkupContainer clearancesContainer = new WebMarkupContainer( "clearances" );
        clearancesContainer.setVisible( !clearances.isEmpty() );
        add( clearancesContainer );
        clearancesContainer.add( new SecurityClassificationsPanel(
                "classifications",
                clearances,
                getFinder() ) );
    }

    private void addSupervisors() {
        List<ContactData> supervisors = contactData.getSupervisorContacts();
        WebMarkupContainer supervisorsContainer = new WebMarkupContainer( "supervisors" );
        supervisorsContainer.setVisible(  !supervisors.isEmpty() );
        add(  supervisorsContainer );
        ListView<ContactData> supervisorsListView = new ListView<ContactData>(
                "contacts",
                supervisors
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject(), getFinder() ) );
            }
        };
        supervisorsContainer.add(  supervisorsListView );
    }

}
