package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.api.procedures.GoalData;
import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.pages.components.diagrams.ChecklistFlowDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

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
public class ChecklistDataPanel extends AbstractDataPanel {

    private ChecklistData checklistData;
    private WebMarkupContainer taskDetailsContainer;
    private WebMarkupContainer receivesContainer;
    private WebMarkupContainer sendsContainer;
    private boolean showingChecklistFlow;
    private WebMarkupContainer checklistContainer;
    private WebMarkupContainer checklistFlowContainer;

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
        addChecklistFlowDiagram();
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
        impactContainer.add( new AttributeModifier( "class", "failure-impact " + severityText ) );
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
        Set<ContactData> contacts = new HashSet<ContactData>();
        contacts.addAll( checklistData.getAssignmentData().getTask().getTeamContacts() );
        WebMarkupContainer teamContainer = new WebMarkupContainer( "teammates" );
        teamContainer.setVisible( !contacts.isEmpty() );
        taskDetailsContainer.add( teamContainer );
        // teammates label
        Label teammatesLabel = new Label(
                "teammatesLabel",
                contacts.size() > 1
                        ? "My teammates are"
                        : "My teammate is"
        );
        teamContainer.add( teammatesLabel );
        // teammates list
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
        checklistContainer = new WebMarkupContainer( "checklist" );
        add( checklistContainer );
        checklistContainer.add( new Label(
                "confirmed",
                checklistData.getConfirmed() ? "Confirmed" : "Not confirmed" ) );
        addChecklistFlowIcon();
        ListView<ChecklistStepData> stepListView = new ListView<ChecklistStepData>(
                "steps",
                checklistData.getSteps()
        ) {
            @Override
            protected void populateItem( ListItem<ChecklistStepData> item ) {
                ChecklistStepData stepData = item.getModelObject();
                item.add( makeChecklistStepDataPanel(
                        "step",
                        checklistData.getAssignment().getPart(),
                        stepData,
                        item.getIndex(),
                        getFinder() ) );
            }
        };
        checklistContainer.add( stepListView );
    }

    private ChecklistStepDataPanel makeChecklistStepDataPanel( String step,
                                                           Part part,
                                                           ChecklistStepData stepData,
                                                           int index,
                                                           ProtocolsFinder finder ) {
        Step aStep = stepData.getStep();
        return aStep.isActionStep()
                ? new ActionStepDataPanel( "step", part, stepData, index, finder )
                : aStep.isCommunicationStep()
                ? new CommunicationStepDataPanel( "step", part, stepData, index, finder )
                : aStep.isReceiptConfirmation()
                ? new ReceiptConfirmationDataPanel( "step", part, stepData, index, finder )
                : new SubTaskStepDataPanel( "step", part, stepData, index, finder );
    }


    private void addChecklistFlowIcon() {
        AjaxLink<String> checklistFlowLink = new AjaxLink<String>( "checklist-flow-link") {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingChecklistFlow = !showingChecklistFlow;
                addChecklistFlowDiagram();
                target.add( checklistFlowContainer );
            }
        };
        checklistContainer.add( checklistFlowLink );
        addTipTitle(
                checklistFlowLink,
                "Open/close the checklist flow diagram" );
    }

    private void addChecklistFlowDiagram() {
        checklistFlowContainer = new WebMarkupContainer( "checklistFlow" );
        checklistFlowContainer.setOutputMarkupId( true );
        makeVisible( checklistFlowContainer, showingChecklistFlow );
        Settings settings = new Settings( "." + getCssClass() + " " + ".picture", null, null, true, true );
        Component diagram = showingChecklistFlow
                ? new ChecklistFlowDiagramPanel(
                "flowDiagram",
                new PropertyModel<Part>( this, "part" ),
                settings,
                false )  // todo - make interactive to link to subtasks in report
                : new Label( "flowDiagram", "" );
        diagram.add( new AttributeModifier( "class", getCssClass() ) );
        checklistFlowContainer.add( diagram );
        checklistContainer.addOrReplace( checklistFlowContainer );
    }

    private String getCssClass() {
        return "flow-" + getPart().getId();
    }

    public Part getPart() {
        return checklistData.getAssignment().getPart();
    }
}
