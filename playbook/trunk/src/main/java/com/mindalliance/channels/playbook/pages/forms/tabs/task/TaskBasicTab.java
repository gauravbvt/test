package com.mindalliance.channels.playbook.pages.forms.tabs.task;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:25:34 PM
 */
public class TaskBasicTab extends InformationActBasicTab {

    protected DynamicFilterTree taskTypeTree;
    protected TimingPanel durationPanel;

    public TaskBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        durationPanel = new TimingPanel("duration", this, "duration", EDITABLE, feedback);
        addReplaceable(durationPanel);
        taskTypeTree = new DynamicFilterTree("taskType", new RefPropertyModel(getElement(), "taskType"),
                new RefQueryModel(getProject(),
                        new Query("findAllTypes", "TaskType")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedType = taskTypeTree.getNewSelection();
                RefUtils.set(getElement(), "taskType", selectedType);
            }
        };
        addReplaceable(taskTypeTree);
    }

}
