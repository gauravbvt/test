package com.mindalliance.channels.playbook.pages.forms.tabs.role;

import com.mindalliance.channels.playbook.ifm.info.Assignment;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AssignmentPanel;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 7:09:26 PM
 */
public class RoleAssignmentsTab extends AbstractFormTab {

    protected ListChoice assignmentsChoice;
    protected AjaxButton deleteAssignmentButton;
    protected AjaxButton addAssignmentButton;

    protected WebMarkupContainer assignmentDiv;
    protected Assignment selectedAssignment;

    public RoleAssignmentsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        assignmentsChoice = new ListChoice("assignments", new Model(),
                                              new RefPropertyModel(getElement(), "assignments"));
        assignmentsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedAssignment = (Assignment)assignmentsChoice.getModelObject();
                loadAssignmentPanel();
                setAssignmentPanelVisibility(target);
                deleteAssignmentButton.setEnabled(selectedAssignment != null);
                target.addComponent(deleteAssignmentButton);
            }
        });
        addReplaceable(assignmentsChoice);
        addAssignmentButton = new AjaxButton("addAssignment") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Assignment assignment = new Assignment();
                RefUtils.add(getElement(), "assignments", assignment);
                target.addComponent(assignmentsChoice);
            }
        };
        addReplaceable(addAssignmentButton);
        deleteAssignmentButton = new AjaxButton("deleteAssignment") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "assignments", selectedAssignment);
                selectedAssignment = null;
                deleteAssignmentButton.setEnabled(false);
                loadAssignmentPanel();
                setAssignmentPanelVisibility(target);
                target.addComponent(deleteAssignmentButton);
                target.addComponent(assignmentsChoice);
            }
        };
        deleteAssignmentButton.setEnabled(false);
        addReplaceable(deleteAssignmentButton);

        assignmentDiv = new WebMarkupContainer("assignmentDiv");
        loadAssignmentPanel();
        addReplaceable(assignmentDiv);
        assignmentDiv.add(new AttributeModifier("style", true, new Model("display:none")));
    }

    private void setAssignmentPanelVisibility(AjaxRequestTarget target) {
        if (selectedAssignment != null) {
            assignmentDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            assignmentDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(assignmentDiv);
    }

    private void loadAssignmentPanel() {
        if (selectedAssignment == null) {
            Label dummyAssignmentPanel = new Label("assignment", "dummy");
            assignmentDiv.addOrReplace(dummyAssignmentPanel);
        } else {
            int index = ((List<Assignment>) RefUtils.get(getElement(), "assignments")).indexOf(selectedAssignment);
            AssignmentPanel assignmentPanel = new AssignmentPanel("assignment", this,
                    "assignments[" + index + "]",
                    EDITABLE, feedback);
            assignmentDiv.addOrReplace(assignmentPanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*assignments.*")) {
            target.addComponent(assignmentsChoice);
        }
    }

}
