package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.procedures.AssignmentData;
import com.mindalliance.channels.api.procedures.GoalData;
import com.mindalliance.channels.api.procedures.NotificationData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.RequestData;
import com.mindalliance.channels.api.procedures.TaskData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Procedure data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/12
 * Time: 12:37 PM
 */
public class ProcedureDataPanel extends AbstractDataPanel {

    private ProcedureData procedureData;
    private WebMarkupContainer taskDetailsContainer;
    private WebMarkupContainer receivesContainer;
    private WebMarkupContainer sendsContainer;

    public ProcedureDataPanel( String id, ProcedureData procedureData, ProtocolsFinder finder ) {
        super( id, finder );
        this.procedureData = procedureData;
        init();
    }

    private void init() {
        add( makeAnchor( "anchor", procedureData.getAnchor() ) );
        addTaskName();
        addTaskDetails();
        addReceives();
        addSends();
    }

    private void addTaskName() {
        add( new Label( "taskName", procedureData.getTaskLabel() ) );
        add( new Label( "title", procedureData.getTitleOrRole() )  );
        add( new Label( "org", procedureData.getOrganizationLabel() )  );

    }

    // TASK DETAILS

    private void addTaskDetails() {
        taskDetailsContainer = new WebMarkupContainer( "task" );
        add( taskDetailsContainer );
        taskDetailsContainer.add( makeAttributeContainer( "instructions", getTask().getInstructions() ) );
        taskDetailsContainer.add( makeAttributeContainer( "category", getTask().getCategory() ) );
        addLocation();
        addGoals();
        addTeammates();
        taskDetailsContainer.add( makeAttributeContainer( "failureImpact", getTask().getFailureImpact() ) );
    }

    private void addLocation() {
        PlaceData placeData = getTask().getLocation();
        WebMarkupContainer locationContainer = new WebMarkupContainer( "location" );
        locationContainer.setVisible( placeData != null );
        taskDetailsContainer.add( locationContainer );
        locationContainer.add(
                placeData == null
                        ? new Label( "place", "" )
                        : new Label( "place", placeData.getLabel() )
        );
    }

    private void addGoals() {
        List<GoalData> goalDataList = getTask().getGoals();
        WebMarkupContainer goalsContainer = new WebMarkupContainer( "goals" );
        goalsContainer.setVisible( !goalDataList.isEmpty() );
        taskDetailsContainer.add( goalsContainer );
        ListView<GoalData> goalsListView = new ListView<GoalData>(
                "goalLabels",
                goalDataList
        ) {
            @Override
            protected void populateItem( ListItem<GoalData> item ) {
                GoalData goalData = item.getModelObject();
                item.add( new Label( "goalLabel", goalData.getLabel() ) );
            }
        };
        goalsContainer.add( goalsListView );
    }

    private void addTeammates() {
        Set<ContactData> contacts = new HashSet<ContactData>(  );
        contacts.addAll( procedureData.getAssignment().getTask().getTeamContacts() );
        WebMarkupContainer teamContainer = new WebMarkupContainer( "teammates" );
        teamContainer.setVisible( !contacts.isEmpty() );
        taskDetailsContainer.add(  teamContainer );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "teammateContacts",
                new ArrayList<ContactData>( contacts )
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "teammateContact", item.getModelObject(), getFinder() ) );
            }
        };
        teamContainer.add( contactsListView );
    }


    private TaskData getTask() {
        return procedureData.getAssignment().getTask();
    }


    // RECEIVES

    private void addReceives() {
        receivesContainer = new WebMarkupContainer( "receives" );
        receivesContainer.setVisible( procedureData.hasReceives() );
        add( receivesContainer );
        addInNotifications();
        addOutRequests();
    }

    private void addInNotifications() {
        ListView<NotificationData> flowListView = new ListView<NotificationData>(
                "inNotifications",
                getAssignment().getInNotifications()
        ) {
            @Override
            protected void populateItem( ListItem<NotificationData> item ) {
                NotificationData notificationData = item.getModelObject();
                item.add( new CommitmentDataPanel( "inNotification", notificationData, true, getFinder() ) );
            }
        };
        receivesContainer.add( flowListView );
    }

    private void addOutRequests() {
        ListView<RequestData> flowListView = new ListView<RequestData>(
                "outRequests",
                getAssignment().getOutRequests()
        ) {
            @Override
            protected void populateItem( ListItem<RequestData> item ) {
                RequestData requestData = item.getModelObject();
                item.add( new CommitmentDataPanel( "outRequest", requestData, true, getFinder() ) );
            }
        };
        receivesContainer.add( flowListView );
    }



    // SENDS

    private void addSends() {
        sendsContainer = new WebMarkupContainer( "sends" );
        sendsContainer.setVisible( procedureData.hasSends() );
        add( sendsContainer );
        addOutNotifications();
        addInRequests();
    }

    private void addOutNotifications() {
        ListView<NotificationData> flowListView = new ListView<NotificationData>(
                "outNotifications",
                getAssignment().getOutNotifications()
        ) {
            @Override
            protected void populateItem( ListItem<NotificationData> item ) {
                NotificationData notificationData = item.getModelObject();
                item.add( new CommitmentDataPanel( "outNotification", notificationData, false, getFinder() ) );
            }
        };
        sendsContainer.add( flowListView );
    }

     private void addInRequests() {
        ListView<RequestData> flowListView = new ListView<RequestData>(
                "inRequests",
                getAssignment().getInRequests()
        ) {
            @Override
            protected void populateItem( ListItem<RequestData> item ) {
                RequestData requestData = item.getModelObject();
                item.add( new CommitmentDataPanel( "inRequest", requestData, false, getFinder() ) );
            }
        };
         sendsContainer.add( flowListView );
    }


    private AssignmentData getAssignment() {
        return procedureData.getAssignment();
    }


}
