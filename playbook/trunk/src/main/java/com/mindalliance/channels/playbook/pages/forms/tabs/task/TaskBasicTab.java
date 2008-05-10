package com.mindalliance.channels.playbook.pages.forms.tabs.task;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
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

    protected DynamicFilterTree taskTypesTree;

    public TaskBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
         super.load();
         taskTypesTree = new DynamicFilterTree("taskTypes", new RefPropertyModel(getElement(), "taskTypes"),
                                                     new RefQueryModel(getProject(),
                                                         new Query("findAllTypes","TaskType"))) {
             public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 List<Ref> selectedTypes = taskTypesTree.getNewSelections();
                 RefUtils.set(getElement(), "taskTypes", selectedTypes);
             }
         };
         addReplaceable(taskTypesTree);
     }

}
