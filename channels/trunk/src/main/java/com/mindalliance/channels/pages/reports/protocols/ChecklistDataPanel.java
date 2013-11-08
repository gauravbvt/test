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
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.diagrams.ChecklistFlowDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
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
    private AjaxLink<String> checklistFlowLink;
    private Label hideShowLabel;

    public ChecklistDataPanel( String id,
                               ChecklistData checklistData,
                               ProtocolsFinder finder ) {
        super( id, finder );
        this.checklistData = checklistData;
        init();
    }

    private void init() {
        add( makeAnchor( "anchor", checklistData.getAnchor() ) );
        addFeedbackPanel();
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

    private void addFeedbackPanel() {
        UserFeedbackPanel feedbackPanel = new UserFeedbackPanel(
                "feedback",
                checklistData.getAssignment().getPart(),
                "Feedback",
                Feedback.CHECKLISTS
        );
        add( feedbackPanel );
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
        WebMarkupContainer failureImpactContainer = new WebMarkupContainer( "failureImpactContainer" );
        failureImpactContainer.setVisible( severity.ordinal() > Level.Low.ordinal() );
        add( failureImpactContainer );
        String severityText = getTask().getFailureImpact().toLowerCase();
        WebMarkupContainer failureImpact = new WebMarkupContainer( "failureImpact" );
        failureImpact.add( new AttributeModifier( "class", "failure-impact " + severityText ) );
        failureImpactContainer.add( failureImpact );
        Label severityLabel = new Label( "severity", severityText );
        failureImpact.add( severityLabel );
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
        addChecklistFlowIcon();
        addChecklistFeedbackPanel();
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
        checklistContainer.setVisible( !checklistData.getSteps().isEmpty() );
        checklistContainer.add( stepListView );
    }

    private void addChecklistFeedbackPanel() {
        UserFeedbackPanel feedbackPanel = new UserFeedbackPanel(
                "feedback",
                checklistData.getAssignment().getPart(),
                "Feedback",
                Feedback.CHECKLISTS,
                "the checklist"
        );
        checklistContainer.add( feedbackPanel );
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
        checklistFlowLink = new AjaxLink<String>( "checklistFlowLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingChecklistFlow = !showingChecklistFlow;
                addChecklistFlowDiagram();
                target.add( checklistFlowContainer );
                addShowHideFlow();
                target.add( checklistFlowLink );
            }
        };
        checklistFlowLink.setOutputMarkupId( true );
        checklistContainer.add( checklistFlowLink );
        addShowHideFlow();
    }

    private void addShowHideFlow() {
        hideShowLabel = new Label( "showHideFlow", ( showingChecklistFlow ? "- Hide flow" : "+ Show flow" ) );
        hideShowLabel.setOutputMarkupId( true );
        addTipTitle(
                hideShowLabel,
                ( showingChecklistFlow ? "Hide" : "Show" )
                        + " the checklist flow diagram" );
        checklistFlowLink.addOrReplace( hideShowLabel );
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
