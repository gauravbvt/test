package com.mindalliance.channels.playbook.pages.forms.tabs.position;

import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 11:02:09 AM
 */
public class PositionResponsibilitiesTab extends ResourceResponsibilitiesTab {

    DynamicFilterTree managedPositionsTree;


    public PositionResponsibilitiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        managedPositionsTree = new DynamicFilterTree("managedPositions", new RefPropertyModel(getElement(), "managedPositions"),
                new RefQueryModel(getElement(), new Query("findOtherPositionsInOrganization", 
                                                           new RefPropertyModel(getElement(), "managedPositions")))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = managedPositionsTree.getNewSelections();
                RefUtils.set(getElement(), "managedPositions", newSelections);
            }
        };
        addReplaceable(managedPositionsTree);
    }
}
