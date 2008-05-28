package com.mindalliance.channels.playbook.pages.forms.tabs.flowAct;

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

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 8:40:30 PM
 */
public class FlowActBasicTab extends InformationActBasicTab {

    protected DynamicFilterTree targetAgentTree;

    public FlowActBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        targetAgentTree = new DynamicFilterTree("targetAgent", new RefPropertyModel(getElement(), "targetAgent"),
                                                    new RefQueryModel(getPlaybook(),
                                                        new Query("findAllAgentsExcept", getElement(), "actorAgent")),
                                                    SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = targetAgentTree.getNewSelection();
                RefUtils.set(getElement(), "targetAgent", selectedResource);
            }
        };
        addReplaceable(targetAgentTree);
    }

}
