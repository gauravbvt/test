package com.mindalliance.channels.playbook.pages.forms.tabs.taskType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
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
 * Date: May 5, 2008
 * Time: 8:23:13 PM
 */
public class TaskTypeIntentTab  extends AbstractFormTab {

    DynamicFilterTree purposeTypesTree;
    DynamicFilterTree eventTypesTree;


    public TaskTypeIntentTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        purposeTypesTree = new DynamicFilterTree("purposeTypes", new RefPropertyModel(getElement(), "purposeTypes"),
                                                 new RefQueryModel(getScope(), new Query("findAllTypes", "PurposeType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = purposeTypesTree.getNewSelections();
                RefUtils.set(getElement(), "purposeTypes", selectedTypes);
            }
        };
        addReplaceable(purposeTypesTree);
        eventTypesTree = new DynamicFilterTree("eventTypes", new RefPropertyModel(getElement(), "eventTypes"),
                                                 new RefQueryModel(getScope(), new Query("findAllTypes", "EventType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = eventTypesTree.getNewSelections();
                RefUtils.set(getElement(), "eventTypes", selectedTypes);
            }
        };
        addReplaceable(eventTypesTree);
    }
}

