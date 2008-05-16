package com.mindalliance.channels.playbook.pages.forms.tabs.monitoring;

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
 * Date: May 16, 2008
 * Time: 1:51:20 PM
 */
public class MonitoringBasicTab extends InformationActBasicTab {

    DynamicFilterTree infoActTree;

    public MonitoringBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoActTree = new DynamicFilterTree("monitored", new RefPropertyModel(getElement(), "monitoredInformationAct"),
                                            new RefQueryModel(getPlaybook(), new Query("findAllInformationActsExcept", getElement())),
                                            SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = infoActTree.getNewSelection();
                RefUtils.set(getElement(), "monitoredInformationAct", selected);
            }
        };
        addReplaceable(infoActTree);
    }
}
