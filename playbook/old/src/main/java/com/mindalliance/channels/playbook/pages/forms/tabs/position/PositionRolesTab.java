package com.mindalliance.channels.playbook.pages.forms.tabs.position;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 9:21:40 AM
 */
public class PositionRolesTab extends AbstractFormTab {

    DynamicFilterTree roleTree;

    public PositionRolesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        roleTree = new DynamicFilterTree("roles", new RefPropertyModel(getElement(), "roles"), 
                                                  new RefQueryModel(getProject(), new Query("findAllTypes", "Role"))) {
             public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = roleTree.getNewSelections();
                setProperty("roles", newSelections);
             }
        };
        addReplaceable(roleTree);
    }
}