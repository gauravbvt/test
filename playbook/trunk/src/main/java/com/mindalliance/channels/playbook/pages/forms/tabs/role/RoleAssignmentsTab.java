package com.mindalliance.channels.playbook.pages.forms.tabs.role;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AssignmentPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.info.Assignment;
import com.mindalliance.channels.playbook.ifm.model.Role;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 7:09:26 PM
 */
public class RoleAssignmentsTab extends AbstractModelElementFormTab {

    protected WebMarkupContainer assignmentsDiv;
    protected RefreshingView assignmentsView;
    protected AjaxButton newAssignmentButton;
    protected Assignment selectedAssignment;
    protected WebMarkupContainer assignmentDiv;

    public RoleAssignmentsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        assignmentsDiv = new WebMarkupContainer("assignmentsDiv");
        assignmentsView = new RefreshingView("assignments", new RefPropertyModel(getElement(), "assignments")) {
            List<Assignment> assignments = (List<Assignment>) RefUtils.get(getElement(), "assignments");

            protected Iterator getItemModels() {
                return new ModelIteratorAdapter(assignments.iterator()) {
                    protected IModel model(Object assignment) {
                        return new Model((Assignment) assignment);
                    }
                };
            }

            protected void populateItem(final Item item) {
                final Assignment assignment = (Assignment) item.getModelObject();
                AjaxLink assignmentLink = new AjaxLink("assignmentLink") {
                    public void onClick(AjaxRequestTarget target) {
                        selectedAssignment = assignment;
                        loadAssignmentPanel();
                        updateAssignmentPanel(target);
                    }
                };
                Label assignmentLabel = new Label("assignmentString", new Model(assignment.toString()));
                assignmentLink.add(assignmentLabel);
                item.add(assignmentLink);
                AjaxLink deleteAssignmentLink = new AjaxLink("deleteAssignment") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getElement(), "assignments", assignment);
                        selectedAssignment = null;
                        loadAssignmentPanel();
                        updateAssignmentPanel(target);
                        target.addComponent(assignmentsDiv);
                    }
                };
                item.add(deleteAssignmentLink);
            }
        };
        assignmentsDiv.add(assignmentsView);
        addReplaceable(assignmentsDiv);
        newAssignmentButton = new AjaxButton("newAssignment") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Assignment assignment = new Assignment();
                RefUtils.add(getElement(), "assignments", assignment);
                target.addComponent(assignmentsDiv);
            }
        };
        addReplaceable(newAssignmentButton);
        assignmentDiv = new WebMarkupContainer("assignmentDiv");
        addReplaceable(assignmentDiv);
        loadAssignmentPanel();
        assignmentDiv.add(new AttributeModifier("style", true, new Model("display:none")));
    }

    private void loadAssignmentPanel() {
        if (selectedAssignment != null) {
            Role role = (Role)getElement().deref();
            int index = role.getAssignments().indexOf(selectedAssignment);
            AssignmentPanel assignmentPanel = new AssignmentPanel("assignment", this, "assignments[" + index + "]", EDITABLE, feedback);
            assignmentDiv.addOrReplace(assignmentPanel);
        } else {
            Label assignmentLabel = new Label("assignment", "dummy");
            assignmentDiv.addOrReplace(assignmentLabel);
        }
    }

    private void updateAssignmentPanel(AjaxRequestTarget target) {
        if (selectedAssignment == null) {
            assignmentDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        } else {
            assignmentDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        target.addComponent(assignmentDiv);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*assignments.*")) {
            target.addComponent(assignmentsDiv);
        }
    }
}
