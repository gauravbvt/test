package com.mindalliance.channels.playbook.pages.forms.tabs.assignation;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AssignmentPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 8:58:39 PM
 */
public class AssignationAssignmentTab extends AbstractFormTab {

    protected AssignmentPanel assignmentPanel;

    public AssignationAssignmentTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        assignmentPanel = new AssignmentPanel("assignment", this, "assignment", EDITABLE, feedback);
        addReplaceable(assignmentPanel);
    }

}
