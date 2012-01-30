package com.mindalliance.channels.playbook.pages.forms.tabs.organization;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 1:13:09 PM
 */
public class OrganizationIdentityTab extends ResourceIdentityTab {

    private DynamicFilterTree organizationTypesTree;
    private DynamicFilterTree subOrganizationsTree;
    private static final long serialVersionUID = -4767328756629468752L;

    public OrganizationIdentityTab(
            String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        organizationTypesTree = new DynamicFilterTree(
                "organizationTypes",
                new RefPropertyModel( getElement(), "organizationTypes" ),
                new RefQueryModel(
                    getScope(),
                    new Query( "findAllTypes", "OrganizationType" ) ) ) {
            private static final long serialVersionUID = 7626157138323204864L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "organizationTypes",
                             organizationTypesTree.getNewSelections() );
            }
        };
        addReplaceable( organizationTypesTree );
        subOrganizationsTree = new DynamicFilterTree(
                "subOrganizations",
                new RefPropertyModel( getElement(), "subOrganizations" ),
                new RefQueryModel(
                        getProject(),
                        new Query(
                            "findCandidateSubOrganizationsFor",
                            getElement() ) ) ) {
            private static final long serialVersionUID = -7344853112902881453L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "subOrganizations",
                             subOrganizationsTree.getNewSelections() );
            }
        };
        addReplaceable( subOrganizationsTree );
    }
}
