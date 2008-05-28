package com.mindalliance.channels.playbook.pages.forms.tabs.assignation;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.ResponsibilityPanel;
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
public class AssignationResponsibilityTab extends AbstractFormTab {

    protected DynamicFilterTree assigneeAgentTree;
    protected ResponsibilityPanel responsibilityPanel;

    public AssignationResponsibilityTab(String id, AbstractElementForm elementForm) {
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
        responsibilityPanel = new ResponsibilityPanel("responsibility", this, "responsibility", EDITABLE, feedback);
        addReplaceable(responsibilityPanel);
    }

}
