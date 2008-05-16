package com.mindalliance.channels.playbook.pages.forms.tabs.assignation;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AssignmentPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 8:58:39 PM
 */
public class AssignationAssignmentTab extends AbstractFormTab {

    protected DynamicFilterTree assigneeAgentTree;
    protected AssignmentPanel assignmentPanel;

    public AssignationAssignmentTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        assigneeAgentTree = new DynamicFilterTree("assigneeAgent", new RefPropertyModel(getElement(), "assigneeAgent"),
                                                    new RefQueryModel(getProject(),
                                                        new Query("findAllAgentsExcept", (Ref)RefUtils.get(getElement(), "actorAgent"))),
                                                    SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = assigneeAgentTree.getNewSelection();
                RefUtils.set(getElement(), "assigneeAgent", selectedResource);
            }
        };
        addReplaceable(assigneeAgentTree);
        assignmentPanel = new AssignmentPanel("assignment", this, "assignment", EDITABLE, feedback);
        addReplaceable(assignmentPanel);
    }

}
