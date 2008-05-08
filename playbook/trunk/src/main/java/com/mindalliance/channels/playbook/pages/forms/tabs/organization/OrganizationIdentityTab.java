package com.mindalliance.channels.playbook.pages.forms.tabs.organization;

import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
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
 * Time: 1:13:09 PM
 */
public class OrganizationIdentityTab extends ResourceIdentityTab {

    DynamicFilterTree organizationTypesTree;
    DynamicFilterTree subOrganizationsTree;


    public OrganizationIdentityTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        organizationTypesTree = new DynamicFilterTree("organizationTypes",
                                                      new RefPropertyModel(getElement(), "organizationTypes"),
                                                      new RefQueryModel(getScope(), new Query("findAllTypes", "OrganizationType"))){
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = organizationTypesTree.getNewSelections();
                RefUtils.set(getElement(), "organizationTypes", selectedTypes);
            }
        };
        addReplaceable(organizationTypesTree);
        subOrganizationsTree = new DynamicFilterTree("subOrganizations",
                                                      new RefPropertyModel(getElement(), "subOrganizations"),
                                                      new RefQueryModel(getProject(),
                                                                        new Query("findCandidateSubOrganizationsFor", getElement())
                                                      )) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedOrganizations = subOrganizationsTree.getNewSelections();
                for (Ref sub : selectedOrganizations) {
                    RefUtils.add(getElement(), "subOrganizations", sub);
                }
            }
        };
        addReplaceable(subOrganizationsTree);
    }
}
