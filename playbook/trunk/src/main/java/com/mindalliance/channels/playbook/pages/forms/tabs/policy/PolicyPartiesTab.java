package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:26:54 PM
 */
public class PolicyPartiesTab extends AbstractFormTab {

    DynamicFilterTree sourceOrganizationTypesTree;
    DynamicFilterTree recipientOrganizationTypesTree;
    DynamicFilterTree relationshipTypesTree;
    Label meaningLabel;

    public PolicyPartiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        sourceOrganizationTypesTree = new DynamicFilterTree("sourceOrganizationTypes",
                                          new RefPropertyModel(getElement(), "sourceOrganizationTypes"),
                                          new RefQueryModel(getScope(), new Query("findAllTypes", "OrganizationType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = sourceOrganizationTypesTree.getNewSelections();
                RefUtils.set(getElement(), "sourceOrganizationTypes", selectedTypes);
                target.addComponent(meaningLabel);
            }
        };
        addReplaceable(sourceOrganizationTypesTree);
        relationshipTypesTree = new DynamicFilterTree("relationshipTypes",
                                          new RefPropertyModel(getElement(), "relationshipTypes"),
                                          new RefQueryModel(getScope(), new Query("findAllTypes", "RelationshipType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = relationshipTypesTree.getNewSelections();
                RefUtils.set(getElement(), "relationshipTypes", selectedTypes);
                target.addComponent(meaningLabel);
            }
        };
        addReplaceable(sourceOrganizationTypesTree);
        recipientOrganizationTypesTree = new DynamicFilterTree("recipientOrganizationTypes",
                                          new RefPropertyModel(getElement(), "recipientOrganizationTypes"),
                                          new RefQueryModel(getScope(), new Query("findAllTypes", "OrganizationType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = recipientOrganizationTypesTree.getNewSelections();
                RefUtils.set(getElement(), "recipientOrganizationTypes", selectedTypes);
                target.addComponent(meaningLabel);
            }
        };
        addReplaceable(recipientOrganizationTypesTree);
        meaningLabel = new Label("meaning", new RefQueryModel(this, new Query("meaning")));
        addReplaceable(meaningLabel);
    }

    private String meaning() {
        return null;  //TODO
    }
}