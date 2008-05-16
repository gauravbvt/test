package com.mindalliance.channels.playbook.pages.forms.tabs.group;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
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
 * Date: May 15, 2008
 * Time: 4:10:24 PM
 */
public class GroupWhereTab extends AbstractFormTab {

    protected DynamicFilterTree organizationTypesTree;
    protected LocationPanel locationPanel;

    public GroupWhereTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        organizationTypesTree = new DynamicFilterTree("organizationTypes", new RefPropertyModel(getElement(), "organizationTypes"),
                                           new RefQueryModel(getProject(), new Query("findAllTypes", "OrganizationType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = organizationTypesTree.getNewSelections();
                RefUtils.set(getElement(), "organizationTypes", selected);
            }
        };
        addReplaceable(organizationTypesTree);
        locationPanel = new LocationPanel("location", this, "location", EDITABLE, feedback);
        addReplaceable(locationPanel);
    }
}
