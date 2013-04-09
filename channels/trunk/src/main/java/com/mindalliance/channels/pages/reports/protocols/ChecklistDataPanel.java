package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.api.procedures.GoalData;
import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Level;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 11:19 AM
 */
public class ChecklistDataPanel  extends AbstractDataPanel {

    private ChecklistData checklistData;
    private WebMarkupContainer taskDetailsContainer;
    private WebMarkupContainer receivesContainer;
    private WebMarkupContainer sendsContainer;

    public ChecklistDataPanel( String id,
                               ChecklistData checklistData,
                               ProtocolsFinder finder ) {
        super( id, finder );
        this.checklistData = checklistData;
        init();
    }

    private void init() {
        add( makeAnchor( "anchor", checklistData.getAnchor() ) );
        addTaskName();
        addFailureImpact();
        addTaskDetails();
        addDocumentation();
        addSteps();
    }

    private void addTaskName() {
        add( new Label( "taskName", checklistData.getTaskLabel() ) );
        //   add( new Label( "title", procedureData.getTitleOrRole() )  );
        //   add( new Label( "org", procedureData.getOrganizationLabel() )  );

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
    }

    private void addFailureImpact() {
        Level severity = getTask().getFailureSeverity();
        String severityText = getTask().getFailureImpact().toLowerCase();
        WebMarkupContainer impactContainer = new WebMarkupContainer( "failureImpact" );
        impactContainer.add( new AttributeModifier( "class", "failureImpact-small " + severityText) );
        add( impactContainer );
        impactContainer.setVisible( severity.ordinal() > Level.Low.ordinal() );
        Label severityLabel = new Label( "severity", severityText );
        impactContainer.add( severityLabel );
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
        contacts.addAll( checklistData.getAssignmentData().getTask().getTeamContacts() );
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
        return checklistData.getAssignmentData().getTask();
    }

    // DOCUMENTATION

    private void addDocumentation() {
        DocumentationData documentationData = checklistData.getAssignmentData().getTask().getDocumentation();
        DocumentationPanel docPanel = new DocumentationPanel( "documentation", documentationData, getFinder() );
        docPanel.setVisible( documentationData.hasReportableDocuments() );
        taskDetailsContainer.add( docPanel );
    }

    // STEPS

    private void addSteps() {
        WebMarkupContainer stepsContainer = new WebMarkupContainer( "checklist" );
        add( stepsContainer );
        ListView<ChecklistStepData> stepListView = new ListView<ChecklistStepData>(
                "steps",
                checklistData.getSteps()
        ) {
            @Override
            protected void populateItem( ListItem<ChecklistStepData> item ) {
                ChecklistStepData stepData = item.getModelObject();
                item.add( new ChecklistStepDataPanel(
                        "step",
                        checklistData.getAssignment().getPart(),
                        stepData,
                        item.getIndex(),
                        getFinder() ) );
            }
        };
        stepsContainer.add( stepListView );
    }

}
